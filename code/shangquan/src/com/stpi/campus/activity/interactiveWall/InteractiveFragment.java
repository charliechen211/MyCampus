package com.stpi.campus.activity.interactiveWall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.entity.interactionWall.InteractionTopicHelper;
import com.stpi.campus.entity.interactionWall.InteractionTopicInfo;
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
 * Created by Administrator on 2014/7/28.
 */
public class InteractiveFragment extends RefreshableFragment {

    private View view = null;
    private Button postButton;
    private ListView topicList;
    private TextView topic;
    private Integer pageId = 0;
    private Integer pageSize = 10;
    private Boolean hasMore = true;
    private ProgressBar progressBar;
    private List<InteractionTopicInfo> interactionTopics = new ArrayList<InteractionTopicInfo>();
    private List<Map<String, Object>> allDatas = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter;
    private Menu menu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_interaction, container, false);
        setHasOptionsMenu(true);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);
        topicList = (ListView) view.findViewById(R.id.topicList);
        topicList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) {
                    if (hasMore) {
                        pageId++;
                        new LoadInteractionTopicTask().execute();
                    } else
                        Toast.makeText(InteractiveFragment.this.getActivity(), "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        adapter = new SimpleAdapter(InteractiveFragment.this.getActivity(), allDatas,
                R.layout.interaction_list_item, new String[]{"content", "commentNum",
                "timestamp", "isFriend"}, new int[]{
                R.id.content, R.id.commentNum, R.id.timestamp, R.id.isFriend}
        );

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof TextView) && (data instanceof Boolean)) {
                    TextView textView = (TextView) view;
                    Boolean isFriend = (Boolean) data;
                    if (isFriend)
                        textView.setText("来自好友");
                    else
                        textView.setText("来自陌生人");
                    return true;
                }
                if((view instanceof TextView) && (data instanceof Integer)){
                    TextView textView = (TextView) view;
                    Integer commentNum = (Integer) data;
                    textView.setText("评论("+commentNum+")");
                    return true;
                }
                return false;
            }
        });
        topicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(InteractiveFragment.this.getActivity(), InteractiveDetailActivity.class);
                intent.putExtra("content", interactionTopics.get(arg2).getContent());
                intent.putExtra("timestamp", interactionTopics.get(arg2).getTimestamp());
                intent.putExtra("isFriend", interactionTopics.get(arg2).isFriend() ? "来自好友" : "来自陌生人");
                intent.putExtra("topicId", interactionTopics.get(arg2).getTopicId());
                startActivity(intent);
            }
        });
        topicList.setAdapter(adapter);

        return view;
    }

    @Override
    public void refresh() {
        super.refresh();
        setActionBarMenu();
        startAsynLoad();
    }

    private void setActionBarMenu() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_interactivity));
        menu.clear();
        menu.add(0,R.integer.add_interactivity, 0, "发帖").setIcon(R.drawable.actionbar_add_icon).setShowAsAction
                (MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.integer.add_interactivity:
                showPostDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAsynLoad() {
        hasMore = true;
        allDatas.clear();
        interactionTopics.clear();
        pageId = 0;
        progressBar.setVisibility(View.VISIBLE);
        new LoadInteractionTopicTask().execute();
    }

    private void showPostDialog() {
        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        View post_topic_layout = inflater.inflate(R.layout.interaction_post_dialog_xml,
                (ViewGroup) this.getActivity().findViewById(R.id.postTopicDialog));

        topic = (EditText) post_topic_layout.findViewById(R.id.postContent);
        new AlertDialog.Builder(this.getActivity()).setTitle("匿名发表秘密").setView(post_topic_layout)
                .setPositiveButton("发表", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String error = attemptAddInfo();
                        if (!error.equals("success"))
                            Toast.makeText(InteractiveFragment.this.getActivity(), error, Toast.LENGTH_LONG).show();
                        startAsynLoad();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private String attemptAddInfo() {
        String topicContent = topic.getText().toString();
        if (topicContent == null || topicContent.length() <= 0) {
            return "请输入内容";
        }
        PostTopicAsyTask postTask = new PostTopicAsyTask(InteractiveFragment.this.getActivity());
        postTask.execute(UserInfo.userId, UserInfo.schoolId, topicContent);
        return "success";
    }

    class LoadInteractionTopicTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = InteractiveFragment.this
                    .getString(R.string.interaction_topic_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("schoolId", String.valueOf(UserInfo.schoolId)));
            param.add(new BasicNameValuePair("userId", UserInfo.userId));
            param.add(new BasicNameValuePair("pageId", String.valueOf(pageId)));
            param.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));

            InteractionTopicHelper interactionTopicHelper = new HttpListGetter<InteractionTopicHelper>()
                    .getFromUrl(url, InteractionTopicHelper.class, param);

            if (interactionTopicHelper == null || interactionTopicHelper.getState().equals("fail")) {
                return res;
            }
            interactionTopics.addAll(interactionTopicHelper.getResult());
            for (InteractionTopicInfo topic : interactionTopicHelper.getResult()) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("content", topic.getContent());
                line.put("commentNum", topic.getCommentNum());
                line.put("isFriend", topic.isFriend());
                line.put("timestamp", topic.getTimestamp());
                res.add(line);
                allDatas.add(line);
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> res) {
            progressBar.setVisibility(View.INVISIBLE);
            if (res.size() == 0) {
                hasMore = false;
                return;
            } else {
                hasMore = true;
            }
            adapter.notifyDataSetChanged();
        }
    }
}