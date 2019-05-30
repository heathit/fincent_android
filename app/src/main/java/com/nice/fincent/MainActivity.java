package com.nice.fincent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.nice.fincent.bridge.AndroidBridge;
import com.nice.fincent.define.Define;
import com.nice.fincent.define.Properties;
import com.nice.fincent.define.TAGs;
import com.nice.fincent.define.URL;
import com.nice.fincent.util.DataStorage;
import com.nice.fincent.util.WatingDialog;
import com.nice.fincent.view.CertCopyFragment;
import com.nice.fincent.view.CertManagerFragment;
import com.nice.fincent.view.FingerPrintFragment;
import com.nice.fincent.view.MainFragment;
import com.nice.fincent.view.RegFingerprintFragment;
import com.nice.fincent.view.SelectLoginFragment;
import com.nice.fincent.view.SettingFragment;
import com.nice.fincent.view.SettingLoginFragment;
import com.nice.fincent.view.SimplePassFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
    private int PERMISSIONS_REQUEST_CODE = 1001;

    public static final int LOGIN_ACTIVITY_FLAG = 2001;
    public static final int WEB_ACTIVITY_FLAG = 2002;

    private LayoutInflater inflater;
    private FragmentManager fragmentManager;

    private WebView webview;
    private WatingDialog watingDialog;

    private ValueCallback<Uri> fileCallback;
    private ValueCallback<Uri[]> fileCallbackOverLollypop;

    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        init();

        webviewInit();

        chkPermission();

        webview.loadUrl(URL.TEST_URL);
//        webview.loadUrl("https://www.daum.net/");
//        webview.loadUrl("https://encodable.com/uploaddemo/");
//        webview.loadUrl("https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb");
    }

    private void init(){
        new DataStorage(this);

        watingDialog = new WatingDialog(this);
        webview = findViewById(R.id.webview);

        inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = getSupportFragmentManager();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.reload_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.reload();
            }
        });
    }

    private void webviewInit() {

        webview.setWebChromeClient(new WebChromeClient() {
            // For Android < 3.0
            public void openFileChooser( ValueCallback<Uri> callback) {
                Log.d(TAGs.WEB, "input file (android 3.0-)");
                openFileChooser(callback, "");
            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback<Uri> callback, String acceptType) {
                Log.d(TAGs.WEB, "input file (android 3.0+)");
                fileCallbackOverLollypop = null;
                fileCallback = callback;

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.WEB_ACTIVITY_FLAG);
            }
            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> callback, String acceptType, String capture) {
                Log.d(TAGs.WEB, "input file (android 4.1+)");
                openFileChooser(callback, acceptType);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams fileChooserParams) {
                Log.d(TAGs.WEB, "input file (android 5.0+)");

                fileCallbackOverLollypop = callback;
                fileCallback = null;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(intent, "File Chooser"), MainActivity.WEB_ACTIVITY_FLAG);

                return true;
            }

        });

        webview.addJavascriptInterface(new AndroidBridge(this), Define.APP_IF);

        setUpWebViewDefaults(webview);
    }


    private void chkPermission() {
        int iChk = 0;
        for(String permission : REQUIRED_PERMISSIONS){
            iChk += ContextCompat.checkSelfPermission(this, permission);
        }
        if (iChk == PackageManager.PERMISSION_GRANTED) {

            //이미 퍼미션을 가진 경우
            //앱 스타트
        }else {
            boolean bChk = false;
            for(String permission : REQUIRED_PERMISSIONS){
                bChk = bChk || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            }
            // 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (bChk){
                //요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(webview, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //앱 스타트
    }



    public void replaceFragment(Fragment fragment, Bundle bundle ){
        if(bundle!=null) fragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.layout_fragment, fragment);
        transaction.commit();
    }


    public boolean clearFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments == null || fragments.size() == 0) return false;
        for (Fragment fragment: fragments) {
            if (fragment!=null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        return true;
    }

    public boolean goBackFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments == null || fragments.size() == 0) return false;
        Fragment fragment = fragments.get(0);
        if (fragment!=null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager().popBackStack();

            return true;
        }
        return false;
    }

    public MainFragment getCurrentFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments == null || fragments.size() == 0) return null;
        return (MainFragment)fragments.get(0);
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(false);
        settings.setLoadWithOverviewMode(false);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(false);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        webView.setWebViewClient(new WebViewClient() {

            // 링크 클릭에 대한 반응
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("tel:")){//전화걸기
                    Intent call_phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(call_phone);
                    return true;
                }

                view.loadUrl(url);
                return true;
            }


            // 웹페이지 호출시 오류 발생에 대한 처리
            @Override
            public void onReceivedError(WebView view, int errorcode,
                                        String description, String fallingUrl) {
            }

            // 페이지 로딩 시작시 호출
            @Override
            public void onPageStarted(WebView view,String url , Bitmap favicon){
                if(refreshLayout.isRefreshing()==false){
                    watingDialog.startWaitingDialog("잠시기다려주세요");
                }
            }

            //페이지 로딩 종료시 호출
            public void onPageFinished(WebView view,String Url){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        watingDialog.dismissWaitingDialog();
                        refreshLayout.setRefreshing(false);
                    }
                }, 200);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");
                    String fileName = contentDisposition.replace("inline; filename=", "").replaceAll("\"", "");
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(MainActivity.this, "파일을 다운로드 중입니다.", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    e.printStackTrace();
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WEB_ACTIVITY_FLAG) {
            if (fileCallback != null) {
                Uri result = (data == null || resultCode != Activity.RESULT_OK) ? null : data.getData();
                fileCallback.onReceiveValue(result);
            } else if (fileCallbackOverLollypop != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fileCallbackOverLollypop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                }
            }

            fileCallback = null;
            fileCallbackOverLollypop = null;
        }
    }


    //지문
    public void showFingerprint(){
        // 6.0 이상만
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            replaceFragment(new FingerPrintFragment(), null);
        }
    }

    /* 설정화면 */
    public void showSetting() {
        replaceFragment(new SettingFragment(), null);
    }
    /* 간편로그인설정화면 */
    public void showLoginSetting() {
        replaceFragment(new SettingLoginFragment(), null);
    }
    /* 공인인증서 관리 */
    public void showCertManager() {
        replaceFragment(new CertManagerFragment(), null);
    }
    /* 공인인증서 가져오기 */
    public void showCertCopy() {
        replaceFragment(new CertCopyFragment(), null);
    }
    /* 간편번호 등록 */
    public void showRegSimplePass() {
        replaceFragment(new SimplePassFragment(), null);
    }
    /* 지문 등록 */
    public void showRegFingerprint() {
        replaceFragment(new RegFingerprintFragment(), null);
    }
    /* 로그인 선택 창 */
    public void showLoginSelect() {
        replaceFragment(new SelectLoginFragment(), null);
    }


    public void onClick(View v){
        MainFragment fragment = getCurrentFragment();
        if(fragment != null){
            fragment.onClick(v);
        }
    }

    long checkTime = 0;
    @Override
    public void onBackPressed() {
        if(goBackFragment()){

        }else if(webview.canGoBack()){
            webview.goBack();
        }else{
            long currentTime = System.currentTimeMillis();
            if (currentTime - checkTime < Properties.FINISH_CHK_TIME) {
                // 설정된 시간 이내에 백버튼을 다시 누른 경우 => 앱 종료
                finish();
            } else {
                Toast.makeText(this, "'뒤로'버튼을 다시 누르면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();

                checkTime = currentTime;
            }
        }
    }

}
