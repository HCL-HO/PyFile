package com.hec.app.framework.cache;

public interface MyCache {

	<T> T get(String key);
    
    <T> T get(String key, T defaultValue);
    
    <T> void put(String key, T value);
    
    void remove(String key);
    
    boolean exists(String key);
}