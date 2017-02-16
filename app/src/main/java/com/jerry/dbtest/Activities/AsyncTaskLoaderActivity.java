package com.jerry.dbtest.Activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jerry.dbtest.R;
import com.jerry.dbtest.dbtools.GameDatabaseAdapter;
import com.jerry.dbtest.entity.GamePlayer;

import java.util.ArrayList;

public class AsyncTaskLoaderActivity extends Activity implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<GamePlayer>> {
    private Button btn_add;
    private ListView lv_player;
    private GameDatabaseAdapter dbAdapter;
    private DataAsyncTaskLoader loader;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        btn_add = (Button) findViewById(R.id.btn_add);
        lv_player = (ListView) findViewById(R.id.lv_players);
        dbAdapter = new GameDatabaseAdapter(this);
        adapter = new MyAdapter(this, dbAdapter.findAll());
        lv_player.setAdapter(adapter);
        btn_add.setOnClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                dbAdapter.add(new GamePlayer("wangzhili", 19, 89));
                loader.onContentChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public Loader<ArrayList<GamePlayer>> onCreateLoader(int id, Bundle args) {
        loader = new DataAsyncTaskLoader(this, dbAdapter);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<GamePlayer>> loader, ArrayList<GamePlayer> gamePlayers) {
        adapter.setPlayers(gamePlayers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<GamePlayer>> loader) {
        adapter.setPlayers(null);
    }

    private class MyAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<GamePlayer> players;

        public MyAdapter(Activity activity, ArrayList<GamePlayer> players) {
            this.activity = activity;
            this.players = players;
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int i) {
            return players.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if (view == null) {
                vh = new ViewHolder();
                view = activity.getLayoutInflater().inflate(
                        R.layout.item_player, null);
                vh.tv_id = (TextView) view.findViewById(R.id.tv_id);
                vh.tv_name = (TextView) view.findViewById(R.id.tv_name);
                vh.tv_level = (TextView) view.findViewById(R.id.tv_level);
                vh.tv_score = (TextView) view.findViewById(R.id.tv_score);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            vh.tv_id.setText(String.valueOf(players.get(i).getId()));
            vh.tv_name.setText(players.get(i).getName());
            vh.tv_score.setText(String.valueOf(players.get(i).getScore()));
            vh.tv_level.setText(String.valueOf(players.get(i).getLevel()));
            return view;
        }

        public void setPlayers(ArrayList<GamePlayer> players) {
            this.players = players;
        }


    }

    private static class ViewHolder {
        TextView tv_id;
        TextView tv_name;
        TextView tv_level;
        TextView tv_score;
    }

    private static class DataAsyncTaskLoader extends AsyncTaskLoader<ArrayList<GamePlayer>> {

        private GameDatabaseAdapter dbAdapter;
        private ArrayList<GamePlayer> data;

        public DataAsyncTaskLoader(Context context, GameDatabaseAdapter dbAdapter) {
            super(context);
            this.dbAdapter = dbAdapter;
        }

        @Override
        public ArrayList<GamePlayer> loadInBackground() {
            data = dbAdapter.findAll();
            return data;
        }

        @Override
        public void deliverResult(ArrayList<GamePlayer> data) {
            if (isReset()) {
                return;
            }
            if (isStarted()) {
                super.deliverResult(data);
            }
        }

        @Override
        protected void onStartLoading() {
            if (data != null) {
                deliverResult(data);
            }
            if (takeContentChanged()) {
                forceLoad();
            }
            super.onStartLoading();
        }
    }
}
