package com.stpi.campus.activity.notification;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import com.stpi.campus.entity.merchant.SubscribeMerchantInfo;
import com.stpi.campus.entity.merchant.SubscribeMerchantInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 13-12-4.
 */
public class SubscribeNewsFragment extends Fragment {

    private View view = null;

    private ListView newsList = null;

    private ProgressBar progressBar = null;

    private LoadSubscribeTask subscribeTask = null;
    private List<SubscribeMerchantInfo> infos = null;

    private String shop_picture_head = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_only, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        shop_picture_head = SubscribeNewsFragment.this.getString(R.string.shop_picture_head);
        newsList = (ListView) view.findViewById(R.id.nearby_news_list);

        progressBar.setVisibility(View.VISIBLE);
        subscribeTask.execute(UserInfo.userId);

        return view;
    }

    class LoadSubscribeTask extends AsyncTask<String, Integer, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = SubscribeNewsFragment.this.getString(R.string.subscribe_merchant_list_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));

            SubscribeMerchantInfoHelper share_tmp = new HttpListGetter<SubscribeMerchantInfoHelper>()
                    .getFromUrl(url, SubscribeMerchantInfoHelper.class);

            if (share_tmp == null || share_tmp.getState().equals("fail"))
                return res;

            infos = share_tmp.getResults();
            System.out.println(" SubscribeNewsFragment result get = " + infos.size());

            if (infos == null)
                return res;


            for (SubscribeMerchantInfo share : infos) {
                Map<String, Object> line = new HashMap<String, Object>();

                line.put("merchant_name", share.getMerchantName());
                line.put("time", share.getFromdate());
                line.put("content", share.getContent());
                line.put("picture", shop_picture_head + share.getPicture());
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            subscribeTask = null;
            progressBar.setVisibility(View.INVISIBLE);

            SimpleAdapter sa = new SimpleAdapter(SubscribeNewsFragment.this.getActivity(), data, R.layout.subscribe_news_item,
                    new String[]{"picture", "merchant_name", "content", "time"},
                    new int[]{R.id.news_image, R.id.news_title, R.id.news_content, R.id.news_time});
            newsList.setAdapter(sa);
            newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(SubscribeNewsFragment.this.getActivity(),
                            SubscribeMerchantActivity.class);
                    intent.putExtra("shop_id", infos.get(i).getMerchantId());
                    startActivity(intent);
                }
            });
        }

        @Override
        protected void onCancelled() {
            subscribeTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
