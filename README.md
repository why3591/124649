# LoveGreenCode

> 温馨提示：不要使用自己的实名GitHub账号进行Star或Fork（请用无法追踪实名的小号），Fork主要是为了使用GitHub的在线编译打包功能，如果有条件自己编译的，可以直接clone项目。

### 获取Apk（能自己本地编译的请直接clone项目，只执行此步骤中的第三步即可）

一、先Fork本项目，然后代码就到你自己的仓库中了，方便后面修改。

![](https://raw.githubusercontent.com/fuckxmz/LoveGreenCode/master/img1.png)

二、然后在Fork后的项目中点击“Action”（初次点击有一个提示，直接点绿色按钮开启即可），成功开启Action就可以看见下面有一个“Android CI”。

![](https://raw.githubusercontent.com/fuckxmz/LoveGreenCode/master/img2.png)

三、编辑项目中的 `gradle.properties` 文件。

![](https://raw.githubusercontent.com/fuckxmz/LoveGreenCode/master/img3.png)

把 `APPLICATION_ID`（包名）这个属性随便改一个值，不要和默认一样就行，一般就是com.xxx123.yyy456这类似的格式，改完点下面的绿色提交按钮，Action就会自动生成Apk了。

![](https://raw.githubusercontent.com/fuckxmz/LoveGreenCode/master/img4.png)

四、回到刚才的Action页面，点击最新的那个Workflow，然后在最下面就能下载Apk了（有的手机不让安装debug包，可以在电脑上使用命令 `adb install -t xxx.apk` 安装）。

![](https://raw.githubusercontent.com/fuckxmz/LoveGreenCode/master/img5.png)

### 使用技巧

1. 顶部滚动公告、名字、身份证，都可以点击修改。

2. 点击扫码按钮会跳到场所信息，长按的话是快速跳转。

3. 除1和2以外，点首页的其他地方，都是跳支付宝的健康通。

4. 场所信息页面长按第一块区域可以添加地点。

### 敬告

此项目仅用于学习娱乐和提高生活效率，首页的绿码是无效的，请避免被扫。
