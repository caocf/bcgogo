package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.network.MyBluetoothService;
import com.tonggou.andclient.network.MyBluetoothService.OnConnectCallback;
import com.tonggou.andclient.vo.OBDDevice;

public abstract class AbsScanObdDeviceDialogActivity extends AbsUMengActivity implements OnItemClickListener, OnConnectCallback {
	
	private static final int REQUEST_ENABLE_BT = 0x1;
	
	private final int MSG_TIMEOUT_CONNECT = 0x12;
	private final int MSG_TIMEOUT_READ_VIN = 0x13;
	private final int TIMEOUT_INTERVAL_CONNECT = 10 * 1000;  // 连接超时时间为10 秒
	private final int TIMEOUT_INTERVAL_READ_VIN = 5 * 1000;  // 读取 VIN 超时时间为 5 秒
	private final int READ_VIN_RETRY_TIMES = 2;				 // 读取VIN超时重试次数 2次
	
	private BluetoothAdapter mBtAdapter;
	private TextView mTitle;
	private ImageButton mResearchBtn;
	private ProgressBar mSearchDevicesIndicator;
	private ListView mPairedDevicesList;
	private ListView mNewDevicesList;
	private DeviceAdapter mPairedAdapter;
	private DeviceAdapter mNewAdapter;
	private MyBluetoothService myBTService = null;
	
	// 用这个参数来记录上一次用户点击连接的蓝牙设备
	private String mLastConnectDeviceMacAddess = "";
	private boolean mIsConnectLock = false;
	private int mReadVinTimes = 1; 
	
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	OBDDevice obdDevice = new OBDDevice();
                	String name = device.getName();
        			obdDevice.setDeviceName( TextUtils.isEmpty(name) ? "未命名" : name );
                	obdDevice.setDeviceAddress(device.getAddress());
                	if( !mNewAdapter.getData().contains(obdDevice ) ) {
                		mNewAdapter.add(obdDevice);
                	}
                }
                
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	// 每次调用  BluetoothAdapter.cancelDiscovery() 方法都会执行
            	TongGouApplication.showLog("ACTION_DISCOVERY_FINISHED");
            	// 扫描完成
            	setScanDialogTitle(R.string.connect_obd_devieces);
            	showResearchBtn();
                setProgressBarIndeterminateVisibility(false);
                
                if( mIsConnectLock ) {
                	changeTitleForConnecting();
                }
            }
        }
    };
    
    /**
     * 连接设备的回调
     */
    @SuppressLint("HandlerLeak")
	private Handler mConnectDeviceHandler = new Handler() {
    	
    	@Override
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
		    	case MyBluetoothService.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case MyBluetoothService.STATE_CONNECTED: onStateChange(MyBluetoothService.STATE_CONNECTED); break;	// 处于已经连接的状态
						case MyBluetoothService.STATE_CONNECTING:
							onStartConnect(); 
							onStateChange(MyBluetoothService.STATE_CONNECTING); 
							break; // 处于正在连接的状态
						case MyBluetoothService.STATE_LISTEN: onStateChange(MyBluetoothService.STATE_LISTEN); break;
						case MyBluetoothService.STATE_NONE: onStateChange(MyBluetoothService.STATE_NONE); break; // 初始状态  
					}
				break;
				case MyBluetoothService.MESSAGE_WRITE: onSendOrderSuccess(); break; 
				case MyBluetoothService.MESSAGE_READ: onReceiveResultSuccess((String) msg.obj);  break; 
				case MyBluetoothService.MESSAGE_DEVICE_NAME: onConnectSuccess( (OBDDevice)msg.obj ); break; // 提醒已经和设备完成连接
				case MyBluetoothService.MESSAGE_CONNECT_FAILT: onConnectFailure((String)msg.obj); break; // 连接失败
				case MyBluetoothService.MESSAGE_CONNECT_LOST: onConnectLost(); break; // 连接断掉
				default: break;
			}
		}
    };
    
    @SuppressLint("HandlerLeak")
	private Handler mTimeoutHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
					case MSG_TIMEOUT_CONNECT: onConnectFailure("连接超时"); break;
					case MSG_TIMEOUT_READ_VIN:
						if(mReadVinTimes < READ_VIN_RETRY_TIMES) {
							mReadVinTimes ++;
							onSendOrder();
						} else {
							resetReadVinTimes();
							onConnectFailure("读取 OBD 超时");
						}
						break;
					default: break;
				}
			super.handleMessage(msg);
		}
    	
    };

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog_scan_bluetooth_device);
		
		BaseConnectOBDService.addingCar = true;
		// 先端，才可以扫描
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server","STOP");
		sendBroadcast(intentS);
		
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			setFinishOnTouchOutside(false);
		}
		
        mTitle = (TextView) findViewById(R.id.dialog_title);
        mResearchBtn = (ImageButton) findViewById(R.id.obd_devices_research);
        mSearchDevicesIndicator = (ProgressBar) findViewById(R.id.scan_devices_indicator);
		mPairedDevicesList = (ListView) findViewById(R.id.paired_devices_list);
		mNewDevicesList = (ListView) findViewById(R.id.other_devices_list);
		mPairedDevicesList.setEmptyView(findViewById(R.id.paired_devices_list_empty_view));
		mNewDevicesList.setEmptyView(findViewById(R.id.other_divices_list_empty_view));
		afterViews();
	}
	
	private void afterViews() {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		registerBluetoothReceiver();
		myBTService = new MyBluetoothService(this, mConnectDeviceHandler);
		setScanDialogTitle(R.string.connect_obd_devieces);
		showResearchBtn();
		setListener();
		listBindData();
		findDevices();
	}
	
	private void registerBluetoothReceiver() {
		IntentFilter filter = new IntentFilter();
	    // Register for broadcasts when a device is discovered
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // Register for broadcasts when discovery has finished
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
	}
	
	/**
	 * 设置标题栏标题
	 * @param res
	 */
	protected void setScanDialogTitle(int res) {
		setScanDialogTitle(getString(res));
	}
	
	/**
	 * 设置标题栏标题
	 * @param title
	 */
	protected void setScanDialogTitle(String title) {
		mTitle.setText(title);
		TongGouApplication.showLog(mTitle.getText());
	}
	
	/**
	 * 显示
	 */
	private void showResearchBtn() {
		mResearchBtn.setVisibility(View.VISIBLE);
		mSearchDevicesIndicator.setVisibility(View.GONE);
	}
	
	/**
	 * 显示在加载的 progressbar
	 */
	protected void showScanDevicesIndiactor() {
		mResearchBtn.setVisibility(View.GONE);
		mSearchDevicesIndicator.setVisibility(View.VISIBLE);
	}
	
	private void setListener() {
		mResearchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				findDevices();
			}
		});
		
		mPairedDevicesList.setOnItemClickListener(this);
		mNewDevicesList.setOnItemClickListener(this);
	}
	
	/**
	 * listView 绑定数据
	 */
	private void listBindData() {
		mPairedAdapter = new DeviceAdapter(this, new ArrayList<OBDDevice>(), R.layout.item_list_devices );
		mNewAdapter = new DeviceAdapter(this, new ArrayList<OBDDevice>(), R.layout.item_list_devices);
		
		mPairedDevicesList.setAdapter(mPairedAdapter);
		mNewDevicesList.setAdapter(mNewAdapter);
	}

	/**
	 * 查找设备
	 */
	private void findDevices() {
		mLastConnectDeviceMacAddess = "";
		mIsConnectLock = false;
		// 查看本地蓝牙设备是否可用，若不可用则开启
		if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            return;
        }
		
		mPairedAdapter.clear();
		mNewAdapter.clear();
		resetPairedListHeight();
		
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		if( pairedDevices.size() > 4 ) {
			setMaxHeightOfPairedListHeight();
		}
		
		for( BluetoothDevice device : pairedDevices ) {
			OBDDevice obdDevice = new OBDDevice();
			String name = device.getName();
			obdDevice.setDeviceName( TextUtils.isEmpty(name) ? "未命名" : name );
			obdDevice.setDeviceAddress(device.getAddress());
			mPairedAdapter.add(obdDevice);
		}
		
		doDiscovery();
	}
	
	/**
	 * 重置已配对设备列表的高度
	 */
	private void resetPairedListHeight() {
		LayoutParams lp = (LayoutParams) mPairedDevicesList.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mPairedDevicesList.setLayoutParams(lp);
	}
	
	/**
	 * 设置已配对设备列表的最大高度
	 */
	private void setMaxHeightOfPairedListHeight() {
		LayoutParams lp = (LayoutParams) mPairedDevicesList.getLayoutParams();
		lp.height = getResources().getDimensionPixelSize(R.dimen.obd_scan_dialog_paired_list_max_height);
		mPairedDevicesList.setLayoutParams(lp);
	}

	/**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
    
    	// Indicate scanning in the title
    	showScanDevicesIndiactor();
    	setScanDialogTitle(R.string.scan_bluetooth_scanning);
        setProgressBarIndeterminateVisibility(true);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        
        if (myBTService != null) {
			myBTService.stop();
		}
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        TongGouApplication.getInstance().queryVehicleList();
        BaseConnectOBDService.addingCar = false;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 蓝牙设备打开时调用
		if( requestCode == REQUEST_ENABLE_BT  ) {
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	findDevices();
            } else {
                // User did not enable Bluetooth or an error occured
            	TongGouApplication.showToast(getString( R.string.bt_not_enabled_leaving ));
            	setResult(RESULT_CANCELED);
            	TongGouApplication.getInstance().notifyBindOBDCancle();
                finish();
            }
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if( mBtAdapter.isDiscovering() ) {
			mBtAdapter.cancelDiscovery();
		}
		if( mIsConnectLock ) {
			return;
		}
		OBDDevice obdDevice = (OBDDevice)parent.getAdapter().getItem(position);
		mLastConnectDeviceMacAddess = obdDevice.getDeviceAddress();
		if( TextUtils.isEmpty( obdDevice.getDeviceAddress()) ) {
			return;
		} 
		mIsConnectLock = true;
		TongGouApplication.showLog("select pos " + position + " -- >" + obdDevice.toString());
		updateDevicesResultStatus();
		doConnectDevice(obdDevice);
	}
	
	private void updateDevicesResultStatus() {
		mNewAdapter.notifyDataSetChanged();
		mPairedAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 开始连接操作
	 * @param obdDevice 要连接的设备
	 */
	private void doConnectDevice(OBDDevice obdDevice) {
		TongGouApplication.showLog("connect");
		changeTitleForConnecting();
		myBTService.connecting(mBtAdapter.getRemoteDevice(obdDevice.getDeviceAddress()));
	}
	
	/**
	 * 将标题栏状态改为正在链接设备
	 */
	private void changeTitleForConnecting() {
		setScanDialogTitle(R.string.connect_obd_running);
		showScanDevicesIndiactor();
	}
	
	class DeviceAdapter extends AbsViewHolderAdapter<OBDDevice> {

		public DeviceAdapter(Context context, List<OBDDevice> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, OBDDevice itemData) {
			ProgressBar connectIndicator = getViewFromHolder(convertView, R.id.connect_indicator);
			TextView deviceInfo = getViewFromHolder(convertView, R.id.device_info);
			connectIndicator.setVisibility(View.INVISIBLE);
			deviceInfo.setText(itemData.getDeviceName());
			
			// 点击 item 之后当调用 notifyDataSetChange() 方法后, setData() 方法就会重新调用
			// 达到刷新 UI 的目的
			if( !TextUtils.isEmpty( mLastConnectDeviceMacAddess)
					&& itemData.getDeviceAddress().equals(mLastConnectDeviceMacAddess)) {
				connectIndicator.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void onStateChange(int statusCode) {
	}
	
	/**
	 * 开始连接设备
	 */
	protected void onStartConnect() {
		mTimeoutHandler.sendEmptyMessageDelayed(MSG_TIMEOUT_CONNECT, TIMEOUT_INTERVAL_CONNECT);
	}

	@Override
	public void onReceiveResultSuccess(String result) {
		mTimeoutHandler.removeMessages(MSG_TIMEOUT_READ_VIN);
		resetReadVinTimes();
		mLastConnectDeviceMacAddess = "";
		updateDevicesResultStatus();
		showResearchBtn();
		if( TextUtils.isEmpty( result ) 
				|| !result.contains("##VIN") 
				|| result.length() <= "##VIN:".length() )  {
			onConnectFailure("OBD 数据读取失败");
			return;
		} 
		setScanDialogTitle("连接 OBD 成功");
	}

	/**
	 * 发送成功不一定读VIN 成功，这个方法可能会调用多次
	 */
	@Override
	public void onSendOrderSuccess() {
	}

	@Override
	public void onConnectSuccess(OBDDevice device) {
		setScanDialogTitle("正在读取设备信息");
		onSendOrder();
	}
	
	private void onSendOrder() {
		mTimeoutHandler.removeMessages(MSG_TIMEOUT_CONNECT);
		myBTService.write("##VIN\r\n".getBytes());
		mTimeoutHandler.sendEmptyMessageDelayed(MSG_TIMEOUT_READ_VIN, TIMEOUT_INTERVAL_READ_VIN);
	}

	@Override
	public void onConnectFailure(String msg) {
		mTimeoutHandler.removeMessages(MSG_TIMEOUT_CONNECT);
		mTimeoutHandler.removeMessages(MSG_TIMEOUT_READ_VIN);
		myBTService.stop();
		mLastConnectDeviceMacAddess = "";
		updateDevicesResultStatus();
		showResearchBtn();
		setScanDialogTitle(msg);
		mIsConnectLock = false;
	}
	
	/**
	 * 重置读取 VIN 次数
	 * 在连接成功 或者 连接次数超过最大重试次数时，应该调用次方法
	 */
	private void resetReadVinTimes() {
		mReadVinTimes = 1;
	}

	@Override
	public void onConnectLost() {
		onConnectFailure("连接已断开");
	}
	
}
