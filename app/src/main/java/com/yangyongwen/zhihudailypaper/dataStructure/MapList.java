package com.yangyongwen.zhihudailypaper.dataStructure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samsung on 2016/3/15.
 */
public class MapList<K,V> {
    HashMap mHashMap;
    ArrayList<V> mArrayList;

    public MapList(int cap){
        mArrayList=new ArrayList<V>(cap);
        mHashMap=new HashMap<K,Integer>();
    }

    public MapList(){
        this(0);
    }

    public void setValue(K key,V value){
        mArrayList.add(value);
        mHashMap.put(key, new Integer(mArrayList.size() - 1));
    }

    public void remove(K key){
        Integer index=(Integer)mHashMap.get(key);
        if(index==null){
            return;
        }
        mHashMap.remove(key);
        mArrayList.remove(index);
    }

    public V getValue(int index){
        return mArrayList.get(index);
    }

    public V getValue(K key){
        Integer index=(Integer)mHashMap.get(key);
        if(index==null){
            return null;
        }
        return mArrayList.get(index);
    }


}
