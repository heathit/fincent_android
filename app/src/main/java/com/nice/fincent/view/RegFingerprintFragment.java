package com.nice.fincent.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nice.fincent.MainActivity;
import com.nice.fincent.R;

public class RegFingerprintFragment extends MainFragment {
    private MainActivity activity;

    public RegFingerprintFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reg_fingerprint, container, false);

        init(rootView);

        return rootView;
    }


    public void init(View view){
        activity = (MainActivity)getActivity();


    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_confirm:

                break;
            case R.id.btn_close:
                activity.goBackFragment();
                break;
        }
    }
}
