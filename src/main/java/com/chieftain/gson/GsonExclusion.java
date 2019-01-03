package com.chieftain.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Gson 字段排除
 * @author Goofy
 *
 */
public class GsonExclusion implements ExclusionStrategy {
	
	public String fields[];
	

	public GsonExclusion(String[] fields) {
		this.fields = fields;
	}

	@Override
    public boolean shouldSkipField(FieldAttributes f) {
		for(String s:fields){
			if(s.equalsIgnoreCase(f.getName())) {
                return true;
            }
		}
		return false;
	}
	
	

	@Override
    public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

}
