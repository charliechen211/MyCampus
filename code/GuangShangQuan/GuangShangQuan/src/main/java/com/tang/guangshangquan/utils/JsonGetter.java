package com.tang.guangshangquan.utils;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by Administrator on 13-11-27.
 */
public class JsonGetter<T> {

    public T getFromUrl(String url, Class<T> type) {

        String json = "";
        try {
            json = Net.getInstance().get(url);
            Gson gson = new Gson();
            T result = gson.fromJson(json, type);
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public T getFromUrl(String url, Class<T> type, List<NameValuePair> params) {
        String json = "";
        try {
            json = Net.getInstance().post(url, params);
            Gson gson = new Gson();
            T result = gson.fromJson(json, type);
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

}
