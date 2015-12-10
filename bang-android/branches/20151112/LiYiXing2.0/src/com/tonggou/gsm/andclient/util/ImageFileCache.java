package com.tonggou.gsm.andclient.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
/**
 * ImageFileCache
 * @author peter
 *
 */
public class ImageFileCache {
	private static final String IMAGE_CACHE_DIR = "ImgCach";
	private static final String IMAGE_SUFFIX_CACH = ".cach";
	private static final int MB = 1024 * 1024;
	private static final int IMAGE_CACHE_SIZE = 10;
	private static final int FREE_SD_SPACE_NEEDED_TO_IMAGE_CACHE = 10;

	public ImageFileCache() {
		cleanCacheDir(getCacheDirectory());
	}

	public Bitmap getBitmap(final String uri) {
		final String bmpPath = getCacheDirectory() + "/" + getBitmapNameFromURI(uri);
		File file = new File(bmpPath);

		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(bmpPath);
			if (bmp == null) {
				file.delete();
			} else {
				updateFileModifiTime(bmpPath);
				return bmp;
			}
		}

		return null;
	}

	public void saveBitmap(Bitmap bmp, String bmpUri) {
		if (bmp == null) {
			return;
		}

		if (FREE_SD_SPACE_NEEDED_TO_IMAGE_CACHE > availableSpaceOnSD()) {
			return;
		}

		String bmpNameStr = getBitmapNameFromURI(bmpUri);
		String bmpCacheDirStr = getCacheDirectory();
		File bmpDirfile = new File(bmpCacheDirStr);
		if (!bmpDirfile.exists())
			bmpDirfile.mkdirs();

		File bmpDirectPathfile = new File(bmpCacheDirStr +"/" + bmpNameStr);
		try {
			bmpDirectPathfile.createNewFile();
			OutputStream bmpDirOutStream = new FileOutputStream(bmpDirectPathfile);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bmpDirOutStream);
			bmpDirOutStream.flush();
			bmpDirOutStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* Calculate the file size of the storage directory，
	* when the file size is greater than the provisions of the CACHE_SIZE or sdcard of the remaining space is less than FREE_SD_SPACE_NEEDED_TO_CACHE
	* Then delete the 40% file that has not been used recently
	*/
	private boolean cleanCacheDir(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		if (files == null) {
			return true;
		}

		if (!android.os.Environment.getExternalStorageState().equals(
			android.os.Environment.MEDIA_MOUNTED)) {
				return false;
			}

			int dirSize = 0;
			for (int i = 0; i < files.length; i++) {

				if (files[i].getName().contains(IMAGE_SUFFIX_CACH)) {
					dirSize += files[i].length();
				}
			}

			if (dirSize > IMAGE_CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_IMAGE_CACHE > availableSpaceOnSD()) {
				int removeFactor = (int) ((0.4 * files.length) + 1);
				Arrays.sort(files, new SortFileByLastModifTime());
				for (int i = 0; i < removeFactor; i++) {
					if (files[i].getName().contains(IMAGE_SUFFIX_CACH)) {
						files[i].delete();
					}
				}
			}

			if (availableSpaceOnSD() <= IMAGE_CACHE_SIZE) {
				return false;
			}

			return true;
	}

	/** Last modification time **/
	public void updateFileModifiTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/** Calculate the file size of the storage directory on SD Card **/
	private int availableSpaceOnSD() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdAvailableMB = ((double)stat.getAvailableBlocks() * (double)stat.getBlockSize()) / MB;

		return (int) sdAvailableMB;
	}

	/** Get Bitmap name from URI **/
	private String getBitmapNameFromURI(String uri) {
		String[] strs = uri.split("/");

		return strs[strs.length - 1] + IMAGE_SUFFIX_CACH;
	}

	/** Get cache directory **/
	private String getCacheDirectory() {
		String dir = getSDCardPath() + "/" + IMAGE_CACHE_DIR;

		return dir;
	}

	/** Get SD Card path **/
	private String getSDCardPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
		android.os.Environment.MEDIA_MOUNTED);

		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}

		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	/**
	* Sort by the last modification time of the file
	*/
	private class SortFileByLastModifTime implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}