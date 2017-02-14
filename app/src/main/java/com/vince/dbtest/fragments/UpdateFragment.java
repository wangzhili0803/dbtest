package com.vince.dbtest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vince.dbtest.R;
import com.vince.dbtest.entity.GamePlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends Fragment {

	private UpdateListener updateListener;
	private static UpdateFragment updateFragment;
	private EditText et_name;
	private EditText et_level;
	private EditText et_score;

	public static UpdateFragment getInstance(int id) {
		if (updateFragment == null) {
			updateFragment = new UpdateFragment();
		}
		return updateFragment;
	}

	public interface UpdateListener {
		public void update(GamePlayer gamePlayer);

		public GamePlayer findById(int id);
	}

	public UpdateFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			updateListener = (UpdateListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_update, null);
		et_name = (EditText) view.findViewById(R.id.et_name);
		et_level = (EditText) view.findViewById(R.id.et_level);
		et_score = (EditText) view.findViewById(R.id.et_score);
		view.findViewById(R.id.btn_queding).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						save();
					}
				});
		view.findViewById(R.id.btn_quxiao).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						getActivity().getFragmentManager().popBackStack();
					}
				});
		return view;
	}

	private void save() {
		GamePlayer p = new GamePlayer(et_name.getText().toString(),
				Integer.valueOf(et_level.getText().toString()),
				Integer.valueOf(et_score.getText().toString()));
		updateListener.update(p);
		getActivity().getFragmentManager().popBackStack();
	}
}
