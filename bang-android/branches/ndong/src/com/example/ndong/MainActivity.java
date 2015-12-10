package com.example.ndong;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {  
	  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
        // 设置使用main.xml定义的界面布局  
        setContentView(R.layout.activity_main);  
          
        // 获得组件  
        final TextView text = (TextView) findViewById(R.id.text);  
        Button ok = (Button) findViewById(R.id.ok);  
          
        // 绑定点击事件监听器  
        ok.setOnClickListener(new View.OnClickListener() {  
              
            public void onClick(View v) {  
                // 通过getResources().getString(R.string.advanced)，可以直接获得字符串内容  
                // text.setText(getResources().getString(R.string.advanced));  
                text.setText(R.string.test);  
            }  
        });  
    }  
}  
