package com.jerry.dbtest.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.jerry.dbtest.dbtools.GameDataBase;
import com.jerry.dbtest.dbtools.GamePlayerDBHelper;

/**
 * Created by Administrator on 2015/11/30.
 */
public class HelloProvider extends ContentProvider {
    private static final String AUTHORITY = "com.vince.com.jerry.com.jerry.dbtest.provider.HelloProvider";

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int MUTIPLE_CODE = 1;//查询多个则返回1
    private static final int SINGLE_CODE = 2;//查询单个则返回2
    private static final String SINGLE_TYPE = "vnd.android.cursor.item/player";
    private static final String MUNIPLE_TYPE = "vnd.android.cursor.dir/player";

    static {
        //content://com.vince.com.jerry.com.jerry.dbtest.provider.HelloProvider/player 匹配1
        uriMatcher.addURI(AUTHORITY, "player", MUTIPLE_CODE);
        //content://com.vince.com.jerry.com.jerry.dbtest.provider.HelloProvider/player/hehe 匹配2
        uriMatcher.addURI(AUTHORITY, "player/#", SINGLE_CODE);
    }

    private GamePlayerDBHelper helper;

    @Override
    public boolean onCreate() {
        helper = new GamePlayerDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case SINGLE_CODE:
                long id = ContentUris.parseId(uri);
                selection = GameDataBase.GameDatabaseTable.PLAYER_ID + "=?";
                selectionArgs = new String[]{String.valueOf(id)};
                cursor = db.query(true, GameDataBase.GameDatabaseTable.TABLE_PLAYER, projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
            case MUTIPLE_CODE:
                cursor = db.query(true, GameDataBase.GameDatabaseTable.TABLE_PLAYER, projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SINGLE_CODE:
                return SINGLE_TYPE;
            case MUTIPLE_CODE:
                return MUNIPLE_TYPE;
        }
        return null;
    }

    ////content://com.vince.com.jerry.com.jerry.dbtest.provider.HelloProvider/player
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case SINGLE_CODE:
                SQLiteDatabase db = helper.getWritableDatabase();
                long id = db.insert(GameDataBase.GameDatabaseTable.TABLE_PLAYER, null, contentValues);
                uri = ContentUris.withAppendedId(uri, id);
                db.close();
                break;
            case MUTIPLE_CODE:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db;
        switch (uriMatcher.match(uri)) {
            case SINGLE_CODE:
                db = helper.getWritableDatabase();
                long id = ContentUris.parseId(uri);
                selection = GameDataBase.GameDatabaseTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(id)};
                int row = db.delete(GameDataBase.GameDatabaseTable.TABLE_PLAYER, selection, selectionArgs);
                db.close();
                return row;
            case MUTIPLE_CODE:
                db = helper.getWritableDatabase();
                row = db.delete(GameDataBase.GameDatabaseTable.TABLE_PLAYER, selection, selectionArgs);
                db.close();
                return row;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[]
            selectionArgs) {
        SQLiteDatabase db;
        switch (uriMatcher.match(uri)) {
            case SINGLE_CODE:
                db = helper.getWritableDatabase();
                long id = ContentUris.parseId(uri);
                selection = GameDataBase.GameDatabaseTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(id)};
                int row = db.update(GameDataBase.GameDatabaseTable.TABLE_PLAYER, contentValues, selection, selectionArgs);
                db.close();
                return row;
            case MUTIPLE_CODE:
                db = helper.getWritableDatabase();
                row = db.update(GameDataBase.GameDatabaseTable.TABLE_PLAYER, contentValues, selection, selectionArgs);
                db.close();
                return row;
        }
        return 0;
    }
}
