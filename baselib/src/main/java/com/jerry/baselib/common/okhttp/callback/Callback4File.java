package com.jerry.baselib.common.okhttp.callback;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.util.FileUtil;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by wzl on 2018/11/23.
 *
 * @Description
 */
public class Callback4File<T> extends Callback<T> {

    private String path;

    public Callback4File(String cache) {
        path = cache;
    }

    @Override
    public T parseNetworkResponse(final Response response) throws IOException {
        BufferedSink sink = null;
        try {
            File dest = new File(path);
            sink = Okio.buffer(Okio.sink(dest));
            Buffer buffer = sink.buffer();
            long sum = 0;
            long total = response.body().contentLength();
            long len;
            int bufferSize = 200 * 1024; //200kb
            BufferedSource source = response.body().source();
            while ((len = source.read(buffer, bufferSize)) != -1) {
                sink.emit();
                sum += len;
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().post(() -> inProgress(finalSum * 1.0f / total));
            }
            source.close();
            sink.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("DOWNLOAD", "download failed");
        } finally {
            FileUtil.close(sink);
        }
        return super.parseNetworkResponse(response);
    }
}
