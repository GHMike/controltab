package com.mike.cn.controltab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.mike.cn.controltab.R;
import com.mike.cn.controltab.model.MenuInfoModel;
import com.mike.cn.controltab.tools.HideNavBarUtil;

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
        super(context, R.style.CustomDialog);
        this.buttonClickListener = listener;
        this.mContext = context;
        this.data = data;

        HideNavBarUtil.hideNavigation(this);
    }

    public void setData(MenuInfoModel data) {
        this.data = data;
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_layout);
        this.setCancelable(false);

        positiveButton = findViewById(R.id.btn_positive);
        negativeButton = findViewById(R.id.btn_negative);
        tvName = findViewById(R.id.tv_Name);
        iv_Image = findViewById(R.id.iv_Image);
        tv_code = findViewById(R.id.tv_code);

        initData();
        iv_Image.setOnClickListener(view -> PictureSelector.create(mContext)
                .openSystemGallery(SelectMimeType.ofImage()).setSelectionMode(SelectModeConfig.SINGLE)
                .forSystemResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        LocalMedia paths = result.get(0);

                        // 获取文件名中最后一个点（.）的位置
                        int lastIndex = paths.getRealPath().lastIndexOf(".");
                        String fileExtension = "";
                        if (lastIndex != -1) {
                            // 从最后一个点的位置开始截取字符串，得到后缀名
                            fileExtension = paths.getRealPath().substring(lastIndex + 1);
                        }
                        if (fileExtension.contains("jpg") || fileExtension.contains("JPG") || fileExtension.contains(
                                "jpeg"
                        ) || fileExtension.contains("JPEG") ||
                                fileExtension.contains("png") || fileExtension.contains("PNG") || fileExtension.contains(
                                "gif"
                        ) || fileExtension.contains("GIF")
                        ) {
                            iv_Image.setTag(paths.getRealPath());
                            Glide.with(mContext).load(paths.getRealPath()).into(iv_Image);
                        } else {
                            Toast.makeText(getContext(), "请选择正确的图片格式", Toast.LENGTH_LONG).show();
                        }

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
                data.setImage(iv_Image.getTag() != null ? iv_Image.getTag().toString() : "");

                buttonClickListener.onNegativeButtonClick(data);
            }
            dismiss();
        });
    }


    private void initData() {
        tvName.setText(data.getName());
        tv_code.setText(data.getCode());
        iv_Image.setTag(data.getImage());

        Glide.with(mContext).load(data.getImage()).error(R.mipmap.ic_launcher_round).into(iv_Image);
    }


    public interface OnButtonClickListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick(MenuInfoModel a);

    }

}
