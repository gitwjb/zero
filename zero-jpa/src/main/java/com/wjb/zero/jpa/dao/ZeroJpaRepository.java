/*
 * Copyright (c) 2012-2032 ACCA.
 * All Rights Reserved.
 */
package com.wjb.zero.jpa.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @version Zero v1.0
 * @author 
 */
@NoRepositoryBean
public interface ZeroJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public boolean batchSave(List<T> entities);
	
	public boolean batchUpdate(List<T> entities);
	
	public int sqlExecute(String sqlText);
	
	public List<Object> sqlQuery(String sqlText);
	public Query  sqlQuerys(String sqlText);
	
	public int jpqlExecute(String jpqlText);
	
	public List<T> jpqlQuery(String jpqlText);
	public EntityManager getEntityManager();
}
