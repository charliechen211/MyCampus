package com.tang.guangshangquan.utils;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Net {
    private static Net instance;
    private DefaultHttpClient client;

    public Net() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setConnectionTimeout(params, 6000);
        HttpConnectionParams.setSoTimeout(params, 6000);
        client = new DefaultHttpClient(params);
    }

    public static Net getInstance() {
        if (instance == null) {
            instance = new Net();
        }
        return instance;
    }

    public void clear() {
        HttpParams params = new BasicHttpParams();
        //this how tiny it might seems, is actually absoluty needed. otherwise http client lags for 2sec.
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        client = new DefaultHttpClient(params);
    }

    public String get(String URL) throws Exception {
        String resultString;
        HttpGet sourceaddr = new HttpGet(URL);
        try {
            HttpResponse httpResponse = client.execute(sourceaddr);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                resultString = readstream(httpResponse.getEntity().getContent());
            } else {
                throw new NetworkErrorException("can't connect the network");
            }
            return resultString.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    public String post(String URL, List<NameValuePair> params) throws Exception {
        String resultString;
        try {
            HttpPost httpRequest = new HttpPost(URL);
            httpRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse httpResponse = client.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                resultString = readstream(httpResponse.getEntity().getContent());
            } else {
                throw new NetworkErrorException("can't connect the network");
            }
            return resultString;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean checknetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private String readstream(InputStream in) {
        StringBuffer resultString = new StringBuffer();
        try {
            BufferedReader inbuff = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = "";
            while ((line = inbuff.readLine()) != null) {
                resultString.append('\n');
                resultString.append(line);
            }

        } catch (Exception e) {
        }
        return resultString.toString();
    }
}
