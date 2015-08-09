package com.stpi.campus.activity.personalService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stpi.campus.entity.cart.CartInfo;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.SubmitCartTask;
import com.stpi.campus.R;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.entity.cart.CartInfoHelper;
import com.stpi.campus.entity.item.PreviewItemInfo;
import com.stpi.campus.entity.item.PreviewItemInfoHelper;
import com.stpi.campus.util.HttpListGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 14-1-1.
 */
public class MyCartActivity extends Activity {

    private ListView cartListView = null;
    private Button submitBtn = null;
    private ProgressBar progressBar = null;
    private List<CartInfo> infos = null;
    private List<Map<String, Integer>> submitInfos = null;

    private List<Map<String, Object>> submitData;

    private SubmitCartTask submitCartTask = null;

    private String picture_head = "";

    // in shop deal
    private String shopId = "";
    private String shopName = "";
    private boolean inShop = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        picture_head = this.getString(R.string.item_picture_head);

        cartListView = (ListView) this.findViewById(R.id.cartListView);
        submitBtn = (Button) this.findViewById(R.id.submitButton);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        progressBar.setVisibility(View.VISIBLE);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubmit();
            }
        });

        inShop = this.getIntent().getBooleanExtra("in_shop", false);
        shopId = this.getIntent().getStringExtra("shop_id");
        shopName = this.getIntent().getStringExtra("shop_name");

        new LoadCartTask().execute(UserInfo.userId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "返回").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    void attemptSubmit() {
        String userId = UserInfo.userId;

        submitInfos = new ArrayList<Map<String, Integer>>();
        for (CartInfo cart_item : infos) {
            Map<String, Integer> subLine = new HashMap<String, Integer>();
            subLine.put("itemId", cart_item.getItemId());
            subLine.put("number", cart_item.getNumber());
            submitInfos.add(subLine);
        }
        Gson gson = new Gson();
        String subJson = gson.toJson(submitInfos);

        submitCartTask = new SubmitCartTask(MyCartActivity.this, progressBar);
        submitCartTask.execute(userId, subJson);
        progressBar.setVisibility(View.VISIBLE);
    }

    class LoadCartTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

            if (!inShop) {
                String url = MyCartActivity.this.getString(R.string.fetch_cart_list_head);
                url += "?userId=" + params[0];

                CartInfoHelper tmp = new HttpListGetter<CartInfoHelper>()
                        .getFromUrl(url, CartInfoHelper.class);

                if (tmp == null || tmp.getState().equals("fail")) {
                    return res;
                }

                System.out.println("aa - " + tmp.getResults().size());
                infos = tmp.getResults();
                System.out.println("result get = " + infos.size());
            } else {
                String url = MyCartActivity.this.getString(R.string.itemlist_merchant_head);
                url += "?merchantId=" + shopId;
                PreviewItemInfoHelper tmp = new HttpListGetter<PreviewItemInfoHelper>().getFromUrl(url, PreviewItemInfoHelper.class);
                if (tmp == null || tmp.getState().equals("fail")) {
                    return res;
                }
                List<PreviewItemInfo> tmp_infos = tmp.getResults();
                infos = new ArrayList<CartInfo>();
                for (PreviewItemInfo item : tmp_infos) {
                    CartInfo cart_item = new CartInfo();
                    cart_item.setItemId(item.getItemId());
                    cart_item.setItemName(item.getItemName());
                    cart_item.setNumber(0);
                    cart_item.setPicture(item.getPicture());
                    cart_item.setPrice(item.getPrice());
                    cart_item.setRate(item.getRate());
                    cart_item.setTags(item.getTags());
                    infos.add(cart_item);
                }
            }

            for (CartInfo cart_item : infos) {
                Map<String, Object> line = new HashMap<String, Object>();

                if (cart_item.getItemName() != null)
                    line.put("name", cart_item.getItemName());
                else
                    line.put("name", "");
                if (cart_item.getPicture() != null && cart_item.getPicture().length() > 0)
                    line.put("picture", picture_head + cart_item.getPicture());
                else
                    line.put("picture", picture_head + "000000.jpg");
                line.put("averageCost", "人均" + String.valueOf(cart_item.getPrice()) + "元");
                line.put("number", cart_item.getNumber());
                line.put("tag1", "");
                line.put("tag2", "");
                line.put("tag3", "");
                if (cart_item.getTags() != null) {
                    if (cart_item.getTags().size() >= 1)
                        line.put("tag1", cart_item.getTags().get(0));
                    if (cart_item.getTags().size() >= 2)
                        line.put("tag2", cart_item.getTags().get(1));
                    if (cart_item.getTags().size() >= 3)
                        line.put("tag3", cart_item.getTags().get(2));
                }
                res.add(line);
            }


            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {

            submitData = data;

            MyCartAdapter friend_list_adapter = new MyCartAdapter(
                    MyCartActivity.this
            );

            cartListView.setAdapter(friend_list_adapter);
            cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MyCartActivity.this,
                            ItemDetailActivity.class);
                    intent.putExtra("item_id", String.valueOf(infos.get(i).getItemId()));
                    startActivity(intent);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public final class CartViewHolder {
        public ImageView img;
        public TextView name;
        public TextView tag1;
        public TextView tag2;
        public TextView tag3;
        public TextView consume;
        public TextView numbers;
        public Button add;
        public Button minus;
        public int tot;
    }

    public class MyCartAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyCartAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return submitData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            CartViewHolder holder = null;
            if (convertView == null) {

                holder = new CartViewHolder();

                convertView = mInflater.inflate(R.layout.cart_list_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.itemPicture);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.tag1 = (TextView) convertView.findViewById(R.id.textTab1);
                holder.tag2 = (TextView) convertView.findViewById(R.id.textTab2);
                holder.tag3 = (TextView) convertView.findViewById(R.id.textTab3);
                holder.consume = (TextView) convertView.findViewById(R.id.consume);

                holder.add = (Button) convertView.findViewById(R.id.add_button);
                holder.minus = (Button) convertView.findViewById(R.id.minus_button);
                holder.numbers = (TextView) convertView.findViewById(R.id.number_of_item);
                holder.tot = 0;
                final CartViewHolder finalHolder = holder;
                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalHolder.tot++;
                        finalHolder.numbers.setText(String.valueOf(finalHolder.tot));
                        infos.get(position).setNumber(finalHolder.tot);
                    }
                });
                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (finalHolder.tot <= 0)
                            return;
                        finalHolder.tot--;
                        finalHolder.numbers.setText(String.valueOf(finalHolder.tot));
                        infos.get(position).setNumber(finalHolder.tot);
                    }
                });

                convertView.setTag(holder);
            } else {

                holder = (CartViewHolder) convertView.getTag();
            }

            String imageUri = (String) submitData.get(position).get("picture");
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
            imageLoader.displayImage(imageUri, holder.img, options);
            holder.name.setText((String) submitData.get(position).get("name"));
            holder.consume.setText((String) submitData.get(position).get("averageCost"));

            holder.tot = (Integer) submitData.get(position).get("number");
            holder.numbers.setText(String.valueOf(holder.tot));

            holder.tag1.setText((String) submitData.get(position).get("tag1"));
            if (holder.tag1.getText().toString().trim().length() <= 0)
                holder.tag1.setVisibility(View.INVISIBLE);
            holder.tag2.setText((String) submitData.get(position).get("tag2"));
            if (holder.tag2.getText().toString().trim().length() <= 0)
                holder.tag2.setVisibility(View.INVISIBLE);
            holder.tag3.setText((String) submitData.get(position).get("tag3"));
            if (holder.tag3.getText().toString().trim().length() <= 0)
                holder.tag3.setVisibility(View.INVISIBLE);

            return convertView;
        }
    }

}