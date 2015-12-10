package com.tonggou.andclient.app;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadCastReceiver extends BroadcastReceiver{   
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
    //private Context nowContext; 
    public void onReceive(Context context, Intent intent2) {
    	Log.e("CONTEETTT","onReceive....."+intent2.getAction());
        if (intent2.getAction().equals(ACTION)) {
        	//ÔÝÊ±ÆÁ±Î
            Intent intentSer = new Intent(context, com.tonggou.andclient.app.TongGouService.class);
            context.startService(intentSer);
        	

       
        	
        }   
    }  
  
    
    
   
}  