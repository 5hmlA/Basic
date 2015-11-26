package com.yun.jonas.utills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

public class AnimationUtills {
	// 属性动画

	private static ScaleAnimation sa;
	private static AlphaAnimation aa;

	// 帧动画====================================
	public static void rotate() {

	}

	/**
	 * 以中心为缩放中心 的缩放动画
	 * 
	 * @param v
	 *            执行动画的view
	 * @param fromX
	 * @param toX
	 * @param fromY
	 * @param toY
	 * @param fillAfter
	 * @param durationMillis
	 * @return 返回 动画对象
	 */
	public static ScaleAnimation scacle(View v, float fromX, float toX,
			float fromY, float toY, boolean fillAfter, long durationMillis) {
		if (sa == null) {
			sa = new ScaleAnimation(fromX, toX, fromY, toY,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		}
		sa.setFillAfter(fillAfter);
		sa.setDuration(durationMillis);
		v.startAnimation(sa);
		return sa;
	}

	/**
	 * 
	 * @param v
	 * @param fromAlpha
	 * @param toAlpha
	 * @param fillAfter
	 * @param durationMillis
	 * @return 返回动画对象
	 */
	public static AlphaAnimation alpha(View v, Context fromAlpha,
			AttributeSet toAlpha, boolean fillAfter, long durationMillis) {
		if (aa == null) {
			aa = new AlphaAnimation(fromAlpha, toAlpha);
		}
		aa.setFillAfter(fillAfter);
		aa.setDuration(durationMillis);
		v.startAnimation(aa);
		return aa;
	}

	/**
	 * 在上一次动画执行之后 继续执行下一次动画
	 * 
	 * @param view
	 *            执行动画的控件
	 * @param fromXDelta
	 *            起点的绝对横坐标
	 * @param toXDelta
	 *            终点绝对横坐标
	 * @param fromYDelta
	 *            起点的绝对纵坐标
	 * @param toYDelta
	 *            终点的绝对纵坐标
	 * @param durationMillis
	 */
	private void TranslateAnimations(final View view, float fromXDelta,
			float toXDelta, float fromYDelta, float toYDelta,
			long durationMillis) {
		final int positionX = (int) toXDelta;
		final int positionY = (int) toYDelta;

		TranslateAnimation ta = new TranslateAnimation(fromXDelta
				- view.getLeft(), toXDelta - view.getLeft(), fromYDelta
				- view.getTop(), toYDelta - view.getTop());
		ta.setDuration(durationMillis);
		ta.setFillAfter(true);
		view.startAnimation(ta);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画执行完后 将控件移动到动画结束的位置
				view.clearAnimation();// 必须清除动画 否则会执行两次
				view.layout(positionX, positionY, positionX + view.getWidth(),
						positionY + view.getHeight());
			}
		});
	}

	/**
	 * 在上一次动画执行之后 继续执行下一次动画
	 * 
	 * @param view
	 *            执行动画的控件
	 * @param fromXDelta
	 *            起点的相对横坐标
	 * @param toXDelta
	 *            终点的相对横坐标
	 * @param fromYDelta
	 *            起点的相对纵坐标
	 * @param toYDelta
	 *            终点的相对纵坐标
	 * @param durationMillis
	 */
	private void TranslateAnimations2(final View view, float fromXDelta,
			float toXDelta, float fromYDelta, float toYDelta,
			long durationMillis) {
		final int positionX = (int) toXDelta + view.getLeft();
		final int positionY = (int) toYDelta + view.getTop();

		TranslateAnimation ta = new TranslateAnimation(fromXDelta, toXDelta,
				fromYDelta, toYDelta);
		ta.setDuration(durationMillis);
		ta.setFillAfter(true);
		view.startAnimation(ta);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画执行完后 将控件移动到动画结束的位置
				view.clearAnimation();// 必须清除动画 否则会执行两次
				view.layout(positionX, positionY, positionX + view.getWidth(),
						positionY + view.getHeight());
			}
		});
	}

	/**
	 * 在原来执行完动画效果后的基础上继续执行动画
	 * 
	 * @param view
	 *            执行动画的控件
	 * @param moveX
	 *            x轴移动的距离
	 * @param moveY
	 *            y轴移动的距离
	 * @param durationMillis
	 *            持续时间
	 */
	private void TranslateAnimations(final View view, final int moveX,
			final int moveY, long durationMillis) {
		TranslateAnimation ta = new TranslateAnimation(0, moveX, 0, moveY);
		ta.setDuration(durationMillis);
		ta.setFillAfter(true);
		view.startAnimation(ta);
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.clearAnimation();// 必须清除动画 否则会执行两次
//				view.layout(view.getLeft() + moveX, view.getTop() + moveY,
//						view.getLeft() + moveX + view.getWidth(), view.getTop()
//								+ moveY + view.getHeight());
				view.layout(view.getLeft() + moveX, view.getTop() + moveY,
						view.getRight() + moveX, view.getBottom() + moveY);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

	}

}
