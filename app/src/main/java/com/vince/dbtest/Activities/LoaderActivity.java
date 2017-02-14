package com.vince.dbtest.Activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.vince.dbtest.R;
import com.vince.dbtest.dbtools.GameDataBase;
import com.vince.dbtest.dbtools.GameDatabaseAdapter;
import com.vince.dbtest.entity.GamePlayer;

public class LoaderActivity extends Activity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Button btn_add;
    private ListView lv_player;
    private GameDatabaseAdapter dbAdapter;
    private CursorLoader loader;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        btn_add = (Button) findViewById(R.id.btn_add);
        lv_player = (ListView) findViewById(R.id.lv_players);
        dbAdapter = new GameDatabaseAdapter(this);
        adapter = new SimpleCursorAdapter(this, R.layout.item_player,
                dbAdapter.getCursor(), new String[]{GameDataBase.GameDatabaseTable.PLAYER_ID,
                GameDataBase.GameDatabaseTable.PLAYER,
                GameDataBase.GameDatabaseTable.PLAYER_LEVEL,
                GameDataBase.GameDatabaseTable.PLAYER_SCORE},
                new int[]{R.id.tv_id,
                        R.id.tv_name,
                        R.id.tv_level,
                        R.id.tv_score},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv_player.setAdapter(adapter);
        btn_add.setOnClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = Uri.parse("content://com.vince.com.jerry.com.jerry.dbtest.provider.HelloProvider/player");
        loader = new CursorLoader(this, uri, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                dbAdapter.add(new GamePlayer("kfsjdfk", 19, 324));
                loader.onContentChanged();
                break;
            default:
                break;
        }
    }
}
