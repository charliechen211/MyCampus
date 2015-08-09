package com.stpi.campus.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.stpi.campus.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydata.db"; // ��ݿ����
    private static final int version = 1; // ��ݿ�汾
    private static String[] shop_columns = {"shop_id", "pic_id", "name",
            "tel", "addr"};
    private static String[] item_columns = {"item_id", "pic_id", "name",
            "price", "shop_id"};
    private static String[] cart_columns = {"cart_id", "shop_id",
            "update_time", "state"};
    private static String[] cart_detail_columns = {"cart_id", "item_id",
            "number"};
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create tables
        String sql = null;
        sql = "create table shop(shop_id varchar(20), pic_id integer, name varchar(20), tel varchar(20), addr varchar(50));";
        db.execSQL(sql);
        sql = "create table item(item_id varchar(20), pic_id integer, name varchar(20), price integer, shop_id varchar(20));";
        db.execSQL(sql);
        sql = "create table cart(cart_id varchar(50), shop_id varchar(20), update_time timestamp, state smallint);";
        db.execSQL(sql);
        sql = "create table cart_detail(cart_id varchar(50), item_id varchar(20), number integer);";
        db.execSQL(sql);

        // insert data
        ContentValues cv = null;
        cv = new ContentValues();
        cv.put("shop_id", "kfc_111");
        cv.put("pic_id", R.drawable.kfc_1);
        cv.put("name", "肯德基");
        cv.put("tel", "400-88088");
        cv.put("addr", "百联五楼");
        db.insert("shop", null, cv);

        cv = new ContentValues();
        cv.put("shop_id", "mdl_111");
        cv.put("pic_id", R.drawable.mdl_1);
        cv.put("name", "麦当劳");
        cv.put("tel", "400-88088");
        cv.put("addr", "百联五楼");
        db.insert("shop", null, cv);

        cv = new ContentValues();
        cv.put("item_id", "kfc_dish_111");
        cv.put("pic_id", R.drawable.kfcdish_1);
        cv.put("name", "香辣翅");
        cv.put("price", 20);
        cv.put("shop_id", "kfc_111");
        db.insert("item", null, cv);

        cv = new ContentValues();
        cv.put("item_id", "kfc_dish_222");
        cv.put("pic_id", R.drawable.kfcdish_2);
        cv.put("name", "巨无霸");
        cv.put("price", 100);
        cv.put("shop_id", "kfc_111");
        db.insert("item", null, cv);

        cv = new ContentValues();
        cv.put("item_id", "mdl_dish_111");
        cv.put("pic_id", R.drawable.mdldish_1);
        cv.put("name", "麦旋风");
        cv.put("price", 20);
        cv.put("shop_id", "mdl_111");
        db.insert("item", null, cv);

        cv = new ContentValues();
        cv.put("item_id", "mdl_dish_222");
        cv.put("pic_id", R.drawable.mdldish_2);
        cv.put("name", "香辣鸡腿堡");
        cv.put("price", 50);
        cv.put("shop_id", "mdl_111");
        db.insert("item", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public ShopInfo getShopInfo(String shop_id) {

        ShopInfo res = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("shop", shop_columns, "shop_id=\"" + shop_id
                + "\"", null, null, null, null);
        if (cursor.moveToFirst()) {
            res = new ShopInfo();
            res.shop_id = cursor.getString(0);
            res.pic_id = cursor.getInt(1);
            res.shop_name = cursor.getString(2);
            res.tel = cursor.getString(3);
            res.addr = cursor.getString(4);
        }
        cursor.close();

        db.close();
        return res;
    }

    public ItemInfo getItemInfo(String item_id) {

        ItemInfo res = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("item", item_columns, "item_id=\"" + item_id
                + "\"", null, null, null, null);
        if (cursor.moveToFirst()) {
            res = new ItemInfo();
            res.item_id = cursor.getString(0);
            res.pic_id = cursor.getInt(1);
            res.item_name = cursor.getString(2);
            res.item_price = cursor.getInt(3);
            res.shop_id = cursor.getString(4);
        }
        cursor.close();

        db.close();
        return res;
    }

    // state=0 means it is in cart
    public CartInfo getCartInfoByShopId(String shop_id) {

        CartInfo res = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cart", cart_columns, "shop_id=\"" + shop_id
                + "\" and state=0", null, null, null, null);
        if (cursor.moveToFirst()) {
            res = new CartInfo();
            res.cart_id = cursor.getString(0);
            res.shop_id = cursor.getString(1);
            res.update_time = cursor.getString(2);
            res.state = cursor.getInt(3);

            cursor.close();
            cursor = db.query("cart_detail", cart_detail_columns, "cart_id=\""
                    + res.cart_id + "\"", null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    res.items.put(cursor.getString(1), cursor.getInt(2));
                    if (!cursor.moveToNext())
                        break;
                }
            }
        }
        cursor.close();

        db.close();
        return res;
    }

    public CartInfo getCartInfoByCartId(String cart_id) {

        CartInfo res = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cart", cart_columns, "cart_id=\"" + cart_id
                + "\"", null, null, null, null);
        if (cursor.moveToFirst()) {
            res = new CartInfo();
            res.cart_id = cursor.getString(0);
            res.shop_id = cursor.getString(1);
            res.update_time = cursor.getString(2);
            res.state = cursor.getInt(3);

            cursor.close();
            cursor = db.query("cart_detail", cart_detail_columns, "cart_id=\""
                    + res.cart_id + "\"", null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    res.items.put(cursor.getString(1), cursor.getInt(2));
                    if (!cursor.moveToNext())
                        break;
                }
            }
        }
        cursor.close();

        db.close();
        return res;
    }

    private void createCart(CartInfo cart) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cart_id", cart.cart_id);
        cv.put("shop_id", cart.shop_id);
        cv.put("update_time", cart.update_time);
        cv.put("state", cart.state);
        db.insert("cart", null, cv);
        System.out.println("createing.. " + cart.cart_id + " " + cart.shop_id
                + " " + cart.state);

        for (String item_id : cart.items.keySet()) {
            cv = new ContentValues();
            cv.put("cart_id", cart.cart_id);
            cv.put("item_id", item_id);
            cv.put("number", cart.items.get(item_id));
            db.insert("cart_detail", null, cv);
        }
        db.close();
    }

    private void createCartDetail(String cart_id, String item_id, int count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cart_id", cart_id);
        cv.put("item_id", item_id);
        cv.put("number", count);
        db.insert("cart_detail", null, cv);
        db.close();
    }

    private void updateCartDetail(CartInfo cart, String item_id, int count) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("number", count);
        db.update("cart_detail", cv, "cart_id=? and item_id=?", new String[]{
                cart.cart_id, item_id});

        cv = new ContentValues();
        cv.put("update_time", cart.update_time);
        db.update("cart", cv, "cart_id=?", new String[]{cart.cart_id});

        db.close();
    }

    private void deleteCartDetail(String cart_id, String item_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart_detail", "cart_id=? and item_id=?", new String[]{
                cart_id, item_id});
        db.close();
    }

    public CartInfo insertIntoCart(ItemInfo item, int count) {
        CartInfo cart = getCartInfoByShopId(item.shop_id);
        if (cart == null) {
            cart = new CartInfo();
            cart.cart_id = UUID.randomUUID().toString();
            cart.shop_id = item.shop_id;
            cart.update_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .format(new Date());
            cart.state = 0;
            createCart(cart);
        }
        if (count > 0) {
            if (cart.items.containsKey(item.item_id)) {
                // already have
                cart.update_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                int number = cart.items.get(item.item_id);
                number += count;
                cart.items.put(item.item_id, number);
                updateCartDetail(cart, item.item_id, number);
            } else {
                // create new
                cart.items.put(item.item_id, count);
                createCartDetail(cart.cart_id, item.item_id, count);
            }
        }
        return cart;
    }

    public List<CartInfo> getCarts(int state) {
        List<CartInfo> carts = new ArrayList<CartInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cart", cart_columns,
                "state=" + String.valueOf(state), null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                CartInfo res = new CartInfo();
                res.cart_id = cursor.getString(0);
                res.shop_id = cursor.getString(1);
                res.update_time = cursor.getString(2);
                res.state = cursor.getInt(3);
                Cursor cursor_detail = db.query("cart_detail",
                        cart_detail_columns, "cart_id=\"" + res.cart_id + "\"",
                        null, null, null, null);
                if (cursor_detail.moveToFirst()) {
                    while (!cursor_detail.isAfterLast()) {
                        res.items.put(cursor_detail.getString(1),
                                cursor_detail.getInt(2));
                        if (!cursor_detail.moveToNext())
                            break;
                    }
                }
                cursor_detail.close();
                carts.add(res);

                if (!cursor.moveToNext())
                    break;
            }
        }
        cursor.close();

        db.close();
        return carts;
    }

    public void updateCartState(CartInfo cart) {
        cart.update_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("state", cart.state);
        cv.put("update_time", cart.update_time);
        db.update("cart", cv, "cart_id=?", new String[]{cart.cart_id});
        db.close();
    }

    public List<ItemInfo> getItems(ShopInfo shop) {
        List<ItemInfo> items = new ArrayList<ItemInfo>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("item", item_columns, "shop_id=\"" + shop.shop_id + "\"", null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ItemInfo item = new ItemInfo();
                item.item_id = cursor.getString(0);
                item.pic_id = cursor.getInt(1);
                item.item_name = cursor.getString(2);
                item.item_price = cursor.getInt(3);
                item.shop_id = cursor.getString(4);
                items.add(item);
                if (!cursor.moveToNext())
                    break;
            }
        }
        cursor.close();

        db.close();
        return items;
    }

}
