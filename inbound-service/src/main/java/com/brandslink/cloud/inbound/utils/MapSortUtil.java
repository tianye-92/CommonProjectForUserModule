package com.brandslink.cloud.inbound.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: map集合排序
 * @date 2019/6/21 9:36
 */
public class MapSortUtil {

    /**
     * @description: 根据map的value降序排列
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static  <K, V extends Comparable<? super V>> Map<K, V> desSortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * @description: 根据map的key降序排列
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K extends Comparable<? super K>, V > Map<K, V> desSortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByKey()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * @description: 根据map的value升序排列
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> ascSortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                ).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * @description: 根据map的key升序排列
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> ascSortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                ).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

}
