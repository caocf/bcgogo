package com.tonggou.andclient.myview;

import java.io.BufferedInputStream;

import com.tonggou.andclient.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/***
 * 自定义控件
 * 
 * 
 *         在这里我要说明一点 我们在创建RectF矩形的时候，
 * 
 *         参照物原点是所在"父控件的左上角".
 * 
 */
public class ActionBar extends LinearLayout implements OnClickListener {

	private ImageView tv1;
	private ImageView tv2;
	private ImageView tv3;
	//private ImageView tv4;
	private Paint paint;// 画笔
	private RectF curRectF;// draw当前bar
	private RectF tarRectF;// draw被点击bar

	private final int space_x = 0;// 相当于pading.
	private final int space_y = 0;// 相当于pading
	private final double step = 32;// 速度step.

	private Action action;// 动作

	public interface Action {
		void action();
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public ActionBar(Context context) {
		super(context);
	}

	/***
	 * 构造方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		LayoutInflater.from(context).inflate(R.layout.home_tab, this, true);
		paint = new Paint();
		paint.setAntiAlias(true);
		tv1 = (ImageView) findViewById(R.id.tv1);
		tv2 = (ImageView) findViewById(R.id.tv2);
		tv3 = (ImageView) findViewById(R.id.tv3);
		//tv4 = (ImageView) findViewById(R.id.tv4);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		//tv4.setOnClickListener(this);
		curRectF = null;
		tarRectF = null;
		
		
	}

	/***
	 * invalidate()：调用这个方法会执行onDraw()方法，但是前提是：自己把invalidate()方法执行结束在进行执行.
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		paint.setColor(Color.RED);
		// 如果当前curRectF=null，也就是第一次访问，则默认为draw第一个bar
		if (curRectF == null)
			curRectF = new RectF(tv1.getLeft() + space_x, tv1.getTop()
					+ space_y, tv1.getRight() - space_x, tv1.getBottom()
					- space_y);

		// 第一次方位tarRectF=null，默认为draw
		if (tarRectF == null)
			tarRectF = new RectF(tv1.getLeft() + space_x, tv1.getTop()
					+ space_y, tv1.getRight() - space_x, tv1.getBottom()
					- space_y);
		/***
		 * 作用：如果在这个范围内则，以这个为最终位置，（不明的白的话，你可以把这个注释运行下你就知道why了.）
		 */
		if (Math.abs(curRectF.left - tarRectF.left) < step) {
			curRectF.left = tarRectF.left;
			curRectF.right = tarRectF.right;
		}

		/***
		 * 说明目标在当前的左侧,需要向左移动（每次矩形移动step，则进行invalidate（）,从新进行移动...）
		 */
		if (curRectF.left > tarRectF.left) {
			curRectF.left -= step;
			curRectF.right -= step;
			invalidate();// 继续刷新，从而实现滑动效果，每次step32.
		}
		/***
		 * 说明目标在当前的右侧,需要向右移动（每次矩形移动step，则进行invalidate（）,从新进行移动...）
		 */
		else if (curRectF.left < tarRectF.left) {
			curRectF.left += step;
			curRectF.right += step;
			invalidate();
		}
		// canvas.drawRect(curRectF, paint);
		// 参数，矩形，弧度，画笔
		canvas.drawRoundRect(curRectF, 5, 5, paint);
		
	}

	/****
	 * 这里要记录目标矩形的坐标
	 */
	@Override
	public void onClick(View v) {
		tarRectF.left = v.getLeft() + space_x;
		tarRectF.right = v.getRight() - space_x;
		invalidate();// 刷新

		System.out.println("tarRectF.top=" + tarRectF.top + ",v.getTop()="
				+ v.getTop() + ", v.getBottom()" + v.getBottom());
	}

}
