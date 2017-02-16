package com.jerry.dbtest;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by wzl-pc on 2017/2/16.
 */

public class SampleApplication extends TinkerApplication {
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.jerry.dbtest.SampleApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}