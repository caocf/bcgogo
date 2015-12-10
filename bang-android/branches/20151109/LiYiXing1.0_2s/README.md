# 统购 ANDROID GSM 【利宜行2S】代码结构说明

> 该文档主要阅读对象是进行 【利宜行2S】Android 端开发的人员。
> 
> 该文档主要介绍项目的托管地址、项目的结构以及开发需要注意的地方

- 夏云辉 QQ:`1954388803` 于 `2015-11-13`

### SVN 地址

- SVN 地址为 `http://192.168.1.46/svn/android/branches/20151109`

### SVN 仓库结构

- 项目采用*主项目*+*依赖库项目* 的结构，SVN 仓库中大体结构如下图
		
		../
			|- GSM/
				|- 1.0/
					|- pull_to_refresh_library/
					|- sliding_menu_library/
					|- LiYiXing1.0_2s/

	其中要注意的地方有两个
	
	* `GSM` 是 GSM 版本的总目录，`1.0` 为版本号
	* `Tonggou2.0_gsm` 为主项目， `*_library` 为依赖库项目。以后再要添加依赖项目，其后缀应该用`_library`。

### SVN 分支结构

- 代码主分支是 `GSM/1.0`, 其余的分支都是从 `GSM/1.0` 中分出的，分出的分支都是发布的分支（分支名字就是发布的版本号）。所有的开发记录都是在 `GSM/1.0`中。


### Git 托管

>#### 地址

- [http://git.oschina.net/bcgogo/TonggouAndroidApp_GSM](http://git.oschina.net/bcgogo/TonggouAndroidApp_GSM) 需要授权

> #### 当前已有分支

- `master`	主分支



## 项目代码的包结构

- 包名 `com.tonggou.gsm.andclient`
- 项目大致结构为
	
		package/					
			|- bean/				// Java bean
				|- type/				// 枚举类
			|- db/				// 数据库
				|- table/			// 数据库表
				|- dao/			// 数据库操作类
			|- net/				// 网络
				|- parser/			// 解析
				|- request/			// 请求
				|- response/			// 响应
			|- obd/				// OBD 空目录，暂时没用
			|- service/			// 服务，例如 Serviec 和 公共的 BroadcastReceiver
			|- ui/				// UI 部分
				|- fragment/			// fragment
				|- view/				// 自定义 View
			|- util/				// 工具类
			|- test/				// 一些测试类 
	
	可根据需要再扩展

## 主要的第三方库

- [`AsyncHttpClient`](http://loopj.com/android-async-http/) - loopj. 网络处理
- [`Ormlite`](http://ormlite.com/sqlite_java_android_orm.shtml) - 数据库
- BaiduMap - 百度地图
- Zbar - 二维码扫描

## 常见问题

- keyStore 的位置及密码

	keyStore 文件在项目的根目录下，文件名为：`tonggou.keystore`， 密码为 `tonggou`

- 关于 Android 版本的 VersionCode

	 在 AnidroidManifest 中的 `android:versionCode="1049"` 的版本号与 SVN 的代码版本号是一致的。

<br>
<br>
<br>
<br>
<br>
<br>
<br>


	


					 
		