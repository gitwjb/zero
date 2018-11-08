/*
 * Copyright (c) 2012-2032 ACCA.
 * All Rights Reserved.
 */
package com.wjb.zero.jpa.dao;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * @version Zero v1.0
 * @author 
 */
public class ZeroJpaRepositoryFactory extends JpaRepositoryFactory{

	public ZeroJpaRepositoryFactory(EntityManager entityManager) {   
        super(entityManager);   
    }   
    
    @Override  
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {   
        return ZeroJpaRepositoryImpl.class;   
    }
}
