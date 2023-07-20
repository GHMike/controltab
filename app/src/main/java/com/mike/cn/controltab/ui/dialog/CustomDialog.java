package com.mike.cn.controltab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.mike.cn.controltab.R;
import com.mike.cn.controltab.model.MenuInfoModel;

import java.util.ArrayList;

/**
 * 自定义编辑弹窗
 */
public class CustomDialog extends Dialog {

    private Button positiveButton;
    private Button negativeButton;
    private EditText tvName;
    private EditText tv_code;
    private ImageView iv_Image;
    private OnButtonClickListener buttonClickListener;
    private MenuInfoModel data;
    private Context mContext;

    public CustomDialog(Context context, MenuInfoModel data, OnButtonClickListener listener) {
        super(context);
        this.buttonClickListener = listener;
        this.mContext = context;
        this.data = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_layout);

        positiveButton = findViewById(R.id.btn_positive);
        negativeButton = findViewById(R.id.btn_negative);
        tvName = findViewById(R.id.tv_Name);
        iv_Image = findViewById(R.id.iv_Image);
        tv_code = findViewById(R.id.tv_code);

        tvName.setText(data.getName());
        tv_code.setText(data.getCode());

        Glide.with(mContext).load(data.getImage()).error(R.mipmap.ic_launcher_round).into(iv_Image);
        iv_Image.setOnClickListener(view -> PictureSelector.create(mContext)
                .openSystemGallery(SelectMimeType.ofImage()).setSelectionMode(SelectModeConfig.SINGLE)
                .forSystemResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        LocalMedia in = result.get(0);
                        data.setImage(in.getPath());
                        Glide.with(mContext).load(in.getPath()).into(iv_Image);
                    }

                    @Override
                    public void onCancel() {

                    }
                }));


        positiveButton.setOnClickListener(v -> {
            if (buttonClickListener != null) {
                buttonClickListener.onPositiveButtonClick();
            }
            dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            if (buttonClickListener != null) {
                data.setName(tvName.getText().toString());
                data.setCode(tv_code.getText().toString());
                buttonClickListener.onNegativeButtonClick(data);
            }
            dismiss();
        });
    }

    public interface OnButtonClickListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick(MenuInfoModel a);

    }
}
