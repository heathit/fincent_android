package com.nice.fincent.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.infotech.IFTCrypto.iftCoreEnV2;
import com.nice.fincent.MainActivity;
import com.nice.fincent.R;
import com.nice.fincent.util.CertUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class CertManagerFragment extends MainFragment {
    private MainActivity activity;

    private iftCoreEnV2 iftLib;

    private ViewPager viewPager;
    private CertPagerAdapter pagerAdapter;
    private int currCertPos = -1;

    private RelativeLayout noCertLayout;
    private Button deleteBtn;

    ArrayList<LinkedTreeMap<String, String>> certList = new ArrayList<>();
    private CertUtil certUtil = new CertUtil();
    private ArrayList<ImageView> indicatorList = new ArrayList<>();
    private LinearLayout indicatorLayout;

    //indicator
    private int indiPx;
    private int activePx;
    private LinearLayout.LayoutParams normalParams;
    private LinearLayout.LayoutParams activeParams;


    public CertManagerFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cert_mng, container, false);

        init(rootView);

        return rootView;
    }


    public void init(View view){
        activity = (MainActivity)getActivity();

        iftLib = new iftCoreEnV2(activity);

        noCertLayout = (RelativeLayout)view.findViewById(R.id.layout_no_cert);
        deleteBtn = (Button)view.findViewById(R.id.btn_delete);

        indiPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, activity.getResources().getDisplayMetrics());
        activePx = indiPx * 5;
        normalParams = getNomalIndiParams();
        activeParams = getActiveIndiParams();

        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        pagerAdapter = new CertPagerAdapter(getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(20);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("onPageSelected", ">>> position : " + position);
                currCertPos = position;
                setIndicatorActive(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        indicatorLayout = (LinearLayout)view.findViewById(R.id.layout_indicator);

        getCertList();
        
    }

    private void getCertList(){
        certList.clear();
        String certListString = iftLib.getCertList();

        //Log.d("certListString", certListString);

        if(certListString != null && certListString.length() > 0) {
            certList = new Gson().fromJson(certListString, new TypeToken<ArrayList<LinkedTreeMap<String, String>>>() {}.getType());
            if(certList != null){

                //test
                //certList.clear();


                if(certList.size() > 0){
                    pagerAdapter.notifyDataSetChanged();
                    setIndicatorActive(0);
                }else{
                    //인증서가 없는 경우
                    noCertLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);

                }
            }

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        indicatorLayout.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_cert:
                activity.showCertCopy();
                break;
            case R.id.btn_delete:
                //1 공인인증서 비번 입력 화면(키패드) 호출
                //2 비번 체크 후 삭제/알림창
                //3 삭제

                LinkedTreeMap<String, String> certMap = certList.get(currCertPos);

                String signCert = certMap.get("certPath");
                String signPri = certMap.get("keyPath");
                String signPath = "" + Environment.getExternalStorageDirectory() + "/NPKI/" + certMap.get("issuerName") + "/USER/" + certMap.get("subjectName");

                String certPw = "";//인증서 비번 입력값 get

                boolean isChked = matchCertPw(certPw, signCert, signPri);
                if(isChked){
                    deleteDir(signPath);
                }else{

                }
                break;
            case R.id.btn_close:
                activity.goBackFragment();
                break;
        }
    }

    public class CertPagerAdapter extends PagerAdapter {

        private LayoutInflater mInflater;
        private Context mContext;

        public CertPagerAdapter() {}

        // Context를 전달받아 mContext에 저장하는 생성자 추가.
        public CertPagerAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null ;

            if (mContext != null) {
                // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
                //LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = mInflater.inflate(R.layout.page_cert, container, false);

                CertHolder holder = (CertHolder)view.getTag();
                if(holder == null){
                    holder = new CertHolder(view);
                    view.setTag(holder);
                }

                LinkedTreeMap<String, String> certMap = certList.get(position);
                holder.certNameTv.setText(certUtil.getCN(certMap.get("subjectName")));
                holder.issuerTv.setText(certUtil.getIssuerName(certMap.get("issuerName")));
                holder.expireTv.setText(certMap.get("notAfter"));
            }

            // 뷰페이저에 추가.
            container.addView(view) ;
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 뷰페이저에서 삭제.
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return certList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return (view == (View)object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            updateIndicator();
        }

        class CertHolder{
            TextView certNameTv;
            TextView issuerTv;
            TextView expireTv;

            public CertHolder(View v){
                certNameTv = (TextView) v.findViewById(R.id.tv_cert_name) ;
                issuerTv = (TextView) v.findViewById(R.id.tv_issuer_text) ;
                expireTv = (TextView) v.findViewById(R.id.tv_expire_text) ;
            }
        }
    }

    private void updateIndicator() {
        int i = certList.size();
        int j = indicatorList.size();
        if(j < i){
            for(int k = j; k < i; k++){
                indicatorList.add(getIndicator());
            }
        }

        indicatorLayout.removeAllViews();
        for(int k = 0; k < certList.size(); k++){
            indicatorLayout.addView(indicatorList.get(k));
        }
    }

    private void setIndicatorActive(int position) {
        //ImageView iv = (ImageView)indicatorLayout.getChildAt(position);
        //indicatorLayout.setLayoutParams(getIndiLayoutParams());

        for(int i = 0; i < indicatorList.size(); i++){
            if(i == position){
                ImageView iv = indicatorList.get(i);
                iv.setImageResource(R.drawable.indicator_on);
                iv.setLayoutParams(activeParams);
            }else{
                ImageView iv = indicatorList.get(i);
                iv.setImageResource(R.drawable.indicator_off);
                iv.setLayoutParams(normalParams);
            }
        }


    }

    private ImageView getIndicator() {
        ImageView iv = new ImageView(getContext());
        iv.setLayoutParams(normalParams);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(R.drawable.indicator_off);
        return iv;
    }

    private LinearLayout.LayoutParams getNomalIndiParams(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indiPx, indiPx);
        params.leftMargin = 5;
        params.rightMargin = 5;
        return params;
    }
    private LinearLayout.LayoutParams getActiveIndiParams(){
//        int activePx =indiPx * 5;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(activePx, indiPx);
        params.leftMargin = 5;
        params.rightMargin = 5;
        return params;
    }

    private boolean matchCertPw(String signPw, String signCert, String signPri) {
        boolean result = false;
        try {
            JSONObject ret = null;

            String certValue = iftLib.checkCert(signCert, signPri, signPw);
            ret = new JSONObject(certValue);
            if(ret.getString("errYn").equals("N")) {
                //인증서 비밀번호를 올바르게 입력한 경우
                //deleteDir(signPath);
                return true;
            } else {
                return false;
/*
                new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle("인증서 비밀번호 확인")
                        .setMessage(ret.getString("errMsg"))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void deleteDir(String dirPath) {
        File dir = new File(dirPath);
        if(dir.isDirectory()) {
            for(File files : dir.listFiles()) {
                files.delete();
            }
            dir.delete();
        }
    }
}
