# 教你用Java来玩答题(百万英雄/冲刺大会等)

# MillionHero
今日头条的百万英雄答题助手 

《百万英雄》是一档全民知识互动游戏，在《百万英雄》里每场12道题目全部回答正确的人，将瓜分奖金。
游戏模式
  
  一共12道题，全部答对就可以平分奖金

如果可以把直播中的问题和答案提取出来，然后百度，然后统计一下哪个更相关，就可以辅助你答题了。
当然也可以直接把百度出来题目和答案都展示出来。本文用的第一种简单粗暴。

# 运行展示

![](1516095737427.gif)
# 工具介绍
* JAVA8
* Android 手机
* Adb 驱动
* TessOCR

# 原理说明

1. 将手机点击到直播界面（在这里我们先打开一张图片）；
2. 用Adb 工具获取当前手机截图
3. 用OCR进行图像识别，提取文字；
4. 将文字中的问题和答案提取出来；
5. 使用搜索工具并打开网页
6. 计算问题和答案的相似度，此处调用百度的Nlp接口或者采用PMI的方式
> 该公式的依据来自于维基百科:
      https://en.wikipedia.org/wiki/Pointwise_mutual_information
      
# 使用步骤
  相关软件工具安装和使用步骤请参考  [Android操作步骤](https://github.com/lingfengsan/MillionHero/wiki/Android%E6%93%8D%E4%BD%9C%E6%AD%A5%E9%AA%A4)

  
>PS:注意程序中的adb驱动目录要更换成自己的目录
  
  > 我的屏幕是1920*1080，如果是别的分辨率，暂时需要修改一下代码中的图片参数等。
    


# FAQ
+  详见 [Wiki-FAQ](https://github.com/lingfengsan/MillionHero/wiki)
# 更新日志
+  详见 [changelog](https://github.com/lingfengsan/MillionHero/blob/master/changelog.md)
# 开发者列表
+  详见 [contributors](https://github.com/lingfengsan/MillionHero/graphs/contributors)
# TODO
+  可以增加一个图形化界面，分别对题目和答案进行搜索并进行展示。 

# QQ交流
+  283616637（500人）
