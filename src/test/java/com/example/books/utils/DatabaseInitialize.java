package com.example.books.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseInitialize implements InitializingBean {

    @PersistenceContext
    private EntityManager em;

    private List<String> tables;


    @Override
    public void afterPropertiesSet()  {
        try {
            tables = em.getMetamodel().getEntities().stream()
                    .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                    .map(e -> {
                        if (e.getJavaType().getAnnotation(Table.class) != null){
                            return e.getJavaType().getAnnotation(Table.class).name();
                        }
                        return camelToLowerUnderscore(e.getName());
                    })
                    .collect(Collectors.toList());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    public void execute(){
        em.flush();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tables) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            em.createNativeQuery("ALTER TABLE IF EXISTS " + tableName + " ALTER COLUMN ID RESTART WITH 1")
                    .executeUpdate();
        }

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String camelToLowerUnderscore(String s){
        String regex = "([a-z])([A-Z]+)";
        String replace = "$1_$2";
        return s.replaceAll(regex, replace).toLowerCase();
    }
}
