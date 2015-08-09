package com.stpi.campus.activity.partTimeJob;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.stpi.campus.entity.partTimeJob.PartTimeJobHelper;
import com.stpi.campus.entity.partTimeJob.PartTimeJobInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/8/11.
 */

/**
 * 0 is for internship, 1 is for parttime job
 */
public class PartTimeJobFragment extends RefreshableFragment {

    List<List<Map<String, Object>>> jobInfoDatas = new ArrayList<List<Map<String, Object>>>();
    private View view;
    private ListView[] jobListViews = new ListView[2];
    private Integer[] pageId = new Integer[2];
    private Integer pageSize = 10;
    private Boolean[] hasMore = new Boolean[2];
    private TabHost tabHost;
    private ProgressBar progressBar;
    private List<List<PartTimeJobInfo>> jobInfoList = new ArrayList<List<PartTimeJobInfo>>();
    private ImageButton addNewItem;
    private SimpleAdapter[] adapters = new SimpleAdapter[2];
    private Menu menu;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.parttime_job, container, false);
        setHasOptionsMenu(true);
        jobListViews[0] = (ListView) view.findViewById(R.id.internshipList);
        jobListViews[1] = (ListView) view.findViewById(R.id.parttimeList);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("0").setIndicator(createTabWidget("找实习")).setContent(R
                .id.internshipList));
        tabHost.addTab(tabHost.newTabSpec("1").setIndicator(createTabWidget("找兼职")).setContent(R
                .id.parttimeList));
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
                startAsynLoad(tabHost.getCurrentTab());
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
        initAdaptor();
        initEvent();
        return view;
    }

    @Override
    public void refresh() {
        initActionBar();
        startAsynLoad(tabHost.getCurrentTab());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    public void initActionBar() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_parttime));
        menu.add(0, R.integer.add_parttime, 0, "发帖").setIcon(R.drawable.actionbar_add_icon).setShowAsAction
                (MenuItem
                .SHOW_AS_ACTION_IF_ROOM);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.integer.add_parttime:
                addNewItem();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewItem() {
        Intent intent = new Intent(PartTimeJobFragment.this.getActivity(), JobPublishActivity.class);
        startActivity(intent);
    }

    public View createTabWidget(String title) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab_widget,null);
        TextView tabTitle = (TextView) view.findViewById(R.id.tabTitle);
        tabTitle.setText(title);
        tabTitle.setTextColor(getResources().getColor(R.color.not_choose_tab_fold));
        if(title.equals("找实习")) {
            view.setBackgroundResource(R.drawable.tab_selected_background);
            tabTitle.setTextColor(getResources().getColor(R.color.choose_tab_fold));
        }
        return view;
    }

    private void startAsynLoad(Integer tabId) {
        for (int i = 0; i < 2; i++) {
            hasMore[i] = true;
            jobInfoDatas.get(i).clear();
            jobInfoList.get(i).clear();
            pageId[i] = 0;
        }
        progressBar.setVisibility(View.VISIBLE);
        new LoadPartTimeJobTask().execute(tabId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initAdaptor() {
        for (int i = 0; i < 2; i++) {
            jobInfoDatas.add(i, new ArrayList<Map<String, Object>>());
            jobInfoList.add(i, new ArrayList<PartTimeJobInfo>());
            adapters[i] = new SimpleAdapter(PartTimeJobFragment.this.getActivity(), jobInfoDatas.get(i),
                    R.layout.parttime_job_list_item, new String[]{"title", "date",}, new int[]{
                    R.id.jobTitle, R.id.jobTime}
            );
            jobListViews[i].setAdapter(adapters[i]);
        }
    }

    public void initEvent() {
        jobListViews[0].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[0]) {
                        pageId[0]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadPartTimeJobTask().execute(0);
                    } else
                        Toast.makeText(PartTimeJobFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        jobListViews[1].setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore[1]) {
                        pageId[1]++;
                        progressBar.setVisibility(View.VISIBLE);
                        new LoadPartTimeJobTask().execute(1);
                    } else
                        Toast.makeText(PartTimeJobFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        jobListViews[0].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PartTimeJobFragment.this.getActivity(), PartTimeJobDetailActivity.class);
                intent.putExtra("itemId", jobInfoList.get(0).get(i).getJobId());
                intent.putExtra("title", jobInfoList.get(0).get(i).getTitle());
                intent.putExtra("createTime", jobInfoList.get(0).get(i).getDate());
                startActivity(intent);
            }
        });
        jobListViews[1].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PartTimeJobFragment.this.getActivity(), PartTimeJobDetailActivity.class);
                intent.putExtra("itemId", jobInfoList.get(1).get(i).getJobId());
                intent.putExtra("title", jobInfoList.get(1).get(i).getTitle());
                intent.putExtra("createTime", jobInfoList.get(1).get(i).getDate());
                startActivity(intent);
            }
        });
    }

    class LoadPartTimeJobTask extends
            AsyncTask<Integer, Integer, List<Map<String, Object>>> {
        private Integer jobType = 0;

        @Override
        protected List<Map<String, Object>> doInBackground(Integer... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            jobType = params[0];
            String url = PartTimeJobFragment.this
                    .getString(R.string.part_time_job_list_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("pageId", String.valueOf(pageId[jobType])));
            param.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
            param.add(new BasicNameValuePair("jobtype", String.valueOf(jobType)));
            PartTimeJobHelper partTimeJobHelper = new HttpListGetter<PartTimeJobHelper>()
                    .getFromUrl(url, PartTimeJobHelper.class, param);

            if (partTimeJobHelper == null || partTimeJobHelper.getState().equals("fail")) {
                return res;
            }

            List<PartTimeJobInfo> nowJobInfoList = partTimeJobHelper.getResult();
            if (nowJobInfoList == null)
                return res;

            for (PartTimeJobInfo job : nowJobInfoList) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("title", job.getTitle());
                line.put("date", job.getDate());
                jobInfoDatas.get(jobType).add(line);
                res.add(line);
                jobInfoList.get(jobType).add(job);
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> res) {
            progressBar.setVisibility(View.INVISIBLE);
            if (res.size() == 0) {
                hasMore[jobType] = false;
                return;
            } else {
                hasMore[jobType] = true;
            }
            adapters[jobType].notifyDataSetChanged();
        }
    }
}
