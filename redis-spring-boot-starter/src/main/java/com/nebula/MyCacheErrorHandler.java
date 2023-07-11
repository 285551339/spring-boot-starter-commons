package com.nebula;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * @Description redis异常处理
 * @Author chenxudong
 * @Date 2019/11/20 14:48
 */
@Slf4j
public class MyCacheErrorHandler implements CacheErrorHandler {

	@Override
	public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
		log.error("Redis缓存CacheGet方法异常,缓存队列名:{},key:{}", cache.getName(), o.toString(), e);
	}

	@Override
	public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
		log.error("Redis缓存CachePut方法异常,缓存队列名:{},key:{}", cache.getName(), o.toString(), e);
	}

	@Override
	public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
		log.error("Redis缓存CacheEvict方法异常,缓存队列名:{},key:{}", cache.getName(), o.toString(), e);
	}

	@Override
	public void handleCacheClearError(RuntimeException e, Cache cache) {
		log.error("Redis缓存CacheClear方法异常,缓存队列名:{}", cache.getName(), e);
	}
}
