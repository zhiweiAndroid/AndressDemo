package sinaflashmoney.credit.sina.com.addressdemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import sinaflashmoney.credit.sina.com.addressdemo.address.City;
import sinaflashmoney.credit.sina.com.addressdemo.address.County;
import sinaflashmoney.credit.sina.com.addressdemo.address.Province;


public class MainActivity extends Activity {

	private ArrayList<Province> provinces = new ArrayList<Province>();
	private Button selectAreaBtn;
	private Button singlePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		selectAreaBtn = (Button) findViewById(R.id.select_area_btn);
		selectAreaBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (provinces.size() > 0) {
					showAddressDialog();
				} else {
					new InitAreaTask(MainActivity.this).execute(0);
				}
			}
		});

	}


	private void showAddressDialog() {
		new CityPickerDialog(MainActivity.this, provinces, null, null, null,
				new CityPickerDialog.onCityPickedListener() {

					@Override
					public void onPicked(Province selectProvince,
										 City selectCity, County selectCounty) {
						StringBuilder address = new StringBuilder();
						address.append(
								selectProvince != null ? selectProvince
										.getAreaName() : "")
								.append(selectCity != null ? selectCity
										.getAreaName() : "")
								.append(selectCounty != null ? selectCounty
										.getAreaName() : "");
						String text = selectCounty != null ? selectCounty
								.getAreaName() : "";
						selectAreaBtn.setText(address);
					}
				}).show();
	}


	private class InitAreaTask extends AsyncTask<Integer, Integer, Boolean> {

		Context mContext;

		Dialog progressDialog;

		public InitAreaTask(Context context) {
			mContext = context;
			progressDialog = WheelProgressDialog.createLoadingDialog(mContext, "请稍等...", true,
					0);
		}

		@Override
		protected void onPreExecute() {

			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (provinces.size()>0) {
				showAddressDialog();
			} else {
				Toast.makeText(mContext, "数据初始化失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			String address = null;
			InputStream in = null;
			try {
				in = mContext.getResources().getAssets().open("address.txt");
				byte[] arrayOfByte = new byte[in.available()];
				in.read(arrayOfByte);
				address = EncodingUtils.getString(arrayOfByte, "UTF-8");
				JSONArray jsonList = new JSONArray(address);
				Gson gson = new Gson();
				for (int i = 0; i < jsonList.length(); i++) {
					try {
						provinces.add(gson.fromJson(jsonList.getString(i),
								Province.class));
					} catch (Exception e) {
					}
				}
				return true;
			} catch (Exception e) {
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
			return false;
		}

	}



}
