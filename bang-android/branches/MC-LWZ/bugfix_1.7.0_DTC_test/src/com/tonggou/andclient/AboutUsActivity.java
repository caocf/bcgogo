package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tonggou.andclient.util.INFO;

public class AboutUsActivity extends BaseActivity {
	private View back,link,call;
	String[] phones;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.aboutus);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutUsActivity.this.finish();
			}
		});
		((TextView)findViewById(R.id.version)).setText("v"+INFO.VERSION);
		link=findViewById(R.id.link);
		call=findViewById(R.id.phone);
		link.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toWeb("http://www.bcgogo.com");
			}
		});
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//String phonenumber="0512-66733331,400-6388528";
				String phonenumber="0512-66733331,";
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				phones=phonenumber.split(",");							

				for(int i=0;i<phones.length;i++){		
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name",phones[i].trim());
					list.add(map);
				}
				View shoutCamrea = LayoutInflater.from(AboutUsActivity.this).inflate(R.layout.pop_list3, null);
				shoutCamrea.setBackgroundDrawable(null);
				ListView synList = (ListView)shoutCamrea.findViewById( R.id.server_type_list );
				SimpleAdapter adapter = new SimpleAdapter(AboutUsActivity.this,list,
						R.layout.popview_item,
						new String[]{"name"},
						new int[]{R.id.popview_name});
				synList.setAdapter(adapter);
				synList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						toPhone(phones[arg2]);
					}

				});

				AlertDialog	phonenums = new AlertDialog.Builder(AboutUsActivity.this) 
				.setView(shoutCamrea)
				.setTitle(getString(R.string.choosephone)) 
				.setNegativeButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				}).show();
			}
		});
	}
	public void toPhone(String PhoneNum){
		if(PhoneNum!=null ){
			Uri uri = Uri.parse("tel:"+PhoneNum); 
			Intent it = new Intent(Intent.ACTION_DIAL, uri);   
			startActivity(it);  			     	        
		}
	}
	public void toWeb(String url){
		if(url!=null ){
			Uri uri = Uri.parse(url); 
			Intent it  = new Intent(Intent.ACTION_VIEW,uri); 
			startActivity(it); 	
			/*}catch(ActivityNotFoundException e){
				Toast.makeText(AboutUsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

			}*/

		}
	}
}
