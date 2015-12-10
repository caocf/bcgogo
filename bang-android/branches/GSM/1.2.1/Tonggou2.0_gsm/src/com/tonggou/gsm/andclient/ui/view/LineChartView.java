package com.tonggou.gsm.andclient.ui.view;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tonggou.gsm.andclient.R;

public class LineChartView extends View {

	private Grid mGrid;
	private float mAxisLabelHeight;
	private float mAxisLabelSeparation;
	
	private Paint mGridPaint;
	private float mGridThickness;
	private int mGridColor = Color.parseColor("#77777777");
	private Paint mAxisLabelPaint;
	private int mAxisLabelColor = Color.BLACK;
	private float mAxisLabelTextSize;
	private Paint mDataPaint;
	private int mDataColor = Color.parseColor("#0099CC");
	private float mDataPointThickness;
	private float mDataPointRadius;
	private Paint mSeriesPaint;
	private int mSeriesColor = Color.parseColor("#0099CC");
	private float mSeriesThickness;
	private Paint mDataLabelPaint;
	private float mDataLabelTextSize;
	private float mDataLabelWidth;
	private float mDataLabelTextPadding;
	private int mDataLabelColor = Color.BLACK;
	private Paint mDataLabelBackgroudPaint;
	private float mDataLabelBackgroundRadius;
	private int mDataLabelBackgroundColor = Color.parseColor("#44777777");
	
	private float[] mAxisXPositionsBuffer = new float[]{};
    private float[] mAxisYPositionsBuffer = new float[]{};
    private float[] mAxisXLinesBuffer = new float[]{};
    private float[] mAxisYLinesBuffer = new float[]{};
    
    private float[] mData = new float[]{};
    private float[] mSeriesLinesBuffer = new float[]{};
    private RectF mTextBackgroundRect = new RectF();
//    private Path mSeriesPath = new Path();
    private String[] mAxisXLabels = new String[]{};
    
	public LineChartView(Context context) {
		this(context, null);
	}
	
	public LineChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		mGrid = new Grid();
		mGrid.width = getResources().getDimensionPixelOffset(R.dimen.chart_cell_width);
		mGrid.column = 12;
		mGrid.row = 10;
		
		// 只有加上这句，才会将虚线显示出来，与 android:hardwareAccelerated="false" 效果一样
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
		
		mGridThickness = getResources().getDimension(R.dimen.chart_grid_thickness);
		mDataPointThickness = getResources().getDimension(R.dimen.chart_data_point_thickness);
		mDataPointRadius = getResources().getDimension(R.dimen.chart_data_point_radius);
		mSeriesThickness = getResources().getDimension(R.dimen.chart_data_series_thickness);
		mDataLabelTextSize = getResources().getDimension(R.dimen.chart_data_label_text_size);
		mDataLabelTextPadding = getResources().getDimension(R.dimen.chart_data_label_text_padding);
		mDataLabelBackgroundRadius = getResources().getDimension(R.dimen.chart_data_label_text_bg_radius);
		mAxisLabelTextSize = getResources().getDimension(R.dimen.chart_axis_label_text_size);
		mAxisLabelSeparation = getResources().getDimension(R.dimen.chart_axis_label_padding);
		initPaints();
		
		mDataLabelWidth = mDataLabelPaint.measureText("0000");
		mAxisLabelHeight = - mAxisLabelPaint.getFontMetricsInt().top;
	}
	
	private void initPaints() {
		mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStrokeWidth(mGridThickness);
        mGridPaint.setColor(mGridColor);
        mGridPaint.setStyle(Paint.Style.STROKE);
        float dashPathEffectFactor = getResources().getDimension(R.dimen.chart_dash_path_effect);
        mGridPaint.setPathEffect( new DashPathEffect( new float[]{
        				dashPathEffectFactor,
        				dashPathEffectFactor * 2,
        				dashPathEffectFactor * 3,
        				dashPathEffectFactor * 4}, 0));
        
        mAxisLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisLabelPaint.setColor(mAxisLabelColor);
        mAxisLabelPaint.setTextSize(mAxisLabelTextSize);
        mAxisLabelPaint.setTextAlign(Align.CENTER);
        
        
        mDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataPaint.setColor(mDataColor);
        mDataPaint.setStyle(Paint.Style.STROKE);
        mDataPaint.setStrokeWidth(mDataPointThickness);
        
        mSeriesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSeriesPaint.setColor(mSeriesColor);
        mSeriesPaint.setStyle(Paint.Style.STROKE);
        mSeriesPaint.setStrokeWidth(mSeriesThickness);
        
        mDataLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataLabelPaint.setColor(mDataLabelColor);
        mDataLabelPaint.setTextSize(mDataLabelTextSize);
        mDataLabelPaint.setTextAlign(Align.CENTER);
        
        mDataLabelBackgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataLabelBackgroudPaint.setColor(mDataLabelBackgroundColor);
        mDataLabelBackgroudPaint.setTextAlign(Align.CENTER);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = Math.max(getSuggestedMinimumHeight(), resolveSize(0, heightMeasureSpec));
		mGrid.height = ( height - mAxisLabelHeight - mAxisLabelSeparation - getPaddingTop() - getPaddingBottom()) / mGrid.row;
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize( (int)(mGrid.width * mGrid.column + getPaddingLeft() + getPaddingRight()),
                                widthMeasureSpec)),
                height );
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawAxis(canvas);
		drawData(canvas);
	}
	
	/**
	 * 绘制坐标以及 X 轴的标签
	 * @param canvas
	 */
	private void drawAxis(Canvas canvas) {
		int i=0;
		
		if( mAxisXPositionsBuffer.length < mGrid.column ) {
			mAxisXPositionsBuffer = new float[mGrid.column];
		}
		if( mAxisYPositionsBuffer.length < mGrid.row ) {
			mAxisYPositionsBuffer = new float[mGrid.row];
		}
		if( mAxisXLinesBuffer.length < mAxisXPositionsBuffer.length * 4 ) {
			mAxisXLinesBuffer = new float[mAxisXPositionsBuffer.length * 4];
		}
		if( mAxisYLinesBuffer.length < mAxisYPositionsBuffer.length * 4 ) {
			mAxisYLinesBuffer = new float[mAxisYPositionsBuffer.length * 4];
		}
		
		float xOffset = getPaddingLeft() - mGrid.width / 2;
		for( i=0; i<mAxisXPositionsBuffer.length; i++) {
			xOffset += mGrid.width;
			mAxisXPositionsBuffer[i] = xOffset;
		}
		float yOffset = getHeight() - getPaddingBottom() - mAxisLabelHeight - mAxisLabelSeparation;
		for( i=0; i<mAxisYPositionsBuffer.length; i++) {
			mAxisYPositionsBuffer[i] = yOffset;
			yOffset -= mGrid.height;
		}
		
		// draw grids
		for( i=0; i<mAxisXPositionsBuffer.length; i++ ) {
			mAxisXLinesBuffer[i * 4 + 0] = mAxisXPositionsBuffer[i];	// startX
			mAxisXLinesBuffer[i * 4 + 1] = getPaddingTop();		// startY
			mAxisXLinesBuffer[i * 4 + 2] = mAxisXPositionsBuffer[i];	// stopX
			mAxisXLinesBuffer[i * 4 + 3] = getHeight() - getPaddingBottom() - mAxisLabelHeight - mAxisLabelSeparation;	// stopX
		}
		canvas.drawLines(mAxisXLinesBuffer, mGridPaint);
		
		for( i=0; i<mAxisYPositionsBuffer.length; i++) {
			mAxisYLinesBuffer[i * 4 + 0] = getPaddingLeft();	// startX;
			mAxisYLinesBuffer[i * 4 + 1] = mAxisYPositionsBuffer[i];	// startY;
			mAxisYLinesBuffer[i * 4 + 2] = getWidth() - getPaddingRight();	// stopX;
			mAxisYLinesBuffer[i * 4 + 3] = mAxisYPositionsBuffer[i];	// stopY;
		}
		canvas.drawLines(mAxisYLinesBuffer, mGridPaint);
		
		// draw label
		int length = Math.min(mAxisXPositionsBuffer.length, mAxisXLabels.length);
		for(i=0; i<length; i++) {
			canvas.drawText(mAxisXLabels[i], mAxisXPositionsBuffer[i],
					getHeight() - getPaddingBottom(), mAxisLabelPaint);
		}
	}
	
	/**
	 * 绘制数据
	 * @param canvas
	 */
	private void drawData(Canvas canvas) {
		int i=0;
		if( mSeriesLinesBuffer.length < mData.length * 4 ) {
			mSeriesLinesBuffer = new float[mData.length * 4];
		}
		
		float maxY = getMaxYValue();
		float axisY = getHeight() - getPaddingTop() - getPaddingBottom() - mAxisLabelHeight - mAxisLabelSeparation - mGrid.height;
		mSeriesLinesBuffer[i * 4 + 0] = mAxisXPositionsBuffer[0];
		mSeriesLinesBuffer[i * 4 + 1] = axisY  - mData[0] / maxY * axisY + getPaddingTop() + mGrid.height;
		mSeriesLinesBuffer[i * 4 + 2] = mSeriesLinesBuffer[0];
		mSeriesLinesBuffer[i * 4 + 3] = mSeriesLinesBuffer[1];
//		mSeriesPath.moveTo(mSeriesLinesBuffer[0], getHeight() - getPaddingBottom() - mLabelHeight - mLabelSeparation);
//		mSeriesPath.lineTo(mSeriesLinesBuffer[0], mSeriesLinesBuffer[1]);
		for( i=1; i<mData.length; i++ ) {
			mSeriesLinesBuffer[i * 4 + 0] = mSeriesLinesBuffer[ ( i - 1 ) * 4 + 2];
			mSeriesLinesBuffer[i * 4 + 1] = mSeriesLinesBuffer[ ( i - 1 ) * 4 + 3];
			mSeriesLinesBuffer[i * 4 + 2] = mAxisXPositionsBuffer[i];
			mSeriesLinesBuffer[i * 4 + 3] = axisY  - mData[i] / maxY * axisY + getPaddingTop() + mGrid.height;
			
//			mSeriesPath.lineTo(mSeriesLinesBuffer[i * 4 + 2], mSeriesLinesBuffer[i * 4 + 3]);
		}
//		mSeriesPath.lineTo(mAxisXPositionsBuffer[11], getHeight() - getPaddingBottom() - mLabelHeight - mLabelSeparation);
//		mSeriesPath.close();
//		canvas.drawPath(mSeriesPath, mSeriesPaint);
		canvas.drawLines(mSeriesLinesBuffer, mSeriesPaint);
		
		for( i = 0; i<mSeriesLinesBuffer.length/4; i++ ) {
			mDataLabelPaint.setColor(Color.BLACK);
			mDataPaint.setColor(mDataColor);
			mDataPaint.setStyle(Style.STROKE);
			canvas.drawCircle(mSeriesLinesBuffer[i * 4 + 2], mSeriesLinesBuffer[i * 4 + 3], mDataPointRadius, mDataPaint);
			mDataPaint.setColor(Color.WHITE);
			mDataPaint.setStyle(Style.FILL);
			canvas.drawCircle(mSeriesLinesBuffer[i * 4 + 2], mSeriesLinesBuffer[i * 4 + 3], mDataPointRadius - 2, mDataPaint);
			canvas.drawText(labelFormat(mData[i]), mSeriesLinesBuffer[i * 4 + 2], 
					mSeriesLinesBuffer[i * 4 + 3] - 2 * mDataPointRadius - mDataLabelTextPadding, mDataLabelPaint);
			
			mTextBackgroundRect.set(mSeriesLinesBuffer[i * 4 + 2] - mDataLabelWidth / 2 - mDataLabelTextPadding,
 				   mSeriesLinesBuffer[i * 4 + 3] - 4 * mDataPointRadius - mDataLabelTextPadding, 
 				   mSeriesLinesBuffer[i * 4 + 2] + mDataLabelWidth / 2 + mDataLabelTextPadding,
 				   mSeriesLinesBuffer[i * 4 + 3] - 2 * mDataPointRadius + mDataLabelTextPadding);
	        canvas.drawRoundRect(mTextBackgroundRect, mDataLabelBackgroundRadius, mDataLabelBackgroundRadius, mDataLabelBackgroudPaint);
		}
	}
	
	private String labelFormat(float value) {
		return String.format(Locale.getDefault(), "%.1f", value);
	}

	private float getMaxYValue() {
		float maxY = mData[0];
		for( int i=1; i<mData.length; i++ ) {
			maxY = Math.max(maxY, mData[i]);
		}
		return maxY;
	}
	
	public void setData(float[] data) {
		if( data == null || data.length <= 0 ) {
			Log.e("ChartView", "data do not empty");
			return;
		}
		mData = data;
//		mGrid.column = mData.length;
	}
	
	public void setAxisXLabels(String[] labels) {
		mAxisXLabels = labels;
	}
	
	class Grid {
		int row;
		int column;
		float width;
		float height;
	}
}
