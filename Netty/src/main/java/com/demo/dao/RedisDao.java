package com.demo.dao;

public interface RedisDao {
	
	public void saveString(String key,String value);
	
	public String getString(String key);
	
	public void deleteString(String key);
	
}
