package com.stpi.campus.activity.otherService;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class SearchFriends {
    private ResponseData responseData;

    public static SearchFriends search(String[] args) throws Exception {
        String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
        String search = args.length > 0 ? args[0] : "stackoverflow";
        String charset = "UTF-8";

        URL url = new URL(google + URLEncoder.encode(search, charset));
        Reader reader = new InputStreamReader(url.openStream(), charset);
        SearchFriends results = new Gson()
                .fromJson(reader, SearchFriends.class);

        // Show title and URL of 1st result.
        System.out.println(results.getResponseData().getResults().get(0)
                .getTitle());
        System.out.println(results.getResponseData().getResults().get(0)
                .getUrl());

        return results;
    }

    public ResponseData getResponseData() {
        return this.responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    @Override
    public String toString() {
        return "ResponseData[" + this.responseData + "]";
    }

    static class ResponseData {
        private List<Result> results;

        public List<Result> getResults() {
            return this.results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        @Override
        public String toString() {
            return "Results[" + this.results + "]";
        }
    }

    static class Result {
        private String url;
        private String title;

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "Result[url:" + this.url + ",title:" + this.title + "]";
        }
    }
}
