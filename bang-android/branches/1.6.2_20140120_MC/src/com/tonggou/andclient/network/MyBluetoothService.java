package com.tonggou.andclient.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.vo.OBDDevice;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices.
 */
public class MyBluetoothService {
	
	/**
	 * 连接的回调接口
	 * @author lwz
	 *
	 */
	public static interface OnConnectCallback {
		/**
		 * 连接状态改变
		 * @param statusCode 状态码  MyBluetoothService.STATE_
		 */
		public void onStateChange(int statusCode);
		/**
		 * 成功接收 OBD 返回指令信息
		 * @param result
		 */
		public void onReceiveResultSuccess(String result);
		/**
		 * 发送 OBD 设备指令 成功
		 */
		public void onSendOrderSuccess();
		/**
		 * 连接设备成功
		 * @param deviceName 设备名称
		 */
		public void onConnectSuccess(OBDDevice device);
		/**
		 * 连接设备失败
		 */
		public void onConnectFailure(String msg);
		
		/**
		 * 断掉连接(被动的)
		 */
		public void onConnectLost();
	} 
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECT_FAILT = 6;
    public static final int MESSAGE_CONNECT_LOST = 7;          //连接断掉
	
    // Debugging
    private static final String TAG = "Bluetooth thinks";
//    private static final boolean D = true;

    // Unique UUID for this application

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
   
    private ConnectingThread mConnectingThread;
    private ReadWriteThread readWriteThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    private Context _context = null;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public MyBluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        _context = context;
    }

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//        	BluetoothSocket tmp = null;
//        	BluetoothDevice deviceExtra = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
//            Parcelable[] uuids = intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
//           // ParcelUuid[] uuids = (ParcelUuid)uuidExtra;
//        	UUID uuid = MY_UUID;
//            if (uuids.length > 0) {
//        		 int c = uuids.length;
//        		 uuid = ((ParcelUuid)uuids[0]).getUuid();
//        	 }      
//            // Start the thread to connect with the given device
//            mConnectingThread = new ConnectingThread(deviceExtra, uuid);
//            mConnectingThread.start();
//            setState(STATE_CONNECTING);
//     
//        }
//    };

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        //if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        //if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectingThread != null) {
        	mConnectingThread.cancel(); 
        	mConnectingThread = null;
        }

        // Cancel any thread currently running a connection
        if (readWriteThread != null) {
        	readWriteThread.cancel(); 
        	readWriteThread = null;
        }

        setState(STATE_LISTEN);
    }

    /**
     * 启动连接蓝牙设备线程
     */
    public synchronized void connecting(BluetoothDevice device) {
        //if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
//        if (mState == STATE_CONNECTING) {
            if (mConnectingThread != null){
            	mConnectingThread.cancel(); 
            	mConnectingThread = null;
        	}
//        }

        // Cancel any thread currently running a connection
        if (readWriteThread != null) {
        	readWriteThread.cancel();
        	readWriteThread = null;
        }

//        ////////////////////////////////////////////////////////////////
//        String action = "android.bluetooth.device.action.UUID";
//        IntentFilter filter = new IntentFilter(action);
//        _context.registerReceiver(mReceiver, filter);
//        boolean b = device.fetchUuidsWithSdp();
        ////////////////////////////////////////////////////////////////////////

       // Start the thread to connect with the given device
        mConnectingThread = new ConnectingThread(device);
        mConnectingThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void beginReadAndWrite(BluetoothSocket socket, BluetoothDevice device) {
        //if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectingThread != null) {
        	mConnectingThread.cancel(); 
        	mConnectingThread = null;
        }

        // Cancel any thread currently running a connection
        if (readWriteThread != null) {
        	readWriteThread.cancel(); 
        	readWriteThread = null;
        }


        // Start the thread to manage the connection and perform transmissions
        readWriteThread = new ReadWriteThread(socket);                                      //启动读写线程
        readWriteThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);         //提醒ui ，已经和设备完成连接
        OBDDevice obdDevice = new OBDDevice();
        obdDevice.setDeviceName(device.getName());
        obdDevice.setDeviceAddress(device.getAddress());
        msg.obj = obdDevice;
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        //if (D) Log.d(TAG, "stop");
        if (mConnectingThread != null) {
        	mConnectingThread.cancel();
        	mConnectingThread = null;
        }
        if (readWriteThread != null) {
        	readWriteThread.cancel(); 
        	readWriteThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
    	ReadWriteThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = readWriteThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
        // Send a failure back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_CONNECT_FAILT);
        msg.obj = "无法连接设备";
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
    	//Log.e("testthread", "connecte bt lost...蓝牙断开");
        setState(STATE_LISTEN);
        // Send a failure message back to the Activity
        mHandler.sendEmptyMessage(MESSAGE_CONNECT_LOST);
    }

    private void closeConn(BluetoothSocket socket) {
    	if( socket != null) {
    		try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	socket = null;
    }
 
    /**
     * 连接蓝牙设备
     */
    private class ConnectingThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        //public UUID uuid = MY_UUID;
 
        public ConnectingThread(BluetoothDevice device/*, UUID aUuid*/) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
            	
            	// 若 设备已经配对了，并且 Android 版本大于等于 10，那么就使用不安全的连接。
            	// NOTE： 不安全连接连到的设备不会是已配对设备，所以若该设备没有配对过，那么就用安全连接去配对。
            	// 		 安全连接会让用户输入 PIN 码。只有输入正确的 PIN 码才会在认为该设备已配。
            	if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1 
            			&& device.getBondState() == BluetoothDevice.BOND_BONDED ) {
            		tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            	} else {
            		tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            	}
				      
            } catch (Exception e) {
                Log.e(TAG, "create() failed", e);
                closeConn(tmp);
                connectionFailed();
            }
            mmSocket = tmp;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            
            if(mmSocket==null){
            	connectionFailed();
            	MyBluetoothService.this.start();
                return;
            }
           
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();                    //这步过后已说明连接成功
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                closeConn(mmSocket);
                // Start the service over to restart listening mode
                MyBluetoothService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (MyBluetoothService.this) {
            	mConnectingThread = null;            //连接完成后，将线程置空
            }

            // Start the connected thread         //启动读写线程
            beginReadAndWrite(mmSocket, mmDevice);
        }

        public void cancel() {
        	closeConn(mmSocket);
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.    
     * 读写线程
     */
    private class ReadWriteThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private StringBuffer sb;
        
        public ReadWriteThread(BluetoothSocket socket) {
            //Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            sb = new StringBuffer();
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Log.e(TAG, "temp sockets not created", e);
            	closeConn(mmSocket);
            	connectionLost();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String itemStr = new String(buffer,0,bytes);
                    //Log.d(TAG, "back:"+itemStr);
                    if(itemStr.indexOf("\r\n")!=-1){
                    	String lastPart = "";
                     	String[] ends = itemStr.split("\r\n");
                     	for(int i=0;i<ends.length;i++){
                     		if(i==0){                      //第一个
                     			sb.append(ends[i]);                   	
                             	String endStr1 = sb.toString().trim();
                     			mHandler.obtainMessage(MESSAGE_READ,endStr1).sendToTarget();
                     		}else if(i== (ends.length-1)){ //最后一个
                     			if((itemStr.length())>2&&(itemStr.indexOf("\r\n", itemStr.length()-3)!=-1)){  //最后的字符串是\r\n结束
                     				String endStr2 = ends[i];
                         			mHandler.obtainMessage(MESSAGE_READ,endStr2).sendToTarget();
                     			}else{
                     				lastPart = ends[i];
                     			}
                     		}else{                         //中间的
                     			String endStr3 = ends[i];
                     			mHandler.obtainMessage(MESSAGE_READ,endStr3).sendToTarget();
                     		}
                     	}
                     	sb = new StringBuffer();
                     	if(!"".equals(lastPart)){
                     		sb.append(lastPart);
                     	}

//                    	String strPart1 = itemStr.substring(0, itemStr.indexOf("\r\n"));
//                    	String strPart2 = itemStr.substring( itemStr.indexOf("\r\n")+2,itemStr.length());
//                    	sb.append(strPart1);                   	
//                    	String endStr = sb.toString().trim();
//                    	//sb.append(itemStr);                   	
//                    	//String endStr = sb.toString().trim();
//                    	//Log.d(TAG, "back:end..............."+endStr);
//                    	/*if (endStr.indexOf("MIL")!=-1){
//                    		Log.d(TAG, "back:end..............."+endStr);
//                    	}*/              	
//                    	mHandler.obtainMessage(MESSAGE_READ,endStr).sendToTarget();
//                    	sb = new StringBuffer();
//                    	sb.append(strPart2);
                    }else{
                    	sb.append(itemStr);
                    }
                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(BaseConnectOBDActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (Exception e) {
                    //Log.e(TAG, "disconnected", e);
                	closeConn(mmSocket);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
        	if( buffer != null && buffer.length > 0 ) {
        		TongGouApplication.showLog( "send to OBD : " + new String(buffer) );
        	} else {
        		TongGouApplication.showLog( "send to OBD : empty return" );
        		return;
        	}
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, buffer).sendToTarget();
            } catch (IOException e) {
                //Log.e(TAG, "Exception during write", e);
            	closeConn(mmSocket);
            	connectionLost();
            }
        }

        public void cancel() {
        	closeConn(mmSocket);
        }
    }
}
