package com.stpi.campus.activity.item;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.activity.personSetting.LoginActivity;
import com.stpi.campus.entity.comment.CommentInfo;
import com.stpi.campus.entity.comment.CommentInfoHelper;
import com.stpi.campus.entity.tag.TagInfoHelper;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.IntoCartTask;
import com.stpi.campus.task.ShareTask;
import com.stpi.campus.R;
import com.stpi.campus.entity.item.DetailItemInfoHelper;
import com.stpi.campus.entity.item.PreviewItemInfo;
import com.stpi.campus.task.AddCommentTask;
import com.stpi.campus.task.CollectTask;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDetailActivity extends Activity {

    final Context context = this;
    private String item_id = "2";
    private ListView commentList = null;
    private EditText tagText = null;
    private ShareTask shareTask = null;
    private EditText commentText = null;

    private AddCommentTask addCommentTask = null;
    private EditText addCommentText = null;
    private RatingBar commentRating1 = null;
    private RatingBar commentRating2 = null;
    private RatingBar commentRating3 = null;

    private CollectTask collectTask = null;
    private IntoCartTask intoCartTask = null;

    private List<CommentInfo> infos = new ArrayList<CommentInfo>();
    private List<String> tagsInfos = new ArrayList<String>();

    private TextView nameView = null;
    private ImageView itemPicture = null;
    private TextView priceView = null;
    private RatingBar ratingView = null;
    private TextView tagView_1 = null;
    private TextView tagView_2 = null;
    private TextView tagView_3 = null;
    private TextView commentNumView = null;
    private EditText consumeText = null;
    private Button fetchIntoCart = null;

    private ProgressBar progressBar = null;
    private String picture_head = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // get context
        if (this.getIntent().getStringExtra("item_id") != null)
            item_id = this.getIntent().getStringExtra("item_id");
        picture_head = this.getString(R.string.item_picture_head);

        nameView = (TextView) this.findViewById(R.id.textView2);
        priceView = (TextView) this.findViewById(R.id.textView1);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);
        ratingView = (RatingBar) this.findViewById(R.id.ratingBar1);
        tagView_1 = (TextView) this.findViewById(R.id.textTab1);
        tagView_2 = (TextView) this.findViewById(R.id.textTab2);
        tagView_3 = (TextView) this.findViewById(R.id.textTab3);
//        fetchIntoCart = (Button) this.findViewById(R.id.fetchIntoCart);
        commentNumView = (TextView) this.findViewById(R.id.textView3);
        commentList = (ListView) this.findViewById(R.id.comments_list);

//        if(UserInfo.userId.equals("0"))
//            fetchIntoCart.setVisibility(View.INVISIBLE);

//        fetchIntoCart.setOnClickListener(new OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                showIntoCartDialog();
//            }});

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

        progressBar.setVisibility(View.VISIBLE);
//        new LoadTask().execute(item_id, "2");

        itemPicture = (ImageView) this.findViewById(R.id.itemPicture);
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadTask().execute(item_id, "2");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.finish();
                break;
            case 2:
                showIntoCartDialog();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "返回").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 0, "加入购物车").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    private void showIntoCartDialog() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(ItemDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_into_cart,
                (ViewGroup) findViewById(R.id.into_cart_dialog));

        final TextView numView = (TextView) layout.findViewById(R.id.number_of_item);
        Button minusButton = (Button) layout.findViewById(R.id.minus_button);
        Button addButton = (Button) layout.findViewById(R.id.add_button);
        minusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf((String) numView.getText());
                if (num > 0)
                    num--;
                numView.setText(String.valueOf(num));
            }
        });
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf((String) numView.getText());
                num++;
                numView.setText(String.valueOf(num));
            }
        });

        new AlertDialog.Builder(ItemDetailActivity.this).setTitle("添加购物车").setView(layout)
                .setPositiveButton("加入购物车", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attemptIntoCart(String.valueOf(numView.getText()));
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void attemptIntoCart(String num) {

        String userId = UserInfo.userId;
        String entityId = item_id;

        intoCartTask = new IntoCartTask(ItemDetailActivity.this, progressBar);
        intoCartTask.execute(userId, entityId, num);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void attemptCollect() {

        if (UserInfo.userId.equals("0")) {
            Toast.makeText(ItemDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        String userId = UserInfo.userId;
        String entityId = item_id;
        String typeId = "2";

        collectTask = new CollectTask(ItemDetailActivity.this, progressBar);
        collectTask.execute(userId, entityId, typeId);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showShareDialog() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(ItemDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_share,
                (ViewGroup) findViewById(R.id.share_dialog));

        commentText = (EditText) layout.findViewById(R.id.shareComment);


        new AlertDialog.Builder(ItemDetailActivity.this).setTitle("分享").setView(layout)
                .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String error = attemptShare();
                        if (!error.equals("success"))
                            Toast.makeText(ItemDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private String attemptShare() {
        if (shareTask != null)
            return "系统繁忙";

        String userId = UserInfo.userId;
        String entityId = item_id;
        String typeId = "2";
        String content = commentText.getText().toString();
        String shareTo = "2";

        shareTask = new ShareTask(ItemDetailActivity.this, progressBar);
        shareTask.execute(userId, entityId, typeId, content, shareTo);
        progressBar.setVisibility(View.VISIBLE);
        return "success";
    }

    private void showAddCommentDialog() {
        if (UserInfo.userId.equals("0")) {
            Toast.makeText(ItemDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
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

        new AlertDialog.Builder(ItemDetailActivity.this).setTitle("添加评论").setView(add_comment_layout)
                .setPositiveButton("评论", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String error = attemptAddComment();
                        if (error.equals("success"))
                            onResume();
                        else
                            Toast.makeText(ItemDetailActivity.this, error, Toast.LENGTH_LONG).show();
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

        String entityId = item_id;
        String userId = UserInfo.userId;
        String content = addCommentText.getText().toString();
        String rate1 = String.valueOf(commentRating1.getRating());
        String rate2 = String.valueOf(commentRating2.getRating());
        String rate3 = String.valueOf(commentRating3.getRating());
        String entityType = "2";
        String tags = tagText.getText().toString();
        String consume = consumeText.getText().toString();

        if (content.length() <= 0) {
            return "请输入评论";
        }
        if (consume.length() <= 0) {
            return "请输入人均消费";
        }
        if (tags.length() <= 0) {
            return "请打标签";
        }

        addCommentTask = new AddCommentTask(ItemDetailActivity.this, progressBar);
        addCommentTask.execute(entityId, userId, content, rate1, rate2, rate3, entityType, tags, consume);
        progressBar.setVisibility(View.VISIBLE);
        return "success";
    }

    class LoadTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

        String name = "";
        String picture = "";
        int commentNum = 0;
        float price = 0;
        float rate = 0;
        String image = "";
        String tag_1 = "";
        String tag_2 = "";
        String tag_3 = "";

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            String url = ItemDetailActivity.this.getString(R.string.detail_item_head);
            url += "?itemId=" + params[0];

            DetailItemInfoHelper tmp_0 = new HttpListGetter<DetailItemInfoHelper>().getFromUrl(url, DetailItemInfoHelper.class);
            if (tmp_0 == null || tmp_0.getState().equals("fail")) {
                return res;
            }

            PreviewItemInfo helper = tmp_0.getResult();

            if (helper.getItemName() != null)
                name = helper.getItemName();
            if (helper.getPicture() != null && helper.getPicture().length() > 0)
                picture = helper.getPicture();
            else
                picture = "000000.jpg";
            price = helper.getPrice();
            rate = helper.getRate();
            if (helper.getPicture() != null)
                image = helper.getPicture();
            if (helper.getTags() != null) {
                if (helper.getTags().size() > 0)
                    tag_1 = helper.getTags().get(0);
                if (helper.getTags().size() > 1)
                    tag_2 = helper.getTags().get(1);
                if (helper.getTags().size() > 2)
                    tag_3 = helper.getTags().get(2);
            }

            url = ItemDetailActivity.this.getString(R.string.tags_comment_head);
            url += "?uid=" + UserInfo.userId;
            url += "&eid=" + params[0];
            url += "&eType=" + params[1];

            TagInfoHelper tags = new HttpListGetter<TagInfoHelper>().getFromUrl(url, TagInfoHelper.class);
            if (tags == null || tags.getState().equals("fail")) {
                ;
                //return res;
            } else
                tagsInfos = tags.getRecResult();

            url = ItemDetailActivity.this
                    .getString(R.string.commentlist_head);
            url += "?entityId=" + params[0];
            url += "&entityType=" + params[1];

            CommentInfoHelper tmp = new HttpListGetter<CommentInfoHelper>()
                    .getFromUrl(url, CommentInfoHelper.class);

            if (tmp == null || tmp.getState().equals("fail")) {
                return res;
            }

            System.out.println("aa - " + tmp.getResults().size());
            infos = tmp.getResults();
            System.out.println("result get = " + infos.size());
            commentNum = infos.size();

            for (CommentInfo comment : infos) {
                Map<String, Object> line = new HashMap<String, Object>();
                if (comment.getUserName() != null)
                    line.put("name", comment.getUserName());
                else
                    line.put("name", "");
                line.put("averageCost", "人均" + String.valueOf(comment.getConsume()) + "元");
                line.put("averageRate", Float.valueOf(comment.getRate1()));
                if (comment.getContent() != null) {
                    line.put("comment", comment.getContent());
                } else {
                    line.put("comment", "");
                }
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            String imageUri = picture_head + picture;
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
            imageLoader.displayImage(imageUri, itemPicture, options);

            nameView.setText(name);
            priceView.setText("人均" + String.valueOf(price) + "元");
            commentNumView.setText("点评（" + String.valueOf(infos.size()) + "）");
            ratingView.setRating(rate);
            progressBar.setVisibility(View.INVISIBLE);
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

            SimpleAdapter comment_adapter = new SimpleAdapter(ItemDetailActivity.this, data, R.layout.comment_list_item, new String[]{"name",
                    "averageRate",
                    "comment", "averageCost"}, new int[]{
                    R.id.name,
                    R.id.ratingBar,
                    R.id.comment, R.id.averageCost});

            comment_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof RatingBar) && (data instanceof Float)) {
                        RatingBar rb = (RatingBar) view;
                        Float r = (Float) data;
                        rb.setRating(r);
                        return true;
                    }
                    return false;
                }
            });

            commentList.setAdapter(comment_adapter);
        }
    }

}
