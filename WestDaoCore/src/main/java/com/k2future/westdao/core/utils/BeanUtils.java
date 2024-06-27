package com.k2future.westdao.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author West
 * @date create in 2019/9/3
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }else if (srcValue instanceof String && StringUtils.isBlank(srcValue.toString())){
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     *  复制资对象属性到目标对象
     *  并忽略空属性
     * @param source 资源对象
     * @param target 目标对象
     */
    public static void copyBeanIgnoreNull(Object source, Object target) {
        copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 根据属性名访问属性
     * @param fieldName 属性名
     * @param object 被访问对象
     * @return
     */
    public static String getFieldValueByFieldName(String fieldName,Object object){
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return  field.get(object).toString();
        } catch (Exception e) {
            return null;
        }
    }
}
