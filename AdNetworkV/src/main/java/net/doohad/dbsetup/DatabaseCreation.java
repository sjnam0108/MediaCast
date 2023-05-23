package net.doohad.dbsetup;

import net.doohad.utils.HibernateUtil;

public class DatabaseCreation {
	/**
	 * 대상 데이터베이스 스키마 생성
	 */
	public void createDatabaseSchema() {
		HibernateUtil.initSchema();
	}
	
	/**
	 * Main Method
	 */
	public static void main(String[] args) {
		new DatabaseCreation().createDatabaseSchema();
		
		System.out.println("--------------------------------------------------");
		System.out.println("creation finished!!");
	}
}
