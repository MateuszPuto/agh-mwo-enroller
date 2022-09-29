package com.company.enroller.persistence;

import java.util.Collection;
import java.util.Optional;

import com.company.enroller.model.Participant;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Optional<Meeting> findById(long id) {
		Session session = connector.getSession();

		Optional<Meeting> meeting = session.get(Meeting.class, id) == null ?
				Optional.empty() : Optional.of(session.get(Meeting.class, id));

		return meeting;
	}

	public boolean createMeeting(Meeting meeting) {
		Session session = connector.getSession();

		boolean success = false;
		if(this.findById(meeting.getId()).isEmpty()) {
			Transaction transaction = session.beginTransaction();
			session.save(meeting);
			transaction.commit();

			success = true;
		}

		return success;
	}

	public boolean updateMeeting(Meeting meeting) {
		Session session = connector.getSession();

		boolean success = false;

		if(this.findById(meeting.getId()).isPresent()) {
			Transaction transaction = session.beginTransaction();
			session.merge(meeting);
			transaction.commit();
		}

		return success;
	}
}
