# 统购 【一发软件】ANDROID 客户端说明文档

> 该文档主要阅读对象是进行 【一发软件】Android 端开发的人员。
> 
> 该文档主要介绍项目的托管地址、项目的结构以及开发需要注意的地方

- 刘汶竹 QQ:`1161685104` 于 `2014-06-06`
## 项目的托管地址及结构

### SVN 托管

SVN 为公司内部服务器

>#### 地址

- [http://192.168.1.46/svn/android/branches/YF/](http://192.168.1.46/svn/android/branches/YF/)

>#### 仓库结构

- 项目采用*主项目*+*依赖库项目* 的结构，SVN 仓库中大体结构如下图
		
		../
			|- YF/
				|- 1.0/
					|- pull_to_refresh_library/
					|- Tonggou_net_module/
					|- Tonggou_yf/

	其中要注意的地方有3个
	
	* `YF` 是 一发App 版本的总目录，`1.0` 为版本号
	* `Tonggou_yf` 为主项目， `*_library` 或 `*_module` 为依赖库项目。以后再要添加依赖项目，其后缀应该用`_library` 或 `_module`。
	* 依赖项目并不是说主项目中只依赖依赖库项目，而是包括主项目中的 `libs` 文件夹中的库文件

### Git 托管

>#### 地址

- [http://git.oschina.net/bcgogo/TonggouAndroidApp_YF](http://git.oschina.net/bcgogo/TonggouAndroidApp_YF) 需要授权

> #### 当前已有分支

- `master`	主分支
- `develop` 开发分支	

	**特别说明** 最新的代码在 `develop` 分支中，还没有合并入 `master`


## 项目代码的包结构

- 包名 `com.tonggou.yf.andclient`
- 代码大致结构为
	
		package/					
			|- bean/				// Java bean
				|- type/				// 枚举类
			|- net/				// 网络
				|- request/			// 请求
				|- response/			// 响应
			|- service/			// 服务，例如 Serviec 和 公共的 BroadcastReceiver
			|- ui/				// UI 部分
				|- fragment/			// fragment
			|- util/				// 工具类
			|- widget/				// 一些自定义组件
				|- view/			// 自定义 view
	
	可根据需要自行扩展

## 主要的第三方库

- [`AsyncHttpClient`](http://loopj.com/android-async-http/) - loopj. 网络处理
- [`Ormlite`](http://ormlite.com/sqlite_java_android_orm.shtml) - 数据库
- [`AndroidAnnotation`](http://androidannotations.org/) - 通过注解来实现快速开发

	**特别说明** 强烈建议在阅读代码前先了解 `AndroidAnnotation` 这个快速开发库 [https://github.com/excilys/androidannotations/wiki](https://github.com/excilys/androidannotations/wiki)
	
	关于如何在 eclipse 配置，可以参考[https://github.com/lwz0316/ConfigAndroidAnnotationInEclipse](https://github.com/lwz0316/ConfigAndroidAnnotationInEclipse)

## 常见问题
- keyStore 的位置及密码

	keyStore 文件在项目的根目录下，文件名为：`tonggou.keystore`， 密码为 `tonggou`

- 关于使用 `AndroidAnnotation`框架构建项目时报错问题

	这个问题（一般是重新打开Eclipse，自动构建项目的时候）的解决方法是：打开项目中报错的文件，编辑一下（一般可以在任意位置打个空格）然后保存即可消除错误。这是由于`Eclipse`的自动构建 BUG。在解决报错文件时的顺序应该是先解决 Fragment 的报错文件，然后再解决 Activity 的报错文件。

<br>
<br>
<br>
<br>
<br>
<br>
<br>


	


					 
		