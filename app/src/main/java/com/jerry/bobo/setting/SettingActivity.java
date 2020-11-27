package com.jerry.bobo.setting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.BackgroundTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.greendao.ProductDao.Properties;
import com.jerry.bobo.R;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @description 设置页面
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        TextView tvVerion = findViewById(R.id.about_version);
        tvVerion.setText(BaseApp.Config.VERSION_NAME);
        findViewById(R.id.tv_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_clear) {
            loadingDialog();
            PreferenceHelp.putString(PreferenceHelp.PDD_COOKIE, Key.NIL);
            AppTask.withoutContext().assign((BackgroundTask<Boolean>) () -> {
                List<String> enablePics = new ArrayList<>();
                List<Product> products = ProManager.getInstance().queryAll(Product.class, null, Properties.UpdateTime);
                for (Product product : products) {
                    List<String> paths = StringUtil.getPicsFromStr(product.getPicPath());
                    enablePics.addAll(paths);
                }
                File file = new File(FileUtil.getAppExternalPath());
                File[] fs = file.listFiles();
                if (fs != null) {
                    for (File f : fs) {
                        if (f.isDirectory()) {
                            if (!"backup".equals(f.getName())) {
                                FileUtil.clearFile(f);
                            }
                            continue;
                        }
                        String name = f.getName();
                        if (name.contains("icon")) {
                            continue;
                        }
                        if (name.endsWith(".jpg") || name.endsWith(".png")) {
                            boolean contains = false;
                            for (String enablePic : enablePics) {
                                if (enablePic.contains(name)) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains) {
                                f.delete();
                            } else {
                                Uri uri = Uri.fromFile(f);
                                BaseApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            }
                        } else if (name.endsWith(".xls")) {
                            f.delete();
                        }
                    }
                }
                return true;
            }).whenDone((WhenTaskDone<Boolean>) result -> {
                if (result) {
                    toast("清理成功！");
                }
            }).whenTaskEnd(this::closeLoadingDialog).execute();
        }
    }
}