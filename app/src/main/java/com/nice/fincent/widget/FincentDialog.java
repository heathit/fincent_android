package com.nice.fincent.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nice.fincent.R;

public class FincentDialog extends Dialog {
    private Context context;

    public FincentDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        this.context = context;
    }

    @Override
    public void setTitle(int titleId) {
        TextView titleTv = (TextView)findViewById(R.id.tv_dialog_title);
        titleTv.setText(titleId);
        ((LinearLayout)titleTv.getParent()).setVisibility(View.VISIBLE);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        TextView titleTv = (TextView)findViewById(R.id.tv_dialog_title);
        titleTv.setText(title);
        ((LinearLayout)titleTv.getParent()).setVisibility(View.VISIBLE);
    }

    public void setMessage(int msgId) {
        TextView msgTv = (TextView)findViewById(R.id.tv_dialog_msg);
        msgTv.setText(msgId);
    }

    public void setMessage(@Nullable CharSequence title) {
        TextView msgTv = (TextView)findViewById(R.id.tv_dialog_msg);
        msgTv.setText(title);
    }

    public void setAddText(int msgId) {
        TextView addTv = (TextView)findViewById(R.id.tv_dialog_add);
        addTv.setText(msgId);
        addTv.setVisibility(View.VISIBLE);
    }

    public void setAddText(@Nullable CharSequence msg) {
        TextView addTv = (TextView)findViewById(R.id.tv_dialog_add);
        addTv.setText(msg);
        addTv.setVisibility(View.VISIBLE);
    }

    public void setCode(int codeId) {
        TextView addTv = (TextView)findViewById(R.id.tv_dialog_code);
        addTv.setText(codeId);
        addTv.setVisibility(View.VISIBLE);
    }

    public void setCode(@Nullable CharSequence code) {
        TextView addTv = (TextView)findViewById(R.id.tv_dialog_code);
        addTv.setText(code);
        addTv.setVisibility(View.VISIBLE);
    }

    public void setBtn(int btnText, View.OnClickListener listener){
        Button btn = (Button)findViewById(R.id.btn_dialog);
        btn.setText(btnText);
        btn.setOnClickListener(listener);
        btn.setVisibility(View.VISIBLE);
    }

    public void setBtn(String btnText, View.OnClickListener listener){
        Button btn = (Button)findViewById(R.id.btn_dialog);
        btn.setText(btnText);
        btn.setOnClickListener(listener);
        btn.setVisibility(View.VISIBLE);
    }

    public void setBtn(int posiBtnText, View.OnClickListener posiListener, int negaBtnText, View.OnClickListener negaListener){
        Button btnR = (Button)findViewById(R.id.btn_dialog_r);
        btnR.setText(posiBtnText);
        btnR.setOnClickListener(posiListener);
        btnR.setVisibility(View.VISIBLE);

        Button btnL = (Button)findViewById(R.id.btn_dialog_l);
        btnL.setText(negaBtnText);
        btnL.setOnClickListener(negaListener);
        btnL.setVisibility(View.VISIBLE);
    }

    public void setBtn(String posiBtnText, View.OnClickListener posiListener, String negaBtnText, View.OnClickListener negaListener){
        Button btnR = (Button)findViewById(R.id.btn_dialog_r);
        btnR.setText(posiBtnText);
        btnR.setOnClickListener(posiListener);
        btnR.setVisibility(View.VISIBLE);

        Button btnL = (Button)findViewById(R.id.btn_dialog_l);
        btnL.setText(negaBtnText);
        btnL.setOnClickListener(negaListener);
        btnL.setVisibility(View.VISIBLE);
    }
}
