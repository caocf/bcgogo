package com.tonggou.andclient;

import com.tonggou.andclient.vo.type.ServiceScopeType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class ReservationServiceActivity extends BaseActivity {
	private View backView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reservation_service);
		
		backView = findViewById(R.id.left_button);
		backView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ReservationServiceActivity.this.finish();
			}
		});
		
		View weixiu = findViewById(R.id.weixiu_bg);
		weixiu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ReservationServiceActivity.this,StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.category",ServiceScopeType.OVERHAUL_AND_MAINTENANCE);       //机修保养
				//toHome.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_weixin));  
				intent.putExtra("tonggou.shop.categoryname",StoreQueryActivity.ALLCATEGORY);     
				startActivity(intent);
				((TextView)findViewById(R.id.weixiu_tv)).setTextColor(0xff666666);
			}
		});
		weixiu.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				((TextView)findViewById(R.id.weixiu_tv)).setTextColor(0xffffffff);
				return false;
			}
		});
		
		
		View baoyang = findViewById(R.id.baoyang_bg);
		baoyang.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ReservationServiceActivity.this,StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.category",ServiceScopeType.DECORATION_BEAUTY);       //美容装潢
				//toHome.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_meirong)); 
				intent.putExtra("tonggou.shop.categoryname",StoreQueryActivity.ALLCATEGORY);     
				startActivity(intent);
				((TextView)findViewById(R.id.baoyang_tv)).setTextColor(0xff666666);
			}
		});
		baoyang.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				((TextView)findViewById(R.id.baoyang_tv)).setTextColor(0xffffffff);
				return false;
			}
		});
		
		
		View baoxian = findViewById(R.id.baoxian_bg);
		baoxian.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent toHome = new Intent(ReservationServiceActivity.this,StoreQueryActivity.class);
				toHome.putExtra("tonggou.shop.category",ServiceScopeType.PAINTING);       //钣金喷漆
				//toHome.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_banjin)); 
				toHome.putExtra("tonggou.shop.categoryname",StoreQueryActivity.ALLCATEGORY);     
				startActivity(toHome);
				((TextView)findViewById(R.id.baoxian_tv)).setTextColor(0xff666666);
			}
		});
		baoxian.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				((TextView)findViewById(R.id.baoxian_tv)).setTextColor(0xffffffff);
				return false;
			}
		});
		
		
		View yanche = findViewById(R.id.yanche_bg);
		yanche.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent toHome = new Intent(ReservationServiceActivity.this,StoreQueryActivity.class);
				toHome.putExtra("tonggou.shop.category",ServiceScopeType.INSURANCE);       ///保险验车
				//toHome.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_baoxianlipei)); 
				toHome.putExtra("tonggou.shop.categoryname",StoreQueryActivity.ALLCATEGORY);     
				startActivity(toHome);
				((TextView)findViewById(R.id.yanche_tv)).setTextColor(0xff666666);
			}
		});	
		yanche.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				((TextView)findViewById(R.id.yanche_tv)).setTextColor(0xffffffff);
				return false;
			}
		});
		
	
	}
	

}
