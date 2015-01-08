package net.vmordo.weather;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import android.database.Cursor;

class HttpTask extends AsyncTask<String, Integer, String> {
	private String cityName = "";
	@Override
	protected String doInBackground(String... params) {
		if (params.length >= 3)
			cityName = params[2];
		String data = ((new HttpClient()).getData(params[0]));
		android.util.Log.i("HttpClient", params[0] + " -> " + data);
		try {
			Uri CONTACT_URI = Uri.parse(params[1]);
			JSONObject jObj = new JSONObject(data);
			if (!jObj.has("name"))
				publishProgress(1);
			else {
				String city = jObj.getString("name");
				if (!city.isEmpty()) {
					JSONObject sysObj = jObj.getJSONObject("sys");
					JSONObject mainObj = jObj.getJSONObject("main");

					ContentValues cv = new ContentValues();
					cv.put("city_name", city);
					cv.put("ctemp",
							(Math.round(mainObj.getDouble("temp")) - 273)
									+ " C");
					cv.put("JSon_data", data);
					cv.put("date_time", new java.util.Date().toString());
					cv.put("city_cn", sysObj.getString("country"));

					// check city in bd
					boolean bFound = false;
					Cursor c = Weather.contentResolver.query(CONTACT_URI, null,
							null, null, null);
					if (c.moveToFirst()) {
						int nameColIndex = c.getColumnIndex("city_name");
						int idColIndex = c.getColumnIndex("_id");
						do {
							if (city.equalsIgnoreCase(c.getString(nameColIndex))) {
								String idr = c.getString(idColIndex);
								cv.put("_id", idr);
								String[] selectionArgs = { idr };
								android.util.Log.w("test update", idr);
								int res = Weather.contentResolver.update(
										Uri.parse(params[1]), cv, " _id = ?  ",
										selectionArgs);
								android.util.Log.w("HttpClient_newUri",
										"result = " + res);
								bFound = true;
							}
						} while (c.moveToNext());
					}
					if (!bFound) {
						Uri newUri = Weather.contentResolver.insert(
								CONTACT_URI, cv);
						android.util.Log.w("HttpClient_newUri",
								newUri.toString());
					}
					c.close(); // закрываем подключение к БД
				} else {
					android.util.Log.w("HttpClient_city", data);
					publishProgress(1);
				}
			}
		} catch (JSONException e) {
			android.util.Log.e("HttpTask_parse", e.toString() + " " + data);
		} catch (Exception e) {
			android.util.Log.e("HttpTask error ", e.toString() + " " + data);
		}
		return data;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Toast.makeText(Weather.wthr, "City '"+cityName+"' not found, try again.",
					Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Weather.wthr.Refresh_List(false);
	}
}
