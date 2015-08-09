package com.stpi.campus.activity.campusNews;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.stpi.campus.entity.campusNews.CampusNewsHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.entity.campusNews.CampusNewsInfo;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/7/29.
 */
public class CampusNewsFragment extends RefreshableFragment {
    private View view;

    /**
     * ListView
     */
    private ListView[] newsList = new ListView[5];

    private TabHost tabHost;
    private ProgressBar progressBar;
    private Integer pageSize = 10;
    /**
     * hasMore is to determine whether the list gets to the bottom
     */
    private Boolean[] hasMore = new Boolean[5];

    /**
     * pageId is to log where are we on the list
     */
    private Integer[] pageId = new Integer[5];
    /**
     * List<CampusNewsInfo> store datas used by list item click
     */
    private List<List<CampusNewsInfo>> news = new ArrayList<List<CampusNewsInfo>>();
    /**
     * List<Map<String, Object>> store datas used by simple adaptor
     */

    private Menu menu;
    private List<List<Map<String, Object>>> datas = new ArrayList<List<Map<String, Object>>>();

    private SimpleAdapter[] adapters = new SimpleAdapter[5];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.campus_news, container, false);
        setHasOptionsMenu(true);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);
        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(
                tabHost.newTabSpec("0").setIndicator(createTabWidget("要闻")).setContent(R.id
                                .campus_news_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("1").setIndicator(createTabWidget("学术")).setContent(R.id
                                .scholar_news_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("2").setIndicator(createTabWidget("院系")).setContent(R.id
                        .department_news_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("3").setIndicator(createTabWidget("专题")).setContent(R.id
                        .topic_news_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("4").setIndicator(createTabWidget("人物")).setContent(R.id
                        .person_news_list)
        );
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                for(int i=0;i< tabHost.getTabWidget().getChildCount();i++){
                    View view = tabHost.getTabWidget().getChildAt(i);
                    TextView tv = (TextView) view.findViewById(R.id.tabTitle);
                    if(i == tabHost.getCurrentTab()) {
                        tv.setTextColor(getResources().getColor(R.color.choose_tab_fold));
                        view.setBackgroundResource(R.drawable.tab_selected_background);
                    }else {
                        tv.setTextColor(getResources().getColor(R.color.not_choose_tab_fold));
                        view.setBackgroundResource(R.drawable.tab_unselected_background);
                    }
                }
                loadAsynNews(tabHost.getCurrentTab());
            }
        });

        /**
         * be strict compliant with previous define
         */
        newsList[0] = (ListView) view.findViewById(R.id.campus_news_list);
        newsList[1] = (ListView) view.findViewById(R.id.scholar_news_list);
        newsList[2] = (ListView) view.findViewById(R.id.department_news_list);
        newsList[3] = (ListView) view.findViewById(R.id.topic_news_list);
        newsList[4] = (ListView) view.findViewById(R.id.person_news_list);

        newsViewInit();
        eventInit();


        return view;
    }


    @Override
    public void refresh() {
        super.refresh();
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_campus_news));
        menu.clear();
        loadAsynNews(tabHost.getCurrentTab());
    }

    public View createTabWidget(String title) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab_widget,null);
        TextView tabTitle = (TextView) view.findViewById(R.id.tabTitle);
        tabTitle.setText(title);
        tabTitle.setTextSize(16);
        tabTitle.setTextColor(getResources().getColor(R.color.not_choose_tab_fold));
        if(title.equals("要闻")) {
            view.setBackgroundResource(R.drawable.tab_selected_background);
            tabTitle.setTextColor(getResources().getColor(R.color.choose_tab_fold));
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    private void loadAsynNews(Integer tabId) {
        progressBar.setVisibility(View.VISIBLE);
        for (int i = 0; i < 5; i++) {
            pageId[i] = 0;
            hasMore[i] = true;
            datas.get(i).clear();
            news.get(i).clear();
        }
        new LoadCampusNewsTask().execute(tabId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * news List init
     */

    public void newsViewInit() {
        for (int i = 0; i < 5; i++) {
            news.add(i, new ArrayList<CampusNewsInfo>());
            datas.add(i, new ArrayList<Map<String, Object>>());
            adapters[i] = new SimpleAdapter(CampusNewsFragment.this.getActivity(), datas.get(i),
                    R.layout.campusnews_list_item, new String[]{"title", "timestamp",}, new int[]{
                    R.id.newsTitle, R.id.newsTime}
            );
            newsList[i].setAdapter(adapters[i]);
        }

    }

    /**
     * news List event init
     */

    public void eventInit() {

        newsList[0].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[0]) {
                        pageId[0]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadCampusNewsTask().execute(0);
                    } else
                        Toast.makeText(CampusNewsFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        newsList[1].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[1]) {
                        pageId[1]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadCampusNewsTask().execute(1);
                    } else
                        Toast.makeText(CampusNewsFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        newsList[2].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[2]) {
                        pageId[2]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadCampusNewsTask().execute(2);
                    } else
                        Toast.makeText(CampusNewsFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        newsList[3].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[3]) {
                        pageId[3]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadCampusNewsTask().execute(3);
                    } else
                        Toast.makeText(CampusNewsFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        newsList[4].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[4]) {
                        pageId[4]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadCampusNewsTask().execute(4);
                    } else
                        Toast.makeText(CampusNewsFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        newsList[0].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                Intent intent = new Intent(CampusNewsFragment.this.getActivity(), CampusNewsDetailActivity.class);
                intent.putExtra("topicId", news.get(0).get(j).getTopicId());
                intent.putExtra("newsTitle", news.get(0).get(j).getTitle());
                intent.putExtra("newsTime", news.get(0).get(j).getTimestamp());
                startActivity(intent);
            }
        });

        newsList[1].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                Intent intent = new Intent(CampusNewsFragment.this.getActivity(), CampusNewsDetailActivity.class);
                intent.putExtra("topicId", news.get(1).get(j).getTopicId());
                intent.putExtra("newsTitle", news.get(1).get(j).getTitle());
                intent.putExtra("newsTime", news.get(1).get(j).getTimestamp());
                startActivity(intent);
            }
        });
        newsList[2].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                Intent intent = new Intent(CampusNewsFragment.this.getActivity(), CampusNewsDetailActivity.class);
                intent.putExtra("topicId", news.get(2).get(j).getTopicId());
                intent.putExtra("newsTitle", news.get(2).get(j).getTitle());
                intent.putExtra("newsTime", news.get(2).get(j).getTimestamp());
                startActivity(intent);
            }
        });
        newsList[3].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                Intent intent = new Intent(CampusNewsFragment.this.getActivity(), CampusNewsDetailActivity.class);
                intent.putExtra("topicId", news.get(3).get(j).getTopicId());
                intent.putExtra("newsTitle", news.get(3).get(j).getTitle());
                intent.putExtra("newsTime", news.get(3).get(j).getTimestamp());
                startActivity(intent);
            }
        });
        newsList[4].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                Intent intent = new Intent(CampusNewsFragment.this.getActivity(), CampusNewsDetailActivity.class);
                intent.putExtra("topicId", news.get(4).get(j).getTopicId());
                intent.putExtra("newsTitle", news.get(4).get(j).getTitle());
                intent.putExtra("newsTime", news.get(4).get(j).getTimestamp());
                startActivity(intent);
            }
        });

    }

    class LoadCampusNewsTask extends
            AsyncTask<Integer, Integer, List<Map<String, Object>>> {
        /**
         * indicate current news type
         */
        private Integer newsType;

        @Override
        protected List<Map<String, Object>> doInBackground(Integer... params) {
            newsType = params[0];
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = CampusNewsFragment.this.getString(R.string.campus_news_list_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("schoolId", String.valueOf(UserInfo.schoolId)));
            param.add(new BasicNameValuePair("topicType", String.valueOf(newsType)));
            param.add(new BasicNameValuePair("pageId", String.valueOf(pageId[newsType])));
            param.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
            CampusNewsHelper campusNewsHelper = new HttpListGetter<CampusNewsHelper>()
                    .getFromUrl(url, CampusNewsHelper.class, param);

            if (campusNewsHelper == null || campusNewsHelper.getState().equals("fail")) {
                return res;
            }

            news.get(newsType).addAll(campusNewsHelper.getResult());

            for (CampusNewsInfo info : campusNewsHelper.getResult()) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("title", info.getTitle());
                line.put("timestamp", info.getTimestamp());
                datas.get(newsType).add(line);
                res.add(line);
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            progressBar.setVisibility(View.INVISIBLE);
            if (data.size() == 0) {
                hasMore[newsType] = false;
                return;
            } else {
                hasMore[newsType] = true;
            }
            adapters[newsType].notifyDataSetChanged();
        }
    }
}