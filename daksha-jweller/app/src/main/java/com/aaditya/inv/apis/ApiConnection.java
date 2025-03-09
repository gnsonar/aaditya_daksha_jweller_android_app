package com.aaditya.inv.apis;

import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;


import java.io.IOException;
import java.util.Map;

public final class ApiConnection {

    private static final ApiConnection _instance = new ApiConnection();
    private ApiConnection() {}

    public static ApiConnection getInstance(){
        return _instance;
    }

    private final CloseableHttpClient client = HttpClients.createDefault();
    public CloseableHttpResponse callGetAPI(String path, Map<String, String> params) throws IOException {
        HttpGet get = new HttpGet(Constants.APIS_CONSTANTS.API_HOST + Commons.appendUrlParams(path, params));
        return client.execute(get);
    }

    public CloseableHttpResponse callPostAPI(String path, Map<String, String> params, Map<String, Object> body) throws IOException {
        HttpPost post = new HttpPost(Constants.APIS_CONSTANTS.API_HOST + Commons.appendUrlParams(path, params));
        post.setEntity(new StringEntity(Commons.gson.toJson(body), ContentType.TEXT_PLAIN));
        return client.execute(post);
    }
}
