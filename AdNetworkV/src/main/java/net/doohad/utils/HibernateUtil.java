package net.doohad.utils;

import java.util.EnumSet;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

public class HibernateUtil {
	private static SessionFactory sessionFactory = configureSessionFactory();
	
	/**
	 * Hibernate 세션 팩토리 구성
	 */
	private static SessionFactory configureSessionFactory() {
		SessionFactory sf = null;
		try {
			/*
			Configuration configuration = new Configuration();
			configuration.configure();
			
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sf = configuration.buildSessionFactory(serviceRegistry);
			//sf = new Configuration().configure().b .buildSessionFactory();
			*/
			
            StandardServiceRegistry standardRegistry  = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
            
            Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();
            sf = metadata.getSessionFactoryBuilder().build();

		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
		
		return sf;
	}
	
	/**
	 * Hibernate 세션 팩토리 획득
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * Hibernate 세션 팩토리 닫기
	 */
	public static void shutdown() {
		getSessionFactory().close();
	}
	
	/**
	 * Hibernate 모델 구성 정보를 바탕으로 데이터베이스 개체 초기화
	 */
	public static void initSchema() {
		/*
		Configuration cfg = new Configuration().configure();
		SchemaExport schemaExport = new SchemaExport(cfg);
		
		schemaExport.create(true, true);
		*/
		
        Configuration cfg = new Configuration().configure();
        
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        MetadataSources metadata = new MetadataSources(serviceRegistry);
        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.execute(enumSet, org.hibernate.tool.hbm2ddl.SchemaExport.Action.BOTH, metadata.buildMetadata());

	}
}
