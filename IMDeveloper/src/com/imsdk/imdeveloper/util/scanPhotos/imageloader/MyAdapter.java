package com.imsdk.imdeveloper.util.scanPhotos.imageloader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.util.cropImage.CropHelper;
import com.imsdk.imdeveloper.util.cropImage.CropParams;
import com.imsdk.imdeveloper.util.scanPhotos.utils.CommonAdapter;

public class MyAdapter extends CommonAdapter<String>
{

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();
	private boolean isneedTakePhoto ;
	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	private Activity context;
	private CropParams mCropParams;
	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath,boolean isneedTakePhoto,CropParams mCropParams)
	{
		super(context, mDatas, itemLayoutId);
		this.context = (Activity) context;
		this.mDirPath = dirPath;
		this.isneedTakePhoto = isneedTakePhoto;
		this.mCropParams = mCropParams;
		
	}

	@Override
	public void convert(final com.imsdk.imdeveloper.util.scanPhotos.utils.ViewHolder helper, final String item, final int position)
	{
		if (isneedTakePhoto&&position==0) {
			helper.setImageResource(R.id.id_item_image, R.drawable.tk_photo);
			helper.setImageVisibility(R.id.id_item_select,View.GONE);
		}else {
			helper.setImageVisibility(R.id.id_item_select,View.GONE);
			//设置no_pic
			helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
			//设置no_selected
			helper.setImageResource(R.id.id_item_select,
							R.drawable.picture_unselected);
			//设置图片
			helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
//			Uri uri = Uri.fromFile(	new File(mDirPath + "/" + item));
//			IMApplication.imageLoader.displayImage(uri.toString(), (ImageView)helper.getView(R.id.id_item_image),IMApplication.options);
		}

		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener()
		{
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v)
			{

				if (isneedTakePhoto&&position==0)
				{
					Intent intent = CropHelper.buildCaptureIntent(mCropParams.temUri);
					context.startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
				}else {
					// 已经选择过该图片
					if (mSelectedImage.contains(mDirPath + "/" + item))
					{
						
						
					} else
						// 未选择该图片
					{
						mSelectedImage.add(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
						mSelectedImage.remove(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.picture_unselected);
						mImageView.setColorFilter(null);
						
						File file = new File(mDirPath + "/" + item);
						Uri mUri = Uri.fromFile(file);
						if(null == mUri)return;
						Intent intent = new Intent();
						intent.setAction("com.android.camera.action.CROP");
						intent.setDataAndType(mUri, "image/*");// mUri是已经选择的图片Uri
						intent.putExtra("crop", "true");
						intent.putExtra("aspectX", 1);// 裁剪框比例
						intent.putExtra("aspectY", 1);
						intent.putExtra("outputX", 500);// 输出图片大小
						intent.putExtra("outputY", 500);
						intent.putExtra("return-data", false)
				          //.setType(params.type)
		                .putExtra("scale", true)
		                .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
		                .putExtra("noFaceDetection", true)
		                .putExtra("scaleUpIfNeeded", true)
						.putExtra(MediaStore.EXTRA_OUTPUT, mCropParams.temUri);
						context.startActivityForResult(intent, CropHelper.REQUEST_CROP);
						
					}
					
				}

			}
		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item))
		{
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}
}
