package net.vmordo.weather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db_Helper extends SQLiteOpenHelper {

	public SQLiteDatabase db;

	public Db_Helper(Context context) {
		// конструктор суперкласса
		super(context, "myDB", null, 2);
		// подключаемс€ к Ѕƒ
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// создаем таблицу городов с пол€ми
		db.execSQL("create table city ("
				+ "_id integer primary key autoincrement," 
				+ "city_name text ,"
				+ "city_cn text," 
				+ "JSon_data text,"
				+ "ctemp text, "
				+ "date_time TIMESTAMP default CURRENT_TIMESTAMP );");
		// fill data
		db.execSQL("insert into city (city_name, city_cn, ctemp ) Values ('Almaty', 'KZ', '-'); ");
		db.execSQL("insert into city (city_name, city_cn, ctemp ) Values ('Saint Petersburg', 'RU', '?'); ");
		db.execSQL("insert into city (city_name, city_cn, ctemp ) Values ('Moscow', 'RU', '?'); ");
		//{"coord":{"lon":30.26,"lat":59.89},"sys":{"message":0.2303,"country":"RU","sunrise":1403829541,"sunset":1403897109},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],"base":"cmc stations","main":{"temp":285.24,"humidity":65,"pressure":1018,"temp_min":285.15,"temp_max":285.37},"wind":{"speed":4.63,"gust":5.14,"deg":91},"clouds":{"all":48},"dt":1403855760,"id":498817,"name":"Saint Petersburg","cod":200}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Cursor get_city_cur() {
		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		return db.query("city", null, " city_name is not null ", null, null, null,
				" _id DESC ");
	}

	public void deleteAll() {
		db.delete("city", null, null);
	}
}
