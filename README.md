# Arcaea B30 Fucker

## 一. 简介

这是一个基于**Xposed**的微型后端，提供下列资源查询(打勾为已实现，未打勾则是饼)：

- [x] 歌曲信息查询
- [ ] Best30查询
- [ ] 曲绘查询
- [x] 单曲成绩查询

> 您可以通过Issue来增加要写的功能
>
> 欢迎四方大神前来维护

## 二. 使用

### 2.1 LSPosed

1. 在`release`中下载最新版本并安装，在`LSPosed`的管理页面启用模块

2. 强行停止Arcaea，重启

3. 在手机浏览器上访问:`http://localhost:61616/arcapi/v1/version`，若出现下面的字样，证明后端服务部署成功

   ```json
   `{"code":200,"data":{"versionCode":1,"versionName":"1.0"},"msg":"success"}`
   ```

   ~~下面的教程均为实现，为饼，实不实现看我心情~~

4. 下载`release`中的`Frontend`模块，点击生成b30，查询单曲信息等，然后分享之。

## 2.2 LSPatch

等待施工...

## 三. API列表

1. 公共前缀：`http://localhost:61616/arcapi/v1`

2. 注意事项：

   1. 若下文未特别注明，则正常code一律为`200`，正常msg一律为`success`
   1. 如无特殊说明，下文中提到的所有`id`为一个全小写无空格的字符串
   1. 必选参数使用`*`注释

3. 公共响应码：

   | Code | Description          |
   | ---- | -------------------- |
   | 200  | 接口返回正常         |
   | -1   | 接口内部错误接口说明 |
   | 1001 | 未输入id             |
   | 1002 | id不正确             |
   | 1003 | 未游玩此id代表的谱面 |

4. 接口说明：

   - 获取版本信息

     **描述**

     获取后端的版本信息

     **URL**

     GET /version

     **返回参数**

     | Key              | Description |
     | ---------------- | ----------- |
     | data             | 版本数据    |
     | data.versionCode | 版本代码    |
     | data.versionName | 版本号      |

   - 根据id获取歌曲详情

     **描述**

     根据id获取**唯一**的歌曲详情，其中id一般为小写字母不带空格。

     **URL**

     POST /res/getSongInfoById

     **query参数**

     | Key  | Description                |
     | ---- | -------------------------- |
     | *id  | 一个字符串，代表要查询的id |

     **返回参数**

     > 注: 精确到小数点的定数在服务器，本地没有，等待改进

     | Key                              | Description                                |
     | -------------------------------- | ------------------------------------------ |
     | data                             | 本次查询到的歌曲数据                       |
     | data.title_localized             | 为一个列表，存放了不同语言下显示的不同名称 |
     | data.artist                      | 曲目的作者                                 |
     | data.date                        | 曲目实装到Arcaea的日期                     |
     | date.version                     | 曲目实装到Arcaea的版本                     |
     | date.difficulties                | 难度列表                                   |
     | date.difficulties.ratingClass    | 难度标识，0为pst，1为prs，2为ftr，3为byd   |
     | date.difficulties.rating         | 曲目的定级                                 |
     | date.difficulties.chartDesigner  | 铺师                                       |
     | date.difficulties.jacketDesigner | 封面作者(无则为空)                         |

     **返回示例**

     ```json
     {
         "code": 200,
         "data": {
             "idx": 57,
             "id": "dreaminattraction",
             "title_localized": {
                 "en": "Dreamin' Attraction!!"
             },
             "artist": "翡乃イスカ",
             "search_title": {
                 "ja": [
                     "どりーみんあとらくしょん"
                 ],
                 "ko": [
                     "드리밍 어트랙션"
                 ]
             },
             "search_artist": {
                 "en": [
                     "hino isuka"
                 ],
                 "ja": [
                     "ひのいすか"
                 ],
                 "ko": [
                     "히노 이스카"
                 ]
             },
             "bpm": "205",
             "bpm_base": 205,
             "set": "base",
             "purchase": "",
             "audioPreview": 39804,
             "audioPreviewEnd": 60878,
             "side": 0,
             "world_unlock": true,
             "bg": "base_light",
             "bg_inverse": "base_conflict",
             "date": 1509667201,
             "version": "1.5",
             "difficulties": [
                 {
                     "ratingClass": 0,
                     "chartDesigner": "Nitro",
                     "jacketDesigner": "",
                     "rating": 4
                 },
                 {
                     "ratingClass": 1,
                     "chartDesigner": "Nitro",
                     "jacketDesigner": "",
                     "rating": 7
                 },
                 {
                     "ratingClass": 2,
                     "chartDesigner": "Nitro",
                     "jacketDesigner": "",
                     "rating": 9
                 }
             ]
         },
         "msg": "success"
     }
     ```

   - 根据id获取游玩成绩
   
     **描述**
   
     根据id获取游玩成绩，其中id一般为小写字母不带空格。
   
     **URL**
   
     POST /data/getPlayerDataById
   
     **query参数**
   
     | Key  | Description                |
     | ---- | -------------------------- |
     | *id  | 一个字符串，代表要查询的id |
   
     **返回参数**
   
     > 注: 精确到小数点的定数在服务器，本地没有，等待改进
   
     | Key                    | Description                                    |
     | ---------------------- | ---------------------------------------------- |
     | data                   | 本次查询到的歌曲数据，为一个列表。             |
     | data.clearStatus       | 通关代码，详情见下方引用                       |
     | date.difficulty        | 难度                                           |
     | date.farCount          | 爆far数量                                      |
     | date.health            | 歌曲结束时的回忆率，其中困难模式tl的回忆率为-1 |
     | date.id                | 歌曲的id                                       |
     | date.lostCount         | 爆lost数量                                     |
     | date.perfectCount      | P数量                                          |
     | date.score             | 分数                                           |
     | date.shinyPerfectCount | 大P数量                                        |
   
     > ### 关于通关代码
     >
     > | Code | Description   |
     > | ---- | ------------- |
     > | 0    | Track Lost    |
     > | 1    | 普通搭档Clear |
     > | 2    | Full Recall   |
     > | 3    | Pure Memory   |
     > | 4    | 简单搭档Clear |
     > | 5    | 困难搭档Clear |
   
     **返回示例**
   
     ```json
     {
         "code": 200,
         "data": [
             {
                 "clearStatus": 2,
                 "difficulty": "PAST",
                 "farCount": 3,
                 "health": 100,
                 "id": "fairytale",
                 "lostCount": 0,
                 "perfectCount": 333,
                 "score": 9955677,
                 "shinyPerfectCount": 320
             },
             {
                 "clearStatus": 2,
                 "difficulty": "PRESENT",
                 "farCount": 10,
                 "health": 100,
                 "id": "fairytale",
                 "lostCount": 4,
                 "perfectCount": 497,
                 "score": 9824333,
                 "shinyPerfectCount": 459
             },
             {
                 "clearStatus": 2,
                 "difficulty": "FUTURE",
                 "farCount": 7,
                 "health": 100,
                 "id": "fairytale",
                 "lostCount": 0,
                 "perfectCount": 775,
                 "score": 9955932,
                 "shinyPerfectCount": 690
             },
             {
                 "clearStatus": 2,
                 "difficulty": "BEYOND",
                 "farCount": 18,
                 "health": 100,
                 "id": "fairytale",
                 "lostCount": 0,
                 "perfectCount": 914,
                 "score": 9904241,
                 "shinyPerfectCount": 808
             }
         ],
         "msg": "success"
     }
     
     ```
   
   - 刷新资源
   
     **描述**
   
     刷新缓存的歌曲资源
   
     **URL**
   
     GET /res/refreshResource
   
     **返回示例**
   
     ```json
     {"code":200,"msg":"success"}
     ```
   
   - 获取图片
   
     **描述**
   
     根据id等信息获得曲绘
   
     **URL**
   
     GET /res/image
   
     **query参数**
   
     | Key        | Description                                  |
     | ---------- | -------------------------------------------- |
     | *id        | 一个字符串，代表要查询的id                   |
     | difficulty | 难度，可以填写难度和难度代号，默认为future。 |
     | size       | 大小，可填写256和512，默认为256              |
   
     **返回示例**
   
     对应的曲绘，略
