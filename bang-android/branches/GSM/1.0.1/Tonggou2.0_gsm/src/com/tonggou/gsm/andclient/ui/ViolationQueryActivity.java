package com.tonggou.gsm.andclient.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.ViolationRecord;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;

/**
 * 违章查询 Activity
 * @author lwz
 *
 */
public class ViolationQueryActivity extends BackableTitleBarActivity implements OnLoadDataActionListener {

	private PullToRefreshListView mRefreshListView;
	private RefreshViewLoadMoreProxy mLoadMoreProxy;
	private ViolationAdapter mAdapter;
	private TextView mTotalScoreText;
	private TextView mTotalMoneyText;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_violation_query);
		getTitleBar().setTitle(R.string.title_violation_query, R.color.black);
		
		mTotalScoreText = (TextView) findViewById(R.id.total_score_text);
		mTotalMoneyText = (TextView) findViewById(R.id.total_money_text);
		
		mRefreshListView = (PullToRefreshListView) findViewById(R.id.ptr_view);
		mAdapter = new ViolationAdapter(this, R.layout.item_list_violation_record);
		mRefreshListView.setAdapter(mAdapter);
		
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mRefreshListView, Mode.PULL_FROM_START);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		mLoadMoreProxy.refreshing();
	}
	
	private void requestData() {
		FakeLoadResponseData();
	}
	
	// 加载 假数据。模拟网络交互
	//  TODO
	private void FakeLoadResponseData() {
		
		new AsyncTask<Void, Void, ArrayList<ViolationRecord>> () {

			@Override
			protected ArrayList<ViolationRecord> doInBackground(Void... params) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
				}
				
				ArrayList<ViolationRecord> data = new ArrayList<ViolationRecord>();
				final int DATA_SIZE = (int)(Math.random() * 10) * 3 ;
				for( int i=1; i<DATA_SIZE; i++ ) {
					ViolationRecord record = new ViolationRecord();
					record.setFen( String.valueOf((int)(Math.random() * 13)) );
					record.setDate( new SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault()).format(new Date()));
					record.setCode("--");
					record.setMoney(String.valueOf( (int)(Math.random() * 10) * 50 ) );
					record.setAct("事故原因");
					record.setArea("苏州市工业园区创意产业园");
					data.add(record);
				}
				return data;
			}
			
			@Override
			protected void onPostExecute(java.util.ArrayList<ViolationRecord> result) {
				mAdapter.update(result);
				mLoadMoreProxy.loadDataActionComplete(true);
			};
			
		}.execute();
	}
	
	@Override
	public void onRefresh(int page) {
		requestData();
	}

	@Override
	public void onLoadMore(int page) {
		// nothing
	}
	
	class ViolationAdapter extends AbsViewHolderAdapter<ViolationRecord> {

		public ViolationAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, ViolationRecord itemData) {
			setText(R.id.record_date_text, "违章日期：" + itemData.getDate());
			setText(R.id.record_act_text,"违章形式：" + itemData.getAct());
			setText(R.id.record_area_text,"违章地点：" + itemData.getArea());
			setText(R.id.record_area_text, "违章代码：" + itemData.getCode());
			setText(R.id.record_fen_text, "违章扣分：" +itemData.getFen());
			setText(R.id.record_money_text,"违章金额：" +  itemData.getMoney());
		}
		
		private void setText(int id, Object value) {
			((TextView)getViewFromHolder(id)).setText(String.valueOf(value));
		}
		
		@Override
		public synchronized void update(Collection<? extends ViolationRecord> newData) {
			super.update(newData);
			int totalScore = 0;
			int totalMoney = 0;
			for( ViolationRecord record : newData ) {
				totalScore += record.getIntFen();
				totalMoney += record.getFloatMoney();
			}
			updateTotalScoreAndMoneyText(String.valueOf(totalScore), String.valueOf(totalMoney));
		}
	}
	
	/**
	 * 更新 累积扣分和罚款金额 TextView
	 * @param score
	 * @param money
	 */
	private void updateTotalScoreAndMoneyText(String score, String money) {
		mTotalScoreText.setText("累积扣分：" + score);
		mTotalMoneyText.setText("累积罚款：" + money + "元");
	}

}
