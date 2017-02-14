package com.vince.dbtest.dbtools;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2015/11/15.
 */
public class GameDataBase {
    public static final class GameDatabaseTable implements BaseColumns {
        public static final String TABLE_PLAYER = "player_table";
        public static final String PLAYER_ID = "_id";
        public static final String PLAYER = "player";
        public static final String PLAYER_SCORE = "score";
        public static final String PLAYER_LEVEL = "level";
    }
}
