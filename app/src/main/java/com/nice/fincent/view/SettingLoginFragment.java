package com.nice.fincent.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nice.fincent.MainActivity;
import com.nice.fincent.R;
import com.nice.fincent.define.Define;
import com.nice.fincent.util.DataStorage;
import com.nice.fincent.widget.SettingItem;

public class SettingLoginFragment extends MainFragment {
    private MainActivity activity;

    private CheckBox numberRadio;
    private CheckBox fingerRadio;

    private String loginType = "";

    public SettingLoginFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_setting, container, false);

        init(rootView);

        return rootView;
    }

    public void init(View view){
        activity = (MainActivity)getActivity();

        //closeBtn = (Button)view.findViewById(R.id.btn_close);
        numberRadio = (CheckBox)view.findViewById(R.id.radio_number);
        fingerRadio = (CheckBox)view.findViewById(R.id.radio_finger);

        loginType = DataStorage.getString(Define.LOGIN_SETTING);

        setRadios();
    }

    public void setRadios(){
        setFingerRadioChked("F".equals(loginType));
    }

    public void setFingerRadioChked(boolean chked){
        fingerRadio.setChecked(chked);
        numberRadio.setChecked(!chked);
        DataStorage.setString(Define.LOGIN_SETTING, chked?"F":"N");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_close:
                activity.goBackFragment();
                break;
            case R.id.radio_number:
                setFingerRadioChked(false);
                break;
            case R.id.radio_finger:
                setFingerRadioChked(true);
                break;
        }

    }
}
