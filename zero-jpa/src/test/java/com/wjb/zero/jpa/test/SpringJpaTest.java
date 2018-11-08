package com.wjb.zero.jpa.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
//@ContextConfiguration(classes=AppConfig.class)
public class SpringJpaTest {
	
	@Autowired
	TestBean testBean;
	
	@Test
	public void test() {
		System.out.println(testBean.toString());
	}
}
