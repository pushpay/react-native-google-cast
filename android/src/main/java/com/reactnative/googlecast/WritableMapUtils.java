package com.reactnative.googlecast;

import android.support.annotation.NonNull;

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

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;

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

  @NonNull
  public static WritableMap fromMediaStatus(MediaStatus mediaStatus) {
    WritableMap map = Arguments.createMap();
    map.putInt("playerState", mediaStatus.getPlayerState());
    map.putInt("idleReason", mediaStatus.getIdleReason());
    map.putBoolean("muted", mediaStatus.isMute());
    map.putDouble("playbackRate", mediaStatus.getPlaybackRate());
    map.putInt("streamPosition", (int)(mediaStatus.getStreamPosition() / 1000));

    MediaInfo info = mediaStatus.getMediaInfo();
    if (info != null) {
      map.putInt("streamDuration", (int) (info.getStreamDuration() / 1000));
    }
    return map;
  }

  @NonNull
  public static WritableMap toStatusUpdatedEvent(MediaStatus mediaStatus) {
    // needs to be constructed for every message from scratch because reusing a
    // message fails with "Map already consumed"
    WritableMap map = Arguments.createMap();
    map.putMap("mediaStatus", fromMediaStatus(mediaStatus));
    return map;
  }
}
