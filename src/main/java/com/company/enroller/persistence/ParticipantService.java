package com.company.enroller.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.hibernate.PersistentObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpSession;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Optional<Participant> findByLogin(String login) {
		Session session = connector.getSession();

		Optional<Participant> participant = session.get(Participant.class, login) == null ?
				Optional.empty() : Optional.of(session.get(Participant.class, login));

		return participant;
	}

	public Collection<Participant> getAll() {
		String hql = "FROM Participant";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public boolean addParticipant(Participant participant) {
		Session session = connector.getSession();

		boolean success = false;
		if(this.findByLogin(participant.getLogin()).isEmpty()) {
			Transaction transaction = session.beginTransaction();
			session.persist(participant);
			transaction.commit();

			success = true;
		}

		return success;
	}

	public boolean deleteParticipant(Participant participant) {
		Session session = connector.getSession();

		boolean success = false;
		if(this.findByLogin(participant.getLogin()).isPresent()) {
			Transaction transaction = session.beginTransaction();
			session.delete(participant);
			transaction.commit();

			success = true;
		}

		return success;
	}

	public boolean updateParticipant(Participant participant) {
		Session session = connector.getSession();

		boolean success = false;

		if(this.findByLogin(participant.getLogin()).isPresent()) {
			Transaction transaction = session.beginTransaction();
			session.merge(participant);
			transaction.commit();
		}

		return success;
	}
}
