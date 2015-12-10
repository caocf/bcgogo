package com.tonggou.andclient.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.tonggou.andclient.network.Network;

public class BitmapCache {
	static private BitmapCache cache;
	/** 用于Chche内容的存储 */
	private Hashtable<String, MyBitmapSoftRef> hashRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class MyBitmapSoftRef extends SoftReference<Bitmap> {
		private String _key = "";

		public MyBitmapSoftRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	private BitmapCache() {
		hashRefs = new Hashtable<String, MyBitmapSoftRef>();
		q = new ReferenceQueue<Bitmap>();
	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;
	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	public void addCacheBitmap(Bitmap bmp, String key) {
		cleanCache();// 清除垃圾引用
		MyBitmapSoftRef ref = new MyBitmapSoftRef(bmp, q, key);
		hashRefs.put(key, ref);
	}

	/**
	 * 依据所指定的图片url，重新获取相应Bitmap对象的实例
	 */
	public Bitmap getBitmap(String urlKey) {
		Bitmap bmp = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (urlKey!=null&&hashRefs.containsKey(urlKey)) {
			MyBitmapSoftRef ref = (MyBitmapSoftRef) hashRefs.get(urlKey);
			bmp = (Bitmap) ref.get();
		}
		return bmp;
	}

	private void cleanCache() {
		MyBitmapSoftRef ref = null;
		while ((ref = (MyBitmapSoftRef) q.poll()) != null) {
			hashRefs.remove(ref._key);
		}
	}

	/**
	 * 清除Cache内的全部内容
	 */
	public void clearCache() {
		cleanCache();
		hashRefs.clear();
		System.gc();
		System.runFinalization();
	}



	public Bitmap getPicture(String portraitUrl,Context con) {
		Bitmap resultBitmap = getBitmap(portraitUrl);       //先去缓存取
		if(resultBitmap!=null){
			return resultBitmap;
		}

		String externalState = android.os.Environment.getExternalStorageState();
		if (externalState.equals(Environment.MEDIA_MOUNTED)) {       // 可用
			resultBitmap = readPictureFromSDCard(portraitUrl);
			if (resultBitmap != null) {
				addCacheBitmap(resultBitmap, portraitUrl);  //加到缓存
				return resultBitmap;
			} else {
				resultBitmap = writePicture(portraitUrl,con);
				if(resultBitmap!=null){
					addCacheBitmap(resultBitmap, portraitUrl);  //加到缓存
				}else{
					//扩展卡出问题时直接去网取
					resultBitmap = getPictureFromNetwork(portraitUrl,con);                  //网络取
					if(resultBitmap!=null){
						addCacheBitmap(resultBitmap, portraitUrl);  //加到缓存
					}
				}
				return resultBitmap;
			}
		}else{  // 不可用
			resultBitmap = getPictureFromNetwork(portraitUrl,con);                  //网络取
			if(resultBitmap!=null){
				addCacheBitmap(resultBitmap, portraitUrl);  //加到缓存
			}	 
			return resultBitmap;
		} 
	}




	/**
	 * 从扩展卡中读出图片
	 * @param url
	 * @return
	 */
	private Bitmap readPictureFromSDCard(String url) {
		if (url == null || "".equals(url)) {
			return null;
		}
		String s1 = url;
		String s2 = s1.substring(0, s1.length() - 1);
		try {
			String s4 = URLEncoder.encode(s2, "UTF-8");
			return BitmapFactory.decodeFile(android.os.Environment.getExternalStorageDirectory() + "/.tonggou/cache/" + s4 + "k");
		} catch (IOException e) {
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}


	/**
	 * 从网络读出图片
	 * @param portraitUrl
	 * @param type
	 * @return
	 */
	private Bitmap getPictureFromNetwork(String portraitUrl,Context cont) {
		if (portraitUrl != null && portraitUrl.length() > 5 && portraitUrl.startsWith("http://")) {
			byte[] portraitByte;
			portraitByte = Network.getNetwork(cont).httpGetUrlAsByte(portraitUrl);
			if (portraitByte != null) {
				try {
					Bitmap portraitBitmap = BitmapFactory.decodeByteArray(portraitByte, 0, portraitByte.length);
					if (portraitBitmap != null) {
						return portraitBitmap;
					}
				} catch (OutOfMemoryError e) {
				}
			}
		}
		return null;
	}

	/**
	 * 通过网络去取，然后写进扩展卡
	 * @param url
	 * @return
	 */
	private Bitmap writePicture(String url,Context cont) {
		if (url == null || "".equals(url)) {
			return null;
		}
		String s1 = url;
		String s2 = s1.substring(0, s1.length() - 1);
		Bitmap anotherBitmap = null;
		if (url != null && url.length() > 5 && url.startsWith("http://")) {
			File portraitByte = Network.getNetwork(cont).readPicFromNetwork(url,s2);
			if (portraitByte != null ) {
				// 读
				try {
					Bitmap localFile = BitmapFactory.decodeFile(portraitByte.getAbsolutePath());   //!!!!!!!!!!!!!!!有可能内存溢出
					return localFile;
				} catch (OutOfMemoryError e) {
					return null;
				}
			}
		}
		return anotherBitmap;
	}

	public static boolean movePicToDisplay2(String needUrl) {
		FileOutputStream fileOS = null;
		try {
			if (needUrl != null) {
				String s1 = needUrl;
				String s2 = s1.substring(0, s1.length() - 1);	
				
				String s4 = URLEncoder.encode(s2, "UTF-8");
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(android.os.Environment.getExternalStorageDirectory() + "/.tonggou/cache/" + s4 + "k"));        
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);       		       
				byte[] temp = new byte[1024];        
				int size = 0;        
				while ((size = in.read(temp)) != -1) {        
					out.write(temp, 0, size);        
				}        
				in.close();        

				byte[] tempPic = out.toByteArray();     

				File kaikaiPath = new File(android.os.Environment.getExternalStorageDirectory() + "/.tonggou");
				if (!kaikaiPath.exists()) {
					kaikaiPath.mkdir();
				}
				// 写
				File f2 = new File(android.os.Environment.getExternalStorageDirectory() + "/.tonggou/buffer.jpg");
				fileOS = new FileOutputStream(f2);
				fileOS.write(tempPic);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		} catch (OutOfMemoryError er) {
			return false;
		}  finally {
			if (fileOS != null) {
				try {
					fileOS.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
