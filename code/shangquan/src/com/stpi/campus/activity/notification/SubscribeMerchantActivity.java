package com.stpi.campus.activity.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.stpi.campus.MainActivity;
import com.stpi.campus.R;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.CollectTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 13-12-3.
 */
public class SubscribeMerchantActivity extends Activity {

    private String item_id = "2";

    private ListView merchantQecodeList = null;
    private Button goMerchantButton = null;
    private Button collectButton = null;
    private Button moreDiscountButton = null;

    private CollectTask collectTask = null;

    private ProgressBar progressBar = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_merchant);

        progressBar = (ProgressBar) this.findViewById(R.id.progress);
        merchantQecodeList = (ListView) this.findViewById(R.id.merchantQecodeView);
        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.merchant_qecode_item, new String[]{}, new int[]{});
        merchantQecodeList.setAdapter(adapter);

        goMerchantButton = (Button) findViewById(R.id.goMerchantButton);
        goMerchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(SubscribeMerchantActivity.this,
                        MerchantDetailActivity.class);
                startActivity(intent);
            }
        });
        Button collectButton = (Button) this.findViewById(R.id.collectButton);
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                attemptCollect();
//            Toast.makeText(SubscribeMerchantActivity.this, "加入收藏", Toast.LENGTH_SHORT).show();
            }
        });

        Button moreDiscountButton = (Button) this.findViewById(R.id.moreDiscountButton);
        moreDiscountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(SubscribeMerchantActivity.this, "更多优惠", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptCollect() {
        if (collectTask != null)
            return;

        String userId = UserInfo.userId;
        String entityId = item_id;
        String typeId = "1";

        collectTask = new CollectTask(SubscribeMerchantActivity.this, progressBar);
        collectTask.execute(userId, entityId, typeId);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem submit = menu.add(0, 1, 0, "设置");
        MenuItem exit = menu.add(0, 2, 0, "返回");
//        submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    private void goSetting() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_subscribe_setting,
                (ViewGroup) findViewById(R.id.subscribe_setting_dialog));
        new AlertDialog.Builder(this).setTitle("订阅设置").setView(layout)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null).show();
    }

    private void goBack() {
        Intent intent = new Intent(SubscribeMerchantActivity.this,
                MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            goSetting();
        } else if (item.getItemId() == 2) {
            goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> line = new HashMap<String, Object>();
            res.add(line);
        }

        return res;
    }
}