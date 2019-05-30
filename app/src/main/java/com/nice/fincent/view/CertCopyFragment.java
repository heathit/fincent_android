package com.nice.fincent.view;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.infotech.IFTCrypto.InfoTecCore;
import com.loopj.android.http.Base64;
import com.nice.fincent.MainActivity;
import com.nice.fincent.R;
import com.nice.fincent.net.SHttpConnect;
import com.nice.fincent.net.SHttpExecutor;
import com.nice.fincent.net.SHttpExecutorListener;
import com.nice.fincent.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class CertCopyFragment extends MainFragment {
    private MainActivity activity;

    private TextView certUID1Tv;
    private TextView certUID2Tv;
    private TextView certUID3Tv;

    private String uid = "";

    public CertCopyFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cert_copy, container, false);

        init(rootView);

        getUid();

        return rootView;
    }

    public void init(View view){
        activity = (MainActivity)getActivity();

        certUID1Tv = (TextView)view.findViewById(R.id.tv_cert_uid1);
        certUID2Tv = (TextView)view.findViewById(R.id.tv_cert_uid2);
        certUID3Tv = (TextView)view.findViewById(R.id.tv_cert_uid3);
    }

    public void getUid() {

        Log.e("getUid", "getUid");

        new SHttpExecutor<String>()
                .setCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return new SHttpConnect().httpRequest(
                                //"http://192.168.0.18:8680/nx/src/nxCrtC.jsp"
                                "http://www.infotech3.co.kr/nx/src/nxCrtC.jsp"
                                , "POST"
                                , 1000 * 5
                                , 1000 * 30
                                , "application/json"
                                , "application/x-www-form-urlencoded"
                                , "no-cache"
                                , new Uri.Builder()
                                        .appendQueryParameter("op", "getUid")
                                        .build().getEncodedQuery()
                                , "UTF-8"
                        );
                    }
                })
                .setHttpResponseHandler(getUidListener)
                .execute("http://www.infotech3.co.kr/nx/src/nxCrtC.jsp");
    }


    private SHttpExecutorListener<String> getUidListener = new SHttpExecutorListener<String>() {

        @Override
        public void onResponse(String response) {

            Log.e("response", response);

            if(response != null) {
                HashMap<String, String> responseMap = new Gson().fromJson(response, new TypeToken<HashMap<String, String>>() {}.getType());
                uid = responseMap.get("uid") != null && responseMap.get("uid").length() == 12 ? responseMap.get("uid") : "";
            }

            if(uid.length() > 0) {
                certUID1Tv.setText(uid.substring(0, 4));
                certUID2Tv.setText(uid.substring(4, 8));
                certUID3Tv.setText(uid.substring(8));
            } else {

            }
        }

        @Override
        public void onException(Exception e) {

        }

        @Override
        public void onCancelled() {

        }
    };

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_cert:
                //인증서 가져오기
                getCert();
                break;
            case R.id.btn_close:
                activity.goBackFragment();
                break;
        }

    }

    private void getCert(){
        new SHttpExecutor<String>()
                .setCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return new SHttpConnect().httpRequest(
                                //"http://192.168.0.18:8680/nx/src/nxCrtC.jsp"
                                "http://www.infotech3.co.kr/nx/src/nxCrtC.jsp"
                                , "POST"
                                , 1000 * 5
                                , 1000 * 30
                                , "application/json"
                                , "application/x-www-form-urlencoded"
                                , "no-cache"
                                , new Uri.Builder()
                                        .appendQueryParameter("op", "getCrt")
                                        .appendQueryParameter("uid", uid)
                                        .build().getEncodedQuery()
                                , "UTF-8"
                        );
                    }
                })
                .setHttpResponseHandler(getCertListener)
                .execute("http://www.infotech3.co.kr/nx/src/nxCrtC.jsp");
    }

    private SHttpExecutorListener<String> getCertListener = new SHttpExecutorListener<String>() {

        @Override
        public void onResponse(String response) {

            HashMap<String, Object> responseMap = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());

            if(responseMap.containsKey("outJson") && responseMap.get("outJson") != null) {

                LinkedTreeMap<String, String> outJson = (LinkedTreeMap<String, String>) responseMap.get("outJson");

                if(outJson.containsKey("data") && outJson.get("data").length() > 0) {

                    InfoTecCore infoTecCore = new InfoTecCore(activity);

                    String data = infoTecCore.decryptEx(uid, outJson.get("data"));
                    data = data.substring(0, data.lastIndexOf("}") + 1);

                    HashMap<String, String> certJson = new Gson().fromJson(data, new TypeToken<HashMap<String, String>>(){}.getType());

                    if(certJson != null && !certJson.isEmpty()) {

                        String externalStorageDirectory = "" + Environment.getExternalStorageDirectory();
                        externalStorageDirectory += "/NPKI";
                        externalStorageDirectory += "/" + certJson.get("org_nm");
                        externalStorageDirectory += "/USER";
                        externalStorageDirectory += "/" + certJson.get("cert_dir");
                        if(!(new File(externalStorageDirectory).exists())) {
                            new File(externalStorageDirectory).mkdirs();
                        }

                        try {

                            File signDerF = new File(externalStorageDirectory + "/signCert.der");
                            byte[] signDerB = Util.hexStr2Byte(certJson.get("signDer"));
                            signDerB = Base64.decode(certJson.get("signDer"), 0);
                            OutputStream signDerOS;
                            signDerOS = new FileOutputStream(signDerF);
                            signDerOS.write(signDerB);
                            signDerOS.flush();
                            signDerOS.close();

                            File signPriF = new File(externalStorageDirectory + "/signPri.key");
                            byte[] signPriB = Util.hexStr2Byte(certJson.get("signPri"));
                            signPriB = Base64.decode(certJson.get("signPri"), 0);
                            OutputStream signPriOS;
                            signPriOS = new FileOutputStream(signPriF);
                            signPriOS.write(signPriB);
                            signPriOS.flush();
                            signPriOS.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        copyComplete("인증서 가져오기가 완료되었습니다.");
                    } else {
                        copyFaild("인증서 가져오기를 실패하였습니다.\\n<![CDATA[\"<\"]]>인증서 복사<![CDATA[\">\"]]> 에서 인증서를 복사해주세요.\\n같은 문제가 반복되면 앱 종료후 다시 시도해 주세요.");
                    }//end of certJson
                }//end of data in outJson
            } else {
                copyFaild("인증서 가져오기를 실패하였습니다.\\n<![CDATA[\"<\"]]>인증서 복사<![CDATA[\">\"]]> 에서 인증서를 복사해주세요.\\n같은 문제가 반복되면 앱 종료후 다시 시도해 주세요.");
            }
            //end of outJson
        }

        @Override
        public void onException(Exception e) {

        }

        @Override
        public void onCancelled() {

        }
    };

    private void copyComplete(String msg) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//
//        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getRootView().getWindowToken(), 0);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder
//                .setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton(getResources().getString(R.string.cmm_confirm), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                        finish();
//                        Intent exaCertList = new Intent(getBaseContext(), ExaCertList.class);
//                        exaCertList.putExtra("listType", "certList");
//                        startActivity(exaCertList);
//                    }
//                });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
    }

    private void copyFaild(String msg) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getRootView().getWindowToken(), 0);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder
//                .setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton(getResources().getString(R.string.cmm_confirm), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
    }
}
