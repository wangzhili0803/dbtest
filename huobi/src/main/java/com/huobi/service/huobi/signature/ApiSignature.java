package com.huobi.service.huobi.signature;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Base64;

import com.huobi.exception.SDKException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class ApiSignature {

    public static final String op = "op";
    public static final String opValue = "auth";
    private static final String accessKeyId = "AccessKeyId";
    private static final String signatureMethod = "SignatureMethod";
    private static final String signatureMethodValue = "HmacSHA256";
    private static final String signatureVersion = "SignatureVersion";
    private static final String signatureVersionValue = "2";
    private static final String timestamp = "Timestamp";
    private static final String signature = "Signature";
    private static final SimpleDateFormat FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);


    public void createSignature(String accessKey, String secretKey, String method, String host,
        String uri, UrlParamsBuilder builder) {
        StringBuilder sb = new StringBuilder(1024);

        if (accessKey == null || "".equals(accessKey) || secretKey == null || "".equals(secretKey)) {
            throw new SDKException(SDKException.KEY_MISSING,
                "API key and secret key are required");
        }

        sb.append(method.toUpperCase()).append('\n')
            .append(host.toLowerCase()).append('\n')
            .append(uri).append('\n');

        builder.putToUrl(accessKeyId, accessKey)
            .putToUrl(signatureVersion, signatureVersionValue)
            .putToUrl(signatureMethod, signatureMethodValue)
            .putToUrl(timestamp, gmtNow());

        sb.append(builder.buildSignature());
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance(signatureMethodValue);
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                signatureMethodValue);
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            throw new SDKException(SDKException.RUNTIME_ERROR,
                "[Signature] No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new SDKException(SDKException.RUNTIME_ERROR,
                "[Signature] Invalid key: " + e.getMessage());
        }
        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String actualSign = Base64.encodeToString(hash, Base64.NO_WRAP);
        builder.putToUrl(signature, actualSign);
    }

    private static String gmtNow() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        //计时时区的偏移时间(毫秒)
        date.setTime(System.currentTimeMillis() - calendar.get(Calendar.ZONE_OFFSET) - calendar.get(Calendar.DST_OFFSET));
        return FORMAT_UTC.format(date);
    }
}
