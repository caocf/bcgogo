package com.tonggou.andclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.util.SomeUtil;

public class RegisterActivity extends BaseActivity {
	private TextView  next;
	private ImageView back;
	private EditText  name,pass,password,phonenumber;//,nickname
	private String carNum="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register1);
		name=(EditText) findViewById(R.id.registername);
		pass=(EditText) findViewById(R.id.registerpass);
		password=(EditText) findViewById(R.id.registerpassword);
		//nickname=(EditText) findViewById(R.id.registernickname);
		phonenumber=(EditText) findViewById(R.id.registerphonenum);
		name.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					pass.requestFocus(); 
				} 
				return false; 
			} 
		});
		name.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
			@Override 
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String nameStr=name.getText().toString().trim();
					if(nameStr!=null&&!"".equals(nameStr)){	
						if(SomeUtil.isNumeric(nameStr)){
							phonenumber.setText(nameStr);
							carNum="";
						}else{
							if(SomeUtil.isCarNum(nameStr.trim().toUpperCase())){
								carNum=nameStr.trim().toUpperCase();
							}else{
								carNum="";
								//phonenumber.setText("");
							}
						}
					}else{
						carNum="";
						//phonenumber.setText("");
					}
				} 
			}
		});


		pass.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					password.requestFocus(); 
				} 
				return false; 
			} 
		}); 
	 
		
		password.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					phonenumber.requestFocus(); 
				} 
				return false; 
			} 
		}); 
		phonenumber.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					toNext();
				} 
				return false; 
			} 
		});

		name.clearFocus();
		pass.clearFocus();
		password.clearFocus();
		phonenumber.clearFocus();
		next=(TextView) findViewById(R.id.register_next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nameStr=name.getText().toString().trim();
				if(nameStr!=null&&!"".equals(nameStr)){	
					if(SomeUtil.isNumeric(nameStr)){
						//phonenumber.setText(nameStr);
						carNum="";
					}else{
						if(SomeUtil.isCarNum(nameStr.trim().toUpperCase())){
							carNum=nameStr.trim().toUpperCase();
						}else{
							carNum="";
							//phonenumber.setText("");
						}
					}
				}else{
					carNum="";
					//phonenumber.setText("");
				}
				toNext();
			}
		});
		back=(ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisterActivityNext.registercarnameStr=null;
				RegisterActivityNext.carBrand=null;
				RegisterActivityNext.carMold=null;
				RegisterActivityNext.carBrandId=null;
				RegisterActivityNext.carMoldId=null;
				RegisterActivityNext.shop2DCodeStr=null;
				RegisterActivityNext.shop2DCodeId = null;
				RegisterActivityNext.registernextmileStr=null;
				RegisterActivityNext.registernexttimeStr=null;
				RegisterActivityNext.registernexttime2Str=null;
				RegisterActivityNext.currentMileStr=null;
				RegisterActivity.this.finish();
			}
		});
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void onBackPressed() {
		RegisterActivityNext.registercarnameStr=null;
		RegisterActivityNext.carBrand=null;
		RegisterActivityNext.carMold=null;
		RegisterActivityNext.carBrandId=null;
		RegisterActivityNext.carMoldId=null;
		RegisterActivityNext.shop2DCodeStr=null;
		RegisterActivityNext.shop2DCodeId = null;
		RegisterActivityNext.registernextmileStr=null;
		RegisterActivityNext.registernexttimeStr=null;
		RegisterActivityNext.registernexttime2Str=null;
		RegisterActivityNext.currentMileStr=null;
		RegisterActivity.this.finish();
	}


	public void toNext(){
		String nameStr=name.getText().toString().trim().toUpperCase();
		String passStr=pass.getText().toString();
		String passwordStr=password.getText().toString();
		String phonenumberStr=phonenumber.getText().toString();


		if(nameStr!=null&&!"".equals(nameStr)&&nameStr.length()>4){
			if(SomeUtil.isRegisterName(nameStr)){
				if(passStr!=null&&!"".equals(passStr)){
					if(passStr.length()>=6){
						if(passStr.indexOf(" ")!=-1){
							Toast.makeText(RegisterActivity.this,"密码不能含有空格", Toast.LENGTH_SHORT).show();
							return;
						}
						if(SomeUtil.justIfChineseInput(passStr)){
							Toast.makeText(RegisterActivity.this,"密码不能含有中文字符", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(passwordStr!=null&&!"".equals(passwordStr)){
							if(passwordStr.equals(passStr)){
								if(phonenumberStr!=null&&!"".equals(phonenumberStr)){
									 if(SomeUtil.isPhoneNumberValid(phonenumberStr)){
										 Intent intent = new Intent(RegisterActivity.this,RegisterActivityNext.class);
										 intent.putExtra("tonggou.name",nameStr);
										 intent.putExtra("tonggou.password",passwordStr);
										 intent.putExtra("tonggou.carNum",carNum);
										 intent.putExtra("tonggou.phonenumber",phonenumberStr);
										 startActivity(intent);
									 }else{
										 Toast.makeText(RegisterActivity.this,getString(R.string.wrongnumber), Toast.LENGTH_SHORT).show();
									 }
								 }else{
									 Toast.makeText(RegisterActivity.this,getString(R.string.login_phonenumberStr_ull), Toast.LENGTH_SHORT).show();
								 }
							}else{
								Toast.makeText(RegisterActivity.this,getString(R.string.login_password_same), Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(RegisterActivity.this,getString(R.string.login_password_again_null), Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(RegisterActivity.this,"密码不能少于6位数", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(RegisterActivity.this,getString(R.string.login_password_null), Toast.LENGTH_SHORT).show();
				}	
			}else{
				Toast.makeText(RegisterActivity.this,getString(R.string.login_name_wrong), Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(RegisterActivity.this,getString(R.string.login_name_null)+"或者不能少于5个字", Toast.LENGTH_SHORT).show();
		}

	}
}
