package com.yun.jonas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.yun.jonas.application.BaseApplication;
import com.yun.jonas.utills.DeviceUtils;
import com.yun.jonas.utills.DisplayUtils;
import com.yun.jonas.utills.OttConstants;
import com.yun.jonas.utills.SPUtills;
import com.yun.jonas.utills.UIUtils;

public class WelcomeActivity extends Activity implements AnimationListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		System.out.println(DeviceUtils.getDeviceId(this));
		BaseApplication.screenW = DisplayUtils.getWidthPx(this);
		BaseApplication.screenH = DisplayUtils.getHeightPx(this);

		boolean networkConnected = DeviceUtils.isNetworkConnected(this);
		Toast.makeText(getApplicationContext(), ""+networkConnected, Toast.LENGTH_SHORT).show();
		init();
	}

	private void init() {
		View rootView = findViewById(R.id.rl_welcome_root);

		RotateAnimation rotateAnima = new RotateAnimation(
				0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnima.setDuration(1000);
		rotateAnima.setFillAfter(true); // 设置动画执行完毕时, 停留在完毕的状态下.

		ScaleAnimation scaleAnima = new ScaleAnimation(
				0, 1,
				0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnima.setDuration(1000);
		scaleAnima.setFillAfter(true);

		AlphaAnimation alphaAnima = new AlphaAnimation(0, 1);
		alphaAnima.setDuration(2000);
		alphaAnima.setFillAfter(true);


		// 把三个动画合在一起, 组成一个集合动画
		AnimationSet setAnima = new AnimationSet(false);
		setAnima.addAnimation(rotateAnima);
		setAnima.addAnimation(scaleAnima);
		setAnima.addAnimation(alphaAnima);
		setAnima.setAnimationListener(this);

		rootView.startAnimation(setAnima);
	}

	@Override
	public void onAnimationEnd(Animation animation){
		// 去文件中取是否打开过程序的值
		boolean isOpenMainPager = (boolean)SPUtills.get(UIUtils.getContext(), OttConstants.IS_OPEN_MAIN_PAGER, false);
		Intent intent = new Intent();
		if(isOpenMainPager) {
			// 已经打开过一次主界面, 直接进入主界面.
			intent.setClass(WelcomeActivity.this, MainActivity.class);
		}else {
			// 没有打开过主界面, 进入引导页面.
			intent.setClass(WelcomeActivity.this, GuideActivity.class);
		}
		startActivity(intent);

		// 关闭掉欢迎界面
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}
}
