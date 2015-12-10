package com.tonggou.andclient;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.SearchServiceParser;
import com.tonggou.andclient.parse.StoreQueryParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.TGService;
/**
 * 服务查询页面
 * @author think
 *
 */
public class SearchServiceActivity extends BaseActivity {

	private static final int  UNFINISH_NETWORK_FAILD=-1;
	private static final int  UNFINISH_ACTION_SUCCEED=0x001;
	private static final int  FINISH_ACTION_SUCCEED=5;
	private static final int  ACTION_FAILD=0x002;	
	private static final int  UNFINISH_NETWORK_NODATA=0x003;	
	private static final int  FINISH_NETWORK_NODATA=6;	
	private static final int  FINISH_NETWORK_FAILD=4;
	private static final int NEXTPAGE_UNFINISHED_NETWORK_SUCCEED =0x007;    //下一页成功
	private static final int NEXTPAGE_UNFINISHED_NETWORK_NODATA =0x008;     //下一页没有数据
	private static final int NEXTPAGE_FINISHED_NETWORK_SUCCEED =0x009;    //下一页成功
	private static final int NEXTPAGE_FINISHED_NETWORK_NODATA =0x0010;     //下一页没有数据
	private static final int CLOSE_PRO_UNFINISHED=0x0011;
	private static final int CLOSE_PRO_FINISHED=0x0012;
	private static final int IMAGE_FLUSH_UNFINISHED=0x0013;
	private static final int IMAGE_FLUSH_FINISHED=0x0014;

	private Handler handler;
	private View back,fresh,already,no;
	private ListView  nolistView,alreadylistView;
	private ShopsListAdapter shopsListAdapterno,shopsListAdapteralready; 
	private ArrayList<TGService> allServersNo = new ArrayList<TGService>();
	private ArrayList<TGService> allServersNoNext = new ArrayList<TGService>();
	private ArrayList<TGService> allServersReady = new ArrayList<TGService>();
	private ArrayList<TGService> allServersReadyNext = new ArrayList<TGService>();

	private ProgressBar notReadyPB,areadyPB;
	private int pageNumNo=1;
	private int pageNumReady=1;
	private int finishPageNum=1;
	private String unfinshedNum="";
	private String finshedNum="";
	private boolean firstSelectFinished = true;


	int visbleCountUnfinished=0;
	private int firstListItemUnfinished = 0;                      //从哪个位置开始取图片
	private boolean nextPageUnfinishedLock = false,hastoLastUnfinished=false;   
	int visbleCountFinished=0;
	private int firstListItemFinished = 0;                      //从哪个位置开始取图片
	private boolean nextPageFinishedLock = false,hastoLastFinished=false;   
	
	private int nowSelect = 0;      
	private boolean unNetworking = false;
	private boolean reNetworking = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_service);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchServiceActivity.this.finish();
			}
		});
		fresh=findViewById(R.id.right_button);
		fresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(nowSelect==0){
					if(unNetworking){
						return;
					}
					pageNumNo = 1;
					hastoLastUnfinished=false;
					notReadyPB.setVisibility(View.VISIBLE);
					if(allServersNo!=null){
						allServersNo.clear();
					}
					new Thread(){
						public void run(){							
							getUnfinishedServices("NULL","unfinished",currentUserId,pageNumNo,INFO.ITEMS_PER_PAGE+"");
						}
					}.start();
				
				}else{
					if(reNetworking){
						return;
					}
					finishPageNum = 1;
					hastoLastFinished=false;
					areadyPB.setVisibility(View.VISIBLE);
					if(allServersReady!=null){
						allServersReady.clear();
					}
					new Thread(){
						public void run(){
							getFinishedServices("NULL","finished",currentUserId,finishPageNum,INFO.ITEMS_PER_PAGE+"");
						}
					}.start();
					
				}
			}
		});
		no=findViewById(R.id.no);
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nowSelect = 0;
				((TextView)findViewById(R.id.already_tx1)).setTextColor(Color.GRAY);
				((TextView)findViewById(R.id.already_tx2)).setTextColor(Color.GRAY);
				((TextView)findViewById(R.id.no_tx1)).setTextColor(Color.WHITE);
				((TextView)findViewById(R.id.no_tx2)).setTextColor(Color.WHITE);
				alreadylistView.setVisibility(View.GONE);
				nolistView.setVisibility(View.VISIBLE);
			}
		});
		already=findViewById(R.id.already);
		already.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nowSelect = 1;
				((TextView)findViewById(R.id.already_tx1)).setTextColor(Color.WHITE);
				((TextView)findViewById(R.id.already_tx2)).setTextColor(Color.WHITE);
				((TextView)findViewById(R.id.no_tx1)).setTextColor(Color.GRAY);
				((TextView)findViewById(R.id.no_tx2)).setTextColor(Color.GRAY);
				alreadylistView.setVisibility(View.VISIBLE);
				nolistView.setVisibility(View.GONE);
				if(firstSelectFinished){
					firstSelectFinished = false;
					areadyPB.setVisibility(View.VISIBLE);
					new Thread(){
						public void run(){
							getFinishedServices("NULL","finished",currentUserId,finishPageNum,INFO.ITEMS_PER_PAGE+"");
						}
					}.start();
				}
			}
		});

		nolistView = (ListView) findViewById(R.id.list_no);
		shopsListAdapterno = new ShopsListAdapter(SearchServiceActivity.this); 
		nolistView.setDividerHeight(0);
		nolistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {
				Intent intent=new Intent(SearchServiceActivity.this,OrderDetilActivity.class);
				intent.putExtra("tonggou.server.orderid", allServersNo.get(position).getOrderId());
				intent.putExtra("tonggou.server.from", "SearchServiceActivity");
				startActivityForResult(intent, 6060);
			}
		});
		nolistView.setOnScrollListener(new OnScrollListener(){
			int firstItem = 0;
			int lastItem =0;
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				firstItem = firstVisibleItem;
				visbleCountUnfinished = visibleItemCount;
				lastItem = firstVisibleItem + visibleItemCount ;
				firstListItemUnfinished= firstVisibleItem ;
			}
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE||scrollState == 2){					
					if(allServersNo!=null && allServersNo.size()>0){
						new Thread(){
							public void run(){
								addPhoto(allServersNo,firstItem,visbleCountUnfinished,"no");									
							}
						}.start();
						new Thread(){
							public void run(){
								removePhoto(allServersNo,firstItem,visbleCountUnfinished);
							}
						}.start();
					}
					/*if(firstItem==0){
						if(!canNext){
							return;
						}
					}*/
					if(!hastoLastUnfinished&&allServersNo.size()>=INFO.ITEMS_PER_PAGE){	
						if(lastItem==allServersNo.size()){			
							if (!nextPageUnfinishedLock) {
								nextPageUnfinishedLock = true;
								notReadyPB.setVisibility(View.VISIBLE);	
								new Thread() {
									public void run() {								
										getUnfinishedNextPage();
									}
								}.start();
							}
						}
					}
				}
			}        	
		});	
		alreadylistView = (ListView) findViewById(R.id.list_already);
		shopsListAdapteralready = new ShopsListAdapter(SearchServiceActivity.this);
		alreadylistView.setDividerHeight(0);
		alreadylistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {
				Intent intent=new Intent(SearchServiceActivity.this,OrderDetilActivity.class);
				intent.putExtra("tonggou.server.orderid", allServersReady.get(position).getOrderId());
				intent.putExtra("tonggou.server.from", "SearchServiceActivity");
				startActivity(intent);
			}
		});
		alreadylistView.setOnScrollListener(new OnScrollListener(){
			int firstItem = 0;
			int lastItem =0;
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				firstItem = firstVisibleItem;
				visbleCountFinished = visibleItemCount;
				lastItem = firstVisibleItem + visibleItemCount ;
				firstListItemFinished= firstVisibleItem ;
			}
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE||scrollState == 2){					
					if(allServersReady!=null && allServersReady.size()>0){
						new Thread(){
							public void run(){
								addPhoto(allServersReady,firstItem,visbleCountFinished,"already");									
							}
						}.start();
						new Thread(){
							public void run(){
								removePhoto(allServersReady,firstItem,visbleCountFinished);
							}
						}.start();
					}
					/*if(firstItem==0){
						if(!canNext){
							return;
						}
					}*/
					if(!hastoLastFinished&&allServersReady.size()>=INFO.ITEMS_PER_PAGE){	
						if(lastItem==allServersReady.size()){			
							if (!nextPageFinishedLock) {
								nextPageFinishedLock = true;
								areadyPB.setVisibility(View.VISIBLE);	
								new Thread() {
									public void run() {								
										getFinishedNextPage();
									}
								}.start();
							}
						}
					}
				}
			}        	
		});	
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case UNFINISH_NETWORK_FAILD: 
					Toast.makeText(SearchServiceActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					notReadyPB.setVisibility(View.GONE);
					break;
				case FINISH_NETWORK_FAILD: 
					Toast.makeText(SearchServiceActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					areadyPB.setVisibility(View.GONE);
					break;
				case UNFINISH_ACTION_SUCCEED: 
					shopsListAdapterno.notifyDataSetChanged();
					shopsListAdapterno.setDate(allServersNo);
					nolistView.setAdapter(shopsListAdapterno);
					((TextView)findViewById(R.id.no_tx2)).setText("("+unfinshedNum+")");
					notReadyPB.setVisibility(View.GONE);
					shopsListAdapterno.notifyDataSetChanged();
					break;
				case FINISH_ACTION_SUCCEED: 
					//shopsListAdapteralready = new ShopsListAdapter(SearchServiceActivity.this,allServersReady);   

					shopsListAdapteralready.notifyDataSetChanged();
					shopsListAdapteralready.setDate(allServersReady);
					alreadylistView.setAdapter(shopsListAdapteralready);
					((TextView)findViewById(R.id.already_tx2)).setText("("+finshedNum+")");
					areadyPB.setVisibility(View.GONE);
					shopsListAdapteralready.notifyDataSetChanged();
					break;
				case UNFINISH_NETWORK_NODATA: 
					notReadyPB.setVisibility(View.GONE);
					Toast.makeText(SearchServiceActivity.this,"没有数据", Toast.LENGTH_SHORT).show();
					break;
				case FINISH_NETWORK_NODATA: 
					areadyPB.setVisibility(View.GONE);
					Toast.makeText(SearchServiceActivity.this,"没有数据", Toast.LENGTH_SHORT).show();
					break;
				case ACTION_FAILD: 							
					Toast.makeText(SearchServiceActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NEXTPAGE_UNFINISHED_NETWORK_SUCCEED:
					mergeDataUnfinished();
					setImagesUnfinished(); //取照片
					notReadyPB.setVisibility(View.GONE);	
					shopsListAdapterno.notifyDataSetChanged();
					break;
				case NEXTPAGE_UNFINISHED_NETWORK_NODATA:
					//没有数据提示	
					hastoLastUnfinished=true;						
					notReadyPB.setVisibility(View.GONE);						
					Toast.makeText(SearchServiceActivity.this,getString(R.string.nextpage_nodata),Toast.LENGTH_LONG).show();
					break;
				case NEXTPAGE_FINISHED_NETWORK_SUCCEED:
					mergeDataFinished();
					setImagesFinished(); //取照片
					areadyPB.setVisibility(View.GONE);	
					shopsListAdapteralready.notifyDataSetChanged();
					break;
				case NEXTPAGE_FINISHED_NETWORK_NODATA:
					//没有数据提示	
					hastoLastFinished=true;						
					areadyPB.setVisibility(View.GONE);						
					Toast.makeText(SearchServiceActivity.this,getString(R.string.nextpage_nodata),Toast.LENGTH_LONG).show();
					break;
				case CLOSE_PRO_FINISHED:	
					areadyPB.setVisibility(View.GONE);			
					break;
				case IMAGE_FLUSH_FINISHED:	
					shopsListAdapteralready.notifyDataSetChanged();					
					break;
				case CLOSE_PRO_UNFINISHED:	
					notReadyPB.setVisibility(View.GONE);			
					break;
				case IMAGE_FLUSH_UNFINISHED:	
					shopsListAdapterno.notifyDataSetChanged();					
					break;
				}
			}
		};

		notReadyPB = (ProgressBar) findViewById(R.id.search_service_pb1);
		areadyPB = (ProgressBar) findViewById(R.id.search_service_pb2);
		new Thread(){
			public void run(){
				
				getUnfinishedServices("NULL","unfinished",currentUserId,pageNumNo,INFO.ITEMS_PER_PAGE+"");
			}
		}.start();

	}


	private class ShopsListAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private List<TGService>  shops;
		SearchServiceActivity context;
		public ShopsListAdapter(SearchServiceActivity searchServiceActivity ){
			layoutInflater = LayoutInflater.from(searchServiceActivity);	
			this.context=searchServiceActivity;
		}
		public void setDate(List<TGService>   shops) {	
			this.shops =  shops;
		}
		public int getCount() {			
			return shops.size();
		}
		public TGService getItem(int position) {
			return shops.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.search_service_item, null);
			}				
			TextView name = ((TextView)convertView.findViewById(R.id.shoplistname));
			TextView num = ((TextView)convertView.findViewById(R.id.shoplistnum_tv));
			TextView state = ((TextView)convertView.findViewById(R.id.shopliststate_tv));
			TextView service = ((TextView)convertView.findViewById(R.id.shoplistservice_tv));
			TextView time = ((TextView)convertView.findViewById(R.id.shoplisttime));
			ImageView shoplistpicView=(ImageView) convertView.findViewById(R.id.shoplistpicView);
			shoplistpicView.setImageBitmap(getItem(position).getBtm());
		
			name.setText(shops.get(position).getShopName());
			num.setText(shops.get(position).getShopId());
			state.setText(shops.get(position).getStatus());
			service.setText(shops.get(position).getOrderType());
			time.setText(SomeUtil.longToStringDate(shops.get(position).getOrderTime()));
			return convertView;	


		}		
	}


	/**
	 * 未完成服务操作
	 * @param type
	 * @param status
	 * @param userNo
	 * @param pageNo
	 * @param pageSize
	 */
	private void getUnfinishedServices(String type,String status,String userNo,int pageNo,String pageSize) {
		unNetworking = true;
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/historyList/serviceScope/"+type+"/status/"+status+"/userNo/"+userNo+"/pageNo/"+pageNo+"/pageSize/"+pageSize;

		SearchServiceParser searchServiceParser = new SearchServiceParser();		
		NetworkState ns = Network.getNetwork(SearchServiceActivity.this).httpGetUpdateString(url,searchServiceParser);	
		unNetworking = false;
		if(ns.isNetworkSuccess()){
			if(searchServiceParser.isSuccessfull()){
				allServersNo = (ArrayList<TGService>) searchServiceParser.getStoreQueryResponse().getUnFinishedServiceList();
				unfinshedNum = searchServiceParser.getStoreQueryResponse().getUnFinishedServiceCount();
				if(allServersNo!=null){
					/*if(!(allServersNo.size()+"").equals(unfinshedNum)){
						unfinshedNum=allServersNo.size()+"";
					}*/
					if(allServersNo.size()>0){
						
						sendMessage(UNFINISH_ACTION_SUCCEED, searchServiceParser.getStoreQueryResponse().getMessage());
						setImagesUnfinished();
					}else{
						sendMessage(UNFINISH_NETWORK_NODATA,searchServiceParser.getStoreQueryResponse().getMessage());
					}
				}else{
					sendMessage(UNFINISH_NETWORK_NODATA,searchServiceParser.getStoreQueryResponse().getMessage());
				}

			}else{
				sendMessage(UNFINISH_NETWORK_FAILD, searchServiceParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(UNFINISH_NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	private  boolean nextPageUnfinishedNetworking(String type,String status,String userNo,int pageNo,String pageSize) {
		unNetworking = true;
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/historyList/serviceScope/"+type+"/status/"+status+"/userNo/"+userNo+"/pageNo/"+pageNo+"/pageSize/"+pageSize;

		SearchServiceParser searchServiceParser = new SearchServiceParser();		
		NetworkState ns = Network.getNetwork(SearchServiceActivity.this).httpGetUpdateString(url,searchServiceParser);	
		unNetworking = false;
		if(ns.isNetworkSuccess()){
			if(searchServiceParser.isSuccessfull()){
				allServersNoNext = (ArrayList<TGService>) searchServiceParser.getStoreQueryResponse().getUnFinishedServiceList();
				if(allServersNoNext==null||allServersNoNext.size()<=0){
					sendMessage(NEXTPAGE_UNFINISHED_NETWORK_NODATA, searchServiceParser.getErrorMessage());
					return false;
				}
				return true;

			}else{
				sendMessage(NEXTPAGE_UNFINISHED_NETWORK_NODATA, searchServiceParser.getErrorMessage());
				return false;
			}
		}else{
			//网络出错
			sendMessage(NEXTPAGE_UNFINISHED_NETWORK_NODATA, ns.getErrorMessage());
			return false;
		}
	}
	private void getUnfinishedNextPage() {
		pageNumNo++;
		if (!nextPageUnfinishedNetworking("NULL","unfinished",currentUserId,pageNumNo,INFO.ITEMS_PER_PAGE+"")) {
			pageNumNo--;
			nextPageUnfinishedLock = false;
		} else {
			sendMessage(NEXTPAGE_UNFINISHED_NETWORK_SUCCEED, "");
			nextPageUnfinishedLock = false;
		}
	}
	/**
	 * 合并下一页数据到现在的数据中 （需要时进行去重复）
	 */
	private void mergeDataUnfinished() {
		int begin = 0;
		for (int j = begin; j < allServersNoNext.size(); j++) {
			if(allServersNo!=null){
				allServersNo.add(allServersNoNext.get(j));
			}
		}
	}
	public void setImagesUnfinished(){
		new Thread(){
			public void run(){
				addPhoto(allServersNo,firstListItemUnfinished,12,"no");	
				sendMessage(CLOSE_PRO_UNFINISHED, null);
			}
		}.start();
	}
	/**
	 * 取完成服务操作
	 * @param type
	 * @param status
	 * @param userNo
	 * @param pageNo
	 * @param pageSize
	 */
	private void getFinishedServices(String type,String status,String userNo,int pageNo,String pageSize) {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/historyList/serviceScope/"+type+"/status/"+status+"/userNo/"+userNo+"/pageNo/"+pageNo+"/pageSize/"+pageSize;
		reNetworking = true;
		SearchServiceParser searchServiceParser = new SearchServiceParser();		
		NetworkState ns = Network.getNetwork(SearchServiceActivity.this).httpGetUpdateString(url,searchServiceParser);	
		reNetworking = false;
		if(ns.isNetworkSuccess()){
			if(searchServiceParser.isSuccessfull()){
				allServersReady = (ArrayList<TGService>) searchServiceParser.getStoreQueryResponse().getFinishedServiceList();
				finshedNum = searchServiceParser.getStoreQueryResponse().getFinishedServiceCount();
				if(allServersReady!=null){
					/*if(!(allServersReady.size()+"").equals(finshedNum)){
						finshedNum=allServersReady.size()+"";
					}*/
					
					if(allServersReady.size()>0){
						sendMessage(FINISH_ACTION_SUCCEED, searchServiceParser.getStoreQueryResponse().getMessage());
						setImagesFinished();
					}else{
						sendMessage(FINISH_NETWORK_NODATA,searchServiceParser.getStoreQueryResponse().getMessage());
					}
				}else{
					sendMessage(FINISH_NETWORK_NODATA,searchServiceParser.getStoreQueryResponse().getMessage());
				}

			}else{
				sendMessage(FINISH_NETWORK_FAILD, searchServiceParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(FINISH_NETWORK_FAILD, ns.getErrorMessage());
		}
	}

	private  boolean nextPageFinishedNetworking(String type,String status,String userNo,int pageNo,String pageSize) {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/historyList/serviceScope/"+type+"/status/"+status+"/userNo/"+userNo+"/pageNo/"+pageNo+"/pageSize/"+pageSize;
		reNetworking = true;
		SearchServiceParser searchServiceParser = new SearchServiceParser();		
		NetworkState ns = Network.getNetwork(SearchServiceActivity.this).httpGetUpdateString(url,searchServiceParser);	
		reNetworking = false;
		if(ns.isNetworkSuccess()){
			if(searchServiceParser.isSuccessfull()){
				allServersReadyNext = (ArrayList<TGService>) searchServiceParser.getStoreQueryResponse().getFinishedServiceList();
				if(allServersReadyNext==null||allServersReadyNext.size()<=0){
					sendMessage(NEXTPAGE_FINISHED_NETWORK_NODATA, searchServiceParser.getErrorMessage());
					return false;
				}
				return true;

			}else{
				sendMessage(NEXTPAGE_FINISHED_NETWORK_NODATA, searchServiceParser.getErrorMessage());
				return false;
			}
		}else{
			//网络出错
			sendMessage(NEXTPAGE_FINISHED_NETWORK_NODATA, ns.getErrorMessage());
			return false;
		}
	}
	private void getFinishedNextPage() {
		pageNumReady++;
		if (!nextPageFinishedNetworking("NULL","finished",currentUserId,pageNumReady,INFO.ITEMS_PER_PAGE+"")) {
			pageNumReady--;
			nextPageFinishedLock = false;
		} else {
			sendMessage(NEXTPAGE_FINISHED_NETWORK_SUCCEED, "");
			nextPageFinishedLock = false;
		}
	}
	/**
	 * 合并下一页数据到现在的数据中 （需要时进行去重复）
	 */
	private void mergeDataFinished() {
		int begin = 0;
		for (int j = begin; j < allServersReadyNext.size(); j++) {
			if(allServersReady!=null){
				allServersReady.add(allServersReadyNext.get(j));
			}
		}
	}
	public void setImagesFinished(){
		new Thread(){
			public void run(){
				addPhoto(allServersReady,firstListItemFinished,12,"already");	
				sendMessage(CLOSE_PRO_FINISHED, null);
			}
		}.start();
	}
	private void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}public void addPhoto(ArrayList<TGService> scanBooks, int first, int count,String from) {
		int begin = first /*- 3*/;
		if (begin < 0) {
			begin = 0;
		}
		int all = scanBooks.size();
		int end = first + count /*+ 12*/;

		if (end > all) {
			end = all;
		}
		TGService concise = null;
		String imageUrl= null;
		for (int i = begin; i < end; i++) {
			if(i<scanBooks.size()){
				concise = scanBooks.get(i);
						
				imageUrl = concise.getShopImageUrl();
				
				if ( concise.getBtm() == null &&imageUrl!= null) {
					concise.setBtm(getPicture(imageUrl));
				}
				if("no".equals(from)){
					sendMessage(IMAGE_FLUSH_UNFINISHED,null);
				}if("already".equals(from)){
					sendMessage(IMAGE_FLUSH_FINISHED,null);
				}
			}
		}
	}public void removePhoto(ArrayList<TGService> scanBooks, int first, int count) {
		/*
		 * 关于后面的先置空后回收的原因，考虑这种情形：
		 *
		 * 第一次滚动停止：T1开始加载6-10，T2开始回收0-5和11-15
		 *
		 * 第二次滚动停止：T3开始加载11-15（但此时可能T2还被被调度，T3在它前面调度了）
		 * T3开始加载，然后T2开始回收。在T2刚好回收图片还没置空前，控制权被切换给T3，此时
		 * T3判断到图片还不是空的，则将其给ListView显示。此时就会引起异常，即显示已回收的
		 * 图片。关键就是保证回收和置空是原子的
		 */
		int all = scanBooks.size();
		int removeup = first - 3;
		int end = first + count + 3;
		TGService concise = null;
		if (removeup > 0) {// 清除上面
			for (int i = 0; i < removeup; i++) {
				if(i<scanBooks.size()){					
					concise = scanBooks.get(i);
					
					if ((concise.getBtm()) != null) {
						/** 要先置空，在回收 */
						concise.setBtm(null);
					}
				}
			}
		}
		if (all > end) {// 清除下面
			for (int j = end; j < all; j++) {
				if(j<scanBooks.size()){					
					
					concise = scanBooks.get(j);
					if ((concise.getBtm()) != null) {
						/** 要先置空，在回收 */
						concise.setBtm(null);
					}
				}
			}
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==6060){		
			//请求网络
			if("yes".equals(data.getStringExtra("tonggou.isOk"))){		
				if(allServersNo!=null){
					allServersNo.clear();
				}
				notReadyPB.setVisibility(View.VISIBLE);
				new Thread(){
					public void run(){
						getUnfinishedServices("NULL","unfinished",currentUserId,pageNumNo,INFO.ITEMS_PER_PAGE+"");
					}
				}.start();
			}
		}
	}
}
