package com.imsdk.imdeveloper.app;

import imsdk.data.IMSDK;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.util.FileUtil;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class IMApplication extends Application {
	/**
	 * 预定义的头像
	 */
	public static final int[] heads = { R.drawable.h0, R.drawable.h1, R.drawable.h2,
			R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6, R.drawable.h7,
			R.drawable.h8, R.drawable.h9, R.drawable.h10, R.drawable.h11,
			R.drawable.h12, R.drawable.h13, R.drawable.h14, R.drawable.h15,
			R.drawable.h16, R.drawable.h17, R.drawable.h18 };

	public static String sCustomUserID;

	private NotificationManager mNotificationManager;

	// 图片下载初始化
	public static ImageLoader sImageLoader = ImageLoader.getInstance();
	public static DisplayImageOptions sDisplayImageOptions;

	@Override
	public void onCreate() {
		super.onCreate();
		
		IMSDK.init(getApplicationContext(), IMConfiguration.sAppKey);
		
		initData();
	}

	private void initData() {
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		FileUtil.getInstance().initDirs("imsdk", "data", this);
		initImageLoader();
	}

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		}

		return mNotificationManager;
	}

	public void initImageLoader() {
		/********** 异步下载图片缓存类 初始化 */
		IMApplication.initImageLoader(getApplicationContext());

		sDisplayImageOptions = new DisplayImageOptions.Builder()
				// .showImageOnLoading(R.drawable.h1)
				// 加载等待 时显示的图片
				// .showImageOnLoading(R.drawable.ic_stub) // resource or
				// drawable
				// .showImageForEmptyUri(R.drawable.ic_empty) // resource or
				// drawable
				.showImageOnFail(R.drawable.h10)
				// resource or drawable
				// .resetViewBeforeLoading(false) // default
				// .delayBeforeLoading(1000)
				// .cacheInMemory(false) // default
				// .cacheOnDisk(false) // default
				// .preProcessor(...)
				// .postProcessor(...)
				// .extraForDownloader(...)
				.considerExifParams(true)
				// default
				// .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) //
				// default
				// .bitmapConfig(Bitmap.Config.ARGB_8888) // default
				// .decodingOptions(...)
				// .displayer(new SimpleBitmapDisplayer()) // default
				// .handler(new Handler()) // default

				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				/**
				 * .displayer(new RoundedBitmapDisplayer(20))
				 **/
				.build();
	}

	public static void initImageLoader(Context context) {
		DiskCache diskCache = new UnlimitedDiscCache(FileUtil.getInstance()
				.getImagePath().getAbsoluteFile(), null, new Md5FileNameGenerator());

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// Not
				// .memoryCacheExtraOptions(480, 800) // default = device screen
				// dimensions
				// .diskCacheExtraOptions(480, 800, null)
				// .taskExecutor(...)
				// .taskExecutorForCachedImages(...)
				// .threadPoolSize(3) // default
				// .threadPriority(Thread.NORM_PRIORITY - 2) // default
				// .tasksProcessingOrder(QueueProcessingType.FIFO) // default
				// .denyCacheImageMultipleSizesInMemory()
				.diskCache(diskCache).memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(30) // default
				// .diskCache(new UnlimitedDiscCache(cacheDir)) // default
				// .diskCacheSize(50 * 1024 * 1024)
				// .diskCacheFileCount(100)
				// .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				// // default
				// .imageDownloader(new BaseImageDownloader(context)) // default
				// .imageDecoder(new BaseImageDecoder()) // default
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// // default
				.writeDebugLogs().build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}
}