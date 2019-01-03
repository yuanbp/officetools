package com.chieftain.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.chieftain.excel.User;

/**
 * Java反射工具类
 * 
 * @author <a href="xdemo.org">xdemo.org</a>
 * 
 */
public class ReflectUtils {

	/**
	 * 根据成员变量名称获取其值
	 * 
	 * @param obj
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> Object getFieldValue(Object obj, String targetField) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {

		List<Field> list=getFields(obj.getClass(),true);
		for(Field field:list){
			if(field.getName().equals(targetField)){
				field.setAccessible(true);
				return field.get(obj);
			}
		}

		return null;
	}

	
	public static <T> List<Field> getFields(Class<T> clazz,boolean containSupperClass){
		
		List<Field> list=new ArrayList<Field>();
		
		Field[] fields = clazz.getDeclaredFields();
		
		for(Field field:fields){
			list.add(field);
		}
		if(containSupperClass){
			if(clazz.getSuperclass()!=null&&!clazz.getSuperclass().getSimpleName().equals(Object.class.getSimpleName())){
				list.addAll(getFields(clazz.getSuperclass(),containSupperClass));
			}
		}
		
		return list;
	}
	
	public static <T> List<Method> getMethods(Class<T> clazz,boolean containSupperClass){
		
		List<Method> list=new ArrayList<Method>();
		
		Method[] ms = clazz.getDeclaredMethods();
		
		for(Method m:ms){
			list.add(m);
		}
		if(containSupperClass){
			if(clazz.getSuperclass()!=null&&!clazz.getSuperclass().getSimpleName().equals(Object.class.getSimpleName())){
				list.addAll(getMethods(clazz.getSuperclass(),containSupperClass));
			}
		}
		
		return list;
	}




	/**
	 * 调用对象的无参方法
	 * 
	 * @param instance
	 * @param method
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static <T> Object invoke(Object instance, String method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Method m = instance.getClass().getMethod(method, new Class[] {});
		return m.invoke(instance, new Object[] {});
	}


	/**
	 * 通过类的实例，调用指定的方法
	 * 
	 * @param instance
	 * @param method
	 * @param paramClasses
	 * @param params
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static <T> Object invoke(Object instance, String method, Class<T>[] paramClasses, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Method _m = instance.getClass().getMethod(method, paramClasses);
		return _m.invoke(instance, params);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// getFields(User.class);
		User u = new User();
		invoke(u, "setName", new Class[] { String.class }, new Object[] { "xx发大水法大水法x" });
		System.out.println(getFieldValue(u, "name"));
	}

}
