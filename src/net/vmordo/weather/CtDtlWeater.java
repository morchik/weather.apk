package net.vmordo.weather;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CtDtlWeater extends Activity {

	public String jdata;
	public int id;
	public String cityName;
	public String ctemp;
	public String coord;
	public String sun;
	public String press;
	public String hum;
	public String temp_min;
	public String temp_max;

	private TextView idText;
	private TextView cityText;
	private TextView ctempText;
	private TextView coordText;
	private TextView sunText;
	private TextView pressText;
	private TextView humText;
	private TextView temp_minText;
	private TextView temp_maxText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		jdata = this.getIntent().getStringExtra("jdata");
		parse(jdata);

		setContentView(R.layout.activity_ct_dtl_weater);
		idText = (TextView) findViewById(R.id.textView_id);
		idText.setText(jdata);

		cityText = (TextView) findViewById(android.R.id.text1);
		cityText.setText(cityName);

		ctempText = (TextView) findViewById(R.id.text_temp);
		ctempText.setText(ctemp);

		coordText = (TextView) findViewById(R.id.text_coord);
		coordText.setText(coord);

		sunText = (TextView) findViewById(R.id.text_sun);
		sunText.setText(sun);
		
		pressText = (TextView) findViewById(R.id.text_press);
		pressText.setText(press);
		
		humText = (TextView) findViewById(R.id.text_hum);
		humText.setText(hum);
		
		temp_minText = (TextView) findViewById(R.id.text_temp_min);
		temp_minText.setText(temp_min);
		
		temp_maxText = (TextView) findViewById(R.id.text_temp_max);
		temp_maxText.setText(temp_max);
		
		 //= (TextView) findViewById(R.id.);
		//.setText();
		
}

	public void parse(String data) {
		try {
			JSONObject jObj = new JSONObject(data);
			JSONObject sysObj = jObj.getJSONObject("sys");
			JSONObject mainObj = jObj.getJSONObject("main");
			JSONObject coordObj = jObj.getJSONObject("coord");

			cityName = jObj.getString("name") + ", "
					+ sysObj.getString("country");
			ctemp = (Math.round(mainObj.getDouble("temp")) - 273) + " C"; // in
																			// kelvin
			coord = " " + (coordObj.getDouble("lat")) + "  "
					+ (coordObj.getDouble("lon"));

			sun = " " + (sysObj.getInt("sunrise")) + "  "
					+ (sysObj.getInt("sunset"));
			
			press = mainObj.getString("pressure");
			hum =  mainObj.getString("humidity");
			temp_min = (Math.round(mainObj.getDouble("temp_min")) - 273) + " C"; 
			temp_max = (Math.round(mainObj.getDouble("temp_max")) - 273) + " C"; 
		} catch (JSONException e) {
			android.util.Log.e("DtlWeather", e.toString() + " " + data);
			Toast.makeText(this, "error " + e.toString() + " " + data,
					Toast.LENGTH_LONG).show();
		}
	}
}
