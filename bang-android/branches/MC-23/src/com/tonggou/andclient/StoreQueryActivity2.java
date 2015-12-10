package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.ServiceCategoryResponse;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.myview.SingleListPopupWindow;
import com.tonggou.andclient.network.parser.AsyncLoadCacheJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.QueryServiceScopeRequest;
import com.tonggou.andclient.vo.ServiceCategoryDTO;
import com.tonggou.andclient.vo.type.ServiceScopeType;

/**
 * 查询推荐店铺页面
 * @author lwz
 *
 */
public class StoreQueryActivity2 extends AbsBackableActivity {
	
	public static final String PARAM_KEY_SERVICE_SCOPE = "tonggou.shop.category";
	public static final String PARAM_KEY_TITLE = "tonggou.shop.categoryname";
	
	private View mSortTypeContainer;
	private View mServiceTypeContainer;
	private View mPlaceContainer;
	private View mShop4Container;
	private TextView mSortTypeText;
	private TextView mServiceTypeText;
	private TextView mPlaceText;
	private EditText mSearchEdit;
	private ImageButton mClearSearchContentBtn;
	private View mNameListContainer;
	private ListView mNameListView;
	private PullToRefreshListView mShopListView;
	
	private SingleListPopupWindow mSortTypePopList;
	private SingleListPopupWindow mServiceTypePopList;
	private String[] mSortTypeData = null;
	private ServiceCategoryAdapter mServiceCategoryAdapter;
	
	public static final String ALLCATEGORY = "所有";
	private ServiceScopeType mServiceType = ServiceScopeType.ALL;	// 查询的服务类型,默认为所有
	
	@Override
	protected int getContentLayout() {
		return R.layout.storequery2;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setTitle(R.string.shopslist_title);
		titleBar.setRightImageButton(R.drawable.listtomap, android.R.color.transparent);
		titleBar.setOnRightButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		if( TongGouApplication.getInstance().isLogin() ) {
			titleBar.setRightSecondButton("更多", android.R.color.transparent);
			titleBar.setOnRightSecondButtonClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});
		}
	}
	
	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		
		mSortTypeContainer = findViewById(R.id.sort_type_container);
		mServiceTypeContainer = findViewById(R.id.service_type_container);
		mPlaceContainer = findViewById(R.id.place_container);
		mShop4Container = findViewById(R.id.shop_4s_container);
		mSortTypeText = (TextView) findViewById(R.id.sort_type);
		mServiceTypeText = (TextView) findViewById(R.id.service_type);
		mPlaceText = (TextView) findViewById(R.id.place);
		mSearchEdit = (EditText) findViewById(R.id.book_search_et);
		mClearSearchContentBtn = (ImageButton) findViewById(R.id.book_search_close_icon);
		mNameListContainer = findViewById(R.id.name_list_container);
		mNameListView = (ListView) findViewById(R.id.name_list);
		mShopListView = (PullToRefreshListView) findViewById(R.id.shops_list_view);
	}
	
	@Override
	protected void afterViews() {
		super.afterViews();
		mSortTypeData = new String[]{
				getString(R.string.shopslist_arrayplcae),
				getString(R.string.shopslist_arrayscroe)};
		mSortTypeText.setText( mSortTypeData[0] );
		
		mServiceCategoryAdapter = 
				new ServiceCategoryAdapter(this, new ArrayList<ServiceCategoryDTO>(), R.layout.item_pop);
		
		initPops();
		setListener();
	}
	
	private void initPops() {
		mSortTypePopList = new SingleListPopupWindow(this, 
				new ArrayAdapter<String>(this, R.layout.item_pop, mSortTypeData));
		mSortTypePopList.setBackground(R.drawable.pop_bg);
		mSortTypePopList.setOnListItemClickListener(mSortTypePopListItemClickListener);
		
		mServiceTypePopList = new SingleListPopupWindow(this, mServiceCategoryAdapter);
		mServiceTypePopList.setBackground(R.drawable.pop_bg);
		mServiceTypePopList.setOnListItemClickListener(mServiceTypePopListItemClickListener);
	}
	
	private void setListener() {
		mSortTypeContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSortTypePopList();
				
			}
		});
		
		mServiceTypeContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showServiceTypePopList();
			}
		});
	}
	
	private void showSortTypePopList() {
		if( mSortTypePopList != null ) {
			mSortTypePopList.dismiss();
		}
		mSortTypePopList.showAsDropDown(mSortTypeText);
	}
	
	private OnItemClickListener mSortTypePopListItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mSortTypeText.setText( mSortTypeData[position] );
		}
	};
	
	private void showServiceTypePopList() {
		requestServiceScope(ServiceScopeType.ALL);
	}
	
	private OnItemClickListener mServiceTypePopListItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TongGouApplication.showToast( mServiceCategoryAdapter.getData().get(position).getName() );
		}
	};
	
	private void requestServiceScope(ServiceScopeType type) {
		
		QueryServiceScopeRequest request = new QueryServiceScopeRequest();
		request.setApiParams(type);
		request.doRequest(this, new AsyncLoadCacheJsonBaseResponseParseHandler<ServiceCategoryResponse>() {

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				getSimpleTitle().hideLoadingIndicator();
			}

			@Override
			public void onStart() {
				getSimpleTitle().showLoadingIndicator();
			}

			@Override
			public void onParseSuccess(ServiceCategoryResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				List<ServiceCategoryDTO> data = result.getServiceCategoryDTOList();
				mServiceCategoryAdapter.update(data);
				if( data == null ) {
					TongGouApplication.showLog("无服务范围返回");
				} else {
//					doQueryStore(serviceScopeIds, areaId, shopType, keyWord, pageNo, INFO.ITEMS_PER_PAGE, true);
					if( mServiceTypePopList != null ) {
						mServiceTypePopList.dismiss();
					}
					mServiceTypePopList.showAsDropDown(mServiceTypeText);
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				handlerParserFailure(errorCode, errorMsg);			
			}

			@Override
			public Class<ServiceCategoryResponse> getTypeClass() {
				return ServiceCategoryResponse.class;
			}

			@Override
			public void onLoadCache(ServiceCategoryResponse result, String originResult,
					boolean isNetworkConnected) {
				if( result == null ) {
					return;
				}
				List<ServiceCategoryDTO> data = result.getServiceCategoryDTOList();
				mServiceCategoryAdapter.update(data);
				if( mServiceTypePopList != null ) {
					mServiceTypePopList.dismiss();
				}
				mServiceTypePopList.showAsDropDown(mServiceTypeText);
			}

			@Override
			public String getUserNo() {
				return null;
			}
			
		});
	}
	
	/// custom adapter
	class ServiceCategoryAdapter extends AbsViewHolderAdapter<ServiceCategoryDTO> {

		public ServiceCategoryAdapter(Context context, List<ServiceCategoryDTO> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, ServiceCategoryDTO itemData) {
			TextView catsName = getViewFromHolder(convertView, android.R.id.text1);
			catsName.setText( itemData.getName() );
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized void update(Collection<? extends ServiceCategoryDTO> newData) {
			if( newData == null || newData.isEmpty() ) {
				return;
			}
			ServiceCategoryDTO nullCategory = new ServiceCategoryDTO();
			nullCategory.setId("NULL");
			nullCategory.setName(ALLCATEGORY);
			((List<ServiceCategoryDTO>)newData).add(0, nullCategory);
			super.update(newData);
		}
		
	}
}
