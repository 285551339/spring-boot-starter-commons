package com.nebula.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description RedisService
 * @Author chenxudong
 * @Date 2019/11/20 14:48
 */
@Component
public class RedisService {

    private final static String WILDCARD = "*";

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    public boolean del(String... keys) {
        if (keys.length == 1) {
            return redisTemplate.delete(keys[0]);
        }
        return redisTemplate.delete(CollectionUtils.arrayToList(keys)) > 0;
    }

    /**
     * 模糊匹配删除
     * @param keyPrefix
     * @return
     */
    public long delByKeyPrefix(String keyPrefix) {
        Set<String> keys = redisTemplate.keys(keyPrefix + WILDCARD);
        return redisTemplate.delete(keys);
    }

    /************************************** String start******************************************/
    /**
     * 添加值
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 添加值并设置过期时间
     * @param key
     * @param value
     * @param time
     */
    public void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 给指定字符串附加内容
     * @param key
     * @param value
     */
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 设置键的字符串值并返回其旧值
     * @param key
     * @param value
     * @return
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 获取key对应的值
     * @param key
     * @return
     */
    public Object get(String key) {
       return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取key对应的指定索引范围的值
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String get(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 增加给key对应的值(整数)
     * @param key
     * @param delta
     * @return
     */
    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 减少给key对应的值(整数)
     * @param key
     * @param delta
     * @return
     */
    public long decrease(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 增加给key对应的值(浮点数)
     * @param key
     * @param delta
     * @return
     */
    public double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 减少给key对应的值(浮点数)
     * @param key
     * @param delta
     * @return
     */
    public double decrease(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 如果key不存在，设置新值，否则不设置
     * @param key
     * @param value
     * @return
     */
    public boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 获取多个key对应的值
     * @param keys
     * @return
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 设置多个key-value
     * @param map
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 设置多个key-value，如果key不存在的话
     * @param map
     * @return
     */
    public boolean multiSetIfAbsent(Map<String, Object> map) {
        return redisTemplate.opsForValue().multiSetIfAbsent(map);
    }
    /************************************** String end******************************************/



    /************************************** List start******************************************/
    /**
     * 返回列表中指定索引的值
     * @param key
     * @param index
     * @return
     */
    public Object index(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 裁剪列表元素
     * @param key
     * @param start
     * @param end
     */
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 获取列表指定范围内的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 通过索引来设置元素的值。
     * 当索引参数超出范围，或对一个空列表进行 LSET 时，返回一个错误
     * @param key
     * @param index
     * @param value
     */
    public void set(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除等于value的元素，
     * 当count>0时，从表头开始查找，移除count个；
     * 当count=0时，从表头开始查找，移除所有等于value的；
     * 当count<0时，从表尾开始查找，移除|count| 个
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long remove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 获取列表的长度
     * @param key
     * @return
     */
    public long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 插入列表头部
     * @param key
     * @param value
     * @return 列表长度
     */
    public long leftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 在value1的签名插入value2
     * @param key
     * @param value1
     * @param value2
     * @return
     */
    public long leftPush(String key, Object value1, Object value2) {
        return redisTemplate.opsForList().leftPush(key, value1, value2);
    }

    /**
     * 在列表的元素前插入多个元素
     * @param key
     * @param values
     * @return
     */
    public long leftPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 在列表的元素前插入多个元素
     * @param key
     * @param values
     * @return
     */
    public long leftPushAll(String key, Collection values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 在已存在的列表元素前插入元素
     * @param key
     * @param value
     * @return
     */
    public long leftPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 在列表的元素后插入元素
     * @param key
     * @param value
     * @return
     */
    public long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将value2插入到value1的后面
     * @param key
     * @param value1
     * @param value2
     * @return
     */
    public long rightPush(String key, Object value1, Object value2) {
        return redisTemplate.opsForList().rightPush(key, value1, value2);
    }

    /**
     * 在列表的元素后插入多个元素
     * @param key
     * @param values
     * @return
     */
    public long rightfPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 在列表的元素后插入多个元素
     * @param key
     * @param values
     * @return
     */
    public long rightPushAll(String key, Collection values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 在已存在的列表后插入元素
     * @param key
     * @param value
     * @return
     */
    public long rightPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 移出并获取列表的第一个元素
     * @param key
     * @return
     */
    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * @param key
     * @param timeout 超时时间
     * @param unit
     * @return 移除的元素
     */
    public Object leftPop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移出并获取列表的最后一个元素
     * @param key
     * @return
     */
    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * @param key
     * @param timeout 超时时间
     * @param unit
     * @return 移除的元素
     */
    public Object rightPop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 移出并获取列表的最后一个元素，并放入另一个列表的最前面
     * @param sourceKey
     * @param destinationKey
     * @return
     */
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
    }
    /************************************** List end******************************************/



    /************************************** Hash start******************************************/
    /**
     * 设置散列hashKey的值
     * @param key
     * @param hk
     * @param hv
     */
    public void put(String key, Object hk, Object hv) {
        redisTemplate.opsForHash().put(key, hk, hv);
    }

    /**
     * 向hash表添加多个hashkey和值
     * @param key
     * @param map
     */
    public void putAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 当hashKey不存在时才设置hashKey的值
     * @param key
     * @param hk
     * @param hv
     * @return
     */
    public boolean putIfAbsent(String key, Object hk, Object hv) {
        return redisTemplate.opsForHash().putIfAbsent(key, hk, hv);
    }

    /**
     * 批量删除指定key对应的hash表中hashKey的值
     * @param key
     * @param hashKeys
     * @return
     */
    public long delete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(hashKeys);
    }

    /**
     * 删除指定key对应的hash表中hashKey的值
     * @param key
     * @param hashKey
     * @return
     */
    public boolean delete(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取指定key对应的hash表中hashKey的值
     * @param key
     * @param hashKey
     * @return
     */
    public Object get(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取指定key对应的hash表的所有值
     * @param key
     * @return
     */
    public List values(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取指定hashkey的值
     * @param key
     * @param hashKeys
     * @return
     */
    public List multiGet(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 通过给定的delta增加散列hashKey的值（整型）
     * @param key
     * @param hashKey
     * @param delta
     * @return
     */
    public long increment(String key, Object hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 通过给定的delta减少散列hashKey的值（整型）
     * @param key
     * @param hashKey
     * @param delta
     * @return
     */
    public long decrement(String key, Object hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    /**
     * 通过给定的delta增加散列hashKey的值（浮点数）
     * @param key
     * @param hashKey
     * @param delta
     * @return
     */
    public double increment(String key, Object hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 通过给定的delta减少散列hashKey的值（浮点数）
     * @param key
     * @param hashKey
     * @param delta
     * @return
     */
    public double decrement(String key, Object hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    /**
     * 获取key所对应的hash表的key
     * @param key
     * @return
     */
    public Set keys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取key所对应的hash表的大小个数
     * @param key
     * @return
     */
    public long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取hash表数据
     * @param key
     * @return
     */
    public Map<String, Object> entries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 遍历hash表
     * @param key
     * @param options
     * @return
     */
    public Cursor hScan(String key, ScanOptions options) {
        return redisTemplate.opsForHash().scan(key, options);
    }

    /************************************** Hash end******************************************/



    /************************************** Set start******************************************/
    /**
     * 向集合中添加元素
     * @param key
     * @param values
     * @return
     */
    public long add(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 删除集合中指定元素
     * @param key
     * @param values
     * @return
     */
    public long remove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取集合中的所有元素
     * @param key
     * @return
     */
    public Set members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取无序集合中一个元素
     * @param key
     * @return
     */
    public Object randomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取无序集合中多个元素（可能重复）
     * @param key
     * @param count 元素个数
     * @return
     */
    public List randomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 获取无序集合中多个不重复的元素
     * @param key
     * @param count
     * @return
     */
    public Set distinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     *
     * @param sourceKey
     * @param value
     * @param destinationKey
     * @return
     */
    public boolean move(String sourceKey, Object value, String destinationKey) {
        return redisTemplate.opsForSet().move(sourceKey, value, destinationKey);
    }

    /**
     * 移除并返回集合中的一个随机元素
     * @param key
     * @return
     */
    public Object pop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 随机移除多个元素
     * @param key
     * @param count
     * @return
     */
    public List pop(String key, long count) {
        return redisTemplate.opsForSet().pop(key, count);
    }

    /**
     * 获取无序集合元素个数
     * @param key
     * @return
     */
    public long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断元素是否存在集合中
     * @param key
     * @param value
     * @return
     */
    public boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取两个集合的交集
     * @param key1
     * @param key2
     * @return
     */
    public Set intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 获取一个集合与多个集合的交集
     * @param key
     * @param keys
     * @return
     */
    public Set intersect(String key, Collection keys) {
        return redisTemplate.opsForSet().intersect(key, keys);
    }

    /**
     * 将两个无序集合的交集存储到第三个集合中
     * @param key1
     * @param key2
     * @param destKey
     * @return 交集元素个数
     */
    public long intersectAndStore(String key1, String key2, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key1, key2, destKey);
    }

    /**
     * 将一个无序集合与多个无序集合求交集，并存储到第三个集合中
     * @param key
     * @param keys
     * @param destKey
     * @return 交集元素个数
     */
    public long intersectAndStore(String key, Collection keys, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, keys, destKey);
    }

    /**
     * 获取两个集合的并集
     * @param key1
     * @param key2
     * @return
     */
    public Set union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 获取一个集合与多个集合的并集
     * @param key
     * @param keys
     * @return
     */
    public Set union(String key, Collection keys) {
        return redisTemplate.opsForSet().union(key, keys);
    }

    /**
     * 将两个集合的并集存储到另外一个集合中
     * @param key1
     * @param key2
     * @param destKey
     * @return
     */
    public long unionAndStore(String key1, String key2, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key1, key2, destKey);
    }

    /**
     * 将一个集合与多个集合的并集存储到另外一个集合中
     * @param key
     * @param keys
     * @param destKey
     * @return
     */
    public long unionAndStore(String key, Collection keys, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, keys, destKey);
    }

    /**
     * 获取两个集合的差集
     * @param key1
     * @param key2
     * @return
     */
    public Set difference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

    /**
     * 获取一个集合与多个集合的差集
     * @param key
     * @param keys
     * @return
     */
    public Set difference(String key, Collection keys) {
        return redisTemplate.opsForSet().difference(key, keys);
    }

    /**
     *  将两个集合的差集并存储到另外一个集合中
     * @param key1
     * @param key2
     * @param destKey
     * @return
     */
    public long differenceAndStore(String key1, String key2, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key1, key2, destKey);
    }

    /**
     * 将一个集合与多个集合的差集并存储到另外一个集合中
     * @param key
     * @param keys
     * @param destKey
     * @return
     */
    public long differenceAndStore(String key, Collection keys, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, keys, destKey);
    }

    /**
     * 遍历set
     * @param key
     * @param options
     * @return
     */
    public Cursor<Object> sScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }
    /************************************** Set end******************************************/



    /************************************** ZSet start******************************************/
    /**
     * 向有序集合添加元素
     * @param key
     * @param value
     * @param score
     * @return 存才返回false，不存在返回true
     */
    public boolean add(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 向有序集合添加多个元素
     * @param key
     * @param tuples
     * @return 成功添加元素的个数
     */
    public long add(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    /**
     * 删除集合中指定元素
     * @param key
     * @param values
     * @return
     */
    public long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 删除集合中指定索引范围的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public long removeRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值范围来移除元素
     * @param key
     * @param min
     * @param max
     * @return
     */
    public long removeRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取集合元素数量(内部调用zCard)
     * @param key
     * @return
     */
    public long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合元素数量
     * @param key
     * @return
     */
    public long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取集合中元素的score值
     * @param key
     * @param value
     * @return
     */
    public double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public double incrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 取出两个集合的交集并存储到另外一个集合中
     * @param key1
     * @param key2
     * @param destKey
     * @return
     */
    public long zIntersectAndStore(String key1, String key2, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key1, key2, destKey);
    }

    /**
     * 取出一个集合与多个集合的交集并存储到另外一个集合中
     * @param key1
     * @param keys
     * @param destKey
     * @return
     */
    public long zIntersectAndStore(String key1, Collection keys, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key1, keys, destKey);
    }

    /**
     * 取出两个集合的并集并存储到另外一个集合中
     * @param key1
     * @param key2
     * @param destKey
     * @return
     */
    public long zUnionAndStore(String key1, String key2, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key1, key2, destKey);
    }

    /**
     * 取出一个集合与多个集合的并集并存储到另外一个集合中
     * @param key
     * @param keys
     * @param destKey
     * @return
     */
    public long zUnionAndStore(String key, Collection keys, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, keys, destKey);
    }

    /**
     * 获取集合中元素的排名（从小到大）
     * @param key
     * @param value
     * @return
     */
    public long rank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取集合中元素的排名（从大到小）
     * @param key
     * @param value
     * @return
     */
    public long reverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合中指定索引范围的元素（从小到大）
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合中指定索引范围的元素（从大到小）
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set reverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合中指定score范围的元素（从小到大）
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set rangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取集合中指定score范围，同时满足索引的指定数量的元素（从小到大）
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set rangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 获取集合中指定score范围的元素（从大到小）
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set reverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 获取集合中指定score范围，同时满足索引的指定数量的元素（从大到小）
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set reverseRangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * 获取集合中指定索引范围的元素（从小到大）
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 获取集合中指定索引范围的元素（从大到小）
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 获取集合中指定score范围的元素(从小到大)
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * 获取集合中指定score范围，同时满足索引要求的指定数量元素(从小到大)
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 获取集合中指定score范围的元素(从大到小)
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * 获取集合中指定score范围，同时满足索引要求的指定数量元素(从大到小)
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 遍历zset
     * @param key
     * @param options
     * @return
     */
    public Cursor<ZSetOperations.TypedTuple<Object>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }

    /************************************** ZSet end******************************************/
}
