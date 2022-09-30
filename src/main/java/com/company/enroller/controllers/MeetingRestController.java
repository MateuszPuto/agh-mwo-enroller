package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> allMeetings = meetingService.getAll();

        return new ResponseEntity<Collection<Meeting>>(allMeetings, HttpStatus.OK);
    }

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public ResponseEntity getMeeting(@PathVariable("id") long id) {
        Optional<Meeting> meeting = meetingService.findById(id);

        ResponseEntity response;
        if(meeting.isEmpty()) {
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            response = new ResponseEntity(meeting.get(), HttpStatus.OK);
        }

        return response;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity addMeeting(@RequestBody Meeting meeting) {
        boolean success = meetingService.createMeeting(meeting);

        ResponseEntity response;
        if(!success) {
            response = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response = new ResponseEntity(HttpStatus.OK);
        }

        return response;
    }

    @RequestMapping(value = "/add", method = RequestMethod.PATCH)
    public ResponseEntity addParticipantToMeeting(@RequestParam(name = "id") long id, @RequestParam(name = "login") String login) {
        Meeting meeting = meetingService.findById(id).get();
        meeting.addParticipant(participantService.findByLogin(login).get());

        boolean success = meetingService.updateMeeting(meeting);

        ResponseEntity response;
        if(!success) {
            response = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response = new ResponseEntity(HttpStatus.OK);
        }

        return response;
    }

    @RequestMapping(value = "/get-participants", method = RequestMethod.GET)
    public ResponseEntity getMeetingParticipants(@RequestParam("id") long id) {
        Meeting meeting = meetingService.findById(id).get();

        return new ResponseEntity(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMeeting(@PathVariable("id") long id) {
        boolean success = meetingService.deleteMeeting(meetingService.findById(id).get());

        ResponseEntity response;
        if(!success) {
            response = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response = new ResponseEntity(HttpStatus.OK);
        }

        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateMeeting(@PathVariable long id, @RequestBody Meeting meeting) {
        ResponseEntity response;
        if(id != meeting.getId()) {
            response = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        boolean success = meetingService.updateMeeting(meeting);

        if(!success) {
            response = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response = new ResponseEntity(HttpStatus.OK);
        }

        return response;
    }

    @RequestMapping(value = "/delete-participant/{id}", method = RequestMethod.DELETE)
    public void deleteParticipant(@PathVariable long id, @RequestParam String participantLogin) {
        meetingService.findById(id).get().removeParticipant(participantService.findByLogin(participantLogin).get());
    }
}
