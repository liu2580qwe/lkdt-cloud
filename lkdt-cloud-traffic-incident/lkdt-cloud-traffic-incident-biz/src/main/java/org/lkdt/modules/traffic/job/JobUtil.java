package org.lkdt.modules.traffic.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @program: fog
 * @description: 数据采集
 * @create: 2020-07-16 10:00
 **/
@Component
public class JobUtil {

    /**
     * post请求数据
     * @param uri 接口
     * @param data 参数
     * @return
     */
    public final String postJOApp(String uri, String data){
        OkHttpClient client = null;
        Response response = null;
        try {
            client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(uri)
                    .method("POST", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get请求数据
     * @param uri
     * @return
     */
    public final JSONObject getJOApp(String uri){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = null;
        String responseStr = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                responseStr = EntityUtils.toString(responseEntity);
            }
            return JSONObject.parseObject(responseStr);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * get请求数据
     * @param uri
     * @return
     */
    public final JSONArray getJAApp(String uri){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = null;
        String responseStr = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                responseStr = EntityUtils.toString(responseEntity);
            }
            return JSONArray.parseArray(responseStr);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 字符串处理
     * @param obj
     * @return
     */
    public final String toStringHandle(String obj){
        try{
            if(obj == null){
                return "";
            }
            return obj.trim();
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }
}
