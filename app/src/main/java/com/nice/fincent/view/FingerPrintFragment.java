package com.nice.fincent.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
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

public class FingerPrintFragment extends MainFragment {

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private MainActivity activity;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;

    public FingerPrintFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finger, container, false);

        init();

        initFingerprint();

        return rootView;
    }

    public void init(){
        activity = (MainActivity)getActivity();
    }

    public void initFingerprint(){
        Log.d("TEST", ">>>> initFingerprint");

        fingerprintManager = (FingerprintManager) activity.getSystemService(activity.FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) activity.getSystemService(activity.KEYGUARD_SERVICE);

        if(!fingerprintManager.isHardwareDetected()){//Manifest에 Fingerprint 퍼미션을 추가해 워야 사용가능
            Toast.makeText(activity, "지문을 사용할 수 없는 디바이스 입니다.", Toast.LENGTH_SHORT).show();
        } else if(ContextCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity,"지문사용을 허용해 주세요.", Toast.LENGTH_SHORT).show();
            /*잠금화면 상태를 체크한다.*/
        } else if(!keyguardManager.isKeyguardSecure()){
            Toast.makeText(activity,"잠금화면을 설정해 주세요.", Toast.LENGTH_SHORT).show();
        } else if(!fingerprintManager.hasEnrolledFingerprints()){
            Toast.makeText(activity,"등록된 지문이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {//모든 관문을 성공적으로 통과(지문인식을 지원하고 지문 사용이 허용되어 있고 잠금화면이 설정되었고 지문이 등록되어 있을때)
            //Toast.makeText(activity,"손가락을 홈버튼에 대 주세요.", Toast.LENGTH_SHORT).show();

            generateKey();
            if(cipherInit()){
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                //핸들러실행
                FingerprintHandler fingerprintHandler = new FingerprintHandler();
                fingerprintHandler.startAutho(fingerprintManager, cryptoObject);
            }
        }
    }

    String KEY_NAME = "TEST_KEY";
    //Cipher Init()
    public boolean cipherInit(){
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    //Key Generator
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e){
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public class FingerprintHandler extends FingerprintManager.AuthenticationCallback{
        CancellationSignal cancellationSignal;

        public FingerprintHandler(){
        }

        //메소드들 정의
        public void startAutho(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
            cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            this.update("인증 에러 발생" + errString, false);
        }

        @Override
        public void onAuthenticationFailed() {
            this.update("인증 실패", false);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            ActivityManager activityManager = (ActivityManager)activity.getSystemService(activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(20);

            for(int i = 0; i < runningTaskInfos.size(); i++){
                System.out.println(">>>>>>>>>>>>>>>> : " + runningTaskInfos.get(i).topActivity.getClassName());
            }



            this.update("Error: "+ helpString, false);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            FingerprintManager.CryptoObject o = result.getCryptoObject();
            try{

                System.out.println(">>> sign1");
                String sign = Arrays.toString(o.getSignature().sign());
                System.out.println(">>> sign2 : [" + sign + "]");

            }catch (Exception e){
                e.printStackTrace();
            }


            this.update("앱 접근이 허용되었습니다.", true);
        }

        public void stopFingerAuth(){
            if(cancellationSignal != null && !cancellationSignal.isCanceled()){
                cancellationSignal.cancel();
            }
        }

        private void update(String s, boolean b) {
            final TextView tv_message = (TextView) activity.findViewById(R.id.tv_message);
            final ImageView iv_fingerprint = (ImageView) activity.findViewById(R.id.iv_fingerprint);
            final LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.ll_secure);

            //안내 메세지 출력
            tv_message.setText(s);

            if(b == false){
                tv_message.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            } else {//지문인증 성공
                tv_message.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
                //iv_fingerprint.setImageResource(R.mipmap.ic_done);
                linearLayout.setVisibility(LinearLayout.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.clearFragment();
                    }
                }, 200);
            }
        }
    }
}
