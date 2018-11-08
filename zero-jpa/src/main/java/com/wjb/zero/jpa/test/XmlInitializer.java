package com.wjb.zero.jpa.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description  xml 方式加载bean
 * @author wjb
 */
public class XmlInitializer {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-base.xml");
		System.out.println("加载....");
		
		((ConfigurableApplicationContext)context).close();
	}
}
