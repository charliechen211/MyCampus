package com.stpi.campus.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stpi.campus.entity.dynamicShare.AroundShareInfo;

import java.util.List;

/**
 * Created by Administrator on 13-12-4.
 */
public class AroundSharedInfoParser {

    public List<AroundShareInfo> parse(String content) {
        Gson gson = new Gson();
        List<AroundShareInfo> results = gson.fromJson(content, new TypeToken<List<AroundShareInfo>>() {
        }.getType());
//		JsonArray array = parser.parse(content).getAsJsonArray();
//		for (int i = 0 ; i < array.size() ;i++) {
//			retList.add(gson.fromJson(array.get(i), Node.class));
//		}
        return results;
    }

}
