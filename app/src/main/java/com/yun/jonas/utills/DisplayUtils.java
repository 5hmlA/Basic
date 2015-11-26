package com.yun.jonas.utills;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import java.util.List;

public class DisplayUtils {


	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue,Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 得到的屏幕的宽度
	 */
	public static int getWidthPx(Activity activity) {
		// DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
		DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);// 对该结构赋值
		return displaysMetrics.widthPixels;
	}

	/**
	 * 得到的屏幕的高度
	 */
	public static int getHeightPx(Activity activity) {
		// DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
		DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);// 对该结构赋值
		return displaysMetrics.heightPixels;
	}

	/**
	 * 得到屏幕的dpi
	 * @param activity
	 * @return
	 */
	public static int getDensityDpi(Activity activity) {
		// DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
		DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);// 对该结构赋值
		return displaysMetrics.densityDpi;
	}

	/**
	 * 返回状态栏/通知栏的高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getStatusHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	// Action 添加Shortcut
	public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	// Action 移除Shortcut
	public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
	//========================快捷方式创建========================================
	/**
	 * 正宗google少林派 通过广播创建快捷方式：
	 * 添加快捷方式
	 *
	 * @param context      context
	 * @param actionIntent 要启动的Intent
	 * @param name         name
	 */
	public static void addShortcut(Context context, Intent actionIntent, String name,
								   boolean allowRepeat, Bitmap iconBitmap) {
		Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
		// 是否允许重复创建  duplicate这个属性，是设置该快捷方式是否允许多次创建的属性，但是，在很多ROM上都不能成功识别，嗯，这就是我们最开始说的快捷方式乱现象。
		addShortcutIntent.putExtra("duplicate", allowRepeat);
		// 快捷方式的标题
		addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		// 快捷方式的图标
		addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconBitmap);
		// 快捷方式的动作
		addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
		context.sendBroadcast(addShortcutIntent);
	}

	/**
	 *
	 * 正宗google少林派 通过广播创建快捷方式：
	 * 移除快捷方式
	 *
	 * @param context      context
	 * @param actionIntent 要启动的Intent
	 * @param name         name
	 */
	public static void removeShortcut(Context context, Intent actionIntent, String name) {
		Intent intent = new Intent(ACTION_REMOVE_SHORTCUT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.putExtra("duplicate", false);
		//Intent.EXTRA_SHORTCUT_INTENT，与之前创建快捷方式的Intent必须要是同一个，不然是无法删除快捷方式的。
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
		context.sendBroadcast(intent);
	}


	/**
	 * 为PackageName的App添加快捷方式
	 *
	 * @param context context
	 * @param pkg     待添加快捷方式的应用包名
	 * @return 返回true为正常执行完毕
	 */
	public static boolean addShortcutByPackageName(Context context, String pkg) {
		// 快捷方式名
		String title = "unknown";
		// MainActivity完整名
		String mainAct = null;
		// 应用图标标识
		int iconIdentifier = 0;
		// 根据包名寻找MainActivity
		PackageManager pkgMag = context.getPackageManager();
		Intent queryIntent = new Intent(Intent.ACTION_MAIN, null);
		queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);// 重要，添加后可以进入直接已经打开的页面
		queryIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		queryIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);

		List<ResolveInfo> list = pkgMag.queryIntentActivities(queryIntent,
				PackageManager.GET_ACTIVITIES);
		for (int i = 0; i < list.size(); i++) {
			ResolveInfo info = list.get(i);
			if (info.activityInfo.packageName.equals(pkg)) {
				title = info.loadLabel(pkgMag).toString();
				mainAct = info.activityInfo.name;
				iconIdentifier = info.activityInfo.applicationInfo.icon;
				break;
			}
		}
		if (mainAct == null) {
			// 没有启动类
			return false;
		}
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 不允许重复创建
		shortcut.putExtra("duplicate", false);
		ComponentName comp = new ComponentName(pkg, mainAct);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				queryIntent.setComponent(comp));
		// 快捷方式的图标
		Context pkgContext = null;
		if (context.getPackageName().equals(pkg)) {
			pkgContext = context;
		} else {
			// 创建第三方应用的上下文环境，为的是能够根据该应用的图标标识符寻找到图标文件。
			try {
				pkgContext = context.createPackageContext(pkg,
						Context.CONTEXT_IGNORE_SECURITY
								| Context.CONTEXT_INCLUDE_CODE);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (pkgContext != null) {
			Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(pkgContext, iconIdentifier);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		}
		// 发送广播，让接收者创建快捷方式
		// 需权限<uses-permission
		// android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
		context.sendBroadcast(shortcut);
		return true;
	}

	/**
	 * 检查快捷方式是否存在 <br/>
	 * <font color=red>注意：</font> 有些手机无法判断是否已经创建过快捷方式<br/>
	 * 因此，在创建快捷方式时，请添加<br/>
	 * shortcutIntent.putExtra("duplicate", false);// 不允许重复创建<br/>
	 * 最好使用{@link #isShortCutExist(Context, String, Intent)}
	 * 进行判断，因为可能有些应用生成的快捷方式名称是一样的的<br/>
	 */
//	public static boolean isShortCutExist(Context context, String title) {
//		boolean result = false;
//		try {
//			ContentResolver cr = context.getContentResolver();
//			Uri uri = getUriFromLauncher(context);
//			Cursor c = cr.query(uri, new String[]{"title"}, "title=? ", new String[]{title}, null);
//			if (c != null && c.getCount() > 0) {
//				result = true;
//			}
//			if (c != null && !c.isClosed()) {
//				c.close();
//			}
//		} catch (Exception e) {
//			result = false;
//			e.printStackTrace();
//		}
//		return result;
//	}

	/**
	 * 不一定所有的手机都有效，因为国内大部分手机的桌面不是系统原生的<br/>
	 * 更多请参考{@link #isShortCutExist(Context, String)}<br/>
	 * 桌面有两种，系统桌面(ROM自带)与第三方桌面，一般只考虑系统自带<br/>
	 * 第三方桌面如果没有实现系统响应的方法是无法判断的，比如GO桌面<br/>
	 */
//	public static boolean isShortCutExist(Context context, String title, Intent intent) {
//		boolean result = false;
//		try {
//			ContentResolver cr = context.getContentResolver();
//			Uri uri = getUriFromLauncher(context);
//			Cursor c = cr.query(uri, new String[]{"title", "intent"}, "title=?  and intent=?",
//					new String[]{title, intent.toUri(0)}, null);
//			if (c != null && c.getCount() > 0) {
//				result = true;
//			}
//			if (c != null && !c.isClosed()) {
//				c.close();
//			}
//		} catch (Exception ex) {
//			result = false;
//			ex.printStackTrace();
//		}
//		return result;
//	}

//	private static Uri getUriFromLauncher(Context context) {
//		StringBuilder uriStr = new StringBuilder();
//		String authority = LauncherUtil.getAuthorityFromPermissionDefault(context);
//		if (authority == null || authority.trim().equals("")) {
//			authority = LauncherUtil.getAuthorityFromPermission(context, LauncherUtil.getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");
//		}
//		uriStr.append("content://");
//		if (TextUtils.isEmpty(authority)) {
//			int sdkInt = android.os.Build.VERSION.SDK_INT;
//			if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
//				uriStr.append("com.android.launcher.settings");
//			} else if (sdkInt < 19) {// Android 4.4以下
//				uriStr.append("com.android.launcher2.settings");
//			} else {// 4.4以及以上
//				uriStr.append("com.android.launcher3.settings");
//			}
//		} else {
//			uriStr.append(authority);
//		}
//		uriStr.append("/favorites?notify=true");
//		return Uri.parse(uriStr.toString());
//	}


}
