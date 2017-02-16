package com.jerry.dbtest.dbtools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/11/15.
 */
public class GamePlayerDBHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS player_table("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "player TEXT,score INTEGER,level INTEGER) ";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS player_table";

	public GamePlayerDBHelper(Context context) {
		super(context, GameDataBase.GameDatabaseTable.TABLE_PLAYER, null,
				VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion,
			int newversion) {
		sqLiteDatabase.execSQL(DROP_TABLE);
		sqLiteDatabase.execSQL(CREATE_TABLE);
	}
}
