# IMDev-Android


**IMDev**——爱萌开发者，是由爱萌基于IM SDK开发并完全开源的的第一款IM APP 
<br/>

期待大家能找出bug,及时修正，也欢迎大家随时吐槽，请入QQ群：99823660
<br/>

截止2015-04-02 最新版本号：v1.2.5 修复了群聊bug
<br/>
<br/>


## 一分钟集成到现有工程
---

* 1、下载最新Android版IMSDK，并导入到现有项目工程中；
* 2、配置AndroidManifest.xml文件，为您的app添加必要的权限；
* 3、注册成为IMSDK开发者，并创建一个应用；
* 4、5行Java代码，实现IM功能。
<br/>
<br/>

1、下载最新Android版IMSDK，并导入到现有项目工程中；

![下载sdk](http://docs.imsdk.im/download/attachments/2457613/image2015-1-5%2022%3A6%3A52.png?version=1&modificationDate=1420466640000&api=v2&effects=border-simple,blur-border)

<br/>
2、配置AndroidManifest.xml文件，为您的app添加必要的权限；

```
AndroidManifest.xml权限配置

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />

```

<br/>

3、注册成为IMSDK开发者，并创建一个应用；
![注册开发者](http://docs.imsdk.im/download/attachments/2457613/image2015-1-5%2022%3A15%3A52.png?version=1&modificationDate=1420467181000&api=v2&effects=border-simple,blur-border)

<br/>

4、4行Java代码，实现IM功能。

```
// 初始化IMSDK
// 设置applicationContext和appKey
IMMyself.init(getApplicationContext(), "00b6413a92d4c1c84ad99e0a", null);
 
// 设置本用户的用户名
IMMyself.setCustomUserID("im@imsdk.im");
 
// 设置本用户密码
IMMyself.setPassword("impasswordforim");
 
// 一键登录
IMMyself.login(true, 5, null);
 
// 发送文本消息
IMMyself.sendText("Hello!", "lyc@imsdk.im", 5, null);
```
<br/>

以上为入门级的代码，4行代码分开写，间隔一定时间执行，在网络环境正常的前提下，即可收发消息。更多内容请参阅Android开发文档。<br/>
如有疑问，欢迎加入 `爱萌官方QQ群99823660` ，一起探讨IM开发相关话题！