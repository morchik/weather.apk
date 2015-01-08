package net.vmordo.weather;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Weather extends Activity {

	public static Weather wthr;
	public static Db_Helper dbHelper;
	public static ContentResolver contentResolver;

	private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
	// private static String FORE_URL =
	// "http://api.openweathermap.org/data/2.5/forecast/daily?cnt=7&mode=json&q=";
	private static final Uri CONTACT_URI = Uri
			.parse("content://net.vmordo.weather.providers/city");

	private EditText cityText;
	private ListView lvMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		wthr = this;
		dbHelper = new Db_Helper(getBaseContext());
		contentResolver = getContentResolver();
		cityText = (EditText) findViewById(R.id.editText1);
		lvMain = (ListView) findViewById(R.id.lvMain);

		// устанавливаем режим выбора пунктов списка
		// lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		OnItemClickListener oclOnItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = Weather.contentResolver.query(
						Uri.parse(CONTACT_URI.toString() + "/" + id), null,
						null, null, null);
				if (c.moveToFirst()) {
					Intent intent = new android.content.Intent(
							getBaseContext(), CtDtlWeater.class);
					intent.putExtra("id", "" + id);
					intent.putExtra("jdata",
							c.getString(c.getColumnIndex("JSon_data")));
					startActivity(intent);
				}
			}
		};
		lvMain.setOnItemClickListener(oclOnItemClickListener);
		Refresh_List(true);
	}

	public void onClickSearch(View v) {
		String city = cityText.getText().toString();
		if (city.length() > 1) {
			HttpTask task = new HttpTask();
			task.execute(new String[] { BASE_URL + city,
					CONTACT_URI.toString(), city });
		} else
			Toast.makeText(Weather.wthr, "Put City name before",
					Toast.LENGTH_SHORT).show();
	}

	public void onClickRefresh(View v) {
		Refresh_List(true);
	}

	public void Refresh_List(boolean brData) {
		try {
			Cursor cursor = getContentResolver().query(CONTACT_URI, null, null,
					null, null);
			if (brData && cursor.moveToFirst()) {
				int nameColIndex = cursor.getColumnIndex("city_name");
				do {
					HttpTask task = new HttpTask();
					String city = cursor.getString(nameColIndex);
					task.execute(new String[] { BASE_URL + city,
							CONTACT_URI.toString(), city });
					android.util.Log.w("Weater", "result = " + city);
				} while (cursor.moveToNext());
			}

			String from[] = { "city_name", "date_time", "ctemp", "city_cn" };
			int to[] = { android.R.id.text1, android.R.id.text2,
					R.id.text_temp, R.id.text_cn };
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
					R.layout.list_item, cursor, from, to,
					android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			lvMain.setAdapter(adapter);
		} catch (Exception e) {
			android.util.Log.e("setAdapter_getContentResolver", e.toString());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.clear:
			dbHelper.deleteAll();
			Refresh_List(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
