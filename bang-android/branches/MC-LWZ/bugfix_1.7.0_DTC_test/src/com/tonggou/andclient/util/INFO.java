package com.tonggou.andclient.util;

import android.os.Build;

import com.tonggou.andclient.app.TongGouApplication;


public class INFO {
	public static final String APPNAME = "tonggou";	
	
	public static final String VERSION = TongGouApplication.getInstance().getVersionName();
	public static final String MOBILE_PLATFORM = "ANDROID";
	public static final String MOBILE_PLATFORM_VERSION = Build.VERSION.RELEASE;	// 用户手机系统平台版本 3.0 4.0
	public static final String MOBILE_MODEL = Build.MODEL;       //用户手机型号
	public static final String IMAGE_VERSION =  
			TongGouApplication.getInstance().getImageVersion();   //手机硬件分辨率  480 X 800

    public static  String PARTNERID =  "0000" ;
	
    public static final String HTTP_HEAD = "https://"; 
	public static final String HOST_IP = "phone.bcgogo.cn:1443/api";      //测试
    
//    public static final String HTTP_HEAD = "http://";
//	public static final String HOST_IP = "192.168.1.33:8080/api";      //测试
	
//	public static final String HOST_IP = "shop.bcgogo.com/api";    //真实运营
//	public static  int HOST_PORT = 443;
	
	public static final int ITEMS_PER_PAGE = 10;

}
