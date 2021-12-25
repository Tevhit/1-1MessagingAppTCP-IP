package db_operation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class CreateTables {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.picus");
	
	public static void main(String[] args) {
		
		EntityManager em = emf.createEntityManager();

		EntityTransaction et = null;

		try {
			et = em.getTransaction();
			et.begin();
			et.commit();
			System.out.println("All tables have created successfully.");
		} catch (Exception ex) {
			if (et != null) {
				et.rollback();
			}
			ex.printStackTrace();
			System.out.println("Error");
		} finally {
			em.close();
		}


	}

}
