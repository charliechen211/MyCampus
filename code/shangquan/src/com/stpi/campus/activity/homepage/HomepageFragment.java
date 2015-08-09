package com.stpi.campus.activity.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.activity.campusNearby.ItemDetailActivity;
import com.stpi.campus.activity.lvBridge.lvCommentActivity;
import com.stpi.campus.activity.shMarket.shCommentActivity;
import com.stpi.campus.entity.collection.CollectInfo;
import com.stpi.campus.entity.collection.CollectInfoHelper;
import com.stpi.campus.entity.homepage.Subscription;
import com.stpi.campus.entity.homepage.SubscriptionHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.activity.partTimeJob.PartTimeJobDetailActivity;
import com.stpi.campus.util.HttpListGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyl on 2014/10/15.
 */
public class HomepageFragment extends RefreshableFragment {
    private View view = null;
    private ImageView headPic = null;
    private TextView followNum = null;
    private TextView followedNum = null;
    private TextView userName = null;
    private TextView campusName = null;
    private ProgressBar progressBar = null;
    private TabHost tabHost = null;
    private ListView recommendList = null;
    private ListView collectionList = null;
    private ListView subscribeList = null;

    private List<Map<String, String>> recommendViewDatas = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> collectionViewDatas = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> subscriptionViewDatas = new ArrayList<Map<String, String>>();

    private List<CollectInfo> collectionDatas = new ArrayList<CollectInfo>();
    private List<Subscription> subscriptionDatas = new ArrayList<Subscription>();
    private List<CollectInfo> recommendDatas = new ArrayList<CollectInfo>();

    private SimpleAdapter recommendAdaptor;
    private SimpleAdapter collectionAdaptor;
    private SimpleAdapter subscribeAdaptor;

    private Integer follows;
    private Integer followeds;

    private Map<Integer,Class> moduleMap = new HashMap<Integer, Class>();

    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_homepage_fragment, container, false);
        setHasOptionsMenu(true);
        headPic = (ImageView) view.findViewById(R.id.headImage);
        userName = (TextView) view.findViewById(R.id.userName);
        followedNum = (TextView) view.findViewById(R.id.followedNum);
        followNum = (TextView) view.findViewById(R.id.followNum);
        campusName = (TextView) view.findViewById(R.id.campusName);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        recommendList = (ListView) view.findViewById(R.id.recommend_list);
        collectionList = (ListView) view.findViewById(R.id.collection_list);
    //    subscribeList = (ListView) view.findViewById(R.id.subscribe_list);

        reloadUserInfo();
        tabHost.setup();

        tabHost.addTab(
                tabHost.newTabSpec("tab1").setIndicator(createTabWidget("今日推荐")).setContent(R.id
                        .recommend_list)
        );
        tabHost.addTab(
                tabHost.newTabSpec("tab2").setIndicator(createTabWidget("我的收藏")).setContent(R.id
                        .collection_list)
        );
/*        tabHost.addTab(
                tabHost.newTabSpec("tab3").setIndicator(createTabWidget("我的订阅")).setContent(R.id
                        .subscribe_list)
        );*/


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
                tabChangedEvent(s);
            }
        });
        listViewInit();
        initModuleMap();
        eventInit();
        return view;
    }

    private void reloadUserInfo() {
        userName.setText(UserInfo.userName);
        followNum.setText(String.valueOf(UserInfo.followNum));
        followedNum.setText(String.valueOf(UserInfo.fanNum));
        campusName.setText(UserInfo.campusName);
        loadUserPic(headPic);
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadUserInfo();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    @Override
    public void refresh() {
        initActionBar();
        reloadUserInfo();
        collectionViewDatas.clear();
     //   subscriptionViewDatas.clear();
        recommendViewDatas.clear();
        collectionDatas.clear();
     //   subscriptionDatas.clear();
        recommendDatas.clear();
        tabChangedEvent(tabHost.getCurrentTabTag());
    }

    public void tabChangedEvent(String tab) {

        if(tab.equals("tab1")) {
            recommendViewDatas.clear();
            recommendDatas.clear();
            new LoadMyRecommendTask().execute();
        }
        else if (tab.equals("tab2")) {
            collectionViewDatas.clear();
            collectionDatas.clear();
            new LoadMyCollectionTask().execute();
        }
/*        else if (tab.equals("tab3")) {
            subscriptionViewDatas.clear();
            subscriptionDatas.clear();
            new LoadMySubscriptionTask().execute();
        }*/
    }

    public View createTabWidget(String title) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab_widget,null);
        TextView tabTitle = (TextView) view.findViewById(R.id.tabTitle);
        tabTitle.setText(title);
        tabTitle.setTextColor(getResources().getColor(R.color.not_choose_tab_fold));
        if(title.equals("今日推荐")) {
            view.setBackgroundResource(R.drawable.tab_selected_background);
            tabTitle.setTextColor(getResources().getColor(R.color.choose_tab_fold));
        }
        return view;
    }

    public void initActionBar() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_homepage));
    }

    public void listViewInit() {
        collectionAdaptor = new SimpleAdapter(getActivity(), collectionViewDatas, R.layout.collection_list_item,
                new String[]{"moduleName", "title", "content", "date"}, new int[]{R.id.moduleName, R.id.title, R.id.content, R.id.date});
        subscribeAdaptor = new SimpleAdapter(getActivity(), subscriptionViewDatas, R.layout.collection_list_item,
                new String[]{"typeName", "title", "content", "date"}, new int[]{R.id.moduleName, R.id.title, R.id.content, R.id.date});
        recommendAdaptor = new SimpleAdapter(getActivity(), recommendViewDatas,
                R.layout.collection_list_item,
                new String[]{"moduleName", "title", "content", "date"}, new int[]{R.id.moduleName,
                R.id.title, R.id.content, R.id.date});
        /**
         * ModuleName 以图片形式呈现
         */
        collectionAdaptor.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = getString(R.string.module_name_picture_head)+(String) data
                            +".png";
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });

/*        subscribeAdaptor.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    String type = (String) data;
                    ImageView imageView = (ImageView) view;
                    if(type.equals("3"))
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.campus_nearby_food));
                    if(type.equals("4"))
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable
                                .campus_nearby_entertain));
                    if(type.equals("5"))
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable
                                .campus_nearby_live));
                    return true;
                }
                return false;
            }
        });*/
        recommendAdaptor.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof String)) {
                    ImageView imageView = (ImageView) view;
                    String imageUri = getString(R.string.module_name_picture_head)+(String) data
                            +".png";
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                    imageLoader.displayImage(imageUri, imageView, options);
                    return true;
                }
                return false;
            }
        });


        collectionList.setAdapter(collectionAdaptor);
   //     subscribeList.setAdapter(subscribeAdaptor);
        recommendList.setAdapter(recommendAdaptor);
    }

    class LoadMyCollectionTask extends
            AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            String url = HomepageFragment.this.getString(R.string.homepage_collection_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("userId", String.valueOf(UserInfo.userId)));
            CollectInfoHelper collectInfoHelper = new HttpListGetter<CollectInfoHelper>()
                    .getFromUrl(url, CollectInfoHelper.class, param);

            if (collectInfoHelper == null || collectInfoHelper.getState().equals("fail")) {
                return 0;
            }

            for (CollectInfo info : collectInfoHelper.getResults()) {
                Map<String, String> line = new HashMap<String, String>();
                line.put("moduleName", String.valueOf(info.getModuleId()));
                line.put("title", info.getTitle());
                line.put("date", info.getDate());
                line.put("content", info.getContent());
                if(info.getItemLocation() !=null)
                    line.put("itemLocation",info.getItemLocation());
                if(info.getLatitude() != null)
                    line.put("latitude",String.valueOf(info.getLatitude()));
                else
                    line.put("latitude","-1");
                if(info.getLongitude() != null)
                    line.put("longitude",String.valueOf(info.getLongitude()));
                else
                    line.put("longitude","-1");
                if(info.getItemTel() != null)
                    line.put("itemTel",info.getItemTel());
                collectionViewDatas.add(line);
                collectionDatas.add(info);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer hasMore) {
            progressBar.setVisibility(View.INVISIBLE);
            collectionAdaptor.notifyDataSetChanged();
        }
    }

   /* class LoadMySubscriptionTask extends
            AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            String url = HomepageFragment.this.getString(R.string.homepage_subscribe_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("userId", String.valueOf(UserInfo.userId)));
            param.add(new BasicNameValuePair("pageId", String.valueOf("0")));
            param.add(new BasicNameValuePair("pageSize", String.valueOf("10")));
            SubscriptionHelper subscriptionHelper = new HttpListGetter<SubscriptionHelper>()
                    .getFromUrl(url, SubscriptionHelper.class, param);

            if (subscriptionHelper == null) {
                return 0;
            }
            if (subscriptionHelper.getState().equals("fail")) {
                return 0;
            }

            for (Subscription info : subscriptionHelper.getResult()) {
                Map<String, String> line = new HashMap<String, String>();
                line.put("typeName", String.valueOf(info.getTypeId()));
                line.put("title", info.getItemName());
                line.put("date", "");
                line.put("content", info.getItemDescription());
                subscriptionViewDatas.add(line);
                subscriptionDatas.add(info);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer hasMore) {
            progressBar.setVisibility(View.INVISIBLE);
            if (hasMore == 1)
                subscribeAdaptor.notifyDataSetChanged();
        }
    }
*/
    class LoadMyRecommendTask extends
            AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            String url = HomepageFragment.this.getString(R.string.homepage_recommend_head);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            CollectInfoHelper collectInfoHelper = new HttpListGetter<CollectInfoHelper>()
                    .getFromUrl(url, CollectInfoHelper.class, param);

            if (collectInfoHelper == null || collectInfoHelper.getState().equals("fail")) {
                return 0;
            }

            for (CollectInfo info : collectInfoHelper.getResults()) {
                Map<String, String> line = new HashMap<String, String>();
                line.put("moduleName", String.valueOf(info.getModuleId()));
                line.put("title", info.getTitle());
                line.put("date", info.getDate());
                line.put("content", info.getContent());
                if(info.getItemLocation() !=null)
                    line.put("itemLocation",info.getItemLocation());
                if(info.getLatitude() != null)
                    line.put("latitude",String.valueOf(info.getLatitude()));
                if(info.getLongitude() != null)
                    line.put("longitude",String.valueOf(info.getLongitude()));
                if(info.getItemTel() != null)
                    line.put("itemTel",info.getItemTel());
                recommendViewDatas.add(line);
                recommendDatas.add(info);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer hasMore) {
            progressBar.setVisibility(View.INVISIBLE);
            if (hasMore == 1)
                recommendAdaptor.notifyDataSetChanged();
        }
    }
    public void eventInit() {
        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                if(moduleMap.get(collectionDatas.get(j).getModuleId()) != null) {
                    Intent intent = new Intent(HomepageFragment.this.getActivity(),
                            moduleMap.get(collectionDatas.get(j).getModuleId()));
                    if(collectionDatas.get(j).getModuleId() == 1){
                        intent.putExtra("itemId",collectionDatas.get(j).getItemId());
                        intent.putExtra("itemName",collectionDatas.get(j).getTitle());
                        intent.putExtra("itemTel",collectionDatas.get(j).getItemTel());
                        intent.putExtra("itemLocation",collectionDatas.get(j).getItemLocation());
                        if(collectionDatas.get(j).getPicture() !=null && !collectionDatas.get(j)
                                .getPicture().equals(""))
                            intent.putExtra("itemPic",getString(R.string.school_picture_head)
                                +collectionDatas.get(j).getPicture());
                        else
                            intent.putExtra("itemPic",getString(R.string.school_picture_head)
                                +"000000.jpg");
                        intent.putExtra("latitude",collectionDatas.get(j).getLatitude()
                                ==null? "-1":String.valueOf(collectionDatas.get(j)
                                .getLatitude()));
                        intent.putExtra("longitude",collectionDatas.get(j).getLongitude()
                                ==null? "-1":String.valueOf(collectionDatas.get(j)
                                .getLongitude()));
                        startActivity(intent);
                    }else {
                        intent.putExtra("itemId",String.valueOf(collectionDatas.get(j).getItemId()));
                        intent.putExtra("title", collectionDatas.get(j).getTitle());
                        intent.putExtra("content", collectionDatas.get(j).getContent());
                        intent.putExtra("createTime", collectionDatas.get(j).getDate());
                        intent.putExtra("picture", collectionDatas.get(j).getPicture());
                        startActivity(intent);
                    }

                }
            }
        });
        collectionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) {
                showPostDialog(position);
                return true;
                }
            });
             recommendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                     if (moduleMap.get(recommendDatas.get(j).getModuleId()) != null) {
                         Intent intent = new Intent(HomepageFragment.this.getActivity(),
                                 moduleMap.get(recommendDatas.get(j).getModuleId()));
                         if(recommendDatas.get(j).getModuleId() == 1){
                             intent.putExtra("itemId",recommendDatas.get(j).getItemId());
                             intent.putExtra("itemName",recommendDatas.get(j).getTitle());
                             intent.putExtra("itemTel",recommendDatas.get(j).getItemTel());
                             intent.putExtra("itemLocation",recommendDatas.get(j).getItemLocation());
                             if(recommendDatas.get(j).getPicture()!= null && !recommendDatas.get
                                     (j).getPicture().equals(""))
                                intent.putExtra("itemPic",getString(R.string.shop_picture_head)
                                        +recommendDatas.get(j).getPicture());
                             else
                                 intent.putExtra("itemPic",getString(R.string.shop_picture_head)
                                         +"000000.jpg");
                             intent.putExtra("latitude",recommendDatas.get(j).getLatitude()
                                     ==null? "-1":String.valueOf(recommendDatas.get(j)
                                     .getLatitude()));
                             intent.putExtra("longitude",recommendDatas.get(j).getLongitude()
                                     ==null? "-1":String.valueOf(recommendDatas.get(j)
                                     .getLongitude()));
                             startActivity(intent);
                         }else {
                             intent.putExtra("itemId", String.valueOf(recommendDatas.get(j).getItemId()));
                             intent.putExtra("title", recommendDatas.get(j).getTitle());
                             intent.putExtra("content", recommendDatas.get(j).getContent());
                             intent.putExtra("createTime", recommendDatas.get(j).getDate());
                             intent.putExtra("picture", getString(R.string
                                     .shop_picture_head)+recommendDatas.get(j).getPicture());
                             startActivity(intent);
                         }
                     }
                 }
             });
     /*   subscribeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                Intent intent = new Intent(HomepageFragment.this.getActivity(),
                        ItemDetailActivity.class);
                intent.putExtra("itemId", String.valueOf(subscriptionDatas.get(j).getItemId()));
                intent.putExtra("itemName", subscriptionDatas.get(j).getItemName());
                intent.putExtra("itemPic", subscriptionDatas.get(j).getItemPic());
                intent.putExtra("itemTel",subscriptionDatas.get(j).getItemTel());
                intent.putExtra("itemLocation",subscriptionDatas.get(j).getItemLocation());
                startActivity(intent);
            }
        });
*/
            }

            public void loadUserPic(ImageView headPic) {
                String imageUri = getString(R.string.person_picture_head) + String.valueOf(UserInfo.userPic);
                ImageLoader imageLoader = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory().cacheOnDisc()
                        .preProcessor(new BitmapProcessor() {
                            @Override
                            public Bitmap process(Bitmap bitmap) {
                                return ImageFileUtils.getRoundCornerImage(bitmap, R.dimen.size40);
                            }
                        })
                        .build();
                imageLoader.displayImage(imageUri, headPic, options);
            }

            public void initModuleMap() {
                moduleMap.put(1, ItemDetailActivity.class);
                moduleMap.put(3, PartTimeJobDetailActivity.class);
                moduleMap.put(4, shCommentActivity.class);
                moduleMap.put(5, lvCommentActivity.class);
            }
        private void showPostDialog(final Integer position) {
            new AlertDialog.Builder(this.getActivity()).setTitle("确认删除该收藏条目？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delCollection(position);
                        tabChangedEvent(tabHost.getCurrentTabTag());
                    }
                })
                .setNegativeButton("取消", null).show();
        }
        private void delCollection(Integer position) {
            String mid = String.valueOf(collectionDatas.get(position).getModuleId());
            String iid = String.valueOf(collectionDatas.get(position).getItemId());
            new CollectionDelAsyTask(this.getActivity()).execute(UserInfo.userId,mid,iid);
        }




}
