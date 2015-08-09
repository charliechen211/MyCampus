package com.stpi.campus.activity.friend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.activity.personalService.PersonHomepageActivity;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.entity.user.DetailUserInfo;
import com.stpi.campus.entity.user.PreviewUserInfoHelper;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 13-12-11.
 */
public class FriendsFragment extends RefreshableFragment {

    private View view = null;
    private ListView followListView;
    private ListView fanListView;
    private ListView recommendView;

    private SimpleAdapter follow_list_adapter;
    private SimpleAdapter fan_list_adapter;
    private SimpleAdapter recommend_list_adapter;
    private List<Map<String, Object>> followData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> fanData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> recommendData = new ArrayList<Map<String, Object>>();

    private List<DetailUserInfo> followInfos = null;
    private List<DetailUserInfo> fanInfos = null;
    private List<DetailUserInfo> recommendInfos = null;
    private ProgressBar progressBar;

    private String person_picture_head = null;

    private Menu menu = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_friends, container, false);
        setHasOptionsMenu(true);

        person_picture_head = this.getString(R.string.person_picture_head);

        initIcons();
        initFollowView();
        initFanView();
        initRecommendView();
        initEvents();

        return view;
    }

    @Override
    public void refresh() {
        super.refresh();
        setActionBarMenu();
        progressBar.setVisibility(View.VISIBLE);
        new LoadFollowTask().execute(UserInfo.userId);
        new LoadFanTask().execute(UserInfo.userId);
        new LoadRecommendTask().execute(UserInfo.userId);
    }

    private void setActionBarMenu() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_friend));
        menu.clear();
        menu.add(0, R.integer.common_search, 0, "搜索").setIcon(R.drawable.actionbar_search_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.integer.common_search:
                this.beginSearch();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void beginSearch() {
        Intent intent = new Intent(FriendsFragment.this.getActivity(), SearchFriendsActivity.class);
        intent.putExtra("searchContext", "*");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        new LoadFollowTask().execute(UserInfo.userId);
//        new LoadFollowTask().execute(UserInfo.userId);
        new LoadFanTask().execute(UserInfo.userId);
        new LoadRecommendTask().execute(UserInfo.userId);
    }

    private void initIcons() {

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

    }

    private void initEvents() {

        TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost);

        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("我的关注").setIndicator("我的关注")
                .setContent(R.id.friendListView));

        tabHost.addTab(tabHost.newTabSpec("我的粉丝").setIndicator("我的粉丝")
                .setContent(R.id.fanListView));

        tabHost.addTab(tabHost.newTabSpec("好友推荐").setIndicator("好友推荐")
                .setContent(R.id.recommendView));
    }

    private void initFollowView() {

        followListView = (ListView) view.findViewById(R.id.friendListView);

        followListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FriendsFragment.this.getActivity(),
                        PersonHomepageActivity.class);
                intent.putExtra("userId", String.valueOf(followInfos.get(i).getUserId()));
                intent.putExtra("userName", String.valueOf(followInfos.get(i).getUserName()));
                intent.putExtra("schoolName", String.valueOf(followInfos.get(i).getSchoolName()));
                intent.putExtra("regionName", String.valueOf(followInfos.get(i).getRegionName()));
                intent.putExtra("tag1", String.valueOf(followInfos.get(i).getTags().size() > 0 ?
                        followInfos.get(i).getTags().get(0) : ""));
                intent.putExtra("tag2", String.valueOf(followInfos.get(i).getTags().size() > 1 ?
                        followInfos.get(i).getTags().get(1) : ""));
                intent.putExtra("tag3", String.valueOf(followInfos.get(i).getTags().size() > 2 ?
                        followInfos.get(i).getTags().get(2) : ""));
                intent.putExtra("picture",
                        String.valueOf(followInfos.get(i).getPicture() != null ?
                                person_picture_head + followInfos.get(i).getPicture() :
                                person_picture_head + "000000.jpg"));
                startActivity(intent);
            }
        });

        follow_list_adapter = new SimpleAdapter(FriendsFragment.this.getActivity(),
                followData,
                R.layout.friend_list_item,
                new String[]{"name", "picture", "tag_1", "tag_2", "tag_3"},
                new int[]{R.id.name, R.id.headImage, R.id.tagTab1, R.id.tagTab2, R.id.tagTab3});

        follow_list_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory().cacheOnDisc()
                            .preProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bitmap) {
                                    return ImageFileUtils.getRoundCornerImage(bitmap,
                                            R.dimen.size25);
                                }
                            })
                            .build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });
        followListView.setAdapter(follow_list_adapter);

    }

    private void initFanView() {

        fanListView = (ListView) view.findViewById(R.id.fanListView);

        fanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FriendsFragment.this.getActivity(),
                        PersonHomepageActivity.class);
                intent.putExtra("userId", String.valueOf(fanInfos.get(i).getUserId()));
                intent.putExtra("userName", String.valueOf(fanInfos.get(i).getUserName()));
                intent.putExtra("schoolName", String.valueOf(fanInfos.get(i).getSchoolName()));
                intent.putExtra("regionName", String.valueOf(fanInfos.get(i).getRegionName()));
                intent.putExtra("tag1", String.valueOf(fanInfos.get(i).getTags().size() > 0 ?
                        fanInfos.get(i).getTags().get(0) : ""));
                intent.putExtra("tag2", String.valueOf(fanInfos.get(i).getTags().size() > 1 ?
                        fanInfos.get(i).getTags().get(1) : ""));
                intent.putExtra("tag3", String.valueOf(fanInfos.get(i).getTags().size() > 2 ?
                        fanInfos.get(i).getTags().get(2) : ""));
                intent.putExtra("picture",
                        String.valueOf(fanInfos.get(i).getPicture() != null ?
                                person_picture_head + fanInfos.get(i).getPicture() :
                                person_picture_head + "000000.jpg"));
                startActivity(intent);
            }
        });

        fan_list_adapter = new SimpleAdapter(FriendsFragment.this.getActivity(),
                fanData,
                R.layout.friend_list_item,
                new String[]{"name", "picture", "tag_1", "tag_2", "tag_3"},
                new int[]{R.id.name, R.id.headImage, R.id.tagTab1, R.id.tagTab2, R.id.tagTab3});

        fan_list_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory().cacheOnDisc()
                            .preProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bitmap) {
                                    return ImageFileUtils.getRoundCornerImage(bitmap,
                                            R.dimen.size25);
                                }
                            })
                            .build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });
        fanListView.setAdapter(fan_list_adapter);

    }

    private void initRecommendView() {

        recommendView = (ListView) view.findViewById(R.id.recommendView);

        recommendView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FriendsFragment.this.getActivity(),
                        PersonHomepageActivity.class);
                intent.putExtra("userId", String.valueOf(recommendInfos.get(i).getUserId()));
                intent.putExtra("userName", String.valueOf(recommendInfos.get(i).getUserName()));
                intent.putExtra("schoolName", String.valueOf(recommendInfos.get(i).getSchoolName()));
                intent.putExtra("regionName", String.valueOf(recommendInfos.get(i).getRegionName()));
                intent.putExtra("tag1", String.valueOf(recommendInfos.get(i).getTags().size() > 0 ?
                        recommendInfos.get(i).getTags().get(0) : ""));
                intent.putExtra("tag2", String.valueOf(recommendInfos.get(i).getTags().size() > 1 ?
                        recommendInfos.get(i).getTags().get(1) : ""));
                intent.putExtra("tag3", String.valueOf(recommendInfos.get(i).getTags().size() > 2 ?
                        recommendInfos.get(i).getTags().get(2) : ""));
                intent.putExtra("picture",
                        String.valueOf(recommendInfos.get(i).getPicture() != null ?
                                person_picture_head + recommendInfos.get(i).getPicture() :
                                person_picture_head + "000000.jpg"));
                startActivity(intent);
            }
        });

        recommend_list_adapter = new SimpleAdapter(FriendsFragment.this.getActivity(),
                recommendData,
                R.layout.friend_list_item,
                new String[]{"name", "picture", "tag_1", "tag_2", "tag_3"},
                new int[]{R.id.name, R.id.headImage, R.id.tagTab1, R.id.tagTab2, R.id.tagTab3});

        recommend_list_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = (String) data;
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory().cacheOnDisc()
                            .preProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bitmap) {
                                    return ImageFileUtils.getRoundCornerImage(bitmap,
                                            R.dimen.size25);
                                }
                            })
                            .build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });

        recommendView.setAdapter(recommend_list_adapter);

    }

    class LoadFollowTask extends
            AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            String url = FriendsFragment.this.getString(R.string.follow_list_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("userId", params[0]));

            PreviewUserInfoHelper followListResult = new JsonGetter<PreviewUserInfoHelper>()
                    .getFromUrl(url, PreviewUserInfoHelper.class, param);

            if (followListResult == null || followListResult.getState().equals("fail"))
                return false;

            followInfos = followListResult.getResults();

            UserInfo.friendList.clear();

            followData.clear();
            for (DetailUserInfo follow : followInfos) {
                Map<String, Object> line = new HashMap<String, Object>();
                UserInfo.friendList.add(String.valueOf(follow.getUserId()));
                if (follow.getUserName() != null)
                    line.put("name", follow.getUserName());
                else
                    line.put("name", "");
                if (follow.getPicture() != null)
                    line.put("picture", person_picture_head + follow.getPicture());
                else
                    line.put("picture", "");
                line.put("friendId", follow.getUserId());
                if (follow.getTags() == null) {
                    line.put("tag_1", "");
                    line.put("tag_2", "");
                    line.put("tag_3", "");
                } else {
                    if (follow.getTags().size() > 0)
                        line.put("tag_1", follow.getTags().get(0));
                    else
                        line.put("tag_1", "");
                    if (follow.getTags().size() > 1)
                        line.put("tag_2", follow.getTags().get(1));
                    else
                        line.put("tag_2", "");
                    if (follow.getTags().size() > 2)
                        line.put("tag_3", follow.getTags().get(2));
                    else
                        line.put("tag_3", "");
                }
                followData.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                progressBar.setVisibility(View.INVISIBLE);
                follow_list_adapter.notifyDataSetChanged();
            }

        }
    }

    class LoadFanTask extends
            AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            String url = FriendsFragment.this.getString(R.string.fan_list_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("userId", params[0]));

            PreviewUserInfoHelper fanListResult = new JsonGetter<PreviewUserInfoHelper>()
                    .getFromUrl(url, PreviewUserInfoHelper.class, param);

            if (fanListResult == null || fanListResult.getState().equals("fail"))
                return false;

            fanInfos = fanListResult.getResults();

            fanData.clear();
            for (DetailUserInfo follow : fanInfos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (follow.getUserName() != null)
                    line.put("name", follow.getUserName());
                else
                    line.put("name", "");
                if (follow.getPicture() != null)
                    line.put("picture", person_picture_head + follow.getPicture());
                else
                    line.put("picture", "");
                line.put("friendId", follow.getUserId());
                if (follow.getTags() == null) {
                    line.put("tag_1", "");
                    line.put("tag_2", "");
                    line.put("tag_3", "");
                } else {
                    if (follow.getTags().size() > 0)
                        line.put("tag_1", follow.getTags().get(0));
                    else
                        line.put("tag_1", "");
                    if (follow.getTags().size() > 1)
                        line.put("tag_2", follow.getTags().get(1));
                    else
                        line.put("tag_2", "");
                    if (follow.getTags().size() > 2)
                        line.put("tag_3", follow.getTags().get(2));
                    else
                        line.put("tag_3", "");
                }
                fanData.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                progressBar.setVisibility(View.INVISIBLE);
                fan_list_adapter.notifyDataSetChanged();
            }

        }
    }

    class LoadRecommendTask extends
            AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            String url = FriendsFragment.this.getString(R.string.friend_recommend_list_head);
            url += "?userId=" + params[0];

            PreviewUserInfoHelper follow_tmp = new JsonGetter<PreviewUserInfoHelper>()
                    .getFromUrl(url, PreviewUserInfoHelper.class);

            if (follow_tmp == null || follow_tmp.getState().equals("fail"))
                return false;

            recommendInfos = follow_tmp.getResults();
//            if (recommendInfos == null)
//                return false;

            recommendData.clear();
            for (DetailUserInfo follow : recommendInfos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (follow.getUserName() != null)
                    line.put("name", follow.getUserName() + "(" + follow.getSchoolName() + " "
                            + follow.getRegionName() + ")");
                else
                    line.put("name", "");
                if (follow.getPicture() != null)
                    line.put("picture", person_picture_head + follow.getPicture());
                else
                    line.put("picture", "");
                line.put("friendId", follow.getUserId());
                if (follow.getTags() == null) {
                    line.put("tag_1", "");
                    line.put("tag_2", "");
                    line.put("tag_3", "");
                } else {
                    if (follow.getTags().size() > 0)
                        line.put("tag_1", follow.getTags().get(0));
                    else
                        line.put("tag_1", "");
                    if (follow.getTags().size() > 1)
                        line.put("tag_2", follow.getTags().get(1));
                    else
                        line.put("tag_2", "");
                    if (follow.getTags().size() > 2)
                        line.put("tag_3", follow.getTags().get(2));
                    else
                        line.put("tag_3", "");
                }
                recommendData.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                progressBar.setVisibility(View.INVISIBLE);
                recommend_list_adapter.notifyDataSetChanged();
            }

        }
    }

//    public final class FriendViewHolder {
//        public ImageView img;
//        public TextView name;
//        public TextView tag1;
//        public TextView tag2;
//        public Button cancelAttentionBtn;
//    }
//
//    public final class ViewHolder {
//        public ImageView img;
//        public TextView name;
//        public TextView tag1;
//        public TextView tag2;
//        public Button attentionBtn;
//    }
//
//    public class MyFriendAdapter extends BaseAdapter {
//
//        private LayoutInflater mInflater;
//
//        public MyFriendAdapter(Context context) {
//            this.mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return followData.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            FriendViewHolder holder = null;
//            if (convertView == null) {
//
//                holder = new FriendViewHolder();
//
//                convertView = mInflater.inflate(R.layout.friend_list_item, null);
//                holder.img = (ImageView) convertView.findViewById(R.id.headImage);
//                holder.name = (TextView) convertView.findViewById(R.id.name);
//                holder.tag1 = (TextView) convertView.findViewById(R.id.textTab1);
//                holder.tag2 = (TextView) convertView.findViewById(R.id.textTab2);
//                holder.cancelAttentionBtn = (Button) convertView.findViewById(R.id.cancelAttentionButton);
//                convertView.setTag(holder);
//            } else {
//
//                holder = (FriendViewHolder) convertView.getTag();
//            }
//
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.displayImage((String) followData.get(position).get("picture"), holder.img);
//            holder.name.setText((String) followData.get(position).get("name"));
//            holder.tag1.setText((String) followData.get(position).get("tag1"));
//            if (holder.tag1.getText().toString().trim().length() <= 0)
//                holder.tag1.setVisibility(View.INVISIBLE);
//            holder.tag2.setText((String) followData.get(position).get("tag2"));
//            if (holder.tag2.getText().toString().trim().length() <= 0)
//                holder.tag2.setVisibility(View.INVISIBLE);
//
//            holder.cancelAttentionBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    attemptCancelAttention((Integer) followData.get(position).get("friendId"));
//                    new LoadFollowTask().execute(UserInfo.userId);
//                    new LoadRecommendTask().execute(UserInfo.userId);
//                }
//            });
//
//            return convertView;
//        }
//    }
//
//    public class MyAdapter extends BaseAdapter {
//
//        private LayoutInflater mInflater;
//
//
//        public MyAdapter(Context context) {
//            this.mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return recommendData.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            ViewHolder holder = null;
//            if (convertView == null) {
//
//                holder = new ViewHolder();
//
//                convertView = mInflater.inflate(R.layout.friend_recommender_list_item, null);
//                holder.img = (ImageView) convertView.findViewById(R.id.headImage);
//                holder.name = (TextView) convertView.findViewById(R.id.name);
//                holder.tag1 = (TextView) convertView.findViewById(R.id.textTab1);
//                holder.tag2 = (TextView) convertView.findViewById(R.id.textTab2);
//                holder.attentionBtn = (Button) convertView.findViewById(R.id.attentionButton);
//                convertView.setTag(holder);
//
//            } else {
//
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.displayImage((String) recommendData.get(position).get("picture"), holder.img);
//            holder.name.setText((String) recommendData.get(position).get("name"));
//            holder.tag1.setText((String) recommendData.get(position).get("tag1"));
//            if (holder.tag1.getText().toString().trim().length() <= 0)
//                holder.tag1.setVisibility(View.INVISIBLE);
//            holder.tag2.setText((String) recommendData.get(position).get("tag2"));
//            if (holder.tag2.getText().toString().trim().length() <= 0)
//                holder.tag2.setVisibility(View.INVISIBLE);
////            holder.intermediary.setText((String) recommendData.get(position).get("friend_reason"));
//
//            holder.attentionBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    attemptAddAttention((Integer) recommendData.get(position).get("friendId"));
//                    new LoadFollowTask().execute(UserInfo.userId);
//                    new LoadRecommendTask().execute(UserInfo.userId);
//                }
//            });
//
//            return convertView;
//        }
//
//    }

}