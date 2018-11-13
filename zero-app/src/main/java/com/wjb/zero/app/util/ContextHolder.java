package com.wjb.zero.app.util;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ContextHolder {
	private WebApplicationContext ctx;
	public WebApplicationContext getContext(Object xx){
		if(ctx==null){			
//		  ServletContext servletContext = xx.getServletContext();    
//        WebApplicationContext ctx =    WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext); 
		}
		return ctx;
	}
}
