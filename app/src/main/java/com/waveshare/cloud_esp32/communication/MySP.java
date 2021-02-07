package com.waveshare.cloud_esp32.communication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MySP {
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    @SuppressLint("CommitPrefEdits")
    public MySP(SharedPreferences sp){
        sharedPreferences= sp;
        editor= sharedPreferences.edit();
    }
    public  void Save(String key,String str){
        editor.putString(key, str);
        editor.apply();
    }
    public void Save(String[] keys,String[] strs){

        for(int cnt=0; cnt<keys.length; cnt++)
        {
            editor.putString(keys[cnt],strs[cnt]);
        }
        editor.apply();

    }
    public void Save(String key,boolean sw){
        editor.putBoolean(key, sw);
        editor.apply();
    }

    public String Load(String key){
        return sharedPreferences.getString(key,"");
    }
    public String[] Load(String[] keys){
        ArrayList list =new ArrayList();

        for (String key : keys) {
            list.add(sharedPreferences.getString(key, ""));
        }
        String[] gets=(String[])list.toArray();
        return null;
    }
    public boolean Load_bool(String key){
        return sharedPreferences.getBoolean(key,false);
    }

}
