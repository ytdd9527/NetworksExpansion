# AGENTS.md — NetworksExpansion 操作宪章

> 本文件是 AI 编程助手与本仓库开发者的**操作契约**。所有改动（代码、构建、文档）均须遵守本文约定；
> 当本文与代码冲突时，按末尾《文档更新契约》处理。

## TL;DR（太长了不看）

1. 本仓库是 **Minecraft Paper + Slimefun4 附属插件**（Java 21 / Gradle Kotlin DSL），所有改动必须通过 `gradle clean build` 验收后才能提交，禁止提交未编译验证的代码。
2. 玩家可见文案一律走语言文件（`LocalizationService`/`Lang`），日志一律走 `getLogger()` 与 `Debug` 工具类；**禁止** `System.out.println`、**禁止**改动物品/Slimefun ID、**禁止** `git push -f`。
3. 本地直接在 `master` 分支开发（个人仓库，不引入分支流程），提交信息遵循 Conventional Commits（`feat:`/`fix:`/`docs:`/`refactor:`/`chore:`），由开发者本人推送至上游 `ytdd9527/NetworksExpansion`。

---

## 1. 项目身份与边界

| 维度 | 事实 |
| --- | --- |
| 定位 | Minecraft Paper 服务器插件，**Slimefun4（GuguProject fork）附属**，提供物品网络 / 网格 / 仓储系统（NetworkController、Drawer、ItemFlowViewer、QuantumManager 等） |
| 入口类 | `io.github.sefiraat.networks.Networks`（`extends JavaPlugin implements SlimefunAddon`） |
| 版本目标 | Java 21 工具链、Paper 1.21.4 API、Slimefun 2025.1、`api-version: 1.20` |
| 仓库策略 | 本地仓库即开发仓库（个人使用），**直接在 `master` 开发**；由开发者通过 GitHub 推送至上游 `ytdd9527/NetworksExpansion` |
| AI 职责范围 | 编写/修改功能代码、Bug 修复、构建/CI 维护、文档维护 |
| AI 不负责 | 发布版本、推送上游（推送动作由开发者执行） |

### 1.1 代码包地图（改代码前先定位）

| 包 | 职责 |
| --- | --- |
| `io.github.sefiraat.networks` | 插件核心：`Networks`（主类/启动流程）、`commands.NetworksMain`（/networks 命令）、`slimefun.NetworkSlimefunItems` / `slimefun.NetworksSlimefunItemStacks`（核心物品定义）、`slimefun.network.*`（网络核心逻辑）、`managers.*`（Listener/SupportedPlugin 管理） |
| `com.ytdd9527.networksexpansion` | 扩展内容：`setup.SetupUtil`（物品注册入口）、`implementation.ExpansionItems` / `ExpansionItemsMenus`（物品与菜单定义）、`implementation.machines.*`（各机器实现）、`core.managers.ConfigManager`、`core.services.LocalizationService`、`utils.databases.*`（DataSource/DataStorage/QueryQueue） |
| `com.balugaq.netex` | API 与工具：`utils.Debug`、`utils.Lang`、`api.*`（ID、ItemFlowRecord、Keybinds 等） |

---

## 2. 技术栈与项目结构（事实核对）

- **构建**：Gradle Kotlin DSL（`build.gradle.kts`）+ Shadow 插件；`defaultTasks("clean", "build")`；产物 `build/libs/NetworksExpansion-<version>.jar`（fat jar，含 relocation：`org.bstats`→`io.github.sefiraat.networks.bstats`、`net.byteflux.libby`→`com.balugaq.netex.libraries.libby`）。
- **依赖仓库**：仅使用构建脚本中已有的公开远程仓库（Paper、JitPack、CodeMC、Nexus(neetgames) 等）＋ `lib/` 目录下本地 JAR（`compileOnly(fileTree(...))`）。**无公司私服，不要新增私服配置。**
- **核心框架**：Paper API + Slimefun4 + GuizhanLibPlugin（软依赖）；辅助库 SefiLib、Libby（运行时下载 pinyin/opencc4j，见 `Networks.loadLibraries()`）、Lombok、bStats、MorePersistentDataTypes。
- **配置**：`src/main/resources/plugin.yml`（`depend: [Slimefun]`，软依赖列在 `softdepend`，勿动 JustEnoughGuide 注释说明）+ `config.yml`（经 `ConfigManager` 读取，缺失键会在启动时自动合并进服务器 config.yml，但已有键不会被覆盖）。
- **语言文件**：`src/main/resources/lang/*.yml`（zh-CN 等），由 `LocalizationService` 加载；`DEFAULT_LANGUAGE = "zh-CN"`；`Networks.onEnable()` 中 `addLanguage(configManager.getLanguage())` + `addDefaultLanguage("zh-CN")`。
- **日志**：`getLogger()`（info/warning/severe）+ `com.balugaq.netex.utils.Debug`（`Debug.trace(e)` 打异常、`Debug.debug(msg)` 仅在 `config.yml` 的 `debug: true` 时输出、`Debug.log(...)` 走控制台彩色输出）。
- **启动顺序**（`Networks.onEnable()`，新增初始化逻辑须插入对应阶段，不要打乱顺序）：
  `instance = this` → `loadLibraries()` → `ConfigManager` → `LocalizationService` → `superHead()` → `environmentCheck()` → `saveDefaultConfig()` → `tryUpdate()` → `SupportedPluginManager` → `DataSource` → `QueryQueue.startThread()` → `autoSaveThread`（异步定时 `DataStorage.saveAmountChange()`）→ `SetupUtil.setupAll()` → `ListenerManager` → 命令注册 → `setupMetrics()` → 各 `runTaskTimer` 调度 → `AdminDebuggable.load()` → `SlimefunGuideSettings.addOption(...)` → `ID.fetchId()` / `Keybinds.fetchScripts()`。
- **CI**（`.github/workflows/build.yml`）：JDK 21 Temurin，`gradle clean build`，push/PR 到 `master` 触发；提交信息以 `[ci skip]` 开头可跳过。

---

## 3. 决策树：当遇到 A 时，执行 B

### 3.1 新增/修改 Slimefun 物品或机器

- **当新增一个物品/机器时**：在 `ExpansionItems`（扩展物品）或 `NetworksSlimefunItemStacks`（核心物品）中定义 `SlimefunItemStack`，并在 `SetupUtil.setupItem()` 中用 `ExpansionItems.XXX.registerThis()` 注册、挂入对应菜单（`ExpansionItemsMenus.SUB_MENU_*`）。
- **当物品展示名/描述需要玩家可见文案时**：**必须**通过 `LocalizationService.getItem(...)` / `Lang.getItem(...)` 从语言文件取 `items.<id>.name` / `items.<id>.lore`，禁止在代码里硬编码中文/英文文案；新键要同时写入 `lang/zh-CN.yml`。
- **当需要给物品加额外说明行时**：调用 `getItem(id, material, extraLore...)` 的变长参数版本，禁止手动拼接 lore。
- **当注册自定义配方类型时**：用 `LocalizationService.getRecipeType(id, ...)`，key 前缀为 `recipes.`。
- **当需要新菜单（ItemGroup）时**：在 `ExpansionItemsMenus` 中定义并参照 `SetupUtil.setupMenu()` 的 `addTo/addFrom/setTier` 模式挂树，最后 `MAIN_ITEM_GROUP.register(networks)`。

### 3.2 文案与配置

- **当新增玩家可见消息时**：写入语言文件 `messages.<key>`，通过 `LocalizationService.sendMessage(sender, "<key>", args...)` / `getString("messages.<key>", args...)` 或 `Lang.getString("messages.<key>")` 读取；`sendActionbarMessage(player, key, args...)` 用于 ActionBar。
- **当新增配置项时**：在 `src/main/resources/config.yml` 添加默认值（带注释说明用途），并在 `ConfigManager` 增加带默认值的 getter（如 `getInt(path, default)`），禁止在业务代码里直接 `getConfig().getXxx(...)` 散落魔法字符串。
- **当文案中需要颜色时**：语言文件里用 `<Theme名>` 标签（`LocalizationService.color()` 会替换），或用 `TextUtil.color(...)`；禁止在代码里散落 `§` 色码。

### 3.3 异常与日志

- **当捕获到异常需要记录时**：调用 `Debug.trace(e)`（或 `Debug.trace(e, "doing 描述")`），**禁止** `catch` 后吞掉异常、也禁止 `e.printStackTrace()` 裸调用。
- **当需要输出调试信息时**：用 `Debug.debug(...)`（受 `debug` 配置开关控制），**禁止** `System.out.println()`。
- **当需要向玩家发消息但玩家可能离线时**：先判空或捕获异常，参照 `Debug.sendMessage(player, ...)` 的用法。
- **当需要常规日志时**：`getLogger().info(...)` / `warning(...)` / `severe(...)`，文案走语言文件。

### 3.4 网络/仓储核心逻辑

- **当修改网络同步、物品流动逻辑时**：先确认改动属于核心网络（`io.github.sefiraat.networks.slimefun.network.*`、`NetworkController`）还是扩展机器（`com.ytdd9527.networksexpansion.implementation.machines.*`），改动后必须在游戏内验证跨区块/多机器场景，并跑通 `gradle clean build`。
- **当访问/修改已注册 Slimefun 方块的持久化数据时**（适用于**绝大部分粘液方块**，如网络量子存储 `NetworkQuantumStorage`、链式/传送机器等）：使用 `com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils` / `SlimefunBlockData`（参照 `Networks.onEnable()` 中的 dupe 修复逻辑），不要绕过 Slimefun 的存储层直接操作。
- **当数据属于网络抽屉（目前有且仅有这一个例外）时**：不走 `StorageCacheUtils`，必须使用 `com.balugaq.netex.api.data.StorageUnitData` + `DataSource` / `DataStorage` / `QueryQueue` 这套持久化链路（参照 `NetworksDrawer` 与 `DataStorage.createStorageUnitData(...)`），并经 `autoSaveThread` 定期 `DataStorage.saveAmountChange()` 落盘；新增需要自管持久化的方块时须沿用该套件，且 `onDisable()` 中的保存顺序不要改动。
- **当需要向容器菜单推入/取出/检查物品时**：统一使用 `com.balugaq.netex.utils.BlockMenuUtil`——`pushItem(blockMenu, item, slots...)` 推入并返回剩余量（返回非 null 即未放完）、`fits(blockMenu, items, slots...)` 预检容量、`consumeItem(blockMenu, slot, amount, ...)` 消耗/取出；禁止手写堆叠合并逻辑。
- **当需要周期任务时**：参照 `Networks.onEnable()` 中 `Bukkit.getScheduler().runTaskTimer(this, ..., 1, Slimefun.getTickerTask().getTickRate())` 的模式；需要延迟异步执行时用 `runTaskLaterAsynchronously`（参照 `SetupUtil.setupAll()` 里 `setupMenu` 的调度）。

### 3.5 软依赖插件集成

- **当需要与软依赖插件（SlimeHUD、Netheopoiesis、InfinityExpansion、mcMMO、RoseStacker、WildStacker 等）交互时**：先 `supportedPluginManager.isXxx()` 判启用，再用 try/catch 包 `NoClassDefFoundError`（参照 `Networks.setupIntegrations()`），并把注册挂到 `SetupUtil.setupIntegration()`。

### 3.6 依赖与构建

- **当需要新增 Maven 依赖时**：加入 `build.gradle.kts` 的 `dependencies`（区分 `compileOnly` 与 `implementation`；服务端/玩家运行时不打包的用 `compileOnly`），并跑 `gradle build` 验证。
- **当需要运行时动态加载的库（如 pinyin/opencc4j）时**：通过 Libby 在 `Networks.loadLibraries()` 中声明（`libraryManager.addMavenCentral()` + `Library.builder()...build()`），并在 `shadowJar` 的 `relocate` 中处理冲突（如有）。
- **当涉及 `lib/` 本地 JAR 时**：保持 `compileOnly(fileTree(...))` 不变，不要删除 `lib/` 下的文件。
- **当代码需要测试时**：为纯逻辑类（无 Bukkit 运行时依赖的算法/工具类）编写 JUnit 5（Jupiter）测试，放在 `src/test/java`；Mock 用 Mockito。测试基建已就绪：`build.gradle.kts` 已配置 JUnit 5.10（junit-bom）与 `test { useJUnitPlatform() }`，CI（build.yml）含独立 `gradle test` 步骤并上传测试报告。
- **当构建失败时**：先读报错定位（依赖缺失 / 编译错误 / Shadow relocation 冲突），修复后重跑；**禁止**为绕过失败而 `-x test`/`--offline` 硬跳过（本仓库暂无测试，如未来有测试同样禁止跳过）。

### 3.7 提交与推送

- **当准备提交时**：先跑 `gradle clean build` 且通过，再 `git add` 相关文件；提交信息用 Conventional Commits（见第 6 节）。
- **当需要推送时**：推送到本仓库 `master` 即可；推送到上游 `ytdd9527/NetworksExpansion` 的动作**由开发者本人执行**，AI 不代为执行。

---

## 4. 红线（绝对禁止操作）

> 违反以下任一条都属于严重事故。AI 在执行任务时若发现可能触碰红线，必须停下并向开发者说明。

1. **禁止改动物品 ID / Slimefun ID**：删除、重命名、改变 `SlimefunItemStack` 的 id（含 `idPrefix` 拼接规则、`getItemBy` 的 `.toUpperCase(Locale.ROOT)` 结果）会导致玩家存档/方块数据损坏。**改动即视为破坏存档。**
2. **禁止 `git push -f` 或改写共享历史**：禁止 force push、rebase 改写、`git reset --hard` 后强推等任何改写历史的行为。
3. **禁止未构建验证就提交**：任何代码改动（含注释、文案、构建脚本）提交前必须通过 `gradle clean build`；禁止 `--offline`、跳过任务等绕过方式。
4. **禁止在生产路径使用 `System.out.println()`**：日志只能走 `getLogger()` 与 `Debug` 工具类。
5. **禁止绕过 Slimefun 注册流程**：新增/修改物品必须走 `ExpansionItems.XXX.registerThis()` + `SetupUtil` 注册链，禁止绕过注册直接发放/使用 `SlimefunItemStack`。
6. **禁止硬编码玩家可见文案**：所有玩家可见字符串必须来自语言文件（`Lang` / `LocalizationService`）。
7. **禁止修改 `onDisable()` 的保存顺序**：数据库/缓存落盘流程（`ID.saveId()` → `configManager.saveAll()` → `DataStorage.saveAmountChange()` → QueryQueue 排空）不可随意调整，否则可能丢数据。

---

## 5. 质量规范

- **代码风格**：跟随现有代码风格（Google 风格为主、4 空格缩进、`@NotNull`/`@Nullable` 标注、`@SuppressWarnings` 说明理由）。未配置 Checkstyle/SpotBugs/PMD，**不要**在本次任务中擅自引入新 linter 或格式化工具。
- **Lombok**：项目已配置 Lombok（`compileOnly` + `annotationProcessor`），新类可按需使用 `@Getter`/`@Setter`/`@UtilityClass` 等；不要移除既有 Lombok 注解。
- **测试**：纯逻辑代码（算法、无 Bukkit 依赖的工具类）用 **JUnit 5（Jupiter）** 编写单元测试；涉及 Bukkit/Slimefun 运行时的代码不做单元测试，靠游戏内验证。首次引入测试时先补 `build.gradle.kts` 的 `testImplementation` 与 `useJUnitPlatform()`。
- **可编译性**：每个完成的改动都必须是"能编译、能运行"的完整状态；不要留下半成品/死代码/未使用 import。
- **向后兼容**：不得破坏玩家已有存档数据格式（物品 ID、数据库表结构、语言文件 key 结构）。

---

## 6. 常用 Gradle 命令速查表

> 本地推荐使用系统 `gradle`（仓库亦提供 wrapper）；CI 使用 wrapper。受限环境下可加 `--no-daemon --no-watch-fs`。

| 场景 | 命令 |
| --- | --- |
| 标准验收构建（必须通过） | `gradle clean build` |
| 增量编译（快速检查） | `gradle compileJava` |
| 打 fat jar（含 relocation） | `gradle shadowJar`（`build` 已依赖它） |
| 运行单元测试（引入 JUnit 后） | `gradle test` |
| 查看依赖树 | `gradle dependencies` |
| 清理产物 | `gradle clean` |
| 产物位置 | `build/libs/NetworksExpansion-<version>.jar` |

---

## 7. Git 工作流

- **分支**：本地 `master` 直接开发，不建长命分支；推送上游由开发者执行。
- **提交信息**：Conventional Commits，格式 `type(scope): 描述`，中英文均可：
  - `feat:` 新功能/新机器
  - `fix:` Bug 修复
  - `docs:` 文档（含本文件）
  - `refactor:` 重构（行为不变）
  - `chore:` 构建/依赖/杂务
  - 例：`fix(drawer): 修复抽屉计数在异步保存时丢失的问题`
- **提交前自检清单**：
  1. `gradle clean build` 通过；
  2. 未触碰第 4 节红线；
  3. 玩家可见文案已入语言文件；
  4. 只提交相关文件（不提交 `build/`、`.gradle/`、IDE 临时文件）。

---

## 8. 核心类 API 速查与 Contract

> 本节为手写速查，替代逐个翻源码。**Contract 说明**：凡标注"直接改原参"，调用前必须 `clone()`（除非你确实要消费它）；凡标注"新对象"，返回值可安全持有。
> 部分方法源码没有 javadoc（如 `NetworkRoot` 存取族、`LineOperationUtil`），本节 Contract 为**依据实现推断的约定**，写代码时以"调用前 clone"为安全默认。

### 8.1 NetworkRoot — 网络根（`io.github.sefiraat.networks.network.NetworkRoot`）

一个网络（`NetworkController` 管理的方块集合）的根对象，所有"往网络存/从网络取"的最终入口。
获取方式（**推荐，参照 `AbstractGrid` 的写法**——从方块位置取根，含两个判空）：

```java
// 1. 先取该方块位置注册的 NodeDefinition（io.github.sefiraat.networks.NetworkStorage）
NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
// 2. 两个判断：definition 为 null 或其中 Node 为 null → 方块未接入任何网络，立即返回
if (definition == null || definition.getNode() == null) {
    return;
}
// 3. 最终拿到 NetworkRoot
NetworkRoot root = definition.getNode().getRoot();
```

其它获取方式：`NetworkController.getNetworks()`（返回 `Map<Location, NetworkRoot>`，遍历所有网络），或机器生命周期内直接拿到的 `root` 参数；**禁止 `new NetworkRoot(...)`**。

| 方法 | 用途 | Contract |
| --- | --- | --- |
| `addItemStack0(Location accessor, ItemStack incoming)` | 把物品塞进网络（含访问缓存与流记录） | **直接改传入的 `incoming`**：按贪婪块→桶→抽屉→细胞分发，全部吸收后 `incoming.getAmount()==0`；调用后检查剩余量 |
| `addItem(Location accessor, ItemStack incoming)` | `addItemStack0` 的别名 | 同上（改原参） |
| `addItemStack(ItemStack incoming)` | 旧版（无 accessor） | `@Deprecated(forRemoval)`，**改原参**；新代码禁用 |
| `getItemStack0(Location accessor, ItemRequest request)` | 从网络取物品 | **直接改传入的 `request`**（`receiveAmount` 递减剩余需求）；返回**新** ItemStack（非存储活对象），数量可能不足 |
| `requestItem(Location accessor, ItemRequest request)` | `getItemStack0` 包装 | 同上（改 request） |
| `requestItem(Location accessor, ItemStack itemStack)` | 便捷版（内部包 `ItemRequest`） | **不改传入的 itemStack**；返回新 ItemStack |
| `getItemStack(ItemRequest request)` | 旧版（无 accessor） | `@Deprecated(forRemoval)`，**改原参**；新代码禁用 |
| `getAllNetworkItems()` / `getAllNetworkItemsLongType()` / `getAllNetworkItemsLongTypeView()` | 网络全量物品快照 | 只读；返回新 Map |
| `contains(ItemStack)` / `contains(ItemRequest)` / `getAmount(ItemStack)` / `getAmount(Set<ItemStack>)` | 查询 | 只读，不改参数 |
| `getItemStacks(List<ItemRequest>)` / `getItemStacks0(Location, List<ItemRequest>)` | 批量取物 | 批量版会消费 request（同 `getItemStack0` 语义） |
| `getBarrels()` / `getInputAbleBarrels()` / `getOutputAbleBarrels()` | 桶视图 | 只读快照 |
| `getCargoStorageUnitDatas()` / `getInputAbleCargoStorageUnitDatas()` / `getOutputAbleCargoStorageUnitDatas()` | 抽屉/储物单元视图 | 只读快照 |
| `getMapInputAbleBarrels()` / `getMapOutputAbleBarrels()` / `getMapInputAbleCargoStorageUnits()` / `getMapOutputAbleCargoStorageUnits()` | Location→身份 的 map 视图 | 只读快照 |
| `accessInputAbleBarrel(Location)` / `accessOutputAbleBarrel(Location)` | 按位置取桶身份 | 只读 |
| `accessInputAbleDrawerData(Location)` / `accessOutputAbleDrawerData(Location)` / `accessInputAbleCargoStorageUnitData(Location)` / `accessOutputAbleCargoStorageUnitData(Location)` | 按位置取抽屉/单元数据 | 只读 |
| `getCellMenus()` / `getCrafterOutputs()` / `getGreedyBlockMenus()` / `getAdvancedGreedyBlockMenus()` | 对应类型方块菜单集合 | 只读快照 |
| `getNodeCount()` / `getCellsSize()` / `isRealCell(BlockMenu)` | 计数/判定 | 只读 |
| `getNetworkStorage(BlockMenu[, boolean includeEmpty])` / `getInfinityBarrel(...)` / `getFluffyBarrel(...)` / `getBarrel(Location[, includeEmpty])` / `getCargoStorageUnitData(BlockMenu|Location)` | 把 BlockMenu/Location 包装成对应存储身份（静态） | 返回新包装对象，不改参数 |
| `getRootPower()` / `addRootPower(long)` / `removeRootPower(long)` / `retrieveBlockCharge()` | 网络电力 | 改网络电力状态 |
| `allowAccessInput(Location)` / `allowAccessOutput(Location)` / `controlAccessInput/Output(Location)` / `uncontrolAccessInput/Output(Location)` | 输入/输出权限控制 | 改根状态 |
| `addTransportInputMiss(Location)` / `addTransportOutputMiss(Location)` / `reduceTransportInputMiss(Location)` / `reduceTransportOutputMiss(Location)` | 缓存命中统计 | 改统计状态 |
| `tryRecord(Location, ItemRequest)` / `tryRecord(Location, ItemStack before, int after)` | 物品流记录（recordFlow 开启时） | 只做记录 |
| `setOverburdened(boolean)` / `refreshRootItems()` | 过载标记 / 刷新缓存 | 改根状态 |

### 8.2 LineOperationUtil — 沿线传输（`com.balugaq.netex.utils.LineOperationUtil`）

链式传输（LineTransfer 等）与抓取/推送的核心工具：沿一个方向逐格遍历方块，把命中的目标交给回调。

| 方法 | 用途 | Contract |
| --- | --- | --- |
| `doOperation(Location start, BlockFace direction, int limit, [boolean skipNoMenu], [boolean optimizeExperience], Consumer<BlockMenu> consumer)` | 从 start 沿 direction 逐格走 limit 格，对每个 Slimefun 方块菜单执行 consumer | `skipNoMenu=false` 遇到无菜单方块**立即停止**；`true` 则跳过。`optimizeExperience=true` 会多走 1 格（经验修正） |
| `doVanillaOperation(Location, BlockFace, int limit, [skipNoInventory], [optimizeExperience], Consumer<BlockMenu> consumer)` | 同上，目标为原版容器（InventoryHolder） | 回调收到 `VanillaInventoryWrapper`（BlockMenu 包装）；`skipNoInventory=false` 遇非容器停止 |
| `doEnergyOperation(Location, BlockFace, int limit, [allowNoMenu], [optimizeExperience], Consumer<Location> consumer)` | 同上，回调收到 Location（用于能源节点遍历） | `allowNoMenu=false` 遇无菜单方块停止 |
| `grabItem(Location accessor, NetworkRoot root, BlockMenu blockMenu, TransportMode mode, int limitQuantity)` | 从菜单 WITHDRAW 槽**抓取**物品进网络 | **直接改菜单槽里的 ItemStack**（物品被移除）；无 accessor 旧重载 `@Deprecated` |
| `pushItem(Location accessor, NetworkRoot root, BlockMenu blockMenu, List<ItemStack> templates, [int itemIndex,] TransportMode mode, int limitQuantity)` | 从网络**推送**物品进菜单 INSERT 槽 | **改菜单槽内容**；`templates` 仅作模板、**不被修改**（内部包成 `ItemRequest`）；无 accessor 旧重载 `@Deprecated` |
| `pushSlot(Location, NetworkRoot, ItemRequest, BlockMenu, ItemStack template, int slot, int limitQuantity)` | 单槽推送 | 会改 `itemRequest` 数量与菜单槽 |
| `outPower(Location, NetworkRoot root, int rate)` | 把网络电力输出到目标 `EnergyNetComponent` 方块 | 扣减 `root` 电力、增加目标方块电量 |

`TransportMode`（`com.balugaq.netex.api.enums.TransportMode`）语义速查：

| 模式 | grabItem 行为 | pushItem 行为 |
| --- | --- | --- |
| `NONE` / `NONNULL_ONLY` | 抓取全部 | 只填入已存在同类物品的槽（非空优先） |
| `NULL_ONLY` | 无操作 | 只填入空槽 |
| `FIRST_ONLY` / `LAST_ONLY` | 只抓首/末槽 | 只填首/末槽 |
| `FIRST_STOP` | 抓第一个非空槽后停止 | 只作用于第一个可填槽 |
| `LAZY` | 首槽非空才全部抓取 | 首槽为空才推送 |
| `VOID` | 抓取并销毁（垃圾桶） | 从网络取出并销毁 |
| `SPECIFIED_QUANTITY` | 每种物品只保留 limitQuantity，多余抓走 | 补齐到 limitQuantity |
| `P2P` | 无操作 | 按 `itemIndex` 对应槽位推送 |

### 8.3 SetupUtil — 注册入口（`com.ytdd9527.networksexpansion.setup.SetupUtil`）

| 方法 | 用途 |
| --- | --- |
| `setupItem()` | 物品注册总入口：所有 `ExpansionItems.XXX.registerThis()` 与菜单挂接都写在这里 |
| `setupMenu()`（私有） | ItemGroup 菜单树（`addTo`/`addFrom`/`setTier`/`register`），由 `setupAll` 延迟 2 tick 异步调用 |
| `setupWiki()` | 生成 Wiki JSON（`WikiUtils.setupJson`） |
| `setupIntegration()` | 调用 `Networks.getInstance().setupIntegrations()`（软依赖集成） |
| `setupAll()` | 依次 `setupItem → setupWiki → setupIntegration`，最后异步 `setupMenu` |

何时用：**新增机器时只在 `setupItem()` 里追加** `ExpansionItemsMenus.SUB_MENU_XXX.addTo(ExpansionItems.NEW_ITEM.registerThis(), ...)`；不要在其它地方直接调用 `registerThis()`。

### 8.4 ExpansionItems — 物品实例注册表（`com.ytdd9527.networksexpansion.implementation.ExpansionItems`）

- 全部为 `public static final` 机器实例（如 `ADVANCED_QUANTUM_STORAGE`、`CARGO_STORAGE_UNIT_1`、`LINE_TRANSFER_PUSHER`、`QUANTUM_MANAGER`、`DRAWER_MANAGER`…）。
- `registerThis()` 定义在 `SpecialSlimefunItem`（`com.ytdd9527.networksexpansion.core.items`）：`register(Networks.getInstance())` 后返回 `this`（可链式）。
- 何时用：新增机器 → 在此加字段（类型为对应机器类），再在 `SetupUtil.setupItem()` 注册；其余代码通过字段引用实例。
- Contract：字段是**全局单例**，禁止对其做可改状态操作（改 ItemMeta / 加 lore）；展示数据一律来自 `ExpansionItemStacks` / `Lang`。

### 8.5 ExpansionItemStacks — SlimefunItemStack 定义（`com.ytdd9527.networksexpansion.implementation.ExpansionItemStacks`）

- 全部为 `public static final SlimefunItemStack`（id / 材质 / 名称 / lore），名称与 lore 来自 `Lang.getItem(...)`（语言文件 `items.<id>.name/.lore`）。
- 辅助方法：
  - `enchanted(Material)` → 带附魔光效的 `ItemStack`（`ItemStackUtil.getPreEnchantedItemStack`）
  - `Enchanted(Material)` → **`@Deprecated`**，同实现，新代码用 `enchanted`（小写）
- 样式包装：`Theme.random(...)` 随机主题、`Theme.themedSlimefunItemStack(...)` 主题化、`Theme.model(...)` 模型纹理（`io.github.sefiraat.networks.utils.Theme`）。
- Contract：常量**不可变**——不要修改返回的 ItemMeta；**ID 一经发布不可改**（改了即毁档，见红线 1）。

### 8.6 ExpansionRecipes — 配方表（`com.ytdd9527.networksexpansion.implementation.ExpansionRecipes`）

- 全部为 `public static final ItemStack[]`（3×3 共 9 格配方），命名与物品一致（如 `ADVANCED_IMPORT`、`LINE_TRANSFER_PUSHER`、`CARGO_STORAGE_UNIT_1`）。
- 特殊常量：`NULL`（9 个 null = 无配方）、`HOPPER`、`CRAFTING_TABLE`（材料快捷常量）。
- 何时用：新增机器 → 在此添加 `XXX` 配方数组，并作为构造参数传入（`new Xxx(itemGroup, item, TYPE, ExpansionRecipes.XXX[, outputAmount])`；配方数组引用核心物品时用 `NetworkSlimefunItems.XXX.getItem()` 等）。
- Contract：视为**只读常量**，禁止运行时修改数组元素；配方数组是构造期一次性使用。

### 8.7 Lang — 语言文件门面（`com.balugaq.netex.utils.Lang`，全部只读、返回新对象）

| 方法 | 返回 | 说明 |
| --- | --- | --- |
| `get()` | `LocalizationService` | `Networks.getLocalizationService()` |
| `getItem(String id, Material\|String texture\|ItemStack, String... extraLore)` | 新 `SlimefunItemStack` | 键 `items.<id>`；extraLore 追加到 lore 末尾 |
| `getIcon(String key, Material)` | 新 `ItemStack` | 键 `icons.<key>` |
| `getMechanism(String key)` | 新 `ItemStack` | 键 `icons.mechanism.<key>`（Book 图标） |
| `getString(String key)` / `getString(String key, Object... args)` | `String` | 已着色（`&` 与 `<Theme>` 标签）；args 走 `MessageFormat` |
| `getStringList(String key)` / `getStringArray(String key)` | 新 `List<String>` / `String[]` | 已着色 |

Contract：**纯读取**；每次调用返回新实例，调用方可以安全修改返回值；禁止把返回的 `SlimefunItemStack` 缓存为全局后改其 meta（应改用 `ExpansionItemStacks` 常量或重新 `getItem`）。

### 8.8 zh-CN.yml — 语言文件（默认语言 `zh-CN`）

- 位置：`src/main/resources/lang/zh-CN.yml`（约 3882 行）；由 `LocalizationService` 加载，`DEFAULT_LANGUAGE = "zh-CN"`。
- 顶层结构：`messages:`（玩家消息，含 `guide`/`keybind`/`feedback`/`startup`/`shutdown` 等子树）、`icons:`（GUI 图标 `<key>.name/.lore`）、`items:`（物品 `<id>.name/.lore`）。
- 读取方式：消息 → `Lang.getString("messages.<key>")` / `LocalizationService.sendMessage(sender, "<key>", args...)`；物品 → `Lang.getItem("ID", material)`（键 `items.<ID>`）；图标 → `Lang.getIcon("key", material)`。
- 着色：`&` 色码 + `<Theme名>` 标签（如 `<click_info>`，`LocalizationService.color()` 替换）。
- Contract：**新增玩家可见文案必须同步写入此文件**；键一经发布不可改名（旧配置/存档引用失效）；每个键必须有值（缺失会在控制台 severe 提示 `No localization found for path: ...`）。

### 8.9 Debug — 日志/调试工具（`com.balugaq.netex.utils.Debug`，全部静态、不改参数）

| 方法 | 用途 |
| --- | --- |
| `debug(Object.../Object/String.../String)` | 开发期调试输出，前缀 `[Debug] `，**仅在 `config.yml` 的 `debug: true` 时打印** |
| `debug(Throwable)` | 打印 `e.getMessage()` + `trace(e)` |
| `log(Object.../Object/String.../String)` / `log(Throwable)` / `log()` | 彩色控制台输出（不判 debug），用于重要提示 |
| `trace(Throwable[, String doing][, Integer code])` | 打印"DO NOT REPORT…"横幅 + 堆栈（logger severe）——**catch 块必须用它，禁止吞异常** |
| `traceExactly(Throwable, String doing, Integer code)` | 更详细的异常信息版（message/cause/stackTrace/suppressed 全打） |
| `sendMessage(Player, Object.../Object/String.../String)` | 给玩家发 `[插件名]消息`（**原样字符串，不走语言文件**——玩家可见业务文案仍应走 `Lang`） |
| `stackTraceManually()` | 手动打印当前堆栈（调试用） |

Contract：所有方法仅产生副作用（日志/消息），**不修改任何入参**、不返回业务值。

---

## 9. AI 对用户的回答规范

- **先一句话回答**：回答开头用一句话说清"我做了什么/结论是什么"（如"已修复 X：在 Y 中补了 Z"）。
- **再简短补充**：只补充代码/文件里看不出来的信息——决策依据、取舍、待确认事项、风险点。
- **不重复"代码可说明"的内容**：不要把刚写进代码/文档的东西再抄一遍（完整代码片段、逐条复述文档表格、重复列方法签名等）；用户直接查看改动文件即可获得细节。
- **保持简短**：除非用户明确要求详细讲解，回答控制在几句话内。

---

## 10. 文档更新契约

1. **冲突即提示**：当 AI 发现本文与代码不一致时（例如：`build.gradle.kts` 的 Java 版本、构建命令、物品注册流程、语言文件目录与本文描述不符），**必须**在回复中主动指出冲突，并说明应以代码为准还是更新本文。
2. **惯例沉淀**：当本次任务产生了新的、可复用的约定（新的注册模式、新的配置规范、新的测试规范）时，AI 应提议将其补充进本文对应章节（先提议，经开发者确认后修改）。
3. **保持精简**：更新本文时不得堆砌无行动含义的描述性文字；每一条规则都应能被"是否遵守"直接检查。
4. **变更记录**：修改本文后，提交信息使用 `docs(agents): ...`，并在提交说明中一句话概括变更点。
