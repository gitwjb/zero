/*
 * Copyright (c) 2012-2032 ACCA.
 * All Rights Reserved.
 */
package com.wjb.zero.jpa.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * @version Zero v1.0
 * @author 
 */
public class ZeroJpaRepositoryFactoryBean <T extends JpaRepository<Object, Serializable>> extends JpaRepositoryFactoryBean<T, Object, Serializable> {

	@Override  
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {   
  
        return new ZeroJpaRepositoryFactory(em);   
    }

	@Override
	@PersistenceContext(unitName = "default")
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}
}
