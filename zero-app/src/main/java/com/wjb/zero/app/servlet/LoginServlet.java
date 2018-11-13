package com.wjb.zero.app.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.wjb.zero.app.entity.User;


public class LoginServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7164124537596014143L;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class); 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
		logger.info("start ...........");
		System.out.println("wjb   login  on  success  !!!!!");
		
		logger.info("end ...........");
	}
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		System.out.println("wjb   login  on  success  !!!!!");
    }
    
    private void test(){
    	ServletContext servletContext = this.getServletContext();    
        WebApplicationContext ctx =    WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);  
        User user = (User)ctx.getBean("user");  
        user.setName("张三");
        System.out.println(user.getName());
    }
	
}
