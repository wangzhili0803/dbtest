package com.vince.dbtest.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.vince.dbtest.R;
import com.vince.dbtest.entity.GamePlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends DialogFragment {
    private AddListener addListener;
    private Activity activity;
    private static AddFragment addFragment;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment getInstance() {
        if (addFragment == null) {
            addFragment = new AddFragment();
        }
        return addFragment;
    }

    public interface AddListener {
        public void add(GamePlayer gamePlayer);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            addListener = (AddListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = activity.getLayoutInflater().inflate(
                R.layout.create_gameplayer_dialog, null);
        return new AlertDialog.Builder(activity)
                .setIcon(R.mipmap.ic_launcher)
                .setView(view)
                .setTitle("添加玩家信息")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GamePlayer gm = new GamePlayer();
                        EditText et_name = (EditText) view
                                .findViewById(R.id.gpname);
                        EditText et_level = (EditText) view
                                .findViewById(R.id.gplevel);
                        EditText et_score = (EditText) view
                                .findViewById(R.id.gpscore);
                        gm.setName(et_name.getText().toString());
                        gm.setLevel(Integer.valueOf(et_level.getText()
                                .toString()));
                        gm.setScore(Integer.valueOf(et_score.getText()
                                .toString()));
                        addListener.add(gm);
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setCancelable(false).create();
    }

}
