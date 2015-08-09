package com.stpi.campus.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.R;
import com.stpi.campus.activity.personalService.PersonHomepageActivity;
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
 * Created by cyc on 13-12-25.
 */
public class SearchFriendsActivity extends Activity {

    private ListView friendListView;
    private ProgressBar progressBar = null;
    private SimpleAdapter friend_list_adapter = null;
    private SearchView searchView = null;

    private List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
    private List<DetailUserInfo> friends = new ArrayList<DetailUserInfo>();

    private int pageId = 0;
    private int pageSize = 8;
    private boolean have_more = true;
    private String searchContext = "";

    private SearchFriendTask searchTask = null;

    private String person_picture_head = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        person_picture_head = this.getString(R.string.person_picture_head);

        initIcons();
        initEvents();

    }

    private void initIcons() {

        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        searchView = (SearchView) this.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchContext = s;
                pageId = 0;
                have_more = true;
                searchData.clear();
                friends.clear();
                startTask();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.trim().length() <= 0) {
                    searchContext = "";
                    pageId = 0;
                    have_more = true;
                    searchData.clear();
                    friends.clear();
                    startTask();
                } else {
                    searchContext = s.trim();
                    pageId = 0;
                    have_more = true;
                    searchData.clear();
                    friends.clear();
                    startTask();
                }
                return false;
            }
        });

        friendListView = (ListView) this.findViewById(R.id.friendListView);

    }

    private void initEvents() {

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchFriendsActivity.this,
                        PersonHomepageActivity.class);
                intent.putExtra("userId", String.valueOf(friends.get(i).getUserId()));
                intent.putExtra("userName", String.valueOf(friends.get(i).getUserName()));
                intent.putExtra("schoolName", String.valueOf(friends.get(i).getSchoolName()));
                intent.putExtra("regionName", String.valueOf(friends.get(i).getRegionName()));
                intent.putExtra("tag1", String.valueOf(friends.get(i).getTags().size() > 0 ?
                        friends.get(i).getTags().get(0) : ""));
                intent.putExtra("tag2", String.valueOf(friends.get(i).getTags().size() > 1 ?
                        friends.get(i).getTags().get(1) : ""));
                intent.putExtra("tag3", String.valueOf(friends.get(i).getTags().size() > 2 ?
                        friends.get(i).getTags().get(2) : ""));
                intent.putExtra("picture",
                        String.valueOf(friends.get(i).getPicture() != null ?
                                person_picture_head + friends.get(i).getPicture() :
                                person_picture_head + "000000.jpg"));
                startActivity(intent);
            }
        });

        friendListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int lastItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (lastItem == searchData.size() && scrollState == SCROLL_STATE_IDLE) {
                    if (have_more) {
                        pageId++;
                        startTask();
                    } else
                        Toast.makeText(SearchFriendsActivity.this, "已到末尾", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
        searchContext = getIntent().getStringExtra("searchContext");
        startTask();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startTask() {
        if (searchTask != null)
            return;
        searchTask = new SearchFriendTask();
        searchTask.execute(searchContext, String.valueOf(pageId), String.valueOf(pageSize));
        progressBar.setVisibility(View.VISIBLE);
    }

    class SearchFriendTask extends AsyncTask<String, Void, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... args) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = SearchFriendsActivity.this.getString(R.string.search_friend_list_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("content", args[0]));
            params.add(new BasicNameValuePair("pageId", args[1]));
            params.add(new BasicNameValuePair("pageSize", args[2]));

            PreviewUserInfoHelper tmp = new JsonGetter<PreviewUserInfoHelper>().getFromUrl(url, PreviewUserInfoHelper.class, params);

            if (tmp == null || tmp.getState().equals("fail") || tmp.getResults() == null)
                return res;

            for (DetailUserInfo info : tmp.getResults()) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (info.getUserName() != null)
                    line.put("name", info.getUserName());
                else
                    line.put("name", "");
                if (info.getPicture() != null)
                    line.put("picture", person_picture_head + info.getPicture());
                else
                    line.put("picture", "");
                line.put("friendId", info.getUserId());
                if (info.getTags() == null) {
                    line.put("tag_1", "");
                    line.put("tag_2", "");
                    line.put("tag_3", "");
                } else {
                    if (info.getTags().size() > 0)
                        line.put("tag_1", info.getTags().get(0));
                    else
                        line.put("tag_1", "");
                    if (info.getTags().size() > 1)
                        line.put("tag_2", info.getTags().get(1));
                    else
                        line.put("tag_2", "");
                    if (info.getTags().size() > 1)
                        line.put("tag_3", info.getTags().get(2));
                    else
                        line.put("tag_3", "");
                }
                searchData.add(line);
                friends.add(info);
            }
            have_more = (tmp.getResults().size() > 0);
            return searchData;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            searchTask = null;
            progressBar.setVisibility(View.INVISIBLE);

            friend_list_adapter = new SimpleAdapter(SearchFriendsActivity.this,
                    data,
                    R.layout.friend_list_item,
                    new String[]{"name", "picture", "tag_1", "tag_2", "tag_3"},
                    new int[]{R.id.name, R.id.headImage, R.id.tagTab1, R.id.tagTab2, R.id.tagTab3});

            friend_list_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
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
            friendListView.setAdapter(friend_list_adapter);

            friend_list_adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            searchTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}