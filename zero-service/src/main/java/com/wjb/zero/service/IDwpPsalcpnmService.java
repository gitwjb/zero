package com.wjb.zero.service;

import com.wjb.zero.util.entity.ResultInfo;

/**
 * @description 销售基库服务接口
 * @author wjb
 */
public interface IDwpPsalcpnmService {
	
	/**
	 * @description  处理指定日期数据
	 * @param sDate  开始日期
	 * @param eDate  截止日期
	 * @param userId 用户ID
	 * @author wjb
	 */
	public ResultInfo processDwpPsalcpnm(String sDate,String eDate,String userId);
}
