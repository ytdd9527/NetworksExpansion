#!/usr/bin/env bash
# 开服测试：下载/缓存 Paper 服务端并启动，监视 console.log 出现 Done 后正常 stop。
# 用法: bash scripts/run-server.sh <mc版本>
# 依赖环境变量（由 workflow 的 resolve 步骤产出）:
#   PAPER_URL     服务端 jar 下载直链（仅缓存未命中时使用）
#   PAPER_MINJAVA 最低 Java 版本要求
set -euo pipefail

MCVER="$1"
: "${PAPER_URL:?PAPER_URL 未设置}"

# 缓存按 MC 版本命名，不挂 build 号（否则每次 resolve 拿到新 build 都会 miss）；
# GitHub 对 7 天无访问的缓存条目整体驱逐，条目内单文件再按 mtime 14 天清理
mkdir -p .paper-cache
find .paper-cache -type f -mtime +14 -delete 2>/dev/null || true
CACHE_JAR=".paper-cache/server-paper-$MCVER.jar"
mkdir -p server

if [ -s "$CACHE_JAR" ]; then
  cp "$CACHE_JAR" server/paper.jar
  echo "命中服务端缓存: $CACHE_JAR"
else
  echo "下载 Paper $MCVER: $PAPER_URL"
  curl -sfL -o server/paper.jar "$PAPER_URL"
  cp server/paper.jar "$CACHE_JAR"
fi

# 26.x 起的 Paper 要求 Java 25，按 resolve 步骤给出的最低版本切换
MINJAVA="${PAPER_MINJAVA:-21}"
if [ "$MINJAVA" -gt 21 ] && [ -n "${JAVA_HOME_25_X64:-}" ]; then
  export JAVA_HOME="$JAVA_HOME_25_X64"
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "Using Java 25: $(java -version 2>&1 | head -n 1)"
fi

echo "eula=true" > server/eula.txt
echo "online-mode=false" > server/server.properties
echo "level-type=flat" >> server/server.properties
echo "generate-structures=false" >> server/server.properties

cd server

# 服务端起来后不会自己退出，干等 timeout 会白跑满 600s。
# 后台启动，轮询 console.log 见到 Done ( 即认为启动成功，主动停服收工。
# 注意：不能用 "< in.fifo" 把 FIFO 直接接到 java 的 stdin——
# FIFO 以只读方式 open 会阻塞直到有写端打开，而写端（监视循环）
# 又在等 console.log 里的 Done，而 Done 要 java 先跑起来才有，
# 形成死锁：java 卡在 open(fifo) 上 550 秒不启动（CI 曾因此白等 9 分钟）。
stop_server() {
  kill "$SERVER_PID" 2>/dev/null || true
  # 卡死的服务端可能忽略 SIGTERM，限时 wait 后用 SIGKILL 补刀
  for _ in $(seq 1 15); do
    kill -0 "$SERVER_PID" 2>/dev/null || return 0
    sleep 1
  done
  kill -9 "$SERVER_PID" 2>/dev/null || true
}

java -Xmx2G -jar paper.jar nogui > console.log 2>&1 &
SERVER_PID=$!
echo "服务端已后台启动 (PID $SERVER_PID)，等待 Done（上限 600s）"
DONE_RC=1
DEADLINE=$(( $(date +%s) + 600 ))
while kill -0 "$SERVER_PID" 2>/dev/null; do
  # Done 后再等 5s，让各插件的 Enabling / 报错日志都落盘再判
  if grep -q 'Done (' console.log 2>/dev/null; then
    sleep 5
    stop_server
    DONE_RC=0
    break
  fi
  if [ "$(date +%s)" -ge "$DEADLINE" ]; then
    echo "启动超时（600s），强制停服"
    stop_server
    break
  fi
  sleep 2
done
# 兜底：进程自己退出（如崩溃）时上面的循环也会结束
wait "$SERVER_PID" 2>/dev/null || true
echo "服务端已停止 (rc=$DONE_RC)"
cd ..
