package com.nice.fincent.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nice.fincent.MainActivity;
import com.nice.fincent.R;
import com.nice.fincent.define.Define;
import com.nice.fincent.util.DataStorage;

public class SelectLoginFragment extends MainFragment {
    private MainActivity activity;

    public SelectLoginFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_select, container, false);

        init(rootView);

        return rootView;
    }

    public void init(View view){
        activity = (MainActivity)getActivity();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_close:
                activity.goBackFragment();
                break;
            case R.id.btn_id:
                Toast.makeText(activity, "아이디 로그인", Toast.LENGTH_SHORT).show();
                //activity.goBackFragment();
                break;
            case R.id.btn_simple_pass:
                Toast.makeText(activity, "간편번호 로그인", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_finger:
                Toast.makeText(activity, "지문 로그인", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
