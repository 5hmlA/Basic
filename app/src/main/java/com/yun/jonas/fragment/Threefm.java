package com.yun.jonas.fragment;

import android.view.View;

import com.yun.jonas.R;
import com.yun.jonas.base.BaseFragment;
import com.yun.jonas.utills.UIUtils;

public class Threefm extends BaseFragment {

	@Override
	protected View createAfterNetScceed(){
		fmName = "Three";
		return View.inflate(UIUtils.getContext(), R.layout.fm_three, null);
	}

	@Override
	protected String setRequestURL(){
//		return OttConstants.baidumeinv;
		return "https://www.google.com/chrome/?&brand=CHWL&utm_campaign=en&utm_source=en-et-na-us-chrome-bubble&utm_medium=et";
	}

//	@Override
//	protected View setbeforeSucceedContent(LayoutInflater inflater){
//		return View.inflate(UIUtils.getContext(), R.layout.fm_three, null);
//	}

	@Override
	protected <T> int onNetSucceed(T response){
		return STATE_SUCCEED;
	}
}
