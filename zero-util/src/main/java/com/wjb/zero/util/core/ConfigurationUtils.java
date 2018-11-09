/*
 * Copyright (c) 2012-2032 ACCA.
 * All Rights Reserved.
 */
package com.wjb.zero.util.core;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * @version Zero v1.0
 * @author 
 */
public class ConfigurationUtils {

	private static Configurations configs;
	
	public static String getStringValueFromProperties(String fileName, String key){
		if(configs == null){
			configs = new Configurations();
		}
		try{
		    Configuration config = configs.properties(new File(fileName));
		    // access configuration properties
		    String value = config.getString(key);
		    return value;
		}
		catch (ConfigurationException cex){
		    // Something went wrong
			return null;
		}
		
	}
	
	public static String getStringValueFromProperties(String key){
		return getStringValueFromProperties("/config.properties", key);
	}
}
