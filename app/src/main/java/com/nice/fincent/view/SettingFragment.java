package com.nice.fincent.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.nice.fincent.MainActivity;
import com.nice.fincent.R;
import com.nice.fincent.define.Define;
import com.nice.fincent.util.DataStorage;
import com.nice.fincent.widget.FincentDialog;
import com.nice.fincent.widget.SettingItem;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SettingFragment extends MainFragment {
    private MainActivity activity;

    private SettingItem fingerLoginBtn;
    private SettingItem numberLoginBtn;
    private SettingItem versionBtn;
    private SettingItem compAlarmBtn;
    private SettingItem appAlarmBtn;
    private SettingItem noticeAlarmBtn;

    public SettingFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        init(rootView);

        return rootView;
    }

    public void init(View view){
        activity = (MainActivity)getActivity();

        fingerLoginBtn = (SettingItem)view.findViewById(R.id.btn_login_finger);
        numberLoginBtn = (SettingItem)view.findViewById(R.id.btn_login_number);
        fingerLoginBtn.setChecked(DataStorage.getBoolean(Define.LOGIN_FINGER));
        numberLoginBtn.setChecked(DataStorage.getBoolean(Define.LOGIN_NUMBER));

        compAlarmBtn = (SettingItem)view.findViewById(R.id.btn_alarm_comp);
        appAlarmBtn = (SettingItem)view.findViewById(R.id.btn_alarm_app);
        noticeAlarmBtn = (SettingItem)view.findViewById(R.id.btn_alarm_notice);
        compAlarmBtn.setChecked(DataStorage.getBoolean(Define.ALARM_COMP));
        appAlarmBtn.setChecked(DataStorage.getBoolean(Define.ALARM_APP));
        noticeAlarmBtn.setChecked(DataStorage.getBoolean(Define.ALARM_NOTICE));
        //서버 푸시 알람 설정 로드


        versionBtn = (SettingItem)view.findViewById(R.id.btn_version);

        versionBtn.setVersion("V1.0.1");
    }

    @Override
    public void onResume() {
        super.onResume();

        fingerLoginBtn.setChecked(DataStorage.getBoolean(Define.LOGIN_FINGER));
        numberLoginBtn.setChecked(DataStorage.getBoolean(Define.LOGIN_NUMBER));

        compAlarmBtn.setChecked(DataStorage.getBoolean(Define.ALARM_COMP));
        appAlarmBtn.setChecked(DataStorage.getBoolean(Define.ALARM_APP));
        noticeAlarmBtn.setChecked(DataStorage.getBoolean(Define.ALARM_NOTICE));
    }

    @Override
    public void onClick(View v) {
        switch((int)v.getTag()){
            case R.id.btn_alarm_comp:
                DataStorage.setBoolean(Define.ALARM_COMP, compAlarmBtn.isChecked());
                //서버 푸시 알람 세팅
                break;
            case R.id.btn_alarm_app:
                DataStorage.setBoolean(Define.ALARM_APP, appAlarmBtn.isChecked());
                //서버 푸시 알람 세팅
                break;
            case R.id.btn_alarm_notice:
                DataStorage.setBoolean(Define.ALARM_NOTICE, noticeAlarmBtn.isChecked());
                //서버 푸시 알람 세팅
                break;
            case R.id.btn_login_setting:
                //간편로그인 화면
                activity.showLoginSetting();
                break;
            case R.id.btn_login_number:
//간편번호 저장 여부..  체크할것
                if(numberLoginBtn.isChecked()){
                    final FincentDialog dialog = new FincentDialog(activity);
                    dialog.setMessage(R.string.dialog_set_simple_pass);
                    dialog.setBtn(R.string.dialog_confirm_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //DataStorage.setBoolean(Define.LOGIN_NUMBER, numberLoginBtn.isChecked());
                            //체크 여부 판단은 다음 화면 이후..
                            activity.showRegSimplePass();
                            dialog.dismiss();
                        }
                    }, R.string.dialog_cancel_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            numberLoginBtn.setChecked(false);
                            dialog.dismiss();
                            DataStorage.setBoolean(Define.LOGIN_NUMBER, numberLoginBtn.isChecked());
                        }
                    });
                    dialog.show();
                }else{
                    final FincentDialog dialog = new FincentDialog(activity);
                    dialog.setMessage(R.string.dialog_cancel_simple_pass);
                    dialog.setBtn(R.string.dialog_confirm_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            DataStorage.setBoolean(Define.LOGIN_NUMBER, numberLoginBtn.isChecked());
                        }
                    }, R.string.dialog_cancel_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            numberLoginBtn.setChecked(true);
                            dialog.dismiss();
                            DataStorage.setBoolean(Define.LOGIN_NUMBER, numberLoginBtn.isChecked());
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.btn_login_finger:
                //저장된 지문등록 여부 체크할것
                if(fingerLoginBtn.isChecked()){
                    final FincentDialog dialog = new FincentDialog(activity);
                    dialog.setMessage(R.string.dialog_set_fingerprint);
                    dialog.setBtn(R.string.dialog_confirm_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //DataStorage.setBoolean(Define.LOGIN_NUMBER, numberLoginBtn.isChecked());
                            //체크 여부 판단은 다음 화면 이후..
                            activity.showRegFingerprint();
                            dialog.dismiss();
                        }
                    }, R.string.dialog_cancel_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fingerLoginBtn.setChecked(false);
                            dialog.dismiss();
                            DataStorage.setBoolean(Define.LOGIN_FINGER, fingerLoginBtn.isChecked());
                        }
                    });
                    dialog.show();
                }else{
                    final FincentDialog dialog = new FincentDialog(activity);
                    dialog.setMessage(R.string.dialog_cancel_fingerprint);
                    dialog.setBtn(R.string.dialog_confirm_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            DataStorage.setBoolean(Define.LOGIN_FINGER, fingerLoginBtn.isChecked());
                        }
                    }, R.string.dialog_cancel_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fingerLoginBtn.setChecked(true);
                            dialog.dismiss();
                            DataStorage.setBoolean(Define.LOGIN_FINGER, fingerLoginBtn.isChecked());
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.btn_certificate:
                //공인인증서 관리
                activity.showCertManager();
                break;
            case R.id.btn_authority:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", activity.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //권한 설정
                break;
            case R.id.btn_version:
                //업데이트
                break;
        }

    }
}
