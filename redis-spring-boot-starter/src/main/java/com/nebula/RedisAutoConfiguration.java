package com.nebula;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description redis自动配置类
 * @Author chenxudong
 * @Date 2019/11/20 14:48
 */
@Slf4j
@Configuration
@EnableCaching
@EnableConfigurationProperties
public class RedisAutoConfiguration extends CachingConfigurerSupport {

	private final static String separator = ":";
	private final static Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

	static {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping( LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
		jackson2JsonRedisSerializer.setObjectMapper(om);
	}

	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object o, Method method, Object... objects) {
				StringBuilder sb = new StringBuilder();
				int length = objects.length;
				if (length==0){
					sb.append(o.getClass().getName());
					sb.append(separator);
					sb.append(method.getName());
				}else {
					for (int i = 0; i < length; i++) {
						sb.append(objects[i]);
						if (i!=length-1){
							sb.append(separator);
						}
					}
				}
				return sb.toString();
			}
		};
	}

	private static RedisCacheConfiguration createCacheConfiguration(long expiration) {
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(getDefaultExpiration(expiration)));
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory,
									 @Value("${spring.redis.cache.expiration:0}") long expiration,
									 @Value("#{${spring.redis.cache.expries:}}") Map<String, Long> expries) {
		RedisCacheConfiguration defaultCacheConfiguration = createCacheConfiguration(expiration)
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
		Map<String, RedisCacheConfiguration> cacheConfigurationMap = getExpries(expries);
		RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(factory);
		return RedisCacheManager.builder(cacheWriter).cacheDefaults(defaultCacheConfiguration).
				withInitialCacheConfigurations(cacheConfigurationMap).build();
	}

	private static long getDefaultExpiration(long expiration) {
		if (expiration > 0) {
			return expiration;
		}
		return 0;
	}

	private Map<String, RedisCacheConfiguration> getExpries(Map<String, Long> expriesMap) {
		Map<String, RedisCacheConfiguration> expries = new HashMap<>();
		try {
			if (!CollectionUtils.isEmpty(expriesMap)) {
				for (Map.Entry<String, Long> entry : expriesMap.entrySet()) {
					Long value = Long.valueOf(entry.getValue().intValue());
					if (value != null) {
						expries.put(entry.getKey(),
								createCacheConfiguration(value).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)));
					}
				}
			}
		} catch (Exception e) {
			log.error("读取配置文件初始化缓存信息异常，配置信息expries:{}", expriesMap, e);
		}
		return expries;

	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		RedisTemplate<String, Object> template = new RedisTemplate();
		template.setConnectionFactory(factory);
		template.setKeySerializer(stringRedisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

//	public Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
//		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//		ObjectMapper om = new ObjectMapper();
//		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		jackson2JsonRedisSerializer.setObjectMapper(om);
//		return jackson2JsonRedisSerializer;
//	}

	@Override
	@Bean
	public CacheErrorHandler errorHandler() {
		return new MyCacheErrorHandler();
	}

}
