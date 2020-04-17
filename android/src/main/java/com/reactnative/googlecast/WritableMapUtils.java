package com.reactnative.googlecast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WritableMapUtils {
  public static WritableMap toWritableMap(JSONObject json) {
    Map<String, Object> retMap = new HashMap<String, Object>();

    try {
      if (json != null && json != JSONObject.NULL) {
        retMap = toMap(json);
      }
    } catch (Exception ex) {

    }

    return Arguments.makeNativeMap(retMap);
  }

  private static Map<String, Object> toMap(JSONObject object) throws JSONException {
    Map<String, Object> map = new HashMap<String, Object>();

    Iterator<String> keysItr = object.keys();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);

      if (value instanceof JSONArray) {
        value = toList((JSONArray) value);
      } else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value);
      }
      map.put(key, value);
    }
    return map;
  }

  private static List<Object> toList(JSONArray array) throws JSONException {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.length(); i++) {
      Object value = array.get(i);
      if (value instanceof JSONArray) {
        value = toList((JSONArray) value);
      } else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value);
      }
      list.add(value);
    }
    return list;
  }
}
