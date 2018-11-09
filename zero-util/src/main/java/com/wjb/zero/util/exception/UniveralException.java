package com.wjb.zero.util.exception;

public class UniveralException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	private static String msg = "请联系管理员呢.";
	
	public UniveralException() {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	
}
