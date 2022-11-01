package com.example.meetingmanagementapi.service;

import com.example.meetingmanagementapi.data.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @InjectMocks
    private ObjectMapper objectMapper;
    private MeetingService meetingService;
    private UserService userService;


    public void before() throws IOException {
        meetingService = new MeetingService(objectMapper);
        userService = new UserService(objectMapper);
    }

    @Test
    void testCreateMeeting() throws IOException {

        before();
        Person person = userService.getPerson("Julius Crawford");

        Meeting meeting = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID id = meetingService.createMeeting(meeting, person);

        assertTrue(meetingService.getMeetings().containsKey(id));

        meetingService.getMeetings().remove(id);
        meetingService.writeToFile();
    }

    @Test
    void testDeleteMeetingWhenMeetingExist() throws IOException {

        before();
        Person person = userService.getPerson("Julius Crawford");

        Meeting meeting = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID id = meetingService.createMeeting(meeting, person);
        meetingService.deleteMeeting(id, person);

        assertFalse(meetingService.getMeetings().containsKey(id));
    }

    @Test
    void testDeleteMeetingWhenMeetingDoesNotExist() throws IOException {

        before();
        Person person = userService.getPerson("Julius Crawford");

        UUID id = UUID.randomUUID();
        meetingService.deleteMeeting(id, person);

        assertFalse(meetingService.getMeetings().containsKey(id));
    }

    @Test
    void testDeleteMeetingWhenMeetingDoesNotHaveAuthorization() throws IOException {

        before();
        Person person = userService.getPerson("Julius Crawford");
        Person person1 = userService.getPerson("Charlotte Evans");

        Meeting meeting = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID id = meetingService.createMeeting(meeting, person);
        meetingService.deleteMeeting(id, person1);

        assertTrue(meetingService.getMeetings().containsKey(id));

        meetingService.deleteMeeting(id, person);
    }

    @Test
    void testAddParticipantToTheMeetingWhenParticipantIsAlreadyInTheMeeting() throws IOException {

        before();
        Person person = userService.getPerson("Julius Crawford");

        Meeting meeting = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID id = meetingService.createMeeting(meeting, person);

        String message = meetingService.addParticipantToTheMeeting(person, id);
        meetingService.deleteMeeting(id, person);

        assertEquals("Participant is added already" ,message);
    }

    @Test
    void testAddParticipantToTheMeetingWhenParticipantIsAlreadyInAnotherMeeting() throws IOException {

        before();
        Person person1 = userService.getPerson("Julius Crawford");
        Person person2 = userService.getPerson("Charlotte Evans");

        Meeting meeting1 = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        Meeting meeting2 = new Meeting("testMeeting2",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,20, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,50, 0)),
                Category.HUB,
                Type.LIVE);

        UUID meeting1Id = meetingService.createMeeting(meeting1, person1);
        UUID meeting2Id = meetingService.createMeeting(meeting2, person2);
        String message = meetingService.addParticipantToTheMeeting(person2, meeting1Id);

        meetingService.deleteMeeting(meeting1Id, person1);
        meetingService.deleteMeeting(meeting2Id, person2);

        assertEquals("Participant is added, but is in another meeting this time too",message);
    }

    @Test
    void testAddParticipantToTheMeetingWhenNewParticipantIsSuccessfullyAdded() throws IOException {

        before();
        Person person1 = userService.getPerson("Julius Crawford");
        Person person2 = userService.getPerson("Charlotte Evans");

        Meeting meeting1 = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID meeting1Id = meetingService.createMeeting(meeting1, person1);
        String message = meetingService.addParticipantToTheMeeting(person2, meeting1Id);

        meetingService.deleteMeeting(meeting1Id, person1);

        assertEquals("Participant added successfully",message);
    }

    @Test
    void testDeleteParticipantFromMeetingWhenParticipantIsOrganizer() throws IOException {
        before();
        Person person1 = userService.getPerson("Julius Crawford");

        Meeting meeting1 = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID meeting1Id = meetingService.createMeeting(meeting1, person1);

        String message = meetingService.deleteParticipantFromMeeting(person1, meeting1Id);
        meetingService.deleteMeeting(meeting1Id, person1);

        assertEquals("Participant is responsible person. Can't delete.",message);
    }

    @Test
    void testDeleteParticipantFromMeetingWhenParticipantIsDeleted() throws IOException {
        before();
        Person person1 = userService.getPerson("Julius Crawford");

        Meeting meeting1 = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID meeting1Id = meetingService.createMeeting(meeting1, person1);
        Person person2 = userService.getPerson("Charlotte Evans");
        meetingService.addParticipantToTheMeeting(person2, meeting1Id);

        String message = meetingService.deleteParticipantFromMeeting(person2, meeting1Id);
        meetingService.deleteMeeting(meeting1Id, person1);

        assertEquals("Participant deleted successfully.",message);
    }

    @Test
    void testSortedListOfMeetingsByDescriptionWhenThereIsValues () throws IOException {
        before();
        Person person1 = userService.getPerson("Julius Crawford");

        Meeting meeting1 = new Meeting("testMeeting",
                "description",
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,10, 0)),
                LocalDateTime.of(LocalDate.of(2020, 11, 1) , LocalTime.of(10,40, 0)),
                Category.HUB,
                Type.LIVE);

        UUID meeting1Id = meetingService.createMeeting(meeting1, person1);
        Map<UUID, Meeting> meetingMap = meetingService.sortedListOfMeetingsByDescription("description");
        meetingService.deleteMeeting(meeting1Id, person1);

        assertTrue(meetingMap.size() > 0);
    }
}