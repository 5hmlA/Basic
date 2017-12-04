package com.blueprint.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.blueprint.LibApp;

/**
 * Created by _SOLID
 * Date:2016/4/22
 * Time:11:45
 */
public class ClipboardHelper {
    private static ClipboardManager mClipboardManager;
    private static ClipboardManager mNewCliboardManager;


    private static boolean isNew() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private static void instance() {
        if (isNew()) {
            if (mNewCliboardManager == null)
                mNewCliboardManager = (ClipboardManager) LibApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        } else {
            if (mClipboardManager == null)
                mClipboardManager = (ClipboardManager) LibApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }
    }

    /**
     * 为剪切板设置内容
     *
     * @param text
     */
    public static void setText(CharSequence text) {
        if (isNew()) {
            instance();
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("simple text", text);

            // Set the clipboard's primary clip.
            mNewCliboardManager.setPrimaryClip(clip);
        } else {
            instance();
            mClipboardManager.setText(text);
        }
    }

    /**
     * 获取剪切板的内容
     *
     * @return
     */
    public static CharSequence getText() {
        StringBuilder sb = new StringBuilder();
        if (isNew()) {
            instance();
            if (!mNewCliboardManager.hasPrimaryClip()) {
                return sb.toString();
            } else {
                ClipData clipData = (mNewCliboardManager).getPrimaryClip();
                int count = clipData.getItemCount();

                for (int i = 0; i < count; ++i) {

                    ClipData.Item item = clipData.getItemAt(i);
                    CharSequence str = item.coerceToText(LibApp.getContext());
                    sb.append(str);
                }
            }

        } else {
            instance();
            sb.append(mClipboardManager.getText());
        }
        return sb.toString();
    }


    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    public static void copyUri(final Uri uri) {
        ClipboardManager clipboard = (ClipboardManager) LibApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newUri(LibApp.getContext().getContentResolver(), "uri", uri));
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    public static Uri getUri() {
        ClipboardManager clipboard = (ClipboardManager) LibApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getUri();
        }
        return null;
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    public static void copyIntent(final Intent intent) {
        ClipboardManager clipboard = (ClipboardManager) LibApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newIntent("intent", intent));
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    public static Intent getIntent() {
        ClipboardManager clipboard = (ClipboardManager) LibApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getIntent();
        }
        return null;
    }
}
