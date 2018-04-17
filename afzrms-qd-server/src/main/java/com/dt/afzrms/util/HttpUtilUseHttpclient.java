package com.dt.afzrms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年3月10日 下午2:17:22
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class HttpUtilUseHttpclient {
	public static String sendGet(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		StringBuffer realUrl=new StringBuffer();
		realUrl.append(url+"?");
		for (String key : params.keySet()) {
			realUrl.append(key+"="+params.get(key)+"&");
		}
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet(realUrl.toString());

		HttpResponse response = null;
		StringBuffer contentBuffer = new StringBuffer();
		String result = "";
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
			String inputLine = null;
			while ((inputLine = reader.readLine()) != null) {
				contentBuffer.append(inputLine);
				result += inputLine;
				contentBuffer.append("/n");
			}
			reader.close();
			EntityUtils.consume(entity);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
}
