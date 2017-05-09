package com.jerry.dbtest;

import com.mcxiaoke.packer.helper.PackerNg;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by wzl-pc on 2017/2/16.
 */

public class SampleApplication extends TinkerApplication {
    private static final String umeng_appkey="5836565c3eae251bba00003d";

    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.jerry.dbtest.SampleApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 如果没有使用PackerNg打包添加渠道，默认返回的是""
        // com.mcxiaoke.packer.helper.PackerNg
        final String market = PackerNg.getMarket(this);
        // 或者使用 PackerNg.getMarket(Context,defaultValue)
        // 之后就可以使用了，比如友盟可以这样设置
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, umeng_appkey, market));

    }
}