package com.stpi.campus.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stpi.campus.entity.Node;

import java.util.List;

public class ContentParser {
    public List<Node> parse(String content) {
        Gson gson = new Gson();
        List<Node> results = gson.fromJson(content, new TypeToken<List<Node>>() {
        }.getType());
//		JsonArray array = parser.parse(content).getAsJsonArray();
//		for (int i = 0 ; i < array.size() ;i++) {
//			retList.add(gson.fromJson(array.get(i), Node.class));
//		}
        return results;
    }
}
