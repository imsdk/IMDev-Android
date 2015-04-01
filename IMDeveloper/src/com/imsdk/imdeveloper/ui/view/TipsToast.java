package com.imsdk.imdeveloper.ui.view;

import com.imsdk.imdeveloper.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TipsToast extends Toast {
	public TipsToast(Context context) {
		super(context);
	}

	public static TipsToast makeText(Context context, CharSequence text, int duration) {
		TipsToast result = new TipsToast(context);

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.view_tips, null);
		TextView textView = (TextView) view.findViewById(R.id.tips_msg);

		textView.setText(text);
		result.setView(view);

		// setGravity方法用于设置位置，此处为垂直居中
		result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		result.setDuration(duration);

		return result;
	}

	public static TipsToast makeText(Context context, int resId, int duration)
			throws Resources.NotFoundException {
		return makeText(context, context.getResources().getText(resId), duration);
	}

	public void setIcon(int iconResId) {
		if (getView() == null) {
			throw new RuntimeException(
					"This Toast was not created with Toast.makeText()");
		}

		ImageView imageView = (ImageView) getView().findViewById(R.id.tips_icon);

		if (imageView == null) {
			throw new RuntimeException(
					"This Toast was not created with Toast.makeText()");
		}

		imageView.setImageResource(iconResId);
	}

	@Override
	public void setText(CharSequence s) {
		if (getView() == null) {
			throw new RuntimeException(
					"This Toast was not created with Toast.makeText()");
		}

		TextView textView = (TextView) getView().findViewById(R.id.tips_msg);

		if (textView == null) {
			throw new RuntimeException(
					"This Toast was not created with Toast.makeText()");
		}

		textView.setText(s);
	}
}
