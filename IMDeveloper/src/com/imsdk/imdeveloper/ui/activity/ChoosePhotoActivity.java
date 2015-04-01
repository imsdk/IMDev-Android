package com.imsdk.imdeveloper.ui.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.util.FileUtil;
import com.imsdk.imdeveloper.util.cropImage.CropHandler;
import com.imsdk.imdeveloper.util.cropImage.CropHelper;
import com.imsdk.imdeveloper.util.cropImage.CropParams;
import com.imsdk.imdeveloper.util.scanPhotos.bean.ImageFloder;
import com.imsdk.imdeveloper.util.scanPhotos.imageloader.ListImageDirPopupWindow;
import com.imsdk.imdeveloper.util.scanPhotos.imageloader.ListImageDirPopupWindow.OnImageDirSelected;
import com.imsdk.imdeveloper.util.scanPhotos.imageloader.MyAdapter;
import com.imsdk.imdeveloper.util.scanPhotos.utils.ImageLoader;

public class ChoosePhotoActivity extends Activity implements OnImageDirSelected,
		CropHandler {
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private MyAdapter mAdapter;

	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mTitleBack;

	private TextView mImageCount;

	private TextView mChooseDir;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();

			// 为View绑定数据
			data2View();

			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	private CropParams mCropParams;

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(ChoosePhotoActivity.this, "一张图片没扫描到", Toast.LENGTH_SHORT)
					.show();
		} else {
			mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".jpeg")) {
						return true;
					}

					return false;
				}
			}));
		}

		ArrayList<String> list = new ArrayList<String>();
		list.add("");

		if (mImgDir != null) {
			list.addAll(mImgs);
		}

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		Long time = System.currentTimeMillis();

		mCropParams = new CropParams(FileUtil.getInstance().getImagePath(), time
				+ ".jpg");

		if (mImgDir == null) {
			mAdapter = new MyAdapter(ChoosePhotoActivity.this, list,
					R.layout.grid_item, null, true, mCropParams);
		} else {
			mAdapter = new MyAdapter(ChoosePhotoActivity.this, list,
					R.layout.grid_item, mImgDir.getAbsolutePath(), true, mCropParams);
		}

		mGirdView.setAdapter(mAdapter);
		mChooseDir.setText("常用相册");
		mImageCount.setText(list.size() - 1 + "张");
	};

	/**
	 * 初始化展示文件夹的popupWindow
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), mImageFloders,
				LayoutInflater.from(ChoosePhotoActivity.this).inflate(
						R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});

		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_choosephoto);

		DisplayMetrics outMetrics = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			// 通知Handler扫描图片完成
			mHandler.sendEmptyMessage(0x110);
			return;
		}

		// 显示进度条
		new Thread(new Runnable() {
			@Override
			public void run() {
				String firstImage = null;
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver contentResolver = ChoosePhotoActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor cursor = contentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				while (cursor.moveToNext()) {
					// 获取图片的路径
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null) {
						firstImage = path;
					}

					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();

					if (parentFile == null) {
						continue;
					}

					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;

					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					String[] files = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".png")
									|| filename.endsWith(".jpeg")) {
								return true;
							}

							return false;
						}
					});

					if (files == null) {
						continue;
					}

					int picSize = files.length;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}

				cursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mTitleBack = (TextView) findViewById(R.id.title_back);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mBottomLy = (RelativeLayout) findViewById(R.id.layout_bottom);
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});

		mTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {
		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(ChoosePhotoActivity.this, mImgs, R.layout.grid_item,
				mImgDir.getAbsolutePath(), false, mCropParams);
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		CropHelper.handleResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onPhotoCropped(Uri uri) {
		Intent intent = new Intent();
		intent.putExtra("uri", uri);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onCropCancel() {
	}

	@Override
	public void onCropFailed(String message) {
		this.finish();
	}

	@Override
	public CropParams getCropParams() {
		return mCropParams;
	}

	@Override
	public Activity getContext() {
		return ChoosePhotoActivity.this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().onReleaseMemory();
	}
}
