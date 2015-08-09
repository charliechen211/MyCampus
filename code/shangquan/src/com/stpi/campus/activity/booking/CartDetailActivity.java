package com.stpi.campus.activity.booking;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.stpi.campus.util.CartInfo;
import com.stpi.campus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartDetailActivity extends Activity {

    private final CartInfo cart = null;
    private String cart_id = null;
    private ListView itemListView = null;
    private TextView titleView = null;
    private TextView sumView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);

        // get context
        cart_id = this.getIntent().getStringExtra("cart_id");
        itemListView = (ListView) this.findViewById(R.id.cartDetailListView);
        titleView = (TextView) this.findViewById(R.id.cartDetailName);
        sumView = (TextView) this.findViewById(R.id.cartDetailSumUp);

        // set button actions
        Button okButton = (Button) this
                .findViewById(R.id.cartDetailConfirmButton);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CartDetailActivity.this, "下单成功",
                        Toast.LENGTH_SHORT).show();
                CartDetailActivity.this.finish();
            }
        });
        Button cancelButton = (Button) this
                .findViewById(R.id.cartDetailCancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CartDetailActivity.this.finish();
            }
        });

        // execute
        // Toast.makeText(this, "������..", Toast.LENGTH_SHORT).show();
        // new LoadCartTask().execute(cart_id);
        ListAdapter adapter = new SimpleAdapter(CartDetailActivity.this,
                getData(), R.layout.cart_detail_item, new String[]{},
                new int[]{});
        itemListView.setAdapter(adapter);
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

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 1; i++) {
            Map<String, Object> line = new HashMap<String, Object>();
            line.put("name", "德克士");
            res.add(line);
        }

        return res;
    }

}
