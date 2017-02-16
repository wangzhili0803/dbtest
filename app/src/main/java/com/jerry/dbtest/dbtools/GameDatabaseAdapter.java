package com.jerry.dbtest.dbtools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jerry.dbtest.entity.GamePlayer;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/15.
 */
public class GameDatabaseAdapter {

    private GamePlayerDBHelper helper;

    public GameDatabaseAdapter(Context context) {
        helper = new GamePlayerDBHelper(context);
    }

    public void add(GamePlayer gamePlayer) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameDataBase.GameDatabaseTable.PLAYER, gamePlayer.getName());
        values.put(GameDataBase.GameDatabaseTable.PLAYER_SCORE,
                gamePlayer.getScore());
        values.put(GameDataBase.GameDatabaseTable.PLAYER_LEVEL,
                gamePlayer.getLevel());
        db.insert(GameDataBase.GameDatabaseTable.TABLE_PLAYER, null, values);
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String wherClause = GameDataBase.GameDatabaseTable._ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.delete(GameDataBase.GameDatabaseTable.TABLE_PLAYER, wherClause,
                whereArgs);
        db.close();
    }

    public void update(GamePlayer gamePlayer) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameDataBase.GameDatabaseTable.PLAYER, gamePlayer.getName());
        values.put(GameDataBase.GameDatabaseTable.PLAYER_SCORE,
                gamePlayer.getScore());
        values.put(GameDataBase.GameDatabaseTable.PLAYER_LEVEL,
                gamePlayer.getLevel());
        String wherClause = GameDataBase.GameDatabaseTable._ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(gamePlayer.getId())};
        db.update(GameDataBase.GameDatabaseTable.TABLE_PLAYER, values,
                wherClause, whereArgs);
        db.close();
    }

    public GamePlayer findPlayer(int id) {
        GamePlayer player = new GamePlayer();
        SQLiteDatabase db = helper.getWritableDatabase();
        String selectionClause = GameDataBase.GameDatabaseTable._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(true,
                GameDataBase.GameDatabaseTable.TABLE_PLAYER, null,
                selectionClause, selectionArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            player.setId(cursor.getInt(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable._ID)));
            player.setName(cursor.getString(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable.PLAYER)));
            player.setScore(cursor.getInt(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable.PLAYER_SCORE)));
            player.setLevel(cursor.getInt(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable.PLAYER_LEVEL)));
        }
        cursor.close();
        db.close();
        return player;
    }

    public ArrayList<GamePlayer> findAll() {
        String sql = "select _id,player,score,level from player_table order by score desc";
        SQLiteDatabase db = helper.getWritableDatabase();
        GamePlayer player;
        ArrayList<GamePlayer> list = new ArrayList<GamePlayer>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            player = new GamePlayer();
            player.setId(cursor.getInt(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable._ID)));
            player.setName(cursor.getString(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable.PLAYER)));
            player.setScore(cursor.getInt(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable.PLAYER_SCORE)));
            player.setLevel(cursor.getInt(cursor
                    .getColumnIndexOrThrow(GameDataBase.GameDatabaseTable.PLAYER_LEVEL)));
            list.add(player);
        }
        cursor.close();
        db.close();
        return list;
    }

    public int getCount() {
        String sql = "select count(_id) from player_table";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public Cursor getCursor() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(true,
                GameDataBase.GameDatabaseTable.TABLE_PLAYER, null,
                null, null, null, null, null, null);
        return cursor;
    }
}
