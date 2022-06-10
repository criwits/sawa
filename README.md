# SAWA: Simple Aircraft War on Android

SAWA（Simple Aircraft War on Android，简易 Android 飞机大战游戏）是一款 Java 开发的 Android 平台的「飞机大战」主题小游戏。
支持单人游戏、多人同屏游戏两种游戏模式。

SAWA 是哈尔滨工业大学（深圳）的《面向对象的软件构造实践》课程项目的一部分。

## 源码结构

SAWA 是一个以 Gradle 为包管理器的，使用 Android Studio 开发的 Java Android 应用。
其目录结构为 Android Studio 默认的结构，使用 Android Studio 打开整个文件夹就能正常使用。

## 通信约定

SAWA 是 [SAWS](https://git.hit.edu.cn/criwits/saws) 的前端，与 SAWS 以一致的通信方式通信。

协议栈：WebSocket，信息载体：JSON

### 请求登入服务器

```json
{
  "type": "user_query",
  "username": "<用户名>",
  "password": "<密码>"
}
```

#### 返回

如果 uid 为 -1 表示失败。

```json
{
  "type": "user_query_response",
  "uid": -1
}
````

### 请求下载房间列表

```json
{
  "type": "room_info"
}
```

返回：（如果没有可用房间，`rooms` 数组就是空的）

```json
{
  "type": "room_info_response",
  "rooms": [
    {
      "room_id": 1,
      "difficulty": 0
    },
    {
      "room_id": 3,
      "difficulty": 2
    }
  ]
}
```

### 请求新建房间

```json
{
  "type": "create_room",
  "difficulty": 2
}
```

返回创建成功的房间号：

```json
{
  "type": "create_room_response",
  "room_id": 4
}
```

### 加入房间

```json
{
  "type": "join_room",
  "room_id": 4
}
```

#### 成功

```json
{
  "type": "join_room_response",
  "success": true
}
```

#### 失败

```json
{
  "type": "join_room_response",
  "success": false
}
```

#### 告知房主

```json
{
  "type": "room_ready"
}
```

### 上传屏幕信息

```json
{
  "type": "resolution",
  "width": 1080,
  "height": 1920
}
```

返回双方协商后的尺寸信息和难度，双方接到这条消息时开始游戏：

```json
{
  "type": "game_start",
  "ratio": 1.6
}
```
---

### 上传 NPC 信息

只能由房主上传。

```json
{
  "type": "npc_upload",
  "mob": 0,
  "id": 14,
  "location_x": 134,
  "location_y": 476,
  "speed_x": 0,
  "speed_y": 3,
  "hp": 10
}
```

### 下发 NPC 信息

只会发给房客。

```json
{
  "type": "npc_spawn",
  "mob": 0,
  "id": 14,
  "location_x": 134,
  "location_y": 476,
  "speed_x": 0,
  "speed_y": 3,
  "hp": 10
}
```

### 上传移动信息

注意坐标是缩放前的：

```json
{
  "type": "movement",
  "new_x": 753,
  "new_y": 255
}
```

队友将得到：

```json
{
  "type": "teammate_movement",
  "new_x": 753,
  "new_y": 255
}
```

### 上传击伤信息

```json
{
  "type": "damage",
  "id": 14,
  "hp_decrease": 10,
  "location_x": 514,
  "location_y": 114
}
```

### 下发得分和删除飞机信息

```json
{
  "type": "score",
  "remove": 14,
  "score": 20
}
```
如果 remove 为 -1，则无条件得分（炸弹道具）。


### 无条件消除飞机

对应「飞机飞出边界」事件，由房主负责。

```json
{
  "type": "remove_aircraft",
  "remove": 14
}
```

### 道具生成

0 = Blood, 1 = Bomb, 2 = Bullet

```json
    {
      "type": "prop_spawn",
      "props": [
        {
          "id": 1,
          "kind": 0,
          "location_x": 514,
          "location_y": 114
        },
        {
          "id": 2,
          "kind": 0,
          "location_x": 143,
          "location_y": 241
        }
      ]
    }
```

### 道具无条件移除

```json
{
  "type": "remove_prop",
  "remove": 14
}
```

### 道具碰撞

```json
{
  "type": "prop_action",
  "id": 14
}
```

### 道具生效

炸弹生效：接到此消息则清屏，然后给自己加上 `add_score` 的分数

```json
{
  "type": "bomb_action",
  "add_score": 1540
}
```

加血生效：接到此消息，给自己无条件加血 `add_hp`（注意封顶）

```json
{
  "type": "blood_action"
}
```

子弹道具生效：接到此消息，若 `target` 为 `true`，则给自己启用效果；否则，给队友使用效果。

```json
{
  "type": "bullet_action",
  "target": true
}
```

## 第三方库

使用了下面这些库，在 Gradle 文件中已配置好。

- `androidx.room`
- `org.java-websocket`
- `com.alibaba:fastjson`

## 开源许可

MIT