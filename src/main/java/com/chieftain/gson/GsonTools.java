/**
 * 上海蓝鸟集团
 * 上海蓝鸟科技股份有限公司
 * 华东工程中心（无锡）
 * 2015版权所有
 */
package com.chieftain.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson工具类
 * @author Goofy
 */
public class GsonTools {
	

	private boolean serializeNulls = true;
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private GsonExclusion exclusion;
	private GsonKeyRename keyRename;

	/*public String toJson(Object object) {
		return getGson().toJson(object);
	}*/

	/**
	 * 序列号JSON，默认日期格式yyyy-MM-dd HH:mm:ss,如果需要自定义参数，使用
	 * @param object
	 * @return
	 */
	public static String toJson(Object object) {
		return new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(object);
	}
	
	/**
	 * 使用自定义参数
	 * @param object
	 * @return
	 */
	public String toJson2(Object object) {
		return getGson().toJson(object);
	}
	
	public GsonTools serializeNulls(boolean serializeNulls){
		this.serializeNulls=serializeNulls;
		return this;
	}
	
	public GsonTools dateFormat(String dateFormat){
		this.dateFormat=dateFormat;
		return this;
	}
	
	public GsonTools exclusion(GsonExclusion exclusion){
		this.exclusion=exclusion;
		return this;
	}
	
	public GsonTools keyRename(GsonKeyRename keyRename){
		this.keyRename=keyRename;
		return this;
	}

	private Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		if (serializeNulls) {
			builder.serializeNulls();
		}
		
		if(dateFormat!=null){
			builder.setDateFormat(dateFormat);
		}
		
		if (exclusion != null) {
			builder.setExclusionStrategies(exclusion);
		}
		if (keyRename != null) {
			builder.setFieldNamingStrategy(keyRename);
		}
		return builder.create();
	}

	public GsonTools() {
	}

}
