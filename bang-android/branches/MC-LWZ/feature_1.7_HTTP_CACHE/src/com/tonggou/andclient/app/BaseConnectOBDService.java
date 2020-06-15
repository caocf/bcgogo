package com.tonggou.andclient.app;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tonggou.andclient.BaseActivity;
import com.tonggou.andclient.CarConditionQueryActivity;
import com.tonggou.andclient.CarErrorActivity;
import com.tonggou.andclient.DrivingJournalService;
import com.tonggou.andclient.LoginActivity;
import com.tonggou.andclient.OilErrorActivity;
import com.tonggou.andclient.R;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.network.MyBluetoothService;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.HttpRequestClient;
import com.tonggou.andclient.network.request.SendVehicleConditionRequest;
import com.tonggou.andclient.network.request.SendVehicleFaultRequest;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.FaultCodeInfo;
import com.tonggou.andclient.vo.OBDBindInfo;

/**
 * ��̨����obd
 * @author think
 *
 */
public class BaseConnectOBDService extends Service{
	
	private static final boolean DEBUG = true;
	
	public static String TONGGOU_ACTION_START = "com.tonggou.action.startobd";
	public static String TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION = "com.tonggou.ACTION.READ_RTD_CONDITION";
	public static String TONGGOU_ACTION_READ_CURRENT_DTC_CONDITION = "com.tonggou.ACTION.READ_DTC_CONDITION";
	public static String TONGGOU_ACTION_UPDATEUI = "com.tonggou.action.updateui";
	public static String TONGGOU_ACTION_NEW_MESSAGE = "com.tonggou.action.newmessage";
	public static String TONGGOU_ACTION_DISPLAY_MESSAGE = "com.tonggou.action.displaymessage";
	public static String ACTION_ON_GET_RTD_DATAS = "com.tonggou.andclient.app.BaseConnectOBDService.AOGTD";
	public static final String EXTRA_OIL_LEFT = "extra_oil_left"; // ʣ������%
	public static final String EXTRA_OIL_TANK_TEMPERATURE = "extra_oil_tank_temperature"; // ˮ���¶�
	public static final String EXTRA_OIL_AVG_OIL_WEAR = "extra_oil_avg_oil_wear"; // ƽ���ͺģ�Ҳ���ǰٹ����ͺ�
	public static final String EXTRA_TOTAL_DISTANCE = "extra_total_distance"; // �����
	
//	private ArrayList<BluetoothDevice>  devices;

	private BluetoothAdapter mBtAdapter;
	String defaultObdSN = null;
	
    // Key names received from the MyBluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    ///////////////////////////////////////////////////
    
    private MyBluetoothService myBTService = null;
    

    private boolean connectOBDSuccess = false;  //���ɹ� ���������ɹ�  ��Ҫ��鳵vin
    //private String defaultObdSN = null;
    private final Object allow = new Object();
    private final Object allowA = new Object();
    private final Object lockA = new Object();

    private String readFromOBDvehicleVin;
    private ReadCurrentCarConditionReceiver readCurrentCarConditionReceiver;
    public static int connetState = -1;  //-1��ʼ״̬   0--������  1--����  3--���ڶ�ȡobd����  
    public static String cmile;
    public static boolean addingCar = false; //�����������豸����
    
    private SoundPool mOBDConnectStatusSoundPool;
    
	public void onCreate() {		
		TongGouApplication.showLog("BaseConnectOBDService start");
		myBTService = new MyBluetoothService(this, mHandler);
		
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);                                // Register for broadcasts when a device is discovered

       
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);                                // Register for broadcasts when discovery has finished
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();                       // Get the local Bluetooth adapter		
        
        readCurrentCarConditionReceiver = new ReadCurrentCarConditionReceiver();
        IntentFilter readfilter = new IntentFilter();
        readfilter.addAction(TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION);
        readfilter.addAction(TONGGOU_ACTION_READ_CURRENT_DTC_CONDITION);
        registerReceiver(readCurrentCarConditionReceiver, readfilter);
        
        mOBDConnectStatusSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
	  }
	
	  /**
	   * ��ʼ����ɨ������
	   * @param alertUser    �Ƿ�Ҫ��ʾ�û������Ǻ�̨�Զ����Ӳ���
	   */
	 protected void startConnect(){
		  connetState = 1; 
		  connectOBDSuccess = TongGouApplication.connetedOBD;
		  if(mBtAdapter!=null){
	        	// If BT is not on, request that it be enabled.
		        if (!mBtAdapter.isEnabled()) {
		        	
		        }else{
		        	try{
		        		//Log.d("testthread","conneting obd.....");
						doSomeFindingJob();
					}catch(InterruptedException e){
						e.printStackTrace();
					}
		        } 	        
	        }
	  }
	

	 
	  
	  
	  public void onDestroy() {
		  	HttpRequestClient client = new HttpRequestClient(this);
			client.getDefaultClient().cancelRequests(this, true);
	        super.onDestroy();
	        stopConnect();
	        this.unregisterReceiver(mReceiver);//ȡ��ע��Receiver
	        this.unregisterReceiver(readCurrentCarConditionReceiver);//ȡ��ע��Receiver
	        TongGouApplication.showLog("BaseConnectOBDService stop");  
	        mOBDConnectStatusSoundPool.release();
	        mOBDConnectStatusSoundPool = null;
	  }
	  
	  public void stopConnect(){
		  if (myBTService != null) {
	        	myBTService.stop();
      	  }
		  connectOBDSuccess = false;
		  TongGouApplication.connetedOBD = false;
		  connetState = 1;
	  }
	
	/**
	 * ��ʼ���ҹ���
	 */
	  private void doSomeFindingJob()throws InterruptedException{	
		readFromOBDvehicleVin = null;

		boolean intoScan = false; // �Ƿ�����ɨ��

		if (TongGouApplication.sObdLists==null || TongGouApplication.sObdLists.size()== 0) {
			connetState = 1;
			return;
		}

//		devices = new ArrayList<BluetoothDevice>();
		
		String currentDefaultObdSn = null;
		for (int i = 0; i < TongGouApplication.sObdLists.size(); i++) {
			if ("YES".equals(TongGouApplication.sObdLists.get(i).getIsDefault())) {
				currentDefaultObdSn = TongGouApplication.sObdLists.get(i).getObdSN(); // �ҵ���������������Ĭ��obd
				break;
			}
		}
		TongGouApplication.showLog("current obd sn   " + currentDefaultObdSn);
    	TongGouApplication.showLog("last obd sn   " + defaultObdSN);
    	TongGouApplication.showLog("connectOBDSuccess   " + connectOBDSuccess);
		if ( !TextUtils.isEmpty( currentDefaultObdSn) ) { // defaultObdSN such as 00:0D:18:28:4B:6A
			
			if( connectOBDSuccess && currentDefaultObdSn.equals(defaultObdSN) ) {
				connetState = 1; 
				return;
			} else {
				defaultObdSN = currentDefaultObdSn;
				stopConnect();
			}
			
		} else {
			stopConnect();
			return;
		}
		
    	///////////////////////////////
		//�õ��Ѿ���Թ����豸
//    	Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();      // Get a set of currently paired devices
//    	ArrayList<BluetoothDevice> needToChecks = new ArrayList<BluetoothDevice>();
//        // If there are paired devices, add each one to the ArrayAdapter
//        if (pairedDevices!=null && !pairedDevices.isEmpty()) {
//			
//			BluetoothDevice device2 = null;
//			for (BluetoothDevice device : pairedDevices) {
//				if (defaultObdSN.equals(device.getAddress())) { // �ҵ�ɨ�赽���Ѿ���Ե�Ĭ��obd
//					device2 = device;
//					break;
//				}
//			}
//			if (device2 != null) {
//				needToChecks.clear();
//				needToChecks.add(device2);
//			}
//        }
//        
//        cricleConnetOBD(needToChecks); // �����ȼ�����OBD
//    	// û��������Ե���ʼɨ��
//		if (!connectOBDSuccess) {
//			intoScan = true;
//			doDiscovery();
//		}
//		
//		 if(!intoScan){
//	        connetState = 1;
//	     }
		ArrayList<BluetoothDevice> needToChecks = new ArrayList<BluetoothDevice>();
		needToChecks.add( mBtAdapter.getRemoteDevice(defaultObdSN) );
		cricleConnetOBD(needToChecks);
       
	}
	  
	/**
	 * 
	 * @param devices
	 * @param defaultObdSN
	 * @return �ɹ� ��ʧ��
	 * @throws InterruptedException
	 */
	private void cricleConnetOBD(ArrayList<BluetoothDevice> devices)throws InterruptedException {
		if( devices==null || devices.isEmpty()){
			return;
		}

		for (final BluetoothDevice device : devices) {
			if ( TextUtils.isEmpty( device.getAddress() )) {
				continue;
			}
			boolean inMyList = false;
			OBDBindInfo obdInfo = null;
			TongGouApplication.showLog("TongGouApplication.obdLists.size()  = " + TongGouApplication.sObdLists.size());
			for (int i=0; i<TongGouApplication.sObdLists.size(); i++) {
				obdInfo = TongGouApplication.sObdLists.get(i);
				if (device.getAddress().equals(obdInfo.getObdSN())) { 
					inMyList = true;
					break;
				}
			}
			if (!inMyList) {
				continue;
			}
				
			TongGouApplication.showLog("circle connecting 111111111111");

			myBTService.connecting(device);                  //����obd
			
			synchronized(allowA){
				allowA.wait();
			}
			
			//Log.d("testthread", "circle connecting 222222222222");
			
			if (!connectOBDSuccess) {
				connetState = 1;
				continue;  
			}
			//���ӳɹ�
			//������vin �����ж����obd�ͳ����İ󶨹�ϵ
			//Log.d("testthread", "circle readVin 111111111111");
//			String currentConnectVin = readVinFromOBD();
//			//Log.d("testthread", "circle readVin 222222222222:"+currentConnectVin);
//			if( TextUtils.isEmpty( currentConnectVin ) ) {
//				stopConnect();
//				continue;
//			}
//			boolean bInMyVehicleList = false;   // �Ƿ����Լ��ĳ��б���
//			boolean bBindNoChange = false;      // ����OBD��ϵ�Ƿ񲻱�
//				if (currentConnectVin!=null && obdInfo.getVehicleInfo()!=null) {
//					//�Ƿ����Լ��ĳ��б���
//					if ( matchObdVin(currentConnectVin) || "".equals(currentConnectVin)) {
//						bInMyVehicleList = true;
//					}
//					else {
//						bInMyVehicleList = false;
//					}
//					if (!bInMyVehicleList) {
//						//�Ͽ�
//						stopConnect();						
//						continue;
//					}
					
//					String thisObdVin = obdInfo.getVehicleInfo().getVehicleVin();  //���obd�϶�Ӧ�ĳ�
//					if (currentConnectVin.equals(thisObdVin)){ // ����OBD��ϵ�Ƿ񲻱�
//						bBindNoChange = true;
//					}
			finishConnectOBD(obdInfo.getVehicleInfo().getVehicleBrand()
		            +" "+obdInfo.getVehicleInfo().getVehicleModel(),
		            "",obdInfo.getVehicleInfo().getVehicleModelId(),device.getAddress(),obdInfo.getVehicleInfo().getVehicleId());
//					if (!bBindNoChange) {
//						//֪ͨ�����������������������µ�obd��vin�İ󶨹�ϵ����������������������
//						//TODO
//					}
//					return;
//			} else {
//				//�Ͽ�
//				stopConnect();
//			}
        }
	}
	
	/**
	 * ��obd�ж�ȡ����vin
	 * @return vin ����������    null ��ʱ
	 * @throws InterruptedException 
	 */
	private String readVinFromOBD(){
		new Thread(){
			public void run(){
				connetState = 3;
				myBTService.write("##VIN\r\n".getBytes());
			}
		}.start();
		startReadOBDVinTimer();
		synchronized(lockA){
			try {
				lockA.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stopReadOBDVinTimer();
		return readFromOBDvehicleVin;
	}
	
	private void writeCurrentMile(final String miles){
		new Thread(){
			public void run(){
                String writeMiles = "##MIL:"+miles+"\r\n";
				myBTService.write(writeMiles.getBytes());
			}
		}.start();
	}
	
	
	//////////////////
    /**
	 * ɨ�賬ʱscan timeout
	 */
	private ScanTimeOutTask scanTimeoutTask;   					 
	private Timer scanTimeoutTimer;
	private void startTimeoutTimer(){
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
   	 		//Log.d("testthread", "scan ��ʱ............................");
   	 		connetState = 1; //���ҽ���
   	 	}
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * ��ȡobdVIN ��ʱ��
	 */
	private ReadOBDVinTask readOBDVinTask;   					 
	private Timer readOBDVinTimer;
	private void startReadOBDVinTimer(){
		stopReadOBDVinTimer();
		try{
		readOBDVinTask = new ReadOBDVinTask();
		readOBDVinTimer = new Timer();
		readOBDVinTimer.schedule(readOBDVinTask,10000); //10��
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
   	 		//Log.e("testthread", "stop read obd vin ,stop connect obd......��vin��ʱ");
	   	 	synchronized(lockA){ //���Ѷ�vin�߳�
				lockA.notify();
			}
   	 	}
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * �ж����������б����Ƿ������vin
	 * @param obdVin
	 * @return true  ����
	 */
	private boolean matchObdVin(String obdVin){
		if(obdVin==null||"".equals(obdVin)){
			return false;
		}else{
			for(int j=0;j<TongGouApplication.sObdLists.size();j++){ 
				if(TongGouApplication.sObdLists.get(j).getVehicleInfo()!=null
						&&obdVin.equals(TongGouApplication.sObdLists.get(j).getVehicleInfo().getVehicleVin())){
					return true;
				}
			}
			return false;
		}
	}
	

	private void doSendReadRTDOrder() {
		TongGouApplication.showLog("onSendReadRTDOrder");
		if( myBTService == null ) {
			return;
		}
		new Thread(){
			public void run(){
				myBTService.write("##RTD\r\n".getBytes());
			}
		}.start();
	}
	
	private void doSendReadDTCOrder() {
		TongGouApplication.showLog("onSendReadDTCOrder");
		if( myBTService == null ) {
			return;
		}
		new Thread(){
			public void run(){
				myBTService.write("##DTC\r\n".getBytes());
			}
		}.start();
	}
	
	  /**
	   * ���ӳɹ���һЩ����
	   * @param vehicleName
	   * @param vehVin
	   * @param vehModle
	   */
     private void finishConnectOBD(String vehicleName,String vehVin,final String vehModle,String obdSn,String vehicleId){
    	playConnectVoice();  //������ʾ��
    	TongGouApplication.connetedVehicleName = vehicleName;
		TongGouApplication.connetedVIN = vehVin;
		TongGouApplication.connetedObdSN = obdSn;
		TongGouApplication.connetedVehicleID = vehicleId;	
		TongGouApplication.connetedOBD = true;		
		connetState = 3;
		if(cmile!=null&&!"".equals(cmile)){
			//Log.i("Bluetooth thinks", "д����" + cmile);
			writeCurrentMile(cmile);
		}
		doSendReadRTDOrder();
    	doSendReadDTCOrder();
    	// �����г���־����
    	startService(new Intent(DrivingJournalService.ACTION_START_RECORD_DRIVING_JOURNAL));
     }
	
	

	
   
	
	
	//������MyBluetoothService���ص���Ϣ 
	protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MyBluetoothService.MESSAGE_STATE_CHANGE:
                 //Log.i("Bluetooth thinks", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case MyBluetoothService.STATE_CONNECTED:
                	connetState = 3;
                    //�����Ѿ����ӵ�״̬
                    break;
                case MyBluetoothService.STATE_CONNECTING:
                	connetState = 0;
                	//�����������ӵ�״̬
                    break;
                case MyBluetoothService.STATE_LISTEN:
                case MyBluetoothService.STATE_NONE:
                    //��ʼ״̬
                	connetState = -1;
                    break;
                }
                break;
            case MyBluetoothService.MESSAGE_WRITE:
            	//���͡�������
                //byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                //String writeMessage = new String(writeBuf);
                //Log.i("Bluetooth thinks", "send:  " + writeMessage);
         
                break;
            case MyBluetoothService.MESSAGE_READ:
            	//�յ�����������
                String readMessage = (String) msg.obj;
                Log.i("Bluetooth thinks", "receiveAAAAA:  " + readMessage);
                if(readMessage!=null){
                	if(readMessage.indexOf("DTC:")!=-1&&readMessage.indexOf("RTD:")!=-1){
                		String[] dtcAndRtd = readMessage.split("##");
                		if(dtcAndRtd.length>0){
                			for(int i=0;i<dtcAndRtd.length;i++){
                				String temStr = dtcAndRtd[i];
                				if(temStr.indexOf("DTC:")!=-1&&temStr.length()>4){  //������
                            		String obdErrorCode = temStr.substring(temStr.indexOf("DTC:")+4);
                            		prosseingOBDError(obdErrorCode.trim());
                            	}else if(temStr.indexOf("RTD:")!=-1&&temStr.length()>4){
                            		String currentConditon = temStr.substring(temStr.indexOf("RTD:")+4);
                            		prosseingCurrentConditon(currentConditon.trim());
                            	}
                			}
                		}
                		return;
                	}
                	if(readMessage.indexOf("VIN:")!=-1&&readMessage.length()>4){
                		String vehicleVin = readMessage.substring(readMessage.indexOf("VIN:")+4);
                		
                		if(vehicleVin == null){
                			vehicleVin = "";
    	        		}
    	        		final String fvehicleVin = vehicleVin.trim();  
    	        		//readFromOBDvehicleVin = "";  //����
        	        	readFromOBDvehicleVin = fvehicleVin; 
        	        	doSendReadRTDOrder();
        	        	doSendReadDTCOrder();
                		synchronized(lockA){ //���Ѷ�vin�߳�
                			lockA.notify();
            			}
                	}else if(readMessage.indexOf("DTC:")!=-1&&readMessage.length()>4){  //������
                		String obdErrorCode = readMessage.substring(readMessage.indexOf("DTC:")+4).trim();
                		
                		obdErrorCode = SomeUtil.transformDTCResult(obdErrorCode);
                		
                		if( !TextUtils.isEmpty( obdErrorCode ) ) {
                			prosseingOBDError(obdErrorCode);
                		}
                	}else if(readMessage.indexOf("RTD:")!=-1&&readMessage.length()>4){
                		String currentConditon = readMessage.substring(readMessage.indexOf("RTD:")+4);
                		prosseingCurrentConditon(currentConditon.trim());
                	}else if(readMessage.indexOf("MIL")!=-1){
                		if(readMessage.indexOf("OK")!=-1){              			
                			cmile=null;
                			//Log.i("Bluetooth thinks", "�幫��" + cmile);
                		}
                	}
                }
                break;

            case MyBluetoothService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
            	//�Ѿ����豸�������
            	//Log.d("testthread", "connect obd ok���ӳɹ�");
            	
            	
        		connectOBDSuccess = true;
        		synchronized(allow){
        			allow.notify();
    			}
            		
        		synchronized(allowA){
        			allowA.notify();
    			}
            
                break;
            case MyBluetoothService.MESSAGE_CONNECT_FAILT:  //����ʧ��
            	//Log.d("testthread", "connect obd failt����ʧ��");
            	connectOBDSuccess = false;
            	TongGouApplication.connetedOBD = false;
            	connetState = 1;
            	//Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_LONG).show();
        		synchronized(allow){
        			allow.notify();
    			}
            	
        		synchronized(allowA){
        			allowA.notify();
    			}
          
                break;
            case MyBluetoothService.MESSAGE_CONNECT_LOST:  //���Ӷϵ�
            	//Log.d("testthread", "connect obd lost���ӶϿ�");
            	//Toast.makeText(getApplicationContext(), "�������ӶϿ�",Toast.LENGTH_LONG).show();
            	connectOBDSuccess = false;
            	connetState = -1;
            	TongGouApplication.connetedOBD = false;
            	playDisconnectVoice();  //�Ͽ���ʾ��
            	// �Ͽ��г���־����
            	sendBroadcast(new Intent(DrivingJournalService.ACTION_STOP_RECORD_DRIVING_JOURNAL));
            	
            	synchronized(allow){
        			allow.notify();
    			}
            	
            	synchronized(allowA){
        			allowA.notify();
    			}
                break;
            case MyBluetoothService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
	
	
	
	
	
	
	 
	 /*
	  * ���������豸����
	  */
	 private void doDiscovery(){
		 startTimeoutTimer(); //��ʱ
		 //Log.d("testthread", "scanning bluetooth devices ....");
         // If we're already discovering, stop it
         if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
         }
         TongGouApplication.showLog("@@@@@@@ doDiscovery @@@@@@@@");
         //Request discover from BluetoothAdapter
         mBtAdapter.startDiscovery();
	 }
	 
	    // The BroadcastReceiver that listens for discovered devices 
	    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	
	        	if( addingCar ) {
	        		return;
	        	}
	        	
	            String action = intent.getAction();

	            // When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	                // Get the BluetoothDevice object from the Intent
	                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                //Log.d("bbtt", "device:"+device.getName()+":"+device.getAddress());
	                
	                // If it's already paired, skip it, because it's been listed already
//	                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//	                	if(devices!=null){
//	                		//devices.add(device);
//	                		addDevice(device);
//	                	}
	                	TongGouApplication.showLog("ACTION_FOUND  " + defaultObdSN);
	                	if( TextUtils.isEmpty(defaultObdSN) ) {
							connetState = 1; //���ҽ���
							return;
						}
	                	
						final ArrayList<BluetoothDevice> needConnectDevices = new ArrayList<BluetoothDevice>();
						if( defaultObdSN.equals( device.getAddress()) ) {
							needConnectDevices.add(device);
							new Thread(){
								public void run(){
									TongGouApplication.showLog("ACTION_FOUND  Thread device.getAddress()" + device.getAddress());
				                	try {
										if(!BaseConnectOBDService.addingCar){
											cricleConnetOBD(needConnectDevices);
										}
										connetState = 1; //���ҽ���
									} catch (InterruptedException e) {
										connetState = 1; //���ҽ���
										e.printStackTrace();
									}  
								}
		                	}.start();
							mBtAdapter.cancelDiscovery();
						}
						
	                	if(DEBUG) Log.d("bbtt", "device2:"+device.getName()+":"+device.getAddress());
//					}
	                
	            // When discovery is finished, change the Activity title
	            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {		            	
	            	//if(devices!=null){
	            		 //Log.d("testthread", "scanning devices finish ...."+devices.size());	
                	//}
	            	stopTimeoutTimer();   //ͣ����ʱ
	            	TongGouApplication.showLog("BluetoothAdapter DISCOVERY_FINISHED");
	            }
	        }
	    };
	    
	    
	    
//	    private void addDevice(BluetoothDevice devs){
//	    	boolean finded = false;
//	    	for(int i=0;i<devices.size();i++){
//	    		if(devices.get(i).getAddress().equals(devs.getAddress())){
//	    			finded = true;
//	    		}
//	    	}
//	    	if(!finded&&(!"".equals(devs.getAddress())&&(!"".equals(devs.getName())))&&devs.getAddress()!=null&&devs.getName()!=null){
//	    		devices.add(devs);
//	    		if(DEBUG) TongGouApplication.showLog("devices added name:" + devs.getName() + " + addr: " + devs.getAddress() );
//	    	}
//	    }
	    
	    /**
	     * �����յ��Ĺ����룬���ɳ�����ʾ��Ϣ
	     * @param codeStrs  ����:pu003,pu9999
	     */
	    private void prosseingOBDError(final String codeStrs){	  
	    	
	    	if(!TongGouApplication.connetedOBD||codeStrs==null||"".equals(codeStrs)){
				return;
			}
	    	String userID = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, "");
	    	String modleId = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.VEHICLE_MODE_ID, "NULL");
	    	String[] errors = codeStrs.split(",");
	        //errors[0] = "P0403";
	    	int newErrorCount = 0;
	    	if( errors.length > 0 ) {
	    		sendVehicleProblem(codeStrs);
	    	}
	    	for(int i=0;i<errors.length;i++){
	    		if(errors[i].indexOf("#")!=-1){
	    			continue;
	    		}
	    		ArrayList<FaultCodeInfo> modleFaults = SaveDB.getSaveDB(this).getSomeFaultCodesById(modleId,errors[i]);
	    		if(modleFaults==null||modleFaults.size()==0){
	    			modleFaults = SaveDB.getSaveDB(this).getSomeFaultCodesById("common",errors[i]);
	    		}
	    		
	    		CarCondition oneConditionMessage = new CarCondition();
	    		oneConditionMessage.setAlarmId(System.currentTimeMillis()+"");
	    		oneConditionMessage.setUserID(userID);
	    		oneConditionMessage.setReportTime(System.currentTimeMillis()+"");
	    		oneConditionMessage.setVehicleVin(TongGouApplication.connetedVIN);
	    		oneConditionMessage.setVehicleId(TongGouApplication.connetedVehicleID);
	    		oneConditionMessage.setObdSN( TongGouApplication.connetedObdSN );
	    		if(modleFaults!=null&&modleFaults.size()>0){
		    		FaultCodeInfo oneFault = modleFaults.get(0);
		    		oneConditionMessage.setFaultCode(oneFault.getFaultCode());
		    		
		    		if(modleFaults.size()>=2){ //��������
		    			StringBuffer sber = new StringBuffer();
			    		for(int j=0;j<modleFaults.size();j++){
			    			sber.append(modleFaults.get(j).getDescription());
			    			sber.append("\n");
			    		}
			    		oneConditionMessage.setContent(sber.toString());
		    		}else{		    		
		    			oneConditionMessage.setContent(oneFault.getDescription());
		    		}
		    		oneConditionMessage.getFaultCodeInfo().setBackgroundInfo( oneFault.getBackgroundInfo() );
		    		oneConditionMessage.getFaultCodeInfo().setCategory( oneFault.getCategory() );
		    		
	    		}else{	    		
		    		oneConditionMessage.setFaultCode(errors[i]);
		    		oneConditionMessage.setContent("����ʶ��Ĺ�����");
		    		oneConditionMessage.getFaultCodeInfo().setBackgroundInfo( "����ʶ��Ĺ�����" );
	    		}
	    		
	    		int resultFlag = SaveDB.getSaveDB(this).saveAlarm(oneConditionMessage);   //���浽���ݿ�	
	    		if( resultFlag > 0 ) {
	    			newErrorCount++;
	    		}
	    	}
	    	
	    	sendReceivedOBDDTCBroadcast();
	    	
	    	if(newErrorCount>0){//��ʾ�û�
				if ( TongGouApplication.getInstance().isLogin()) {  	    		//����ǵ�¼״̬						
					Intent intent =new Intent(this,CarErrorActivity.class);
					intent.putExtra(CarErrorActivity.EXTRA_NEW_CAR_CONDITION_COUNT, newErrorCount);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);				
				}else{
					Intent toLogin = new Intent(this, LoginActivity.class);
					toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(toLogin);			
				}
	    	}
	    }
	    
	    /**
	     * ���ͽ��յ� OBD ������ ��������Ϣ�㲥
	     */
	    private void sendReceivedOBDDTCBroadcast() {
	    	Intent intent = new Intent();
	    	intent.setAction(CarConditionQueryActivity.ACTION_RECEIVED_OBD_DTC);
	    	sendBroadcast(intent);
	    }
	    
		/**
		 * ���ͳ���������Ϣ
		 */
		private void sendVehicleProblem(String faultCodes){
			String userNo = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, "");
			SendVehicleFaultRequest request = new SendVehicleFaultRequest();
			request.setRequestParams(userNo,
					TongGouApplication.connetedObdSN, 
					TongGouApplication.connetedVIN,
					TongGouApplication.connetedVehicleID,
					System.currentTimeMillis(),
					faultCodes);
			request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				}

				@Override
				public Class<BaseResponse> getTypeClass() {
					return BaseResponse.class;
				}
			
			});
		}

	    
	    /**
	     * �����յ�ʵʱ��ʾ����
	     * @param codeStrs  
	     */
		/**
		 * �����յ�ʵʱ��ʾ����
		 * 
		 * @param codeStrs
		 */
		private void prosseingCurrentConditon(String codeStrs) {
			if (!TongGouApplication.connetedOBD || codeStrs == null || "".equals(codeStrs)) {
				return;
			}
			// OBD�˻ظ���##˲ʱ�ͺģ�ml/s�����ٹ����ͺģ�l/100km��,�ͺ�(l/h),
			// ������%��,��̣�km����������ˮ�£��棩����ƿ��ѹ��V��\r\n

			// ,�ͺ�(l/h)��������%����Щ����Ҳ��֧�֣���ظ�N/A;

			String ssyh = ""; // ˲ʱ�ͺ�
			String pjyh = ""; // �ٹ����ͺ�
			String youhao = "";
			String syyl = ""; // ʣ������
			String licheng = ""; // ���
			String sxwd = ""; // ������ˮ��
			String dianya = "";
			String shudu = ""; // �ٶ�

			String ssyhToSever = ""; // ˲ʱ�ͺ�
			String pjyhToSever = ""; // �ٹ����ͺ�
			String youhaoToSever = "";
			String syylToSever = ""; // ����
			String lichengToSever = "";
			String sxwdToSever = ""; // ������ˮ��
			String dianyaToSever = "";

			String[] errors = codeStrs.split(",");
			if (errors.length >= 1) {
				ssyh = errors[0];
				ssyhToSever = errors[0];
			}
			if (errors.length >= 2) {
				pjyh = errors[1];
				pjyhToSever = errors[1];
			}
			if (errors.length >= 3) {
				youhao = errors[2];
				youhaoToSever = errors[2];
			}
			if (errors.length >= 4) {
				syyl = errors[3];
				syylToSever = errors[3];
			}
			if (errors.length >= 5) {
				licheng = errors[4];
				lichengToSever = errors[4];
			}
			if (errors.length >= 6) {
				sxwd = errors[5];
				sxwdToSever = errors[5];
			}
			if (errors.length >= 7) {
				dianya = errors[6];
				dianyaToSever = errors[6];
			}
			if (errors.length >= 8) {
				shudu = errors[7];
			}

			if ("N/A".equals(ssyh)) {
				ssyh = "- -";
			} else {
				try {
					double ssyhDouble = Double.parseDouble(ssyh);
					double ssyhHour = (ssyhDouble * 3600) / 1000; // ˲ʱ�ͺ� ����ml/s ->
																	// l/h ������ʾ
					BigDecimal bg = new BigDecimal(ssyhHour);
					double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					ssyh = f1 + "";
				} catch (NumberFormatException er) {
					ssyh = "- -";
				}
			}
//			if ("N/A".equals(pjyh)) {
//				pjyh = "- - l/h";
//			} else {
//				if (shudu != null && !"".equals(shudu) && !"N/A".equals(shudu)) {
//					try {
//						double speed = Double.parseDouble(shudu);
//						if (speed < 10) { // ���ٶȵ���10km/hʱ���ٹ����ͺ���l/h��
//							pjyh = pjyh + " l/h";
//						} else { // ���ڵ���ʱ�ٹ����ͺ���l/100Km
//							pjyh = pjyh + " l/100km";
//						}
//					} catch (NumberFormatException ex) {
//						pjyh = "- - l/h";
//					}
	//
//				} else { // �ٹ����ͺ���l/h��
//					pjyh = pjyh + " l/h";
//				}
//			}

			if ("N/A".equals(syyl)) {
				syyl = "- -";
			}

			if ("N/A".equals(sxwd)) {
				sxwd = "- -";
			}

			// Intent intent = new Intent();
			// intent.setAction(TONGGOU_ACTION_UPDATEUI);
			// intent.putExtra(CarConditionQueryActivity.CONDITION_VALUE_SSYH,
			// ssyh);
			// intent.putExtra(CarConditionQueryActivity.CONDITION_VALUE_PJYH,
			// pjyh);
			// intent.putExtra(CarConditionQueryActivity.CONDITION_VALUE_SYYL,
			// syyl);
			// intent.putExtra(CarConditionQueryActivity.CONDITION_VALUE_SXWD,
			// sxwd);
			//
			// CarConditionQueryActivity.ssyhStr = ssyh;
			// CarConditionQueryActivity.pjyhStr = pjyh;
			// CarConditionQueryActivity.syylStr = syyl;
			// CarConditionQueryActivity.sxwdStr = sxwd;
			// sendBroadcast(intent); // ���͹㲥

			Intent intent = new Intent(ACTION_ON_GET_RTD_DATAS);
			intent.putExtra(EXTRA_OIL_LEFT, syyl);
			intent.putExtra(EXTRA_OIL_TANK_TEMPERATURE, sxwd);
			intent.putExtra(EXTRA_OIL_AVG_OIL_WEAR, pjyh);
			intent.putExtra(EXTRA_TOTAL_DISTANCE, licheng);
			sendBroadcast(intent);

			final String ssyhF = ssyhToSever; // ˲ʱ�ͺ�
			final String pjyhF = pjyhToSever; // �ٹ����ͺ�
			final String youhaoF = youhaoToSever;
			final String syylF = syylToSever + "%"; // ����
			final String lichengF = lichengToSever;
			// Log.d("CONTEETTT","read current condition.......���:"+lichengF);
			final String sxwdF = sxwdToSever; // ������ˮ��
			final String dianyaF = dianyaToSever;
			sendVehicleCondition(youhaoF, lichengF, ssyhF, pjyhF, syylF, sxwdF, dianyaF);

			judgeTheOil(syyl);
		}
	    
	    
	    private void judgeTheOil(String syylStr){
	    	if(syylStr==null||"".equals(syylStr)||"- -".equals(syylStr)){
	    		return;
	    	}
	    	try{
	    		float syylFloat = Float.parseFloat(syylStr);
	    		String lastArea = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "2");  // ������һ�ξ�������  0~15--0 /  15~25--1/  25����--2
	  
	    		
	    		if(syylFloat>=TongGouService.interTwo){     //25��������
	    			getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
					.putString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "2").commit();
	    			//������䲻��ʾ
	    		}else if(syylFloat>=TongGouService.interOne&&syylFloat<TongGouService.interTwo){   //15--25����
	    			getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
					.putString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "1").commit();
	    			
	    			if("0".equals(lastArea)||"2".equals(lastArea)){
	    				//��ʾ
	    				Intent intent =new Intent(this,OilErrorActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("tonggou.oil.alert", "��������"+TongGouService.interTwo+"%");
						startActivity(intent);		
	    			}
	    		}else if(syylFloat>=0&&syylFloat<TongGouService.interOne){            //0--15����
	    			getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
					.putString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "0").commit();
	    			if("1".equals(lastArea)||"2".equals(lastArea)){
	    				//��ʾ
	    				Intent intent =new Intent(this,OilErrorActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("tonggou.oil.alert", "��������"+TongGouService.interOne+"%");
						startActivity(intent);		    					    				
	    			}
	    		}
	    	}catch(NumberFormatException ex){    		
	    	}
	    }
	    
	    
		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

		
		/**
		 * ������ʵʱ������Ϣ�㲥
		 * @author think
		 *
		 */
		 private class ReadCurrentCarConditionReceiver extends BroadcastReceiver{
		        public void onReceive(Context context, Intent intent) {
		        	//Log.d("CONTEETTT","RRRRRRRRRRR.....");
		        	if( !TongGouApplication.connetedOBD || myBTService == null){ 
		        		return;
		        	}
		        	String action = intent.getAction();
		        	if( TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION.equals(action) ) {
		        		doSendReadRTDOrder();
		        	} else if( TONGGOU_ACTION_READ_CURRENT_DTC_CONDITION.equals(action) ) {
		        		doSendReadDTCOrder();
		        	}
        			TongGouApplication.showLog( action );
		        }                       
		    }
		 
		 /**
		  * ���ͳ�����Ϣ
		  * @param oilWear    �ͺ�
		  * @param currentMileage  ���
		  * @param instantOilWear  ˲ʱ�ͺ�
		  * @param oilWearPerHundred  �ٹ����ͺ�
		  * @param oilMassStr     ����
		  * @param engineCoolantTemperature   ��������ȴҺ
		  * @param batteryVoltage      ��ƿ��ѹ
		  */
		private void sendVehicleCondition(String oilWear,String currentMileage,String instantOilWear,
				                          String oilWearPerHundred,String oilMassStr,String engineCoolantTemperature,String batteryVoltage){
			String userNo = PreferenceUtil.getString(this, BaseActivity.SETTING_INFOS, BaseActivity.NAME);
			SendVehicleConditionRequest request = new SendVehicleConditionRequest();
			request.setRequestParams(userNo, oilWear, currentMileage, instantOilWear, oilWearPerHundred, oilMassStr, engineCoolantTemperature, batteryVoltage);
			request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

				@Override
				public void onParseFailure(String errorCode, String errorMsg) {
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
						Throwable error) {
				}
				
				@Override
				public Class<BaseResponse> getTypeClass() {
					return BaseResponse.class;
				}
			});
			
		}
		
	private void playConnectVoice() {
		playVoice(R.raw.lianjie);
	}

	private void playDisconnectVoice() {
		playVoice(R.raw.duankai);
	}
	
	private void playVoice(int resId) {
		if (!"NO".equals(getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(
				BaseActivity.CHECKVOICE, null))) {
			mOBDConnectStatusSoundPool.load(this, resId, 1);
			mOBDConnectStatusSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					soundPool.play(sampleId, 1, 1, 0, 0, 1);
				}
			});
		}
	}
}