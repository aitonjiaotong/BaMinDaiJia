package com.aiton.administrator.shane_library.shane.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiton.administrator.shane_library.R;


public final class ToastUtils {

	private static Toast toast;
	public  static void showToast(Context context, String text) {
//		Toast toast = Toast.makeText(this, "toast", Toast.LENGTH_LONG);
		// ���֮ǰ�����񣬾�ȡ��
		if (toast != null){
			toast.cancel();
		}
		toast = new Toast(context);
		View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
		TextView tvText = (TextView) view.findViewById(R.id.textView1);
		tvText.setText(text);
		// toast.setBackground();
		toast.setView(view);
		toast.setGravity(Gravity.TOP, 0, 60);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
	
	private ToastUtils(){}
}
