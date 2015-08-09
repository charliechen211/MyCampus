package com.stpi.campus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.http.NameValuePair;

import java.text.ParseException;
import java.util.List;

public class HttpListGetter<T> {

    public T getFromUrl(String url, Class<T> type) {

        String json = "";
        try {
            json = Net.getInstance().get(url);
            Gson gson = new Gson();
            T result = gson.fromJson(json, type);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
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
        }catch(JsonSyntaxException je) {
            Gson dateGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            return dateGson.fromJson(json,type);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

//    public List<AroundShareInfo> getListFromUrl(String url) {
//
//        String json = "";
//        try {
//            json = Net.getInstance().get(url);
//            Gson gson = new Gson();
//            return new AroundSharedInfoParser().parse(json);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

}
