package com.jeet.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.jeet.api.Movie;
import com.jeet.api.Screen;
import com.jeet.api.Ticket;

public class DAO {

	private static DAO instance;
	private SessionFactory factory;

	private DAO() {
		Configuration conf = new Configuration();
		conf.configure();
		ServiceRegistry registry = new ServiceRegistryBuilder().applySettings(
				conf.getProperties()).buildServiceRegistry();
		factory = conf.buildSessionFactory(registry);
		System.out.println("Creating factory");
	}

	public static synchronized DAO instance() {
		if (instance == null) {
			instance = new DAO();
		}
		return instance;
	}
	
	public Screen getScreen(Movie movie){
		Session session = factory.openSession();
		String hql = "from Screen where movie_movieId = :mId";
		Query query = session.createQuery(hql);
		query.setParameter("mId", movie.getMovieId());
		List<Screen> list = query.list();
		session.close();
		return list.get(0);
	}
	
	public Movie getMovie(String movieName){
		Session session = factory.openSession();
		String hql = "from Movie where name = :mName";
		Query query = session.createQuery(hql);
		query.setParameter("mName", movieName);
		List<Movie> list = query.list();
		return list.get(0);
	}
	
	public Ticket getTicket(String screenId, String seatId){
		Session session = factory.openSession();
		String hql = "from Ticket where screen_screenId=:screenId and seat_seatNum=:seatId";
		Query query = session.createQuery(hql);
		query.setParameter("screenId", screenId);
		query.setParameter("seatId", seatId);
		List<Ticket> list = query.list();
		if(list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	public Ticket bookTicket(Ticket ticket){
		Session session = factory.openSession();
		System.out.println("DAO.bookTicket()");
		session.beginTransaction();
		session.save(ticket);
		session.getTransaction().commit();
		return ticket;
	}
	
	public void cancelTicket(int ticketId){
		System.out.println(ticketId);
		Session session = factory.openSession();
		Transaction trx = session.beginTransaction();
		Ticket tic = (Ticket)session.load(Ticket.class, ticketId);
		session.delete(tic);
		trx.commit();
		session.close();
	}

}
