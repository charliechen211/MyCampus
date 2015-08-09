package com.stpi.campus;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.stpi.campus.R;

public class WebContentActivity extends Activity {
    public WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);

        Bundle bundle = this.getIntent().getExtras();
        String url = bundle.getString("url");
        webView = (WebView) findViewById(R.id.webView_content);
        webView.loadUrl(url);
    }
}
