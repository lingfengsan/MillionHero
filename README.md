# 教你用Java来玩答题(百万英雄/冲刺大会等)

# MillionHero
今日头条的百万英雄答题助手

《百万英雄》是一档全民知识互动游戏，在《百万英雄》里每场12道题目全部回答正确的人，将瓜分奖金。
游戏模式
  
  一共12道题，全部答对就可以平分奖金

如果可以把直播中的问题和答案提取出来，然后百度，然后统计一下哪个更相关，就可以辅助你答题了。
当然也可以直接把百度出来题目和答案都展示出来。本文用的第一种简单粗暴。

# 工具介绍
* JAVA8
* Android 手机
* Adb 驱动

# 原理说明

1. 将手机点击到直播界面（在这里我们先打开一张图片）；
2. 用Adb 工具获取当前手机截图，并用adb将截图pull上来
   >     adb shell screencap -p /sdcard/1.png
   >     adb pull /sdcard/1.png .
3. 用tessOCR进行图像识别，提取文字；
4. 将文字中的问题和答案提取出来；
5. 使用百度搜索并统计搜索得到结果数量
   1. 问题+各个答案count(q&a)；
   2. 问题 count(q)；
   3. 答案 count(a)；
6. 计算匹配值pmi: pmi[i]=count(q&a[i])/(count(q)*count(a[i]))
7. 选择pmi值最高的为答案。
> 该公式的依据来自于维基百科:
      https://en.wikipedia.org/wiki/Pointwise_mutual_information
      
# 使用步骤
1. 安卓手机打开USB调试，设置》开发者选项》USB调试
2. 电脑与手机USB线连接，确保执行adb devices可以找到设备id
3. 打开百万直播
4. 运行我们的java程序，当弹出题目时，输入回车即可

  
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
