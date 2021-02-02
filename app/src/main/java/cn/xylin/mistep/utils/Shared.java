package cn.xylin.mistep.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XyLin
 * @date 2020/8/21 16:39:33
 * Shared.java
 **/
public class Shared {
    private static Shared shared;

    public static Shared getShared() {
        if (shared == null) {
            shared = new Shared();
        }
        return shared;
    }

    private final ConcurrentHashMap<String, SharedPreferences> sharedMap;
    private final ConcurrentHashMap<String, SharedPreferences.Editor> editorMap;

    private Shared() {
        this.sharedMap = new ConcurrentHashMap<>();
        this.editorMap = new ConcurrentHashMap<>();
    }

    public Shared addShared(Context baseContext, String xmlName) {
        sharedMap.put(xmlName, baseContext.getSharedPreferences(xmlName, Context.MODE_PRIVATE));
        editorMap.put(xmlName, sharedMap.get(xmlName).edit());
        return this;
    }

    public <T> T getValue(String xmlName, String key, T defValue) {
        if (sharedMap.containsKey(xmlName)) {
            SharedPreferences preferences = sharedMap.get(xmlName);
            T retValue;
            if (defValue instanceof Integer) {
                retValue = (T) (Integer) preferences.getInt(key, (Integer) defValue);
            } else if (defValue instanceof Boolean) {
                retValue = (T) (Boolean) preferences.getBoolean(key, (Boolean) defValue);
            } else if (defValue instanceof Float) {
                retValue = (T) (Float) preferences.getFloat(key, (Float) defValue);
            } else if (defValue instanceof Long) {
                retValue = (T) (Long) preferences.getLong(key, (Long) defValue);
            } else if (defValue instanceof String) {
                retValue = (T) preferences.getString(key, (String) defValue);
            } else {
                retValue = defValue;
            }
            return retValue;
        }
        return defValue;
    }

    public Shared putValue(String xmlName, String key, Object value) {
        if (editorMap.containsKey(xmlName)) {
            SharedPreferences.Editor editor = editorMap.get(xmlName);
            if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            }
        }
        return this;
    }

    public Shared removeValue(String xmlName, String key) {
        if (editorMap.containsKey(xmlName)) {
            editorMap.get(xmlName).remove(key);
        }
        return this;
    }

    public void applyShared(String xmlName) {
        if (editorMap.containsKey(xmlName)) {
            editorMap.get(xmlName).apply();
            reloadEdit(xmlName);
        }
    }

    public void commitShared(String xmlName) {
        if (editorMap.containsKey(xmlName)) {
            editorMap.get(xmlName).commit();
            reloadEdit(xmlName);
        }
    }

    public void clearShared(String xmlName) {
        if (sharedMap.containsKey(xmlName)) {
            Set<String> keys = sharedMap.get(xmlName).getAll().keySet();
            SharedPreferences.Editor editor = editorMap.get(xmlName);
            for (String key : keys) {
                editor.remove(key);
            }
            editor.commit();
            reloadEdit(xmlName);
        }
    }

    private void reloadEdit(String xmlName) {
        if (sharedMap.containsKey(xmlName)) {
            editorMap.put(xmlName, sharedMap.get(xmlName).edit());
        }
    }
}
