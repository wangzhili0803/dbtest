package com.jerry.dbtest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jerry.dbtest.R;
import com.jerry.dbtest.entity.GamePlayer;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamePlayerFragment extends Fragment {
	private ListView lv_players;
	private GamePlayerListener gamePlayerListener;
	private static GamePlayerFragment gamePlayerFragment;
	private Activity activity;
	private ArrayList<GamePlayer> players;
	private MyAdapter playeraAdapter;

	public interface GamePlayerListener {
		public void showGamePlayer();

		public void showUpdateGamePlayer(int id);

		public void deleteGamePlayer(int id);

		public ArrayList<GamePlayer> findAll();
	}

	public GamePlayerFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		try {
			gamePlayerListener = (GamePlayerListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		players = gamePlayerListener.findAll();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_player, null);
		lv_players = (ListView) view.findViewById(R.id.lv_players);
		playeraAdapter = new MyAdapter(activity, players);
		lv_players.setAdapter(playeraAdapter);
		registerForContextMenu(lv_players);
		return view;
	}

	public static GamePlayerFragment getInstance() {
		if (gamePlayerFragment == null) {
			gamePlayerFragment = new GamePlayerFragment();
		}
		return gamePlayerFragment;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderIcon(R.mipmap.ic_launcher);
		menu.setHeaderTitle("修改/删除");
		activity.getMenuInflater().inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		TextView tv;
		int id;
		AdapterViewAnimator.AdapterContextMenuInfo info;
		switch (item.getItemId()) {
		case R.id.menu_update:
			info = (AdapterViewAnimator.AdapterContextMenuInfo) item
					.getMenuInfo();
			tv = (TextView) info.targetView.findViewById(R.id.tv_id);
			id = Integer.valueOf(tv.getText().toString());
			gamePlayerListener.showUpdateGamePlayer(id);
			changedData();
			break;
		case R.id.menu_delete:
			info = (AdapterViewAnimator.AdapterContextMenuInfo) item
					.getMenuInfo();
			tv = (TextView) info.targetView.findViewById(R.id.tv_id);
			id = Integer.valueOf(tv.getText().toString());
			gamePlayerListener.deleteGamePlayer(id);
			changedData();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void changedData() {
		playeraAdapter.setPlayers(gamePlayerListener.findAll());
		playeraAdapter.notifyDataSetChanged();
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
}
