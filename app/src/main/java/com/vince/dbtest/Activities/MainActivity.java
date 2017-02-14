package com.vince.dbtest.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vince.dbtest.R;
import com.vince.dbtest.dbtools.GameDatabaseAdapter;
import com.vince.dbtest.entity.GamePlayer;
import com.vince.dbtest.fragments.AddFragment;
import com.vince.dbtest.fragments.GamePlayerFragment;
import com.vince.dbtest.fragments.UpdateFragment;

import java.util.ArrayList;

public class MainActivity extends Activity implements AddFragment.AddListener,
        GamePlayerFragment.GamePlayerListener, UpdateFragment.UpdateListener {

    private GameDatabaseAdapter adapter;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private AddFragment addFragment;
    private GamePlayerFragment gamePlayerFragment;
    private UpdateFragment updateFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new GameDatabaseAdapter(this);
        gamePlayerFragment = new GamePlayerFragment();
        showGamePlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            addFragment = AddFragment.getInstance();
            addFragment.show(getFragmentManager(), null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void add(GamePlayer gamePlayer) {
        adapter.add(gamePlayer);
        gamePlayerFragment.changedData();
    }

    @Override
    public void showGamePlayer() {
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.main_layout, gamePlayerFragment);
        ft.addToBackStack(null);// 添加到回退栈
        ft.commit();
    }

    @Override
    public void showUpdateGamePlayer(int id) {
        updateFragment = UpdateFragment.getInstance(id);
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, updateFragment);
        ft.addToBackStack(null);// 添加到回退栈
        ft.commit();
    }

    @Override
    public void deleteGamePlayer(int id) {
        adapter.delete(id);
        gamePlayerFragment.changedData();
    }

    @Override
    public ArrayList<GamePlayer> findAll() {
        return adapter.findAll();
    }

    @Override
    public void update(GamePlayer gamePlayer) {
        adapter.update(gamePlayer);
        gamePlayerFragment.changedData();
    }

    @Override
    public GamePlayer findById(int id) {
        return adapter.findPlayer(id);
    }
}
