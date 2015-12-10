package com.tonggou.andclient;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.MyBluetoothService;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.parse.VehicleListParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.VehicleInfo;


public class BaseConnectOBDActivity extends BaseActivity{
	private static final int REQUEST_ENABLE_BT = 2;
	private AlertDialog searchingAlert,wrongAlert,waitingAlert;
	private AlertDialog devicesListAlert;
	private AlertDialog noDevicesAlert;
	private ArrayList<BluetoothDevice>  pairedDevices;  //已配对的设备
	private ArrayList<BluetoothDevice>  searchDevices;  //搜索的设备	
	private ArrayList<Object>  pairAndSearchDevices;	
	private DevicesListAdapter devicesListAdapter;
	private ListView devicesList;
	private LayoutInflater layoutInflater;	
	private BluetoothAdapter mBtAdapter;

	// Message types sent from the BluetoothChatService Handler
    // Key names received from the MyBluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    ///////////////////////////////////////////////////
    
    private MyBluetoothService myBTService = null;
    private boolean connectingLock = false; //正在连接蓝牙操作时锁住
    private ProgressBar  connectingProgressBar;
    
  
    private String fromUI;
    private String selectObdSN;
    private String shopIDStr ; 
    
    private String readFromOBDvehicleVin ;
    private boolean showingDevise = false;
    private boolean showingNoDevise = false;
    protected boolean cancelShowingDevise = false;         //是否取消了显示
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cancelShowingDevise = false;
		layoutInflater = LayoutInflater.from(this);					
		myBTService = new MyBluetoothService(this, mHandler);		
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);                                // Register for broadcasts when a device is discovered
    
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);                                // Register for broadcasts when discovery has finished
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();                       // Get the local Bluetooth adapter		
        
	  }
	
	  /**
	   * 开始启动扫描流程
	   * @param alertUser    是否要提示用户，还是后台自动连接操作
	   */
	  protected void startConnect(String formUI,String erweima){
		  this.fromUI = formUI;
		  this.shopIDStr = erweima;
		  
		  if(mBtAdapter!=null){
	        	// If BT is not on, request that it be enabled.
		        if (!mBtAdapter.isEnabled()) {
		            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);                         //打开蓝牙
		        }else{
		        	try{
						doSomeFindingJob();
					}catch(InterruptedException e){
						e.printStackTrace();
					}
		        } 	        
	        }else{	        	
	        	showOpenBTWrongAlert();	        	
	        } 
	  }
	
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch (requestCode) {
	        case REQUEST_ENABLE_BT:
	            // 当开打蓝牙返回        When the request to enable Bluetooth returns 
	            if (resultCode == Activity.RESULT_OK) {
	            	try {
						doSomeFindingJob();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            } else {
	            	showOpenBTWrongAlert();
	            }
	        }
	    }
	  
	  
	  public void onDestroy() {
	        super.onDestroy();
	        if (myBTService != null){
	        	myBTService.stop();
        	}	        
	        stopTimeoutTimer();
	        unregisterReceiver(mReceiver); 
	  }
	
	/**
	 * 开始查找工作
	 */
	private void doSomeFindingJob()throws InterruptedException{
		pairedDevices = new ArrayList<BluetoothDevice>();
		pairedDevices.clear();
		searchDevices = new ArrayList<BluetoothDevice>();
		searchDevices.clear();
		pairAndSearchDevices = new ArrayList<Object>();
		pairAndSearchDevices.clear();

		showFindingAlert();	 
		
    	///////////////////////////////
    	Set<BluetoothDevice> pairDevices = mBtAdapter.getBondedDevices();      // Get a set of currently paired devices
        // If there are paired devices, add each one to the ArrayAdapter
        if(pairDevices!=null&&pairDevices.size() > 0){        		
    		for(BluetoothDevice device : pairDevices){
    			pairedDevices.add(device);        //拿到已经配对过的设备       
            }
    		doDiscovery();       	
        }else{
        	doDiscovery();
        }
        //////////////////////////////////
    	
	}
	
	
	/**
	 * 
	 * @param devices
	 * @param defaultObdSN
	 * @return 成功 或失败
	 * @throws InterruptedException
	 */

	
	/**
	 * 从obd中读取汽车vin
	 * @return vin
	 * @throws InterruptedException 
	 */
	private String readVinFromOBD(){
		//myBTService.write("##DAT\r\n".getBytes());
		//byte[] cmd = new byte[] {0x23,0x23,0x56,0x49,0x4E, 0x0A,0x0D };
		//myBTService.write(cmd);
		myBTService.write("##VIN\r\n".getBytes());
		return readFromOBDvehicleVin;
	}
	

	  
	private void showFindingAlert(){
		searchingAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();	
		searchingAlert.show();	
		searchingAlert.setCanceledOnTouchOutside(false);
		Window window = searchingAlert.getWindow();
		window.setContentView(R.layout.searching_obd);		
		searchingAlert.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface arg0) {
			}			
		});
	}
	
	
	private void showWaitingAlert(String alerts){
		waitingAlert = new AlertDialog.Builder(BaseConnectOBDActivity.this).create();	
		waitingAlert.show();	
		waitingAlert.setCanceledOnTouchOutside(false);
		Window window = waitingAlert.getWindow();
		window.setContentView(R.layout.logining);
		TextView waiting_message =(TextView) window.findViewById(R.id.loging_alerttext);
		waiting_message.setText(alerts);		
		waitingAlert.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface arg0) {
				stopReadOBDVinTimer();
			}			
		});
	}
	
	private void closeWaitingAlert(){
		if(waitingAlert!=null){
			waitingAlert.cancel();
			waitingAlert.dismiss();
		}
	}
	
	/**
	 * 打开手机蓝牙失败提示
	 */
	private void showOpenBTWrongAlert(){
		wrongAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.searching_obd_wrong);	
		TextView contentAlert = (TextView)window.findViewById(R.id.content_text_alert);
		contentAlert.setText(R.string.open_bt_wrong);
		wrongAlert.setCancelable(false);
		wrongAlert.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		View retryOper = window.findViewById(R.id.connect_again);  //重试
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				if(mBtAdapter!=null){
		        	// If BT is not on, request that it be enabled.
			        if (!mBtAdapter.isEnabled()) {
			            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);                         //打开蓝牙
			        }else{
			        	try {
							doSomeFindingJob();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
			        } 	        
		        }else{
		        	showOpenBTWrongAlert();
		        }
			}
		});
	
		View cancelOper = window.findViewById(R.id.connect_cancel);  //跳过
		cancelOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
	
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 没找到蓝牙设备提示
	 */
	private void showNotFoundDevicesAlert(){
		showingNoDevise = true;
		noDevicesAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();		
		noDevicesAlert.show();	
		Window window = noDevicesAlert.getWindow();
		window.setContentView(R.layout.searching_obd_wrong);		
		noDevicesAlert.setCancelable(false);
		noDevicesAlert.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				showingNoDevise = false;
			}			
		});
		
		View retryOper = window.findViewById(R.id.connect_again);  //重试
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				noDevicesAlert.cancel();
				noDevicesAlert.dismiss();	
				
				try {
					doSomeFindingJob();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	
		View cancelOper = window.findViewById(R.id.connect_cancel);  //跳过
		cancelOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				noDevicesAlert.cancel();
				noDevicesAlert.dismiss();			
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
				startActivity(intent);
				
				BaseConnectOBDActivity.this.finish();
			}
		});
	}
	
	
	/**
	 * 显示搜索到的蓝牙设备
	 */
	private void showDevicesList(){
		if(cancelShowingDevise){
			return;
		}
		int pairedDeviceSize = 0;
		boolean havePaired = false;
		boolean haveSearch = false;
		int pairedTitlePosition = 0;
		
	    /////处理搜索的
		if(searchDevices!=null&&searchDevices.size()>0){
			haveSearch = true;
			pairedTitlePosition = searchDevices.size()+1;
			pairAndSearchDevices.add("B");
			for(int i=0;i<searchDevices.size();i++){
				pairAndSearchDevices.add(searchDevices.get(i));
			}	
			
			
		}
		
		if(pairedDevices!=null&&pairedDevices.size()>0){
			pairedDeviceSize = pairedDevices.size();
		}
		if(pairedDeviceSize>0){
			havePaired = true;
			pairAndSearchDevices.add("A");
			for(int i=0;i<pairedDeviceSize;i++){
				pairAndSearchDevices.add(pairedDevices.get(i));
			}	
		}
		
		showingDevise = true;
		final View modifyPassView = layoutInflater.inflate(R.layout.searching_obd_list, null);
		devicesListAdapter = new DevicesListAdapter(havePaired,pairedTitlePosition,haveSearch,0);
		devicesList = (ListView) modifyPassView.findViewById(R.id.obd_devices);
		View refreSearching = modifyPassView.findViewById(R.id.obd_devices_research);
		refreSearching.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				devicesListAlert.cancel();
				devicesListAlert.dismiss();			
				try {
					doSomeFindingJob();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		devicesList.setAdapter(devicesListAdapter);
		
		devicesListAlert = new AlertDialog.Builder(BaseConnectOBDActivity.this).create();	
		devicesListAlert.show();	
		devicesListAlert.setCanceledOnTouchOutside(false);
		devicesListAlert.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface arg0) {
				showingDevise = false;
			}			
		});
		Window window = devicesListAlert.getWindow();
		window.setContentView(modifyPassView);		
		
		devicesListAlert.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface arg0) {
				showingDevise = false;
			}			
		});
	}
	
	
	
	//处理从MyBluetoothService返回的消息 
	@SuppressLint("HandlerLeak")
	protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MyBluetoothService.MESSAGE_STATE_CHANGE:
                 //Log.i("Bluetooth thinks", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case MyBluetoothService.STATE_CONNECTED:
                    //处于已经连接的状态
                    break;
                case MyBluetoothService.STATE_CONNECTING:
                	//处于正在连接的状态
                    break;
                case MyBluetoothService.STATE_LISTEN:
                case MyBluetoothService.STATE_NONE:
                    //初始状态
                    break;
                }
                break;
            case MyBluetoothService.MESSAGE_WRITE:
            	//发送。。。。
                //byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                //String writeMessage = new String(writeBuf);
                //Log.i("Bluetooth thinks", "send:  " + writeMessage);        
                break;
            case MyBluetoothService.MESSAGE_READ:
            	//收到。。。。。
                String readMessage = (String) msg.obj;
                //Log.i("Bluetooth thinks", "receive:  " + readMessage);
                //readMessage = "##VIN:";
                if(readMessage!=null&&!showingDevise){
                	if(readMessage.indexOf("VIN:")!=-1){
                		String vehicleVin = readMessage.substring(readMessage.indexOf("VIN:")+4);
                		if( vehicleVin.indexOf("\r\n")!=-1){
                			vehicleVin = vehicleVin.substring(0, vehicleVin.indexOf("\r\n"));
                		}
                		stopReadOBDVinTimer();                      //停掉计时器
    	        		final String fvehicleVin = vehicleVin.trim();   	        		
    	        		if(fromUI!=null&&"addcar".equals(fromUI)){  //新增车辆进来
    	        			//unregisterReceiver(mReceiver);                             
    	        			Intent dataIntent = new Intent();
    						dataIntent.putExtra("tonggou.get.vin",fvehicleVin);
    						dataIntent.putExtra("tonggou.get.obdsn",selectObdSN);
    						setResult(3031, dataIntent);
    						BaseConnectOBDActivity.this.finish();
    	        		}else if(fromUI!=null&&"changecar".equals(fromUI)){  //修改车辆进来
    	        			//unregisterReceiver(mReceiver);                             
    	        			Intent dataIntent = new Intent();
    						dataIntent.putExtra("tonggou.get.vin",fvehicleVin);
    						dataIntent.putExtra("tonggou.get.obdsn",selectObdSN);
    						setResult(3041, dataIntent);
    						BaseConnectOBDActivity.this.finish();
    	        		}else if(fromUI!=null&&"register".equals(fromUI)){//注册时进来
    	        			closeWaitingAlert();
    	        			showWaitingAlert("绑定OBD...");
    	        			new Thread(){
								public void run(){
									findVehicleId(fvehicleVin);
								}
							}.start();	
    	        		}
                	}
                }
                break;

            case MyBluetoothService.MESSAGE_DEVICE_NAME:
            	//提醒已经和设备完成连接
            	//Log.d("testthread", "cccccccccccccccccc1");
            	TongGouApplication.connetedOBD = true;
            	
                String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "成功连接上"+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                connectingProgressBar.setVisibility(View.INVISIBLE);
                //关掉列表、并提示读取设备vin
                if(devicesListAlert!=null){
                	devicesListAlert.cancel();
    				devicesListAlert.dismiss();
    				showingDevise = false;
                }
                showWaitingAlert("读取车辆设备信息...");
                startReadOBDVinTimer();          //启动计时器
                readVinFromOBD();
            
                connectingLock = false;
                break;
            case MyBluetoothService.MESSAGE_CONNECT_FAILT:  //连接失败  
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_LONG).show();
                connectingProgressBar.setVisibility(View.INVISIBLE);           	
                connectingLock = false;
                break;
            case MyBluetoothService.MESSAGE_CONNECT_LOST:  //连接断掉
            	//Log.d("testthread", "CONNECT_LOST");
            	TongGouApplication.connetedOBD = false;
                break;
            case MyBluetoothService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
	
	
	
	
	
	 public class DevicesListAdapter extends BaseAdapter{
		 	private boolean cHavePaireDevices = false;
		    private int cPairedTitlePosition;
			private boolean cHaveSearchDevices = false;
		    private int cSearchTitlePosition;
		    public DevicesListAdapter(boolean havePaireDevices,int pairedTitlePosition,boolean haveSearchDevices,int searchTitlePosition){
		    	cHavePaireDevices = havePaireDevices;
		    	cPairedTitlePosition = pairedTitlePosition;
		    	cHaveSearchDevices = haveSearchDevices;
		    	cSearchTitlePosition = searchTitlePosition;
		    }
		   
			public int getCount() {			
				return pairAndSearchDevices.size();
			}
			public Object getItem(int position) {
				return pairAndSearchDevices.get(position);
			}
			public long getItemId(int position) {		
				return position;
			}
			public View getView(int position, View convertView2, ViewGroup parent) {	
				View convertView = null;
				if(cHavePaireDevices){
					if(position==cPairedTitlePosition){
						convertView = layoutInflater.inflate(R.layout.device_list_item_subtitle, null);
						convertView.setOnClickListener(null);
						convertView.setOnLongClickListener(null);
						convertView.setLongClickable(false);
						TextView titlTxtView = (TextView)convertView.findViewById(R.id.city_subtitle_item);						
						titlTxtView.setText("已配对设备");
						
					}else{
					   if(this.getItem(position) instanceof BluetoothDevice){
						   convertView = layoutInflater.inflate(R.layout.searching_obd_list_item, null);				   	
						   final BluetoothDevice selectDevice = (BluetoothDevice)this.getItem(position);				   
						   ((TextView)convertView.findViewById(R.id.devices_name)).setText(selectDevice.getName());					 
						   final View cvv =  convertView;
					       View bgview = convertView.findViewById(R.id.query_item);	
					       bgview.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									if(connectingLock){
										return;
									}
									connectingProgressBar = (ProgressBar)cvv.findViewById(R.id.connecting_progressBar);
									connectingProgressBar.setVisibility(View.VISIBLE);
									connectingLock = true;
									selectObdSN = selectDevice.getAddress();
									myBTService.connecting(selectDevice);              //连接蓝牙设备
								}
							});
					    }
					}
				}
				
				if(cHaveSearchDevices){
					if(position==cSearchTitlePosition){						
						convertView = layoutInflater.inflate(R.layout.device_list_item_subtitle, null);
						convertView.setOnClickListener(null);
						convertView.setOnLongClickListener(null);
						convertView.setLongClickable(false);
						TextView titlTxtView = (TextView)convertView.findViewById(R.id.city_subtitle_item);						
						titlTxtView.setText("搜索到设备");
						
					}else{
						if(this.getItem(position) instanceof BluetoothDevice){
						   convertView = layoutInflater.inflate(R.layout.searching_obd_list_item, null);				   	
						   final BluetoothDevice selectDevice = (BluetoothDevice)this.getItem(position);				   
						   ((TextView)convertView.findViewById(R.id.devices_name)).setText(selectDevice.getName());					 
						   final View cvv =  convertView;
					       View bgview = convertView.findViewById(R.id.query_item);	
					       bgview.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									if(connectingLock){
										return;
									}
									connectingProgressBar = (ProgressBar)cvv.findViewById(R.id.connecting_progressBar);
									connectingProgressBar.setVisibility(View.VISIBLE);
									connectingLock = true;
									selectObdSN = selectDevice.getAddress();
									myBTService.connecting(selectDevice);              //连接蓝牙设备
								}
							});
						}
					}
				}
					 
				return convertView;			
			}		
	   }
	 
	 /*
	  * 搜索蓝牙设备动作
	  */
	 private void doDiscovery(){
         // If we're already discovering, stop it
         if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
         }
         
         startTimeoutTimer(); //计时
         mBtAdapter.startDiscovery();
	 }
	 
    // The BroadcastReceiver that listens for discovered devices 
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	if(searchDevices!=null){
                		addDevice(device);
                	}
                	//Log.d("bbtt", "device2:"+device.getName()+":"+device.getAddress());
                }
                
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {	
        			//Log.d("bbtt", "cccccccccccccccccccccccccclose");
        			stopTimeoutTimer(); //计时关闭
        			if(scanDeviceTimeout){
        				return;
        			}
        			discoverFinished();
            }
        }
    };
	    
	    
	    private void discoverFinished(){
	    	if(searchingAlert!=null){//关掉装载圈
            	searchingAlert.cancel();
        		searchingAlert.dismiss();
        	}
        	if((pairedDevices!=null&&pairedDevices.size()>0) || (searchDevices!=null&&searchDevices.size()>0)){
        		if(!showingDevise){
        			showDevicesList();  //显示找到的蓝牙设备
        		}
        	}else{	
        		if(!showingNoDevise){
        			showNotFoundDevicesAlert();
        		}	
        	}
	    }

	    //////////////////
	    /**
		 * scan timeout
		 */
		private ScanTimeOutTask scanTimeoutTask;   					 
		private Timer scanTimeoutTimer;
		private boolean scanDeviceTimeout = false;
		private void startTimeoutTimer(){
			scanDeviceTimeout = false;
			stopTimeoutTimer();
			try{
			scanTimeoutTask = new ScanTimeOutTask();
			scanTimeoutTimer = new Timer();
			scanTimeoutTimer.schedule(scanTimeoutTask,60000);
			}catch(Exception ex){}
		}

		private void stopTimeoutTimer(){
			if(scanTimeoutTask != null){
				scanTimeoutTask.cancel();
			}
			scanTimeoutTask = null;
			if(scanTimeoutTimer != null){
				scanTimeoutTimer.cancel();
			}
			scanTimeoutTimer = null;
		}

		 private class ScanTimeOutTask extends TimerTask{
	   	 	public void run(){  	 	
		   	 	 //Log.e("testthread", "timeout.............................");
		   	 	 scanDeviceTimeout = true;
			   	 if (mBtAdapter.isDiscovering()) {
			            mBtAdapter.cancelDiscovery();
		         }
			   	 BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
					  public void run() {
						discoverFinished();
					  }
			     });
	   	 	}
	    }
	    //////////////////////////////////////////////////////////////////////////////////////////////

		 
		 /**
		 * 读取obdVIN 计时器
		 */
		private ReadOBDVinTask readOBDVinTask;   					 
		private Timer readOBDVinTimer;
		private void startReadOBDVinTimer(){
			stopReadOBDVinTimer();
			try{
			readOBDVinTask = new ReadOBDVinTask();
			readOBDVinTimer = new Timer();
			readOBDVinTimer.schedule(readOBDVinTask,30000); //30秒
			}catch(Exception ex){}
		}

		private void stopReadOBDVinTimer(){
			if(readOBDVinTask != null){
				readOBDVinTask.cancel();
			}
			readOBDVinTask = null;
			if(readOBDVinTimer != null){
				readOBDVinTimer.cancel();
			}
			readOBDVinTimer = null;
		}

		private class ReadOBDVinTask extends TimerTask{
	   	 	public void run(){  	 	
	   	 		Log.d("testthread", "stop read obd vin ,stop connect obd......读vin超时");
	   	 		 //提示列表重新连
		   	 	 BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
					  public void run() {
							closeWaitingAlert();
					  }
			     });
	   	 	}
	    }
	    //////////////////////////////////////////////////////////////////////////////////////////////
	    
	
		 
		 
	    private void addDevice(BluetoothDevice devs){
	    	boolean finded = false;
	    	for(int i=0;i<searchDevices.size();i++){
	    		if(searchDevices.get(i).getAddress().equals(devs.getAddress())){
	    			finded = true;
	    		}
	    	}
	    	if(!finded&&(!"".equals(devs.getAddress())&&(!"".equals(devs.getName())))&&devs.getAddress()!=null&&devs.getName()!=null){
	    		searchDevices.add(devs);
	    	}
	    }
	    
	    private void bindObd(String vehicID,String vehicleVin,String vehicleNo,String vehicleModel,
				              String vehicleModelId,String vehicleBrand,String vehicleBrandId,
				              String obdSN,String userNo,String nextMaintainMileage){
			long nextInsuranceTimeLong = SomeUtil.StringDateToLong(RegisterActivityNext.registernexttimeStr);  //保险时间
			long nextExamineTimeLong = SomeUtil.StringDateToLong(RegisterActivityNext.registernexttime2Str);    //验车时间
	
			String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/obd/binding";
			CommonParser commonParser = new CommonParser();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userNo",userNo));	
			nameValuePairs.add(new BasicNameValuePair("obdSN",obdSN));
			nameValuePairs.add(new BasicNameValuePair("vehicleVin",vehicleVin));
			nameValuePairs.add(new BasicNameValuePair("vehicleNo",vehicleNo));
			
			nameValuePairs.add(new BasicNameValuePair("vehicleModelId",vehicleModelId));
			nameValuePairs.add(new BasicNameValuePair("vehicleModel",vehicleModel));
			
			nameValuePairs.add(new BasicNameValuePair("vehicleBrandId",vehicleBrandId));
			nameValuePairs.add(new BasicNameValuePair("vehicleBrand",vehicleBrand));
			//可选
			
			nameValuePairs.add(new BasicNameValuePair("vehicleId",vehicID));
			
			if(shopIDStr!=null&&!"".equals(shopIDStr)){
				nameValuePairs.add(new BasicNameValuePair("sellShopId",shopIDStr));
			}
			
			if(RegisterActivityNext.currentMileStr!=null&&!"".equals(RegisterActivityNext.currentMileStr)){
				nameValuePairs.add(new BasicNameValuePair("currentMileage",RegisterActivityNext.currentMileStr));
			}
			
			if(nextMaintainMileage!=null&&!"".equals(nextMaintainMileage)){			
				nameValuePairs.add(new BasicNameValuePair("nextMaintainMileage",nextMaintainMileage));
			}
		
			if(nextInsuranceTimeLong!=0){
				nameValuePairs.add(new BasicNameValuePair("nextInsuranceTime",nextInsuranceTimeLong+""));
			}
			if(nextExamineTimeLong!=0){
				nameValuePairs.add(new BasicNameValuePair("nextExamineTime",nextExamineTimeLong+""));
			}
		
			final String vehiVinStr = vehicleVin;
			final String vehiIDStr = vehicID;
			final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url,nameValuePairs,commonParser);	
			if(ns.isNetworkSuccess()){
				if(commonParser.isSuccessfull()){
					//保存连接上的车辆id
					sharedPreferences.edit().putString(BaseActivity.VEHICLE_MODE_ID, vehicleModelId).commit();  //保存车型
					
					BaseConnectOBDService.cmile = RegisterActivityNext.currentMileStr;
					
					if (myBTService != null){
			        	myBTService.stop();
		        	}
					
					if(sAllActivities!=null){
	 					try{
	     					for (Activity activity : sAllActivities) {
	     						activity.finish();
	     					}
	     					sAllActivities.clear();
	 					}catch(ConcurrentModificationException ex){	
	 					}
	 				}
					
					Intent intent = new Intent();
	 				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	 				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
	 				startActivity(intent);	
	 				MainActivity.ifAutoLogin = true;
	 				BaseConnectOBDActivity.this.finish();

				}else{
					//提示用户错误
					final String errorAlert = commonParser.getErrorMessage();
					BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
						  public void run() {
							  closeWaitingAlert();
			        			
							    wrongAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();		
								wrongAlert.show();	
								Window window = wrongAlert.getWindow();
								window.setContentView(R.layout.searching_obd_wrong);	
								TextView contentAlert = (TextView)window.findViewById(R.id.content_text_alert);
								contentAlert.setText("绑定OBD错误："+errorAlert);
								wrongAlert.setCancelable(false);
								wrongAlert.setOnCancelListener(new OnCancelListener(){
									@Override
									public void onCancel(DialogInterface arg0) {
										// TODO Auto-generated method stub
										
									}			
								});
								
								View retryOper = window.findViewById(R.id.connect_again);  //重试
								retryOper.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										wrongAlert.cancel();
										wrongAlert.dismiss();
										showWaitingAlert("绑定OBD...");
										new Thread(){
											public void run(){
												bindObd(vehiIDStr,vehiVinStr,RegisterActivityNext.registercarnameStr,RegisterActivityNext.carMold,
														RegisterActivityNext.carMoldId,RegisterActivityNext.carBrand,RegisterActivityNext.carBrandId,
														selectObdSN,sharedPreferences.getString(BaseActivity.NAME, ""),RegisterActivityNext.registernextmileStr);
												
											}
										}.start();	
									}
								});
							
								View cancelOper = window.findViewById(R.id.connect_cancel);  //跳过
								cancelOper.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										wrongAlert.cancel();
										wrongAlert.dismiss();			
										Intent intent = new Intent();
					     				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					     				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
					     				startActivity(intent);				
					     				BaseConnectOBDActivity.this.finish();
									}
								});
							  //Toast.makeText(BaseConnectOBDActivity.this,"绑定车辆："+errorAlert,Toast.LENGTH_LONG).show();
						  }
					});
					
				}
			}else{
				//网络出错
				BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
					  public void run() {
						    closeWaitingAlert();
	        			
						    wrongAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();		
							wrongAlert.show();	
							Window window = wrongAlert.getWindow();
							window.setContentView(R.layout.searching_obd_wrong);	
							TextView contentAlert = (TextView)window.findViewById(R.id.content_text_alert);
							contentAlert.setText("绑定OBD错误："+ns.getErrorMessage());
							wrongAlert.setCancelable(false);
							wrongAlert.setOnCancelListener(new OnCancelListener(){
								@Override
								public void onCancel(DialogInterface arg0) {
									// TODO Auto-generated method stub
									
								}			
							});
							
							View retryOper = window.findViewById(R.id.connect_again);  //重试
							retryOper.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									wrongAlert.cancel();
									wrongAlert.dismiss();
									showWaitingAlert("绑定OBD...");
									new Thread(){
										public void run(){
											bindObd(vehiIDStr,vehiVinStr,RegisterActivityNext.registercarnameStr,RegisterActivityNext.carMold,
													RegisterActivityNext.carMoldId,RegisterActivityNext.carBrand,RegisterActivityNext.carBrandId,
													selectObdSN,sharedPreferences.getString(BaseActivity.NAME, ""),RegisterActivityNext.registernextmileStr);
											
										}
									}.start();	
								}
							});
						
							View cancelOper = window.findViewById(R.id.connect_cancel);  //跳过
							cancelOper.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									wrongAlert.cancel();
									wrongAlert.dismiss();			
									Intent intent = new Intent();
				     				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				     				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
				     				startActivity(intent);				
				     				BaseConnectOBDActivity.this.finish();
								}
							});
					  }
				});
				
			}
		}
	    
	    
	  
	    
		private  void findVehicleId(final String vehVin){
			String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/list/userNo/"+currentUserId;
			VehicleListParser vehicleListParser = new VehicleListParser();		
			NetworkState ns = Network.getNetwork(BaseConnectOBDActivity.this).httpGetUpdateString(url,vehicleListParser);	

			if(ns.isNetworkSuccess()){
				if(vehicleListParser.isSuccessfull()){
					 List<VehicleInfo> vehicleList=vehicleListParser.getVehicleListResponse().getVehicleList();
					if(vehicleList!=null&&vehicleList.size()>0){
						
						String vehicleID = vehicleList.get(0).getVehicleId();
						  if(vehicleID!=null&&!"".equals(vehicleID)){
							  bindObd(vehicleID,vehVin,RegisterActivityNext.registercarnameStr,RegisterActivityNext.carMold,
										RegisterActivityNext.carMoldId,RegisterActivityNext.carBrand,RegisterActivityNext.carBrandId,
										selectObdSN,sharedPreferences.getString(BaseActivity.NAME, ""),RegisterActivityNext.registernextmileStr);
						  }else{
							  BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
								  public void run() {
									  Toast.makeText(BaseConnectOBDActivity.this,"无车辆ID",Toast.LENGTH_LONG).show();
								  }
							  });
						  }
						
						
					}

				}else{
					//解析出错
					final String mess = vehicleListParser.getErrorMessage();
					BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
						  public void run() {
							  closeWaitingAlert();
			        			
							    wrongAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();		
								wrongAlert.show();	
								Window window = wrongAlert.getWindow();
								window.setContentView(R.layout.searching_obd_wrong);	
								TextView contentAlert = (TextView)window.findViewById(R.id.content_text_alert);
								contentAlert.setText("绑定OBD错误："+mess);
								wrongAlert.setCancelable(false);
								
								
								View retryOper = window.findViewById(R.id.connect_again);  //重试
								retryOper.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										wrongAlert.cancel();
										wrongAlert.dismiss();
										showWaitingAlert("绑定OBD...");
										new Thread(){
											public void run(){
												findVehicleId(vehVin);
												
											}
										}.start();	
									}
								});
							
								View cancelOper = window.findViewById(R.id.connect_cancel);  //跳过
								cancelOper.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										wrongAlert.cancel();
										wrongAlert.dismiss();			
										Intent intent = new Intent();
					     				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					     				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
					     				startActivity(intent);				
					     				BaseConnectOBDActivity.this.finish();
									}
								});
							  //Toast.makeText(BaseConnectOBDActivity.this,"取车辆列表:"+mess,Toast.LENGTH_LONG).show();
						  }
					  });
				}
			}else{
				//网络出错
				final String err = ns.getErrorMessage();
				BaseConnectOBDActivity.this.runOnUiThread(new Runnable(){
					  public void run() {
						  closeWaitingAlert();
		        			
						    wrongAlert= new AlertDialog.Builder(BaseConnectOBDActivity.this).create();		
							wrongAlert.show();	
							Window window = wrongAlert.getWindow();
							window.setContentView(R.layout.searching_obd_wrong);	
							TextView contentAlert = (TextView)window.findViewById(R.id.content_text_alert);
							contentAlert.setText("绑定OBD错误："+err);
							wrongAlert.setCancelable(false);
							
							
							View retryOper = window.findViewById(R.id.connect_again);  //重试
							retryOper.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									wrongAlert.cancel();
									wrongAlert.dismiss();
									showWaitingAlert("绑定OBD...");
									new Thread(){
										public void run(){
											findVehicleId(vehVin);
											
										}
									}.start();	
								}
							});
						
							View cancelOper = window.findViewById(R.id.connect_cancel);  //跳过
							cancelOper.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									wrongAlert.cancel();
									wrongAlert.dismiss();			
									Intent intent = new Intent();
				     				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				     				intent.setClass(BaseConnectOBDActivity.this, HomePageActivity.class);
				     				startActivity(intent);				
				     				BaseConnectOBDActivity.this.finish();
								}
							});
					  }
				});
			}
		}
		

	    
}
