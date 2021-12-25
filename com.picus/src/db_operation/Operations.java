package db_operation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import entity_classes.Message;

public class Operations {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.picus");

	public static Boolean insertMessage(Message message) 
	{
		Boolean result = true;

		EntityManager em = emf.createEntityManager();
		EntityTransaction et = null;
		
		try 
		{
			et = em.getTransaction();
			et.begin();
			
			em.persist(message);
			et.commit();
		} catch (Exception ex) 
		{
			if (et != null) {
				et.rollback();
			}
			ex.printStackTrace();
			result = false;
		} finally 
		{
			em.close();
		}
		
		return result;
	}
	
	// Query last X messages
	public static List<Message> getLastXMessage(String nickname, int X)
	{
		
		EntityManager em = emf.createEntityManager();

		List<Message> results = null;

		try 
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			
			CriteriaQuery<Message> cq = cb.createQuery(Message.class);
			
			Root<Message> rootEntry = cq.from(Message.class);
			
			Predicate predicate1 = cb.like(rootEntry.get("fromClient"), nickname);
			Predicate predicate2 = cb.like(rootEntry.get("toClient"), nickname);
			
			CriteriaQuery<Message> all = cq.select(rootEntry)
					.where(cb.or(predicate1, predicate2))
					.orderBy(cb.desc(rootEntry.get("messageId")));
			
			TypedQuery<Message> allQuery = em.createQuery(all).setMaxResults(X);
			
			results = allQuery.getResultList();
		
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			
			results = null;
		
		} finally 
		{
			em.close();
		}
		
		return results;
	}
	
	// Query messages that contain some text
	// "nickname" is required for security (each client can show own messages)
	public static List<Message> getMessagesContainText(String nickname, String someText)
	{
		EntityManager em = emf.createEntityManager();

		List<Message> results = null;

		try 
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			
			CriteriaQuery<Message> cq = cb.createQuery(Message.class);
			
			Root<Message> rootEntry = cq.from(Message.class);
			
			Predicate predicate1 = cb.like(rootEntry.get("fromClient"), nickname);
			Predicate predicate2 = cb.like(rootEntry.get("toClient"), nickname);
			Predicate predicate3 = cb.like(rootEntry.get("msg"), "%" + someText + "%");
			
			CriteriaQuery<Message> all = cq.select(rootEntry)
					.where(cb.and((cb.or(predicate1, predicate2)), predicate3));
			
			TypedQuery<Message> allQuery = em.createQuery(all);
			
			results = allQuery.getResultList();
		
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			
			results = null;
		
		} finally 
		{
			em.close();
		}
		
		return results;
	}
	
	// Query according to message direction (send by me or to me)
	// direction -> "fromClient" | "toClient"
	public static List<Message> getMessagesAccordingDirection(String nickname, String direction)
	{
		EntityManager em = emf.createEntityManager();

		List<Message> results = null;

		try 
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			
			CriteriaQuery<Message> cq = cb.createQuery(Message.class);
			
			Root<Message> rootEntry = cq.from(Message.class);
			
			CriteriaQuery<Message> all = cq.select(rootEntry)
					.where(cb.like(rootEntry.get(direction), nickname));
			
			TypedQuery<Message> allQuery = em.createQuery(all);
			
			results = allQuery.getResultList();
		
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			
			results = null;
		
		} finally 
		{
			em.close();
		}
		
		return results;
	}
}
