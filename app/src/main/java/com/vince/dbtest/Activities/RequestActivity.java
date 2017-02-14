package com.vince.dbtest.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.vince.dbtest.R;

import org.json.JSONObject;

public class RequestActivity extends Activity implements View.OnClickListener {
    private final String TAG = "RequestActivity";
    private static final int EXCEPTIONSTATE = 0;
    private static final int LOGINSTATE = 1;
    private Button btn_request;
    private ImageView iv;
    private TextView tv_response;
    NetworkImageView netiv;
    private RequestQueue queue;
//    private HttpClient httpClient;
    private JSONObject jsondata;
    private LruCache<String, Bitmap> cache;
//    private ArrayList<NameValuePair> listdata;
    private String url_uat = "http://218.94.147.122:9081/SX_MBServer/svt/login.shtml";
    private String url_get = "http://218.94.147.122:9081/BG_MBManager/mobile.shtml?action=act009&mobileType=android&mobileMac=869085024782840&pageSize=8&pageNo=@pageNo&Query=List";// "http://218.94.147.122:9081/SX_MBServer/svt/login.shtml";
    private String url_post = "http://218.94.147.122:9081/BG_MBManager/mobile.shtml";
    private String url_img = "http://img2.imgtn.bdimg.com/it/u=545228853,2699540663&fm=23&gp=0.jpg";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EXCEPTIONSTATE:
                    String str = (String) msg.obj;
                    Toast.makeText(RequestActivity.this, str, Toast.LENGTH_SHORT).show();
                    break;
                case LOGINSTATE:
                    String result = (String) msg.obj;
                    tv_response.setText(result);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        btn_request = (Button) findViewById(R.id.btn_request);
        btn_request.setOnClickListener(this);
        iv = (ImageView) findViewById(R.id.iv);
        tv_response = (TextView) findViewById(R.id.tv_respose);
        netiv = (NetworkImageView) findViewById(R.id.netiv);
        queue = Volley.newRequestQueue(this);
        cache = new LruCache<String, Bitmap>(10 * 1024 * 1024);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_request:
//                StringRequest request = new StringRequest(Request.Method.POST, url_post, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        Message msg = Message.obtain();
//                        msg.what = LOGINSTATE;
//                        msg.obj = s;
//                        handler.sendMessage(msg);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Message msg = Message.obtain();
//                        msg.what = EXCEPTIONSTATE;
//                        msg.obj = volleyError;
//                        handler.sendMessage(msg);
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("action", "act009");
//                        map.put("mobileType", "android");
//                        map.put("mobileMac", "869085024782840");
//                        map.put("pageSize", "8");
//                        map.put("Query", "List");
//                        return map;
//                    }
//                };
//                queue.add(request);

//                jsondata = new JSONObject();
//                try {
//                    jsondata.put("TransId", "MCLoanInterestRateQry");
//                    jsondata.put("LoanType", "0204");
//                    new Thread(new jsonAction(jsondata.toString())).start();
//                } catch (JSONException e) {
//                    Toast.makeText(this, "fail",
//                            Toast.LENGTH_LONG).show();
//                    Log.e(TAG, "fail to JSON data", e);
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            default:
//                break;


//                JSONObject json = new JSONObject();
//                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url_get, json,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject jsonObject) {
//                                System.out.println(jsonObject);
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                });
//                queue.add(request);

                ImageRequest request = new ImageRequest(url_img, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                queue.add(request);

                ImageLoader loader = new ImageLoader(queue, new ImageLoader.ImageCache() {

                    @Override
                    public Bitmap getBitmap(String s) {
                        return cache.get(s);
                    }

                    @Override
                    public void putBitmap(String s, Bitmap bitmap) {
                        cache.put(s, bitmap);
                    }
                });
//                ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv, R.mipmap.ic_launcher, R.mipmap.kahui);
//                loader.get(url_img, listener, 0, 0);
                netiv.setVisibility(View.VISIBLE);
                netiv.setDefaultImageResId(R.mipmap.ic_launcher);
                netiv.setErrorImageResId(R.mipmap.kahui);
                netiv.setImageUrl(url_img, loader);
        }
    }

//    private class LoginAction implements Runnable {
//        String rqStr;
//
//        public LoginAction(String rqStr) {
//            this.rqStr = rqStr;
//        }
//
//        @Override
//        public void run() {
//            Message msg = new Message();
//            try {
//                if (httpClient == null) {
//                    httpClient = HttpClientUtil.getHttpClient();
//                }
//                HttpPost request = new HttpPost(url_post);
//                listdata = new ArrayList<NameValuePair>();
//                listdata.add(new BasicNameValuePair("action", "act009"));
//                listdata.add(new BasicNameValuePair("mobileType", "android"));
//                listdata.add(new BasicNameValuePair("mobileMac", "869085024782840"));
//                listdata.add(new BasicNameValuePair("pageSize", "8"));
//                listdata.add(new BasicNameValuePair("Query", "List"));
//                HttpEntity entity = new UrlEncodedFormEntity(listdata, "UTF-8");
//                request.setEntity(entity);
//                HttpResponse response = httpClient.execute(request);
//                int code = response.getStatusLine().getStatusCode();
//                if (code == 200) {
//                    String rpStr = EntityUtils.toString(response.getEntity());
//                    msg.what = LOGINSTATE;
//                    msg.obj = rpStr;
//                    System.out.println(rpStr);
//                } else {
//                    msg.what = EXCEPTIONSTATE;
//                    msg.obj = "response err";
//                }
//            } catch (Exception e) {
//                Log.d("TAG", "linked fail!", e);
//                msg.what = EXCEPTIONSTATE;
//                msg.obj = "linked fail!";
//            }
//            handler.sendMessage(msg);
//        }
//    }
//
//    private class jsonAction implements Runnable {
//        String rqStr;
//
//        public jsonAction(String rqStr) {
//            this.rqStr = rqStr;
//        }
//
//
//        @Override
//        public void run() {
//            Message msg = new Message();
//            try {
//                if (httpClient == null) {
//                    httpClient = HttpClientUtil.getHttpClient();
//                }
//                HttpPost request = new HttpPost(url_uat);
//                HttpEntity entity = new StringEntity(rqStr, "UTF-8");
//                request.setEntity(entity);
//                HttpResponse response = httpClient.execute(request);
//                int code = response.getStatusLine().getStatusCode();
//                if (code == 200) {
//                    String rpStr = EntityUtils.toString(response.getEntity());
//                    msg.what = LOGINSTATE;
//                    msg.obj = rpStr;
//                    System.out.println(rpStr);
//                } else {
//                    msg.what = EXCEPTIONSTATE;
//                    msg.obj = "response err";
//                }
//            } catch (Exception e) {
//                Log.d("TAG", "linked fail!", e);
//                msg.what = EXCEPTIONSTATE;
//                msg.obj = "linked fail!";
//            }
//            handler.sendMessage(msg);
//        }
//    }
}
