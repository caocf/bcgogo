# 统购 ANDROID GSM 代码结构说明

### SVN 地址

- SVN 地址为 `http://192.168.1.46/svn/android/branches/GSM/`

### SVN 仓库结构

- 项目采用*主项目*+*依赖库项目* 的结构，SVN 仓库中大体结构如下图
		
		../
			|- GSM/
				|- 1.0/
					|- pull_to_refresh_library/
					|- sliding_menu_library/
					|- Tonggou2.0_gsm/

	其中要注意的地方有两个
	
	* `GSM` 是 GSM 版本的总目录，`1.0` 为版本号
	* `Tonggou2.0_gsm` 为主项目， `*_library` 为依赖库项目。以后再要添加依赖项目，其后缀应该用`_library`。


### 主项目的结构

- 包名 `com.tonggou.gsm.andclient`
- 项目大致结构为
	
		package/					
			|- bean/				// Java bean
				|- type/				// 枚举类
			|- db/				// 数据库
				|- table/			// 数据库表
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


<br>
<br>
<br>
<br>
<br>
<br>
<br>


	


					 
		