package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.PersistentObjectException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

import javax.servlet.http.HttpSession;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Participant findByLogin(String login) {
		Session session = connector.getSession();
		Participant participant = session.get(Participant.class, login);
		session.close();

		return participant;
	}

	public Collection<Participant> getAll() {
		String hql = "FROM Participant";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public boolean addParticipant(Participant participant) {
		Session session = connector.getSession();

		try {
			session.persist(participant);

		} catch (PersistentObjectException e) {
			return false;
		}

		session.close();

		return true;
	}

}
