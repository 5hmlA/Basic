package com.yun.jonas.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class FmFactory {

	private static final int ONE = 0;
	private static final int TWO = 1;
	private static final int THREE = 2;
	private static final int FOUR = 3;

	//api19---arrayMap,SparseArray

	private static Map<Integer, Fragment> fmCache = new HashMap<Integer, Fragment>();
	public static Fragment createFragment(int type) {
		Fragment fragment = fmCache.get(type);
		if (fragment == null) {
			switch (type) {
			case ONE:
				fragment = new Onefm();
				break;
			case TWO:
				fragment = new Twofm();
				break;
			case THREE:
				fragment = new Threefm();
				break;
			case FOUR:
				fragment = new Fourfm();
				break;
			}
			fmCache.put(type, fragment);
		}
		return fragment;
	}
}
