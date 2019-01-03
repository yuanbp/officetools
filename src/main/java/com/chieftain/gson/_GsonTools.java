package com.chieftain.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson工具类
 * @author Goofy
 */
@Deprecated
public class _GsonTools {

	private boolean serializeNulls = true;
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private GsonExclusion exclusion;
	private GsonKeyRename keyRename;

	public String toJson(Object object) {
		return getGson().toJson(object);
	}

	/**
	 * 序列号JSON，默认日期格式yyyy-MM-dd HH:mm:ss,如果需要自定义参数，使用
	 * @param object
	 * @return
	 */
	public static String toJson2(Object object) {
		return new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(object);
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

	public _GsonTools() {

	}

	public _GsonTools(GsonExclusion exclusion, GsonKeyRename keyRename) {
		this.exclusion = exclusion;
		this.keyRename = keyRename;
	}

	public _GsonTools(boolean serializeNulls, String dateFormat, GsonExclusion exclusion, GsonKeyRename keyRename) {
		this.serializeNulls = serializeNulls;
		this.dateFormat = dateFormat;
		this.exclusion = exclusion;
		this.keyRename = keyRename;
	}

	public boolean isSerializeNulls() {
		return serializeNulls;
	}

	public void setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public GsonExclusion getExclusion() {
		return exclusion;
	}

	public void setExclusion(GsonExclusion exclusion) {
		this.exclusion = exclusion;
	}

	public GsonKeyRename getKeyRename() {
		return keyRename;
	}

	public void setKeyRename(GsonKeyRename keyRename) {
		this.keyRename = keyRename;
	}

}
