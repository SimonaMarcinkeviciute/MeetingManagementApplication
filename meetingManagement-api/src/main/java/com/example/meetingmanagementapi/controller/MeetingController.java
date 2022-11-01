package com.example.meetingmanagementapi.controller;

import com.example.meetingmanagementapi.data.Category;
import com.example.meetingmanagementapi.data.Meeting;
import com.example.meetingmanagementapi.data.Person;
import com.example.meetingmanagementapi.data.Type;
import com.example.meetingmanagementapi.service.MeetingService;
import com.example.meetingmanagementapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.example.meetingmanagementapi.ApplicationPath.*;

@RestController
@RequestMapping(MEETINGS)
public class MeetingController {

    private final MeetingService meetingService;
    private final UserService userService;


    public MeetingController(MeetingService meetingService, UserService userService) {
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, Meeting> getMeetings() throws IOException {

        return meetingService.getMeetings();
    }

    @PostMapping(value = CREATE_MEETING, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createMeeting(@RequestBody Meeting meeting, @PathVariable(userName) String user) throws IOException {

        Person person = userService.getPerson(user);

        if (person != null) {
            meetingService.createMeeting(meeting, person);
        } else {

            return "User not found";
        }

        return "Meeting created successfully";
    }

    @DeleteMapping(value = DELETE_MEETING)
    public String deleteMeeting(@PathVariable(meetingId) UUID id, @PathVariable(userName) String user) throws IOException {

        Person person = userService.getPerson(user);

        if (person != null) {

            return meetingService.deleteMeeting(id, person);
        } else {

            return "User not found";
        }
    }

    @GetMapping(value = ADD_PARTICIPANT)
    public String addParticipantToTheMeeting(@PathVariable(meetingId) UUID id, @PathVariable(userName) String user) throws IOException {
        Person person = userService.getPerson(user);

        if (person != null) {

            return meetingService.addParticipantToTheMeeting(person, id);
        } else {

            return "User not found";
        }
    }

    @DeleteMapping(value = DELETE_PERSON)
    public String deletePerson(@PathVariable(meetingId) UUID id, @PathVariable(userName) String user) throws IOException {

        Person person = userService.getPerson(user);

        if (person != null) {

            return meetingService.deleteParticipantFromMeeting(person, id);
        } else {

            return "User not found";
        }
    }

    @GetMapping(value = SEARCH_BY_DESCRIPTION, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, Meeting> sortedListOfMeetingsByDescription(@PathVariable(description) String value) throws IOException {

        return meetingService.sortedListOfMeetingsByDescription(value);
    }

    @GetMapping(value = SEARCH_BY_PERSON, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, Meeting> sortedListOfMeetingsByResponsiblePerson(@PathVariable(userName) String user) throws IOException {

        Person person = userService.getPerson(user);

        return meetingService.sortedListOfMeetingsByResponsiblePerson(person);
    }

    @GetMapping(value = SEARCH_BY_CATEGORY, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, Meeting> sortedListOfMeetingsByCategory(@PathVariable(category) Category categoryValue) throws IOException {

        return meetingService.sortedListOfMeetingsByCategory(categoryValue);
    }

    @GetMapping(value = SEARCH_BY_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, Meeting> sortedListOfMeetingsByType(@PathVariable(type) Type typeValue) throws IOException {

        return meetingService.sortedListOfMeetingsByType(typeValue);
    }

    @GetMapping(value = SEARCH_IS_AFTER_DATE)
    public Map<UUID, Meeting> sortedListOfMeetingsIsAfterDate(@PathVariable(startDate) String localDateTime) throws IOException {

        return meetingService.sortedListOfMeetingsIsAfterDate(localDateTime);
    }

    @GetMapping(value = SEARCH_IS_BETWEEN_DATE)
    public Map<UUID, Meeting> sortedListOfMeetingsIsBetween(@PathVariable(startDate) String start, @PathVariable(endDate) String end) throws IOException {

        return meetingService.sortedListOfMeetingsIsBetween(start, end);
    }

    @GetMapping(value = SEARCH_BY_PARTICIPANT_NUMBER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, Meeting> sortedListOfMeetingsByParticipantsNumber(@PathVariable(number) int numb) throws IOException {

        return meetingService.sortedListOfMeetingsByParticipantNumber(numb);
    }
}
