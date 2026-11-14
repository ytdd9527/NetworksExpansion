#!/usr/bin/env bash
# 开服测试：下载/缓存 Paper 服务端并启动，监视 console.log 出现 Done 后正常 stop。
# 用法: bash scripts/run-server.sh <mc版本>
# 依赖环境变量（由 workflow 的 resolve 步骤产出）:
#   PAPER_BUILD   最新 build 号（缓存文件名与 key 的一部分）
#   PAPER_URL     服务端 jar 下载直链
#   PAPER_MINJAVA 最低 Java 版本要求
set -euo pipefail

MCVER="$1"
: "${PAPER_BUILD:?PAPER_BUILD 未设置}" "${PAPER_URL:?PAPER_URL 未设置}"

# 缓存目录按 build 号分 jar；GitHub 对 7 天无访问的缓存条目整体驱逐，
# 条目内单文件再按 mtime 14 天清理，避免旧 build jar 长期占额度
mkdir -p .paper-cache
find .paper-cache -type f -mtime +14 -delete 2>/dev/null || true
CACHE_JAR=".paper-cache/server-paper-$MCVER-$PAPER_BUILD.jar"
mkdir -p server

if [ -s "$CACHE_JAR" ]; then
  cp "$CACHE_JAR" server/paper.jar
  echo "命中服务端缓存: $CACHE_JAR"
else
  echo "下载 Paper $MCVER build $PAPER_BUILD: $PAPER_URL"
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
rm -f in.fifo
mkfifo in.fifo
( for i in $(seq 1 110); do grep -q 'Done (' console.log 2>/dev/null && break; sleep 5; done; sleep 5; echo stop > in.fifo ) &
MON_PID=$!
timeout 600 java -Xmx2G -jar paper.jar nogui < in.fifo > console.log 2>&1 || true
kill $MON_PID 2>/dev/null || true
rm -f in.fifo
cd ..
