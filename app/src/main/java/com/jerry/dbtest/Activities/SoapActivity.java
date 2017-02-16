package com.jerry.dbtest.Activities;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jerry.dbtest.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class SoapActivity extends Activity implements View.OnClickListener {
    private Button btn_request;
    private TextView tv_respose;

    //命名空间
    private static final String nameSpace = "http://WebXml.com.cn/";
    //调用的方法名称
    private static final String methodName = "getMobileCodeInfo";
    //endPoint
    private static final String endPoint = "http://webservice.webXml.com.cn/WebServices/MobileCodeWS.asmx";
    //soapAction
    private static final String soapAction = "http://WebXml.com.cn/getMobileCodeInfo";
    private Handler handler = new Handler() {
        WeakReference<Activity> reference = new WeakReference<Activity>(SoapActivity.this);

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    tv_respose.setText(str);
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap);
        btn_request = (Button) findViewById(R.id.btn_request);
        btn_request.setOnClickListener(this);
        tv_respose = (TextView) findViewById(R.id.tv_respose);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request:
                getMobileInfo();
                break;
            default:
                break;
        }
    }

    private void getMobileInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //指定WebService的命名空间和调用的方法名
                    SoapObject rpc = new SoapObject(nameSpace, methodName);
                    //设置需要调用的WebService接口需要传入的两个参数mobileCode、userId
                    rpc.addProperty("mobileCode", "17786430651");
                    rpc.addProperty("userId", "");

                    //生成调用的WebService方法的SOAP请求信息，并指定SOAP的版本
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                    envelope.bodyOut = rpc;
                    envelope.dotNet = true;//设置是否调用的是dotNet开发的WebService
//        envelope.setOutputSoapObject(rpc);//等价于envelope.bodyOut = rpc
                    HttpTransportSE transportSE = new HttpTransportSE(endPoint);
                    transportSE.call(soapAction, envelope);//调用WebService
                    //获取返回数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    //获取返回结果
                    String result = object.getProperty(0).toString();
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = result;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
