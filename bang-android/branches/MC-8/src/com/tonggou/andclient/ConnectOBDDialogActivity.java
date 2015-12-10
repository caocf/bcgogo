package com.tonggou.andclient;

import android.content.Intent;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.vo.OBDDevice;

/**
 * 连接 OBD 的Activity
 * <p>使用该 Activity 应该使用 startActivityForResult() 方法调用</p>
 * <p>当绑定 OBD 成功后，onActivityResult() 方法的 intent对象中会包含 车辆 VIN（可为 null）, 键为 EXTRA_VEHICLE_VIN</p>
 * @author lwz
 *
 */
public class ConnectOBDDialogActivity extends AbsScanObdDeviceDialogActivity {

	/**
	 * 车辆的 VIN,当成功绑定 OBD 后，会返回车辆 VIN
	 */
	public static final String EXTRA_VEHICLE_VIN = "extra_vehicle_vin";
	
	private OBDDevice mDevice;
	
	@Override
	public void onStateChange(int statusCode) {
		super.onStateChange(statusCode);
		TongGouApplication.showLog("onStateChange  " + statusCode);
	}

	@Override
	public void onSendOrderSuccess() {
		super.onSendOrderSuccess();
		TongGouApplication.showLog("onSendOrderSuccess");
	}
	
	@Override
	public void onReceiveResultSuccess(String result) {
		super.onReceiveResultSuccess(result);
		setScanDialogTitle("已经绑定  OBD");
		
		String vechicleVin = null;
		TongGouApplication.showLog("ODB发来消息：|" + result);
		// Log.i("Bluetooth thinks", "receive:  " + readMessage);
		// readMessage = "##VIN:";
		
		if ( result != null && result.contains("VIN:")) {
			String vehicleVin = result.substring(result.indexOf("VIN:") + 4);
			if (vehicleVin.indexOf("\r\n") != -1) {
				vehicleVin = vehicleVin.replaceAll("\r\n", "");
			}
			TongGouApplication.showLog("fvehicleVin = " + vehicleVin.trim());
			vechicleVin = vehicleVin.trim();
		}
		
		TongGouApplication.getInstance().notifyBindOBDSuccess(mDevice, vechicleVin);
		TongGouApplication.showLog(vechicleVin);
		Intent data = new Intent();
		data.putExtra(EXTRA_VEHICLE_VIN, vechicleVin);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	public void onConnectSuccess(OBDDevice device) {
		super.onConnectSuccess(device);
		mDevice = device;
		TongGouApplication.showLog("onConnectSuccess  device = " + device.toString());
	}

	@Override
	public void onConnectFailure(String msg) {
		super.onConnectFailure(msg);
		TongGouApplication.showLog("onConnectFailure  " + msg);
	}

	@Override
	public void onConnectLost() {
		super.onConnectLost();
		TongGouApplication.showLog("onConnectLost");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		TongGouApplication.getInstance().notifyBindOBDCancle();
	}
	
}
