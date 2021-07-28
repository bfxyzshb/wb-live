package com.weibo.live.rtmp.message.media;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 注解的处理器
 */
public class OrderHandler {

    public static void orderHandle(Object runOrder, Class clazz) {

        try {
            // 2、获取类对象所代表的类的所有方法
            Method[] methods = clazz.getDeclaredMethods();
            // 使用SortedMap类，通过key进行默认升序排序
            SortedMap<Integer, Method> rstMap = new TreeMap<>();

            // 3、处理method数组；找到被Order注解所注解的方法，获取注解里面的元数据
            for (Method method : methods) {
                if (method.isAnnotationPresent(Order.class)) {
                    Order order = method.getAnnotation(Order.class);
                    int num = order.value();
                    //System.out.println("num: " + num + "; method: " + method.getName());
                    rstMap.put(num, method);
                }
            }

            // 循环遍历排序后的方法
            for (Map.Entry<Integer, Method> entry : rstMap.entrySet()) {
                entry.getValue().invoke(runOrder);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
