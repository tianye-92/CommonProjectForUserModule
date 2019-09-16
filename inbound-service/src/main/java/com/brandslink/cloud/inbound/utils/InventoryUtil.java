package com.brandslink.cloud.inbound.utils;

import java.util.Arrays;

/**
 * @author xd
 * @Classname InventoryUtil
 * @Description TODO
 * @Date 2019/8/3 17:22
 */
public class InventoryUtil {

    /**
     * 判断数组中是否包含某一元素
     * @param arr
     * @param str
     * @return
     */
    public static boolean arrayContainsElement(String[] arr,String str){
        return Arrays.asList(arr).contains(str);
    }
}