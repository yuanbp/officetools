package com.chieftain.utils;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * <p>
 * org.mh.framework.utils [workspace_idea_01]
 * Created by Richard on 15:20
 */
public class BeanValidUtil {

    private static Validator validator;

    public static Validator getSimpleValidator(){
        if(null == validator){
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        return validator;
    }

    public static String validateModelFast(Validator validator,Object obj){
        StringBuffer buffer = new StringBuffer(64);
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        Iterator<ConstraintViolation<Object>> iter = constraintViolations.iterator();
        while (iter.hasNext()) {
            String message = iter.next().getMessage();
            buffer.append(message);
        }
        return buffer.toString();
    }

    public static String validateModel(Object obj) { //验证某一个对象

        StringBuffer buffer = new StringBuffer(64); //用于存储验证后的错误信息

        Validator validator = getSimpleValidator();

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);//验证某个对象,，其实也可以只验证其中的某一个属性的

        Iterator<ConstraintViolation<Object>> iter = constraintViolations.iterator();
        while (iter.hasNext()) {
            String message = iter.next().getMessage();
            buffer.append(message);
        }
        return buffer.toString();
    }

    public static String validateModelNew(Object obj) { //验证某一个对象

        StringBuffer buffer = new StringBuffer(64); //用于存储验证后的错误信息

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);//验证某个对象,，其实也可以只验证其中的某一个属性的

        Iterator<ConstraintViolation<Object>> iter = constraintViolations.iterator();
        while (iter.hasNext()) {
            String message = iter.next().getMessage();
            buffer.append(message+"|||");
        }
        return buffer.toString();
    }

    public String validateModelNstatic(Object obj) { //验证某一个对象

        StringBuffer buffer = new StringBuffer(64); //用于存储验证后的错误信息

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);//验证某个对象,，其实也可以只验证其中的某一个属性的

        Iterator<ConstraintViolation<Object>> iter = constraintViolations.iterator();
        while (iter.hasNext()) {
            String message = iter.next().getMessage();
            buffer.append(message);
        }
        return buffer.toString();
    }
}
