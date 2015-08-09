package com.stpi.campus.util;

import java.util.HashMap;
import java.util.Map;

public class CartInfo {

    public String cart_id;
    public String shop_id;
    public String update_time;
    public int state;

    public Map<String, Integer> items = new HashMap<String, Integer>();

}
