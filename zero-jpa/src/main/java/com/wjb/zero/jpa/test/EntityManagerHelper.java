package com.wjb.zero.jpa.test;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers; 

public class EntityManagerHelper {
	private static final EntityManagerFactory emf;   
	private static final ThreadLocal<EntityManager> threadLocal;  
	  
	static {  	
	   emf = Persistence.createEntityManagerFactory("test");    //该处名称与上面配置文件一致  
	   threadLocal = new ThreadLocal<EntityManager>();  
	}  
	    
	
	public static void main(String[] args) {
		String sqlText = "SELECT TABLESPACE_NAME," + 
				"  ROUND(TABLESPACE_SIZE/1024/1024/1024,2) \"TOTAL SPACE(G)\"," + 
				"  ROUND(FREE_SPACE/1024/1024/1024,2) AS \"FREE SPACE(G)\"" + 
				"  FROM DBA_TEMP_FREE_SPACE";
		Query query = getEntityManager().createNativeQuery(sqlText);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Object> list = query.getResultList();
		Map<String, Object> one = (Map<String, Object>) list.get(0);
		System.out.println(one.get("TABLESPACE_NAME")+":总量为"+one.get("TOTAL SPACE(G)")+"G;剩余为"+one.get("FREE SPACE(G)")+"G.");
		System.out.println("..............");
	}
	public static EntityManager getEntityManager() {  
	   EntityManager manager = threadLocal.get();    
	   if (manager == null || !manager.isOpen()) {  
	     
	    manager = emf.createEntityManager();  
	    System.out.println(manager);  
	    threadLocal.set(manager);  
	   }  
	   return manager;  
	}  
	  
	public static void closeEntityManager() {  
	        EntityManager em = threadLocal.get();  
	        threadLocal.set(null);  
	        if (em != null && em.isOpen()) {  
	          em.close();  
	        }  
	    }  
}
