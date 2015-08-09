package com.stpi.campus.activity.merchant;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.activity.personalService.MyCartActivity;
import com.stpi.campus.entity.merchant.DetailMerchantInfo;
import com.stpi.campus.entity.tag.TagInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.ShareTask;
import com.stpi.campus.task.SubscribeTask;
import com.stpi.campus.R;
import com.stpi.campus.entity.item.PreviewItemInfo;
import com.stpi.campus.entity.item.PreviewItemInfoHelper;
import com.stpi.campus.entity.merchant.DetailMerchantInfoHelper;
import com.stpi.campus.task.AddCommentTask;
import com.stpi.campus.task.CollectTask;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantDetailActivity extends Activity {

    final Context context = this;
    // temp
    private String shop_id = "1";
    private String type_id = "1";
    private ListView itemListView = null;
    private TextView nameView = null;
    private TextView addressView = null;
    private TextView telView = null;
    private TextView averageView = null;
    private RatingBar ratingBar = null;
    private TextView tagView_1 = null;
    private TextView tagView_2 = null;
    private TextView tagView_3 = null;
    private ImageView pictureView = null;
    private EditText tagText = null;
    private List<PreviewItemInfo> infos = new ArrayList<PreviewItemInfo>();
    private List<String> tagsInfos = new ArrayList<String>();
    private ShareTask shareTask = null;
    private EditText shareCommentText = null;

    private TextView recommendText = null;
    private AddCommentTask addCommentTask = null;
    private EditText addCommentText = null;
    private RatingBar commentRating1 = null;
    private RatingBar commentRating2 = null;
    private RatingBar commentRating3 = null;
    private EditText consumeText = null;
    private MenuItem back;
    private MenuItem setting;
    private MenuItem queuing;
    //    private Button dealButton;
    private int subscribe_type = 0;
    private int receive_type = 0;

    private CollectTask collectTask = null;

    private ProgressBar progressBar = null;

    private String picture_merchant_head = null;
    private String item_picture_head = null;
    private String merchant_picture = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_detail);

        // get context
        if (this.getIntent().getStringExtra("shop_id") != null)
            shop_id = this.getIntent().getStringExtra("shop_id");
        if (this.getIntent().getStringExtra("type_id") != null)
            type_id = this.getIntent().getStringExtra("type_id");

        picture_merchant_head = this.getString(R.string.shop_picture_head);
        item_picture_head = this.getString(R.string.item_picture_head);

        recommendText = (TextView) this.findViewById(R.id.recommend_list_text);
        itemListView = (ListView) this.findViewById(R.id.merchant_recommend_items);
        nameView = (TextView) this.findViewById(R.id.merchantName);
        addressView = (TextView) this.findViewById(R.id.merchantAddress);
        telView = (TextView) this.findViewById(R.id.merchantTelephone);
        averageView = (TextView) this.findViewById(R.id.merchantAverage);
        ratingBar = (RatingBar) this.findViewById(R.id.merchantRating);
        tagView_1 = (TextView) this.findViewById(R.id.merchantTab_1);
        tagView_2 = (TextView) this.findViewById(R.id.merchantTab_2);
        tagView_3 = (TextView) this.findViewById(R.id.merchantTab_3);
        pictureView = (ImageView) this.findViewById(R.id.merchantPicture);

        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        // execute
        if (shop_id.equals("0"))
            shop_id = "1";

        progressBar.setVisibility(View.VISIBLE);
        //new LoadMerchantTask().execute(shop_id, UserInfo.userId);

        Button commentButton = (Button) this.findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCommentDialog();
            }
        });

        Button shareButton = (Button) this.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showShareDialog();
            }
        });

        Button collectButton = (Button) this.findViewById(R.id.collectButton);
        collectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                attemptCollect();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadMerchantTask().execute(shop_id, UserInfo.userId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //showActionBar();
    }

    private void showActionBar() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void attemptCollect() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(MerchantDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MerchantDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        String userId = UserInfo.userId;
        String entityId = shop_id;
        String typeId = "1";

        collectTask = new CollectTask(MerchantDetailActivity.this, progressBar);
        collectTask.execute(userId, entityId, typeId);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showShareDialog() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(MerchantDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MerchantDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        LayoutInflater inflater = getLayoutInflater();
        View share_layout = inflater.inflate(R.layout.dialog_share,
                (ViewGroup) findViewById(R.id.share_dialog));

        shareCommentText = (EditText) share_layout.findViewById(R.id.shareComment);

        new AlertDialog.Builder(MerchantDetailActivity.this).setTitle("分享").setView(share_layout)
                .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String error = attemptShare();
                        if (!error.equals("success"))
                            Toast.makeText(MerchantDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private String attemptShare() {
        if (shareTask != null)
            return "系统繁忙";

        String userId = UserInfo.userId;
        String entityId = shop_id;
        String typeId = "1";
        String content = shareCommentText.getText().toString();
        String shareTo = "1";

        shareTask = new ShareTask(MerchantDetailActivity.this, progressBar);
        shareTask.execute(userId, entityId, typeId, content, shareTo);
        progressBar.setVisibility(View.VISIBLE);
        return "success";
    }

    private void showAddCommentDialog() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(MerchantDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MerchantDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View add_comment_layout = inflater.inflate(R.layout.dialog_add_comment,
                (ViewGroup) findViewById(R.id.add_comment_dialog));

        addCommentText = (EditText) add_comment_layout.findViewById(R.id.commentsWrite);
        tagText = (EditText) add_comment_layout.findViewById(R.id.tagsWrite);
        commentRating1 = (RatingBar) add_comment_layout.findViewById(R.id.ratingBar1);
        commentRating2 = (RatingBar) add_comment_layout.findViewById(R.id.ratingBar2);
        commentRating3 = (RatingBar) add_comment_layout.findViewById(R.id.ratingBar3);
        consumeText = (EditText) add_comment_layout.findViewById(R.id.consumeDetail);

        Button addTagButton = (Button) add_comment_layout.findViewById(R.id.addTagButton);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTagDialog();
            }
        });

        new AlertDialog.Builder(MerchantDetailActivity.this).setTitle("添加评论").setView(add_comment_layout)
                .setPositiveButton("评论", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String error = attemptAddComment();
                        if (!error.equals("success"))
                            onResume();
                        else
                            Toast.makeText(MerchantDetailActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    public void showTagDialog() {
        //定义复选框选项
        final String[] multiChoiceItems = tagsInfos.toArray(new String[tagsInfos.size()]);
//                = {"很好吃", "性价比高", "速度快", "服务好", "重口味", "有点辣", "餐馆妹纸多","口味较轻","烧烤","西餐","不太好吃"};
        //复选框默认值：false=未选;true=选中 ,各自对应items[i]
        final boolean[] defaultSelectedStatus = new boolean[tagsInfos.size()];
        final StringBuilder sb = new StringBuilder();
        new AlertDialog.Builder(context).setTitle("热门标签").setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
                //来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容
                defaultSelectedStatus[which] = isChecked;
            }
        })  //设置对话框[肯定]按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < defaultSelectedStatus.length; i++) {
                            if (defaultSelectedStatus[i]) {
//                                sb.append(multiChoiceItems[i]);
//                                tagText.setText(multiChoiceItems[i]);
//                                tagText.setText(strText.toCharArray(), 0, strText.length()-1);
                                if (tagText.getText().length() > 0 && tagText.getText().charAt(tagText.getText().length() - 1) != ';')
                                    tagText.setText(tagText.getText() + ";");
                                tagText.setText(tagText.getText() + multiChoiceItems[i] + ";");
                            }
                        }
// TODO Auto-generated method stub
//                        Toast.makeText(context,sb.toString(), Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private String attemptAddComment() {
        if (addCommentTask != null)
            return "系统繁忙";

        boolean cancel = false;

        String entityId = shop_id;
        String userId = UserInfo.userId;
        String content = addCommentText.getText().toString();
        String tags = tagText.getText().toString();
        String consume = consumeText.getText().toString();
        String rate1 = String.valueOf(commentRating1.getRating());
        String rate2 = String.valueOf(commentRating2.getRating());
        String rate3 = String.valueOf(commentRating3.getRating());
        String entityType = "1";
        if (content.length() <= 0) {
            return "请输入评论";
        }
        if (consume.length() <= 0) {
            return "请输入人均消费";
        }
        if (tags.length() <= 0) {
            return "请打标签";
        }

        addCommentTask = new AddCommentTask(MerchantDetailActivity.this, progressBar);
        addCommentTask.execute(entityId, userId, content, rate1, rate2, rate3, entityType, tags, consume);
        progressBar.setVisibility(View.VISIBLE);
        return "success";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        back = menu.add(0, 1, 0, "返回");
        back.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        setting = menu.add(0, 2, 0, "设置");
        menu.add(0, 3, 0, "去点菜");
//        setting.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 4, 0, "联系商家");
        if (type_id.equals("1")) {
            queuing = menu.add(0, 5, 0, "排队");
//            queuing.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.finish();
                break;
            case 2:
                goSetting();
                break;
            case 3:
                goDiancai();
                break;
            case 4:
                goPhoneCall();
                break;
            case 5:
                goQueuing();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goDiancai() {
        if (infos.isEmpty()) {
            Toast.makeText(MerchantDetailActivity.this, "尚无菜单", Toast.LENGTH_SHORT).show();
            return;
        } else if (UserInfo.userId.equals("0")) {
            Toast.makeText(MerchantDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MerchantDetailActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            return;
        }
        Intent openDealIntent = new Intent(MerchantDetailActivity.this,
                MyCartActivity.class);
        openDealIntent.putExtra("in_shop", true);
        openDealIntent.putExtra("shop_id", shop_id);
        openDealIntent.putExtra("shop_name", nameView.getText());
        startActivity(openDealIntent);
    }

    private void goPhoneCall() {
        String telNum = telView.getText().toString();
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNum));
        startActivity(intent);
    }

    private void goSetting() {
        LayoutInflater inflater = getLayoutInflater();
        View settingLayout = inflater.inflate(R.layout.dialog_subscribe_setting,
                (ViewGroup) findViewById(R.id.subscribe_setting_dialog));
        final Switch subscribe_switch = (Switch) settingLayout.findViewById(R.id.subscribeSwitch);
        final Switch receive_switch = (Switch) settingLayout.findViewById(R.id.receiveSwitch);
        boolean isChecked = (subscribe_type == 1) ? true : false;
        subscribe_switch.setChecked(isChecked);
        isChecked = (receive_type == 1) ? true : false;
        receive_switch.setChecked(isChecked);
        new AlertDialog.Builder(this).setTitle("订阅设置").setView(settingLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String subscribe_check = subscribe_switch.isChecked() ? "1" : "2";
                        String receive_check = receive_switch.isChecked() ? "1" : "2";
                        subscribe_type = Integer.valueOf(subscribe_check);
                        receive_type = Integer.valueOf(receive_check);
                        new SubscribeTask(MerchantDetailActivity.this, progressBar).execute(UserInfo.userId, shop_id, subscribe_check);
                    }
                })
                .setNegativeButton("取消", null).show();
//        subscribeDialog.show();
    }

    private void goQueuing() {
        Intent openQueueIntent = new Intent(MerchantDetailActivity.this,
                MerchantQueueActivity.class);
        openQueueIntent.putExtra("shop_id", shop_id);
        openQueueIntent.putExtra("shop_name", nameView.getText());
        openQueueIntent.putExtra("shop_picture", merchant_picture);
        startActivity(openQueueIntent);
    }

    // 乱七八糟的死有泪
    class LoadMerchantTask extends AsyncTask<String, Integer, List<Map<String, Object>>> {

        String address = "";
        String name = "";
        String tel = "";
        int consume = 0;
        float value = 0;
        String tag_1 = "";
        String tag_2 = "";
        String tag_3 = "";
        int typeId = 0;

        @Override
        protected List<Map<String, Object>> doInBackground(String... arg0) {
            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = MerchantDetailActivity.this.getString(R.string.detail_merchant_head);
            url += "?merchantId=" + arg0[0];
            url += "&userId=" + arg0[1];
            DetailMerchantInfoHelper tmp_0 = new HttpListGetter<DetailMerchantInfoHelper>().getFromUrl(url, DetailMerchantInfoHelper.class);
            if (tmp_0 == null || tmp_0.getState().equals("fail"))
                return res;

            DetailMerchantInfo helper = tmp_0.getResult();
            if (helper.getAddress() != null) {
                if (helper.getAddress().length() < 20)
                    address = helper.getAddress();
                else
                    address = helper.getAddress().substring(0, 17) + "...";
            }
            if (helper.getMerchantName() != null)
                name = helper.getMerchantName();
            if (helper.getTelNumber() != null)
                tel = helper.getTelNumber();
            if (helper.getPicture() != null && helper.getPicture().length() > 0)
                merchant_picture = picture_merchant_head + helper.getPicture();
            else
                merchant_picture = picture_merchant_head + "000000.jpg";
            typeId = helper.getTypeId();
            consume = helper.getAverageConsume();
            value = helper.getAverageValue();
            subscribe_type = helper.getType();
            if (helper.getTagName() != null) {
                if (helper.getTagName().size() > 0)
                    tag_1 = helper.getTagName().get(0);
                if (helper.getTagName().size() > 1)
                    tag_2 = helper.getTagName().get(1);
                if (helper.getTagName().size() > 2)
                    tag_3 = helper.getTagName().get(2);
            }

            url = MerchantDetailActivity.this.getString(R.string.itemlist_merchant_head);
            url += "?merchantId=" + arg0[0];
            PreviewItemInfoHelper tmp = new HttpListGetter<PreviewItemInfoHelper>().getFromUrl(url, PreviewItemInfoHelper.class);
            if (tmp == null || tmp.getState().equals("fail")) {
                return res;
            }

            infos = tmp.getResults();
            for (PreviewItemInfo item : infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("name", item.getItemName());
                line.put("price", String.valueOf(item.getPrice()) + "元");
                line.put("rate", Float.valueOf(item.getRate()));
                if (item.getPicture() != null && item.getPicture().length() > 0)
                    line.put("picture", item_picture_head + item.getPicture());
                else
                    line.put("picture", item_picture_head + "000000.jpg");
                if (item.getTags() == null) {
                    line.put("tag1", "");
                    line.put("tag2", "");
                    line.put("tag3", "");
                } else {
                    if (item.getTags().size() > 0)
                        line.put("tag1", item.getTags().get(0));
                    else
                        line.put("tag1", "");
                    if (item.getTags().size() > 1)
                        line.put("tag2", item.getTags().get(1));
                    else
                        line.put("tag2", "");
                    if (item.getTags().size() > 2)
                        line.put("tag3", item.getTags().get(2));
                    else
                        line.put("tag3", "");
                }
                res.add(line);
            }

            url = MerchantDetailActivity.this.getString(R.string.tags_comment_head);
            url += "?uid=" + UserInfo.userId;
            url += "&eid=" + arg0[0];
            url += "&eType=" + "1";

            TagInfoHelper tags = new HttpListGetter<TagInfoHelper>().getFromUrl(url, TagInfoHelper.class);
            if (tags != null && !tags.getState().equals("fail"))
                tagsInfos = tags.getRecResult();

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            if (typeId != 1) {
                recommendText.setVisibility(View.INVISIBLE);
                itemListView.setVisibility(View.INVISIBLE);
//                dealButton.setVisibility(View.INVISIBLE);
            }
            nameView.setText(name);
            addressView.setText("地址：" + address);
            telView.setText("电话：" + tel);
            averageView.setText("人均：" + consume + "元");
            ratingBar.setRating(value);
//            if(subscribe_type != 0)
//            {
//                setting.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//                queuing.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//            }

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(merchant_picture, pictureView);

            if (tag_1.equals(""))
                tagView_1.setVisibility(View.INVISIBLE);
            else
                tagView_1.setText(tag_1);
            if (tag_2.equals(""))
                tagView_2.setVisibility(View.INVISIBLE);
            else
                tagView_2.setText(tag_2);
            if (tag_3.equals(""))
                tagView_3.setVisibility(View.INVISIBLE);
            else
                tagView_3.setText(tag_3);

            if (data == null)
                return;
            SimpleAdapter adapter = new SimpleAdapter(
                    MerchantDetailActivity.this, data,
                    R.layout.item_list_item, new String[]{"name",
                    "price", "rate", "picture", "tag1", "tag2", "tag3"},
                    new int[]{R.id.merchant_name, R.id.item_price,
                            R.id.ratingBar1, R.id.merchant_image,
                            R.id.merchantTab_2, R.id.merchantTab_3, R.id.merchantTab_4}
            );
            adapter.setViewBinder(new ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof RatingBar) && (data instanceof Float)) {
                        RatingBar rb = (RatingBar) view;
                        Float r = (Float) data;
                        rb.setRating(r);
                        return true;
                    } else if ((view instanceof ImageView) && (data instanceof String)) {
                        ImageView imageView = (ImageView) view;
                        String imageUri = (String) data;
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
                        imageLoader.displayImage(imageUri, imageView, options);
                        return true;
                    }
//                    else if ((view instanceof TextView) && (data instanceof String)) {
//                        String s = (String) data;
//                        if (s.length() <= 0) {
//                            view.setVisibility(
//                                    View.INVISIBLE);
//                            return true;
//                        }
//                    }
                    return false;
                }
            });
            itemListView.setAdapter(adapter);
            itemListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            ItemDetailActivity.class);
                    intent.putExtra("item_id", String.valueOf(infos.get(arg2).getItemId()));
//                    intent.putExtra("item_picture", item_picture_head + infos.get(arg2).getPicture());
                    startActivity(intent);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

}
