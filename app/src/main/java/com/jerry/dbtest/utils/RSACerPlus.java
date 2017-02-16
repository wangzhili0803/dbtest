package com.jerry.dbtest.utils;

import android.app.Application;
import android.util.Log;

import com.jerry.dbtest.R;

import org.apache.commons.lang.ArrayUtils;

import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;

/**
 * 用户客户端对数据使用证书公钥加密
 * 
 * @author iven
 * 
 */
public class RSACerPlus {
	private Cipher cipher;

	private static RSACerPlus rsaPlus = null;

	private RSACerPlus() {

	}
	public static RSACerPlus getInstance(Application app) {
		if (null == rsaPlus) {
			rsaPlus = new RSACerPlus();
			try {
				rsaPlus.initCer(app);
			} catch (Exception e) {
				Log.e("TAG", "init the cer ERROR!", e);
			}
		}
		return rsaPlus;
	}

	/**
	 * 初始化加载cer证书
	 * 
	 * @throws Exception
	 */
	private void initCer(Application app) throws Exception {
		CertificateFactory cff = CertificateFactory.getInstance("X.509");
		InputStream in = app.getBaseContext().getResources()
				.openRawResource(R.raw.server_rsa); // 证书文件
		Certificate cf = cff.generateCertificate(in);
		PublicKey pk1 = cf.getPublicKey(); // 得到证书文件携带的公钥
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // 定义算法：RSA
		cipher.init(Cipher.ENCRYPT_MODE, pk1);
	}

	/**
	 * 使用初始化的公钥对数据加密
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 *             : IllegalBlockSizeException, BadPaddingException,
	 *             UnsupportedEncodingException
	 */
	public String doEncrypt(String str) throws Exception {
		// 分段加密
		byte[] data = str.getBytes("UTF-8");
		byte[] encryptdata = null;
		for (int i = 0; i < data.length; i += 100) {
			byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 100));
			encryptdata = ArrayUtils.addAll(encryptdata, doFinal);
		}
		return new BASE64Encoder().encode(encryptdata);
	}
}
