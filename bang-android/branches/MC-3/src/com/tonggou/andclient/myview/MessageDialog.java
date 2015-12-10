package com.tonggou.andclient.myview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonggou.andclient.R;

/**
 * 信息确认 对话框
 * 
 * <p>默认为 错误对话框，可以通过 设置 {@link DialogType} 来设置为正确的消息对话框</p>
 * 
 * @author lwz
 *
 */
public class MessageDialog extends AbsCustomAlertDialog implements View.OnClickListener {
	
	/**
	 * 对话框类型
	 * 	<ul>
	 * 		<li>NEGATIVE 错误消息对话框
	 * 		<li>POSITIVE 正确消息对话框
	 * </ul>
	 * @author lwz
	 *
	 */
	public static enum DialogType {
		/** 错误消息对话框 */
		NEGATIVE,
		/** 正确消息对话框 */
		POSITIVE,
	}
	
	private DialogType type = DialogType.NEGATIVE;
	
	public MessageDialog(Context context) {
		super(context);
	}
	
	/**
	 * 
	 * @param context
	 * @param type	{@link DialogType}
	 */
	public MessageDialog(Context context, DialogType type) {
		this(context);
		this.type = type;
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		View view = View.inflate(getContext(), R.layout.widget_dialog_error, null);
		ImageView icon = (ImageView) view.findViewById(R.id.dialog_icon);
		icon.setImageResource( 
				type == DialogType.NEGATIVE ? R.drawable.ic_negative : R.drawable.ic_positive  );
		TextView msgText =(TextView) view.findViewById(R.id.message);
		msgText.setText(msg);
		view.findViewById(R.id.btn_ok).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		dismissDialog(this);
	}
	
	
}
