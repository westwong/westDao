package cn.k2future.westdao.core.utils;

import cn.k2future.westdao.core.wsql.unit.WFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LambdaUtils {

    private static final Map<WFunction<?, ?>, String> propertyNameCache = new ConcurrentHashMap<>();

    /**
     * 根据方法引用获取属性名，带缓存
     *
     * @param propertyGetter 方法引用
     * @param <T>            对象类型
     * @return 属性名
     */
    public static <T> String getPropertyNameWithCache(WFunction<T, ?> propertyGetter) {
        return propertyNameCache.computeIfAbsent(propertyGetter, pg -> {
            try {
                Method method = pg.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                SerializedLambda serializedLambda = (SerializedLambda) method.invoke(pg);
                String methodName = serializedLambda.getImplMethodName();
                String propertyName = "";
                if (methodName.startsWith("get")) {
                    propertyName = methodName.substring(3);
                } else if (methodName.startsWith("is")) {
                    propertyName = methodName.substring(2);
                }
                if (!propertyName.isEmpty()) {
                    propertyName = Character.toLowerCase(propertyName.charAt(0)) + (propertyName.length() > 1 ? propertyName.substring(1) : "");
                }
                return propertyName;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * 从 SFunction 获取关联的类
     *
     * @param propertyGetter 方法引用
     * @param <T>            对象类型
     * @return 类对象
     */
    public static <T> Class<T> getClassFromFunction(WFunction<T, ?> propertyGetter) {
        try {
            Method method = propertyGetter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(propertyGetter);
            String className = serializedLambda.getImplClass().replace("/", ".");
            return (Class<T>) Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get entity class from function", e);
        }
    }
}
