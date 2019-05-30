package com.nice.fincent.bridge;

import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.nice.fincent.MainActivity;


/**
 * 웹->앱 인터페이스
 */
public class AndroidBridge {
    private final Handler handler = new Handler();
    private MainActivity activity;

    public AndroidBridge(MainActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public boolean chkPlatform() { // must be final
        return true;
    }

    /** 지문 인증 */
    @JavascriptInterface
    public void showFingerprint() { // must be final
        handler.post(new Runnable() {
            @Override
            public void run() {
                activity.showFingerprint();
//                    new DatePickerDialog(getActivity(), dateSetListener, year, month, day).show();
            }
        });
    }

    /** 설정 화면 */
    @JavascriptInterface
    public void showSetting() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                activity.showSetting();
            }
        });
    }

    /** 설정 화면 */
    @JavascriptInterface
    public void showLoginSelect() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                activity.showLoginSelect();
            }
        });
    }
}