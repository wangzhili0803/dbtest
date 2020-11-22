package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.jerry.baselib.R;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 通知类型对话框, (有title)
 */
public class EditDialog extends BaseDialog {

    private TextView tvTitle;
    private EditText mEditText;
    private String title;
    private String hint;
    private String text;

    public EditDialog(Context context) {
        super(context);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_edit;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle = findViewById(R.id.title_tv);
        mEditText = findViewById(R.id.et_url);
        tvTitle.setText(title == null ? mContext.getString(R.string.notice) : title);
        mEditText.setHint(hint == null ? "" : hint);
        mEditText.setText(text == null ? "" : text);
    }

    public void setDialogTitle(String title) {
        this.title = title;
        if (tvTitle != null) {
            tvTitle.setHint(title);
        }
    }

    public void setHint(String hint) {
        this.hint = hint;
        if (mEditText != null) {
            mEditText.setHint(hint);
        }
    }

    public void setText(String text) {
        this.text = text;
        if (mEditText != null) {
            mEditText.setHint(text);
        }
    }

    public String getEditText() {
        return mEditText.getText().toString().trim();
    }
}
