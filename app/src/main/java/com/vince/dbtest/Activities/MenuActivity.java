package com.vince.dbtest.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.vince.dbtest.R;

import java.util.ArrayList;

public class MenuActivity extends Activity implements View.OnClickListener {

    private int[] ids = new int[]{
            R.id.iv1,
            R.id.iv2,
            R.id.iv3,
            R.id.iv4,
            R.id.iv5,
            R.id.iv6
    };
    private boolean isOpen = false;
    private ArrayList<ImageView> ivs = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = (ImageView) findViewById(ids[i]);
            imageView.setOnClickListener(this);
            ivs.add(imageView);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv1:
                if (isOpen) {
                    hideMenu();
                } else {
                    showMenu();
                }

                break;
            case R.id.iv2:
                break;
            case R.id.iv3:
                break;
            case R.id.iv4:
                break;
            case R.id.iv5:
                break;
            case R.id.iv6:
                break;
        }
    }

    private void hideMenu() {
        for (int i = 0; i < ivs.size(); i++) {
            ObjectAnimator a1 = ObjectAnimator.ofFloat(ivs.get(i), "translationX", 150 * i, 0.0f);
            ObjectAnimator a2 = ObjectAnimator.ofFloat(ivs.get(i), "rotation", 360f, 0.0f);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(1000);
            set.playTogether(a1, a2);
            set.setInterpolator(new AnticipateOvershootInterpolator());
            set.start();
        }
        isOpen = false;
    }

    private void showMenu() {
        for (int i = 0; i < ivs.size(); i++) {
            ObjectAnimator a1 = ObjectAnimator.ofFloat(ivs.get(i), "translationX", 0.0f, 150 * i);
            ObjectAnimator a2 = ObjectAnimator.ofFloat(ivs.get(i), "rotation", 0.0f, 360f);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(1000);
            set.playTogether(a1, a2);
            set.setInterpolator(new BounceInterpolator());
            set.start();
        }
        isOpen = true;
    }
}
