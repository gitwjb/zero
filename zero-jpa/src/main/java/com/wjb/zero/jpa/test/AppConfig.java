package com.wjb.zero.jpa.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {
	
	@Bean
	@Primary
	public TestBean getFirstBean() {
		return new TestBean();
	}
	
	@Bean
	public TestBean getSecondBean() {
		return new TestBean();
	}
}
