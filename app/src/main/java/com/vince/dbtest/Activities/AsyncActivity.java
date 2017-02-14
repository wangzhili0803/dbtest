package com.vince.dbtest.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.vince.dbtest.R;

import org.json.JSONObject;

public class AsyncActivity extends Activity implements View.OnClickListener {

    private Button btn;
    private TextView tv;
    private ImageView iv;
    private AsyncHttpClient client;
    private final String url_baidu = "http://www.baidu.com";
    private final String url_img = "http://g.hiphotos.baidu.com/image/h%3D200/sign=eb8e0483b5b7d0a264c9039dfbee760d/9d82d158ccbf6c81c6b044d9bb3eb13533fa407f.jpg";
    private String url_post = "http://218.94.147.122:9081/BG_MBManager/mobile.shtml";
    private JSONObject jsondata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        client = new AsyncHttpClient();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
//                RequestParams params = new RequestParams();
//                params.put("action", "act009");
//                params.put("action", "act009");
//                params.put("mobileType", "android");
//                params.put("mobileMac", "869085024782840");
//                params.put("pageSize", "8");
//                params.put("Query", "List");
//                client.post(url_post, params, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                        String str = new String(bytes);
//                        tv.setText(str);
//                    }
//
//                    @Override
//                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                        if (bytes != null) {
//                            String str = new String(bytes);
//                            tv.setText(str);
//                        } else {
//                            tv.setText("数据错误");
//                        }
//                    }
//
//                    @Override
//                    public void onProgress(int bytesWritten, int totalSize) {
//                        super.onProgress(bytesWritten, totalSize);
//                    }
//                });


//                AsyncHttpClient client = new AsyncHttpClient();
//                client.get(url_img, new FileAsyncHttpResponseHandler(this) {
//                    @Override
//                    public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(int i, Header[] headers, File file) {
//                        try {
//                            Toast.makeText(AsyncActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                            InputStream in = null;
//                            in = new FileInputStream(file);
//                            Bitmap bitmap = BitmapFactory.decodeStream(in);
//                            iv.setImageBitmap(bitmap);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                try {
//                    jsondata = new JSONObject();
//                    jsondata.put("TransId", "MCLoanInterestRateQry");
//                    jsondata.put("LoanType", "0204");
//
//
//                    AsyncHttpClient client = new AsyncHttpClient();
//                    StringEntity entity = new StringEntity(jsondata.toString());
//                    client.post(this, url_post, entity, "application/json", new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            super.onSuccess(statusCode, headers, response);
//                            Toast.makeText(AsyncActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                            super.onFailure(statusCode, headers, responseString, throwable);
//                            if (responseString != null) {
//                                Toast.makeText(AsyncActivity.this, responseString.toString(), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(AsyncActivity.this, "错误", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                break;
            default:
                break;
        }
    }
}
