package com.nice.fincent.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nice.fincent.R;

public class SettingItem extends RelativeLayout {
    private static final int TYPE_SETTING_SWITCH = 1;
    private static final int TYPE_SETTING_LINK = 2;
    private static final int TYPE_SETTING_BTN = 3;
    private static final int TYPE_SETTING_VERSION = 4;

    private int type = 0;

    private TextView titleTv;
    private TextView descTv;
    private CheckBox switchBtn;
    private ImageButton moveBtn;
    private Button updateBtn;
    private LinearLayout versionLayout;
    private TextView versionTv;

    public SettingItem(Context context) {
        super(context);
    }

    public SettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public SettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    public SettingItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SettingItem, defStyleAttr, 0);

        int type = typedArray.getInt(R.styleable.SettingItem_itemType, 1);
        String title = typedArray.getString(R.styleable.SettingItem_title);
        String desc = typedArray.getString(R.styleable.SettingItem_desc);

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.item_setting, this, false);
        addView(v);

        titleTv = (TextView)findViewById(R.id.tv_title);
        titleTv.setText(title);

        if(desc != null && desc.length() > 0){
            descTv = (TextView)findViewById(R.id.tv_desc);
            descTv.setText(desc);
            descTv.setVisibility(View.VISIBLE);
        }

        switch (type) {
            case TYPE_SETTING_SWITCH:
                boolean chked = typedArray.getBoolean(R.styleable.SettingItem_chked, false);
                switchBtn = (CheckBox)findViewById(R.id.btn_switch);
                switchBtn.setChecked(chked);
                switchBtn.setVisibility(View.VISIBLE);
                switchBtn.setTag(this.getId());
                break;
            case TYPE_SETTING_LINK:
                moveBtn = (ImageButton)findViewById(R.id.btn_move);
                moveBtn.setVisibility(View.VISIBLE);
                moveBtn.setTag(this.getId());
                break;
            case TYPE_SETTING_BTN:
                break;
            case TYPE_SETTING_VERSION:
                updateBtn = (Button)findViewById(R.id.btn_update);
                updateBtn.setVisibility(View.VISIBLE);
                updateBtn.setTag(this.getId());
                versionLayout = (LinearLayout)findViewById(R.id.layout_version);
                versionLayout.setVisibility(View.VISIBLE);
                versionTv = (TextView)findViewById(R.id.tv_version);
                break;
        }
    }


    public void setVersion(String version){
        versionTv.setText(version);
    }

    public void setChecked(boolean chk){
        switchBtn.setChecked(chk);
    }

    public boolean isChecked(){
        return switchBtn.isChecked();
    }


}
