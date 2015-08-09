package com.stpi.campus.activity.booking;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.stpi.campus.R;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.util.CartInfo;
import com.stpi.campus.util.DatabaseHelper;
import com.stpi.campus.util.ItemInfo;
import com.stpi.campus.util.ShopInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DianActivity extends Activity {

    List<ItemInfo> items = null;
    private String shop_id = null;
    private TextView dianNameView = null;
    private ListView itemListView = null;
    private Button dianDetailButton = null;
    private ShopInfo shop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("123");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dian);

        // get context
        System.out.println("123");
        shop_id = this.getIntent().getStringExtra("shop_id");
        dianNameView = (TextView) this.findViewById(R.id.dianDisplayName);
        itemListView = (ListView) this.findViewById(R.id.dianItemList);

        // add button actions
        System.out.println("123");
        dianDetailButton = (Button) this.findViewById(R.id.dianCartButton);
        dianDetailButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(DianActivity.this);
                CartInfo cart = db.getCartInfoByShopId(shop.shop_id);
                if (cart == null)
                    Toast.makeText(DianActivity.this, "��δ����",
                            Toast.LENGTH_SHORT).show();
                else {
                    Intent cartDetailIntent = new Intent(DianActivity.this,
                            CartDetailActivity.class);
                    cartDetailIntent.putExtra("cart_id", cart.cart_id);
                    startActivityForResult(cartDetailIntent, 0);
                }
            }
        });
        dianDetailButton.setEnabled(false);

        // execute
        System.out.println("123");
        Toast.makeText(this, "加载中..", Toast.LENGTH_SHORT).show();
        System.out.println("123");
        new LoadItemsTask().execute(shop_id);
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

    class LoadItemsTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            DatabaseHelper db = new DatabaseHelper(DianActivity.this);

            shop = db.getShopInfo(params[0]);
            items = db.getItems(shop);

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            for (ItemInfo item : items) {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("item_name", item.item_name);
                line.put("item_price", "单价" + item.item_price + "Ԫ");
                res.add(line);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            ListAdapter adapter = new SimpleAdapter(DianActivity.this, result,
                    R.layout.dian_list_item, new String[]{"item_name",
                    "item_price"}, new int[]{R.id.dianItemName,
                    R.id.dianItemPrice}
            );
            itemListView.setAdapter(adapter);
            itemListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Intent openItemIntent = new Intent(DianActivity.this,
                            ItemDetailActivity.class);
                    openItemIntent.putExtra("id", items.get(position).item_id);
                    DianActivity.this.startActivity(openItemIntent);

                }
            });
            dianDetailButton.setEnabled(true);
            Toast.makeText(DianActivity.this, "加载完成", Toast.LENGTH_SHORT)
                    .show();
        }

    }

}
