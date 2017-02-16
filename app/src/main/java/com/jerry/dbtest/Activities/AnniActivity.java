package com.jerry.dbtest.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.jerry.dbtest.R;

public class AnniActivity extends Activity {

    private ImageView iv_anni;
    private Animation animation;
    private AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anmi);
        iv_anni = (ImageView) findViewById(R.id.iv_anni);
//        drawable = (AnimationDrawable) iv_anni.getDrawable();
//        drawable.setOneShot(true);
//        animation = AnimationUtils.loadAnimation(this, R.anim.rotates);
//        animation = new AlphaAnimation(0.0f, 1.0f);
//        animation.setDuration(3000);
    }

    public void anniClick(View view) {
        //加载动画资源文件
//        iv_anni.startAnimation(animation);
//        if (drawable.isRunning()) {
//            drawable.stop();
//        } else {
//            drawable.start();
//        }
//        ObjectAnimator.ofFloat(iv_anni, "rotationX", 0f, 360f).setDuration(5000).start();

//        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f, 1.0f);
//        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f, 1.0f);
//        PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f, 1.0f);
//        PropertyValuesHolder p4 = PropertyValuesHolder.ofFloat("rotation", 0f, 360f, 0f);
//        ObjectAnimator.ofPropertyValuesHolder(iv_anni, p1, p2, p3, p4).setDuration(5000).start();

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        ObjectAnimator animator = ObjectAnimator.ofFloat(iv_anni, "translationY", 0f, dm.heightPixels, 0f).setDuration(5000);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                iv_anni.setTranslationY((Float) animation.getAnimatedValue());
//            }
//        });
//        animator.start();

        ObjectAnimator a1 = ObjectAnimator.ofFloat(iv_anni, "translationX", 0f, 200f);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(iv_anni, "translationY", 0f, 200f);
        ObjectAnimator a3 = ObjectAnimator.ofFloat(iv_anni, "rotation", 0f, 360f);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(5000);
        set.play(a1).after(a2);
        set.play(a3).after(a2);
        set.start();
    }
}
