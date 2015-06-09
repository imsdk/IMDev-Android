package com.imsdk.imdeveloper.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Build.VERSION_CODES;

public class FileUtil {
	/**
	 * 删除文件
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名，要在系统内保持唯一
	 * @return boolean 存储成功的标志
	 */
	public static boolean deleteFile(Context context, String fileName) {
		return context.deleteFile(fileName);
	}

	/**
	 * 文件是否存在
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static boolean exists(Context context, String fileName) {
		return new File(context.getFilesDir(), fileName).exists();
	}

	/**
	 * 存储文本数据
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名，要在系统内保持唯一
	 * @param content
	 *            文本内容
	 * @return boolean 存储成功的标志
	 */
	public static boolean writeFile(Context context, String fileName, String content) {
		boolean success = false;
		FileOutputStream fos = null;

		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] byteContent = content.getBytes();
			fos.write(byteContent);

			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return success;
	}

	/**
	 * 存储文本数据
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名，要在系统内保持唯一
	 * @param content
	 *            文本内容
	 * @return boolean 存储成功的标志
	 */
	public static boolean writeFile(String filePath, String content) {
		boolean success = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			byte[] byteContent = content.getBytes();
			fos.write(byteContent);

			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return success;
	}

	/**
	 * 读取文本数据
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名
	 * @return String, 读取到的文本内容，失败返回null
	 */
	public static String readFile(Context context, String fileName) {
		if (!exists(context, fileName)) {
			return null;
		}

		FileInputStream fis = null;
		String content = null;

		try {
			fis = context.openFileInput(fileName);
			if (fis != null) {

				byte[] buffer = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while (true) {
					int readLength = fis.read(buffer);
					if (readLength == -1)
						break;
					arrayOutputStream.write(buffer, 0, readLength);
				}
				fis.close();
				arrayOutputStream.close();
				content = new String(arrayOutputStream.toByteArray());

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			content = null;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 读取文本数据
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名
	 * @return String, 读取到的文本内容，失败返回null
	 */
	public static String readFile(String filePath) {
		if (filePath == null || !new File(filePath).exists()) {
			return null;
		}
		FileInputStream fis = null;
		String content = null;
		try {
			fis = new FileInputStream(filePath);
			if (fis != null) {

				byte[] buffer = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while (true) {
					int readLength = fis.read(buffer);
					if (readLength == -1)
						break;
					arrayOutputStream.write(buffer, 0, readLength);
				}
				fis.close();
				arrayOutputStream.close();
				content = new String(arrayOutputStream.toByteArray());

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			content = null;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 读取文本数据
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名
	 * @return String, 读取到的文本内容，失败返回null
	 */
	public static String readAssets(Context context, String fileName) {
		InputStream is = null;
		String content = null;
		try {
			is = context.getAssets().open(fileName);
			if (is != null) {

				byte[] buffer = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while (true) {
					int readLength = is.read(buffer);
					if (readLength == -1)
						break;
					arrayOutputStream.write(buffer, 0, readLength);
				}
				is.close();
				arrayOutputStream.close();
				content = new String(arrayOutputStream.toByteArray());

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			content = null;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 存储单个Parcelable对象
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名，要在系统内保持唯一
	 * @param parcelObject
	 *            对象必须实现Parcelable
	 * @return boolean 存储成功的标志
	 */
	public static boolean writeParcelable(Context context, String fileName,
			Parcelable parcelObject) {
		boolean success = false;
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			Parcel parcel = Parcel.obtain();
			parcel.writeParcelable(parcelObject,
					Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
			byte[] data = parcel.marshall();
			fos.write(data);

			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		return success;
	}

	/**
	 * 存储List对象
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名，要在系统内保持唯一
	 * @param list
	 *            对象数组集合，对象必须实现Parcelable
	 * @return boolean 存储成功的标志
	 */
	public static boolean writeParcelableList(Context context, String fileName,
			List<Parcelable> list) {
		boolean success = false;
		FileOutputStream fos = null;
		try {
			if (list instanceof List) {
				fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				Parcel parcel = Parcel.obtain();
				parcel.writeList(list);
				byte[] data = parcel.marshall();
				fos.write(data);

				success = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		return success;
	}

	/**
	 * 读取单个数据对象
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名
	 * @return Parcelable, 读取到的Parcelable对象，失败返回null
	 */
	public static Parcelable readParcelable(Context context, String fileName,
			ClassLoader classLoader) {
		Parcelable parcelable = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			fis = context.openFileInput(fileName);
			if (fis != null) {
				bos = new ByteArrayOutputStream();
				byte[] b = new byte[4096];
				int bytesRead;
				while ((bytesRead = fis.read(b)) != -1) {
					bos.write(b, 0, bytesRead);
				}

				byte[] data = bos.toByteArray();

				Parcel parcel = Parcel.obtain();
				parcel.unmarshall(data, 0, data.length);
				parcel.setDataPosition(0);
				parcelable = parcel.readParcelable(classLoader);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			parcelable = null;
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (bos != null)
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return parcelable;
	}

	/**
	 * 读取数据对象列表
	 * 
	 * @param context
	 *            程序上下文
	 * @param fileName
	 *            文件名
	 * @return List, 读取到的对象数组，失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static List<Parcelable> readParcelableList(Context context, String fileName,
			ClassLoader classLoader) {
		List<Parcelable> results = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			fis = context.openFileInput(fileName);
			if (fis != null) {
				bos = new ByteArrayOutputStream();
				byte[] b = new byte[4096];
				int bytesRead;
				while ((bytesRead = fis.read(b)) != -1) {
					bos.write(b, 0, bytesRead);
				}

				byte[] data = bos.toByteArray();

				Parcel parcel = Parcel.obtain();
				parcel.unmarshall(data, 0, data.length);
				parcel.setDataPosition(0);
				results = parcel.readArrayList(classLoader);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			results = null;
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (bos != null)
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return results;
	}

	public static boolean saveSerializable(Context context, String fileName,
			Serializable data) {
		boolean success = false;
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(context.openFileOutput(fileName,
					Context.MODE_PRIVATE));
			oos.writeObject(data);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	public static Serializable readSerialLizable(Context context, String fileName) {
		Serializable data = null;
		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(context.openFileInput(fileName));
			data = (Serializable) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return data;
	}

	/**
	 * 从assets里边读取字符串
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String getFromAssets(Context context, String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(context
					.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 复制文件
	 * 
	 * @param srcFile
	 * @param dstFile
	 * @return
	 */
	public static boolean copy(String srcFile, String dstFile) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {

			File dst = new File(dstFile);
			if (!dst.getParentFile().exists()) {
				dst.getParentFile().mkdirs();
			}

			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(dstFile);

			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return true;
	}

	public static final String imagePathName = "/image/";
	public static final String voicePathName = "/voice/";
	public static final String filePathName = "/file/";
	public static final String videoPathName = "/video/";
	private static File storageDir = null;
	private static FileUtil instance = null;
	private File voicePath = null;
	private File imagePath = null;
	private File videoPath = null;
	private File filePath;

	public static FileUtil getInstance() {
		if (instance == null) {
			instance = new FileUtil();
		}

		return instance;
	}

	public void initDirs(String firstDir, String secondDir, Context context) {
		this.voicePath = generateVoicePath(firstDir, secondDir, context);

		if (!this.voicePath.exists()) {
			this.voicePath.mkdirs();
		}

		this.videoPath = generateVideoPath(firstDir, secondDir, context);

		if (!this.videoPath.exists()) {
			this.videoPath.mkdirs();
		}

		this.imagePath = generateImagePath(firstDir, secondDir, context);

		if (!this.imagePath.exists()) {
			this.imagePath.mkdirs();
		}

		this.filePath = generateFiePath(firstDir, secondDir, context);

		if (!this.filePath.exists()) {
			this.filePath.mkdirs();
		}
	}

	public File getImagePath() {
		return this.imagePath;
	}

	public File getVoicePath() {
		return this.voicePath;
	}

	public File getFilePath() {
		return this.filePath;
	}

	public File getVideoPath() {
		return this.videoPath;
	}

	private static File generateFiePath(String firstDir, String secondDir,
			Context context) {
		String str = null;

		if (firstDir == null) {
			str = secondDir + "/file/";
		} else {
			str = firstDir + "/" + secondDir + "/file/";
		}

		return new File(getStorageDir(context), str);
	}

	private static File getStorageDir(Context context) {
		String path = null;
		if (storageDir == null) {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				
				if(Build.VERSION.SDK_INT >= VERSION_CODES.FROYO){
					
					if(context.getExternalCacheDir() == null){
						path = Environment.getExternalStorageDirectory().getAbsolutePath();
					}else{
						path = context.getExternalCacheDir().getAbsolutePath()
								.replace("cache", "");	
					}
				}else{
					path = Environment.getExternalStorageDirectory().getPath() + 
							"/Android/data/" + context.getPackageName();
				}
				
			} else {
				path = context.getCacheDir().getAbsolutePath().replace("cache", "");
			}

			File localFile = new File(path);

			if (!localFile.exists()) {
				localFile.mkdirs();
			}

			return localFile;
		}

		storageDir = context.getFilesDir();
		return storageDir;
	}

	private static File generateImagePath(String firstDir, String secondDir,
			Context context) {
		String str = null;

		if (firstDir == null) {
			str = secondDir + "/image/";
		} else {
			str = firstDir + "/" + secondDir + "/image/";
		}

		return new File(getStorageDir(context), str);
	}

	private static File generateVoicePath(String firstDir, String secondDir,
			Context context) {
		String str = null;

		if (firstDir == null) {
			str = secondDir + "/voice/";
		} else {
			str = firstDir + "/" + secondDir + "/voice/";
		}

		return new File(getStorageDir(context), str);
	}

	private static File generateVideoPath(String firstDir, String secondDir,
			Context context) {
		String str = null;

		if (firstDir == null) {
			str = secondDir + "/video/";
		} else {
			str = firstDir + "/" + secondDir + "/video/";
		}

		return new File(getStorageDir(context), str);
	}

	public static File getTempPath(File file) {
		File localFile = new File(file.getAbsoluteFile() + ".tmp");

		return localFile;
	}
}
