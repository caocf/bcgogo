package com.tonggou.andclient;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BrandModelResponse;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncLoadCacheJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.QueryBandModelRequest;
import com.tonggou.andclient.vo.BrandModel;

/**
 * 搜索车辆品牌或车辆型号 页面
 * @author think
 *
 */
public class AppointmentNetWorkSearch extends BaseActivity {
	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x003;
	private static final int  NETWORK_NODATA=0x004;
	private View backButton,search,nameListView;
	private ImageView search_clean;
	private EditText searchText;
	private ListView nameSearch;
	private TextView search_sure,title;
	private BookListAdapter bookAdpter;
	private Handler handler;
	private String key,keyId,from,modle="";
	private List<BrandModel> brandmodels;
	private ProgressBar progressCircleBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_network_search);
		progressCircleBar =(ProgressBar)this.findViewById(R.id.topbar_progress);
		progressCircleBar.setVisibility(View.VISIBLE);


		from = getIntent().getStringExtra("tonggou.from");
		modle = getIntent().getStringExtra("tonggou.pinpai");
		title=(TextView) this.findViewById(R.id.title_tx);
		if("chexing".equals(from)){
			title.setText(getString(R.string.search_model));
		}
		if("pinpai".equals(from)){

			title.setText(getString(R.string.search_brand));
		}
		backButton = this.findViewById(R.id.back);
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {	
				//isbnPreference.clearAll();
				AppointmentNetWorkSearch.this.finish();
			}
		});
		search_clean=(ImageView)findViewById(R.id.book_search_close_icon);
		search_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchText.setText("");
				//search.setBackgroundResource(R.drawable.search_bg_d);
				search_clean.setVisibility(View.GONE);
				nameListView.setVisibility(View.GONE);
				if("pinpai".equals(from)){
					getSearchBookAction(null, true, null);
				}if("chexing".equals(from)){
					getSearchBookAction(null, false, modle);
				}	
			}
		});
		search=this.findViewById(R.id.book_search);
		search.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(searchText, 0);
				//search.setBackgroundResource(R.drawable.search_bg_d);
				search_sure.setVisibility(View.VISIBLE);
				searchText.setCursorVisible(true);
				return false;
			}
		});
		searchText =(EditText)this.findViewById(R.id.book_search_et);

		searchText.clearFocus();
		//searchText.setCursorVisible(false);
		searchText.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == KeyEvent.KEYCODE_ENTER) { 
					key = searchText.getText().toString();
					if("pinpai".equals(from)){
						Intent intent = new Intent();
						intent.putExtra("pinpai", key);
						setResult(1010, intent);
						TongGouApplication.getInstance().notifyBrandSelected(false, key, keyId);
						AppointmentNetWorkSearch.this.finish();
					}if("chexing".equals(from)){
						Intent intent = new Intent();
						intent.putExtra("chexing", key);
						setResult(2020, intent);
						TongGouApplication.getInstance().notifyTypeSelected(false, key, keyId);
						AppointmentNetWorkSearch.this.finish();
					}
				} 
				return false; 
			} 
		}); 
		searchText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//search.setBackgroundResource(R.drawable.search_bg_d);
				search_sure.setVisibility(View.VISIBLE);
				searchText.setCursorVisible(true);
				return false;
			}
		});
		searchText.addTextChangedListener(new TextWatcher(){  
			public void afterTextChanged(Editable s) { 
				final String key=s.toString(); 
				if(key!=null&&!"".equals(key)){	
					search_clean.setVisibility(View.VISIBLE);
					progressCircleBar.setVisibility(View.VISIBLE);
					if(key.length()>0){	
						if(brandmodels!=null&&brandmodels.size()>0){
							brandmodels.clear();
						}
						if("pinpai".equals(from)){
							getSearchBookAction(key, true, null);
						}if("chexing".equals(from)){
							getSearchBookAction(key, false, modle);
						}									
					}else{
						if(brandmodels!=null && brandmodels.size()>0){
							brandmodels.clear();
						}
						nameListView.setVisibility(View.GONE);
					}
				}else{
					search_clean.setVisibility(View.GONE);
				}

			}  			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }  
			public void onTextChanged(CharSequence s, int start, int before,  int count) {  }  
		});
		search_sure=(TextView)this.findViewById(R.id.book_search_sure);
		search_sure.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				key = searchText.getText().toString();
				if("pinpai".equals(from)){
					Intent intent = new Intent();
					intent.putExtra("pinpai", key);
					setResult(1010, intent);
					TongGouApplication.getInstance().notifyBrandSelected(false, key, keyId);
					AppointmentNetWorkSearch.this.finish();
				}if("chexing".equals(from)){
					Intent intent = new Intent();
					intent.putExtra("chexing", key);
					setResult(2020, intent);
					TongGouApplication.getInstance().notifyTypeSelected(false, key, keyId);
					AppointmentNetWorkSearch.this.finish();
				}
			}
		});

		//nameAdpter=new NameListAdapter(this);
		nameListView= findViewById(R.id.nameListView);
		nameListView.setVisibility(View.GONE);
		nameSearch=(ListView) findViewById(R.id.nameList);
		nameSearch.setCacheColorHint(0);
		nameSearch.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				progressCircleBar.setVisibility(View.GONE);
				if(brandmodels!=null&&brandmodels.size()>arg2){				

					//searchText.setText(key);
					Editable etext2 = searchText.getText();
					Selection.setSelection(etext2, etext2.toString().length());
					if("pinpai".equals(from)){
						key=brandmodels.get(arg2).getBrandName();
						keyId=brandmodels.get(arg2).getBrandId();
						Intent intent = new Intent();
						intent.putExtra("pinpai", key);
						intent.putExtra("pinpaiId", keyId);
						setResult(1010, intent);
						TongGouApplication.getInstance().notifyBrandSelected(false, key, keyId);
						AppointmentNetWorkSearch.this.finish();
					}if("chexing".equals(from)){
						key=brandmodels.get(arg2).getModelName();
						keyId=brandmodels.get(arg2).getModelId();
						Intent intent = new Intent();
						intent.putExtra("chexing", key);
						intent.putExtra("chexingId", keyId);
						setResult(2020, intent);
						TongGouApplication.getInstance().notifyTypeSelected(false, key, keyId);
						AppointmentNetWorkSearch.this.finish();
					}

				}
			}
		});
		bookAdpter=new BookListAdapter(this);

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){

				case NETWORK_SUCCEED: 
					bookAdpter.setData(brandmodels);	
					bookAdpter.notifyDataSetChanged();
					nameSearch.setAdapter(bookAdpter);
					nameListView.setVisibility(View.VISIBLE);
					progressCircleBar.setVisibility(View.GONE);
					break;				
				case NETWORK_NODATA :
					progressCircleBar.setVisibility(View.GONE);
					Toast.makeText(AppointmentNetWorkSearch.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_FAILD :
					progressCircleBar.setVisibility(View.GONE);
					Toast.makeText(AppointmentNetWorkSearch.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;				

				}
			}
		};
		if("pinpai".equals(from)){
			getSearchBookAction(null, true, null);
		}if("chexing".equals(from)){
			getSearchBookAction(null, false, modle);
		}									
	}
	public void getSearchBookAction(final String keywords,final boolean isQueryBrand,String brandId){
		
		
		final QueryBandModelRequest request = new QueryBandModelRequest();
		request.setApiParams(keywords, isQueryBrand, brandId);
		request.doRequest(this, new AsyncLoadCacheJsonBaseResponseParseHandler<BrandModelResponse>() {

			@Override
			public void onLoadCache(BrandModelResponse result, String originResult,
					boolean isNetworkConnected) {
				if( result != null ) {
					brandmodels = result.getBrandModel();
					if( brandmodels != null && !brandmodels.isEmpty() ) {
						bookAdpter.setData(brandmodels);	
						bookAdpter.notifyDataSetChanged();
						nameSearch.setAdapter(bookAdpter);
						nameListView.setVisibility(View.VISIBLE);
					} else {
						if( !isNetworkConnected ) {
							TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
						}
					}
				} else {
					if( !isNetworkConnected ) {
						TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
					}
				}
			}
			
			@Override
			public void onParseSuccess(BrandModelResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				brandmodels = result.getBrandModel();
				if( brandmodels == null || brandmodels.isEmpty() ) {
					TongGouApplication.showToast( "没有找到对应" + ( isQueryBrand ? "品牌" : "车型") );
				} else {
					bookAdpter.setData(brandmodels);	
					bookAdpter.notifyDataSetChanged();
					nameSearch.setAdapter(bookAdpter);
					nameListView.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				progressCircleBar.setVisibility(View.GONE);
			}
			
			@Override
			public String getUserNo() {
				return null;
			}
			
			@Override
			public boolean isCache() {
				// 若没有关键字则缓存，否则不缓存
				return TextUtils.isEmpty(keywords);
			}

			@Override
			public Class<BrandModelResponse> getTypeClass() {
				return BrandModelResponse.class;
			}
			
		});
		
//		String url ;
//		if(brandId==null||"".equals(brandId)){
//			url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/brandModel/keywords/"+URLEncoder.encode(keywords)+"/type/"+type+"/brandId/NULL";
//		}else{
//			url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/brandModel/keywords/"+URLEncoder.encode(keywords)+"/type/"+type+"/brandId/"+brandId;
//
//		}
//		BrandModelParser brandModelParser = new BrandModelParser();		
//		NetworkState ns = Network.getNetwork(AppointmentNetWorkSearch.this).httpGetUpdateString(url,brandModelParser);	
//
//		if(ns.isNetworkSuccess()){
//			if(brandModelParser.isSuccessfull()){
//				brandmodels=brandModelParser.getBrandModelResponse().getBrandModel();
//				if(brandmodels!=null){
//					sendMessage(NETWORK_SUCCEED, brandModelParser.getBrandModelResponse().getMessage());
//				}else{
//					sendMessage(NETWORK_NODATA,brandModelParser.getBrandModelResponse().getMessage());
//				}
//
//			}else{
//				//解析出错
//				sendMessage(NETWORK_NODATA, brandModelParser.getErrorMessage());
//			}
//		}else{
//			//网络出错
//			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
//		}
	}
	private class BookListAdapter extends BaseAdapter{
		private LayoutInflater mayorInflater;
		private List<BrandModel> data;
		public BookListAdapter(Context context){
			mayorInflater = LayoutInflater.from(context);

		}
		public void setData(List<BrandModel>  data) {
			if (data != null) {
				this.data = data;
			}
		}
		public int getCount() {
			if(data!=null){
				return data.size();
			}else{
				return 0;
			}
		}
		public BrandModel getItem(int position) {
			if(data!=null){
				return data.get(position);
			}else{
				return null;
			}
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View view, ViewGroup parent) {
			View convertView = mayorInflater.inflate(R.layout.popview_item, null);
			TextView name = (TextView)convertView.findViewById(R.id.popview_name);
			name.setTextColor(Color.BLACK);
			if("pinpai".equals(from)){
				if(data.get(position).getBrandName()!=null){			
					name.setText(data.get(position).getBrandName());
				}

			} else if("chexing".equals(from)){
				if(data.get(position).getModelName()!=null){			
					name.setText(data.get(position).getModelName());
				}

			}


			return convertView;
		}

	}
	protected void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if( "pinpai".equals(from) ) {
			TongGouApplication.getInstance().notifyBrandSelected(true, null, null);
		} else if("chexing".equals(from)) {
			TongGouApplication.getInstance().notifyBrandSelected(true, null, null);
		}
	}
}
