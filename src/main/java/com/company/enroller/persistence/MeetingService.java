package com.company.enroller.persistence;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public boolean deleteMeeting(Meeting meeting) {
		Session session = connector.getSession();

		boolean success = false;
		if(this.findById(meeting.getId()).isPresent()) {
			Transaction transaction = session.beginTransaction();
			session.delete(meeting);
			transaction.commit();

			success = true;
		}

		return success;
	}

	public Collection<Meeting> sortedMeetings() {
		List<Meeting> meetings = (List) this.getAll();
		meetings.sort((a, b) -> a.getTitle().compareTo(b.getTitle()));

		return meetings;
	}

	public Collection<Meeting> findMeetings(String query){
		Stream res1 = this.getAll().stream().filter((x) -> x.getTitle().equals(query));
		Stream res2 = this.getAll().stream().filter((x) -> {
			boolean match = false;
			String[] descWords = x.getDescription().split(" ");
			for(String word: descWords) {
				if(word.equals(query)) {
					match = true;
				}
			}

			return match;
		});

		return Stream.concat(res1, res2).toList();
	}

	public Collection<Meeting> findMeetingsWithParticipant(Participant participant) {
		return this.getAll().stream().filter((x) -> {
			boolean found = false;
			for(Participant p: x.getParticipants()) {
				if(p.equals(participant)) {
					found = true;
				}
			}

			return found;
		}).collect(Collectors.toList());
	}
}
