package com.demo.dao.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.demo.dao.RedisDao;

@Repository
public class RedisDaoImpl implements RedisDao {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Resource(name = "stringRedisTemplate")
	ValueOperations<String, String> valOpsStr;

	@Override
	public void saveString(String key, String value) {
		valOpsStr.set(key, value);
	}

	@Override
	public String getString(String key) {
		return valOpsStr.get(key);
	}

	@Override
	public void deleteString(String key) {
		stringRedisTemplate.delete(key);
	}

}
