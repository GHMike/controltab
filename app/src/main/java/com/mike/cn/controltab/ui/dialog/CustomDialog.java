package com.mike.cn.controltab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mike.cn.controltab.R;
import com.mike.cn.controltab.model.MenuInfoModel;

/**
 * 自定义编辑弹窗
 */
public class CustomDialog extends Dialog {

    private Button positiveButton;
    private Button negativeButton;
    private EditText tvName;
    private OnButtonClickListener buttonClickListener;
    private MenuInfoModel data;

    public CustomDialog(Context context, MenuInfoModel data, OnButtonClickListener listener) {
        super(context);
        this.buttonClickListener = listener;
        this.data = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_layout);

        positiveButton = findViewById(R.id.btn_positive);
        negativeButton = findViewById(R.id.btn_negative);
        tvName = findViewById(R.id.tv_Name);

        tvName.setText(data.getName());

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    buttonClickListener.onPositiveButtonClick();
                }
                dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    data.setName(tvName.getText().toString());
                    buttonClickListener.onNegativeButtonClick(data);
                }
                dismiss();
            }
        });
    }

    public interface OnButtonClickListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick(MenuInfoModel a);
    }
}
