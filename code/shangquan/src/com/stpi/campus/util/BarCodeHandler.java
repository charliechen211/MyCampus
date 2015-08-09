package com.stpi.campus.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.stpi.campus.activity.item.ItemDetailActivity;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;

public class BarCodeHandler {

    public static void handle(Context context, String code) {

        String[] res = code.split("#");
        if (res.length != 2) {
            Toast.makeText(context, "ERROR CODE", Toast.LENGTH_SHORT).show();
        } else if (res[0].equals("shop")) {
            Intent openShopIntent = new Intent(context,
                    MerchantDetailActivity.class);
            openShopIntent.putExtra("shop_id", res[1]);
            context.startActivity(openShopIntent);
        } else if (res[0].equals("item")) {
            Intent openItemIntent = new Intent(context,
                    ItemDetailActivity.class);
            openItemIntent.putExtra("item_id", res[1]);
            context.startActivity(openItemIntent);
        }
    }

}
