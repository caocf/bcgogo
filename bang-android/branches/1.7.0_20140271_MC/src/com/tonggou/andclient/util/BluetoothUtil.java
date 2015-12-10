package com.tonggou.andclient.util;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class BluetoothUtil {
	public static final String PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
	    
	    public static boolean createBond(BluetoothDevice btDevice) throws Exception {
	        Class<?> btClass = BluetoothDevice.class;
	        Method createBondMethod = btClass.getMethod("createBond", new Class[]{});
	        Boolean returnValue = (Boolean)createBondMethod.invoke(btDevice, new Object[]{});
	        return returnValue.booleanValue();
	    }
	    
	    public static boolean removeBond(BluetoothDevice btDevice) throws Exception {
	        Class<?> btClass = BluetoothDevice.class;
	        Method removeBondMethod = btClass.getMethod("removeBond", new Class[]{});
	        Boolean returnValue = (Boolean)removeBondMethod.invoke(btDevice, new Object[]{});
	        return returnValue.booleanValue();
	    }
	    
	    public static boolean setPin(BluetoothDevice btDevice, String str) throws Exception {
	        Boolean returnValue = Boolean.valueOf(false);
	        try {
	            Class<?> btClass = BluetoothDevice.class;
	            Method setPinMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
	            returnValue = (Boolean)setPinMethod.invoke(btDevice, new Object[]{str.getBytes()});
	            Log.d("returnValue", returnValue.toString());
	        } catch(SecurityException e) {
	            Log.e("returnValue", e.getMessage());
	            e.printStackTrace();
	        } catch(IllegalArgumentException e) {
	            Log.e("returnValue", e.getMessage());
	            e.printStackTrace();
	        } catch(Exception e) {
	            Log.e("returnValue", e.getMessage());
	            e.printStackTrace();
	        }
	        return returnValue.booleanValue();
	    }
	    
	    public static boolean cancelPairingUserInput(BluetoothDevice btDevice) throws Exception {
	        Class<?> btClass = BluetoothDevice.class;
	        Method cancelPairingUserInputMethod = btClass.getMethod("cancelPairingUserInput", new Class[]{});
	        Boolean returnValue = (Boolean)cancelPairingUserInputMethod.invoke(btDevice, new Object[]{});
	        return returnValue.booleanValue();
	    }
	    
	    public static boolean cancelBondProcess(BluetoothDevice btDevice) throws Exception {
	        Boolean returnValue = Boolean.valueOf(false);
	        try {
	            Class<?> btClass = BluetoothDevice.class;
	            Method cancelBondProcessMethod = btClass.getMethod("cancelBondProcess", new Class[]{});
	            returnValue = (Boolean)cancelBondProcessMethod.invoke(btDevice, new Object[]{});
	        } catch(SecurityException e) {
	            Log.e("returnValue", e.getMessage());
	            e.printStackTrace();
	        } catch(IllegalArgumentException e) {
	            Log.e("returnValue", e.getMessage());
	            e.printStackTrace();
	        } catch(Exception e) {
	            Log.e("returnValue", e.getMessage());
	            e.printStackTrace();
	        }
	        return returnValue.booleanValue();
	    }
	    
	    public static boolean setPairingConfirmation(BluetoothDevice btDevice, boolean confirm) throws Exception {
	    	Class<?> btClass = BluetoothDevice.class;
	        Method setPairingConfirmationMethod = btClass.getMethod("setPairingConfirmation", new Class[]{boolean.class});
	        Boolean returnValue = (Boolean)setPairingConfirmationMethod.invoke(btDevice, new Object[]{confirm});
	        return returnValue.booleanValue();
	    }
}
