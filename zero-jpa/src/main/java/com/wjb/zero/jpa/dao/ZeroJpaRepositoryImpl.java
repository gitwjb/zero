/*
 * Copyright (c) 2012-2032 ACCA.
 * All Rights Reserved.
 */
package com.wjb.zero.jpa.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @version Zero v1.0
 * @author 
 */
public class ZeroJpaRepositoryImpl <T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ZeroJpaRepository<T, ID> {

	private final EntityManager entityManager;
	protected EntityManager getEm() {
		return entityManager;
	}
	public ZeroJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		entityManager = em;
	}

	public ZeroJpaRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation, final EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public boolean batchSave(List<T> entities) {
		List<T> result = new ArrayList<T>();
		if (entities == null) {
			return false;
		}
		for (int i = 0; i < entities.size(); i++) {
			T entity = entities.get(i);
			entityManager.persist(entity);
			if (i % 50 == 0) { 
				entityManager.flush();
				entityManager.clear();
			}
			result.add(entity);
		}
		if(null!=result&&result.size()>0){
			return true;
		}else{
			return false;
		}
	
	}

	@Override
	@Transactional
	public boolean batchUpdate(List<T> entities) {
		List<T> result = new ArrayList<T>();
		if (entities == null) {
			return false;
		}
		for (int i = 0; i < entities.size(); i++) {
			T entity = entities.get(i);
			entityManager.merge(entity);
			if (i % 50 == 0) { // 20, same as the JDBC batch size
				//flush a batch of updates and release memory: 
				entityManager.flush();
				entityManager.clear();
			}
			result.add(entity);
		}
		if(null!=result&&result.size()>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	@Transactional
	public int sqlExecute(String sqlText) {
	    int result = -1;
		Query query = entityManager.createNativeQuery(sqlText);
		result = query.executeUpdate();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> sqlQuery(String sqlText) {
		List<Object> entityList = new ArrayList<Object>();
			Query query = entityManager.createNativeQuery(sqlText);
			entityList = query.getResultList();
		return entityList;
	}

	@Override
	@Transactional
	public int jpqlExecute(String jpqlText) {
		int result = -1;
		Query query = entityManager.createQuery(jpqlText);
		result = query.executeUpdate();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> jpqlQuery(String jpqlText) {
		List<T> entityList = new ArrayList<T>();
			Query query = entityManager.createQuery(jpqlText);
			entityList = query.getResultList();
		return entityList;
	}
	@Override
	public Query sqlQuerys(String sqlText) {
		Query query = getEm().createNativeQuery(sqlText);
		//query.setParameter(3, "201704");
		return query;
	}
	@Override
	public EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return entityManager;
	}
}
