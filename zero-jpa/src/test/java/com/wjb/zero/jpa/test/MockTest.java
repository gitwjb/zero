package com.wjb.zero.jpa.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-base.xml"})
//@ContextConfiguration(classes=AppConfig.class)
public class MockTest {
	
	@Autowired
	TestBean testBean;
	
	MockMvc mockMvc;
	
	@Test
	public void test() {
//		ResultActions reaction=this.mockMvc.perform(MockMvcRequestBuilders.get("/service/test/testController")
//				.accept(MediaType.APPLICATION_JSON)//返回值接收json
//				.header("Timestamp", "1496656373783")
//				.header("AppId", "1003"));
//		reaction.andExpect(MockMvcResultMatchers.status().isOk());
//		MvcResult mvcResult =reaction.andReturn();
//		System.out.println(mvcResult.getResponse().getContentAsString());
	}
}
