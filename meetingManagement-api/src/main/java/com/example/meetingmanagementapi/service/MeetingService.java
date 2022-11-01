package com.example.meetingmanagementapi.service;

import com.example.meetingmanagementapi.data.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final ObjectMapper objectMapper;
    private final Map<UUID, Meeting> meetings;
    private final File file;

    public MeetingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        file = new File("meetings.json");
        objectMapper.registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        meetings = readFromFile();
    }

    public UUID createMeeting(Meeting meeting, Person person) {
        List<MeetingParticipant> meetingParticipants = new ArrayList<>();
        meetingParticipants.add(new MeetingParticipant(person, LocalDateTime.now()));
        meeting.setMeetingParticipants(meetingParticipants);
        meeting.setResponsiblePerson(person);
        UUID id = UUID.randomUUID();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        meetings.put(id, meeting);
        writeToFile();
        return id;
    }

    public Map<UUID, Meeting> getMeetings()  {

        return meetings;
    }

    public String deleteMeeting(UUID id, Person person)  {

        if (meetings.containsKey(id)) {
            if (meetings.get(id).getResponsiblePerson().getId() == person.getId()) {
                meetings.remove(id);
                writeToFile();

                return "Meeting deleted successfully";
            }
        } else {

            return "Meeting doesn't exist";
        }

        return "User doesn't have authorization to delete meeting";
    }

    public String addParticipantToTheMeeting(Person person, UUID id)  {
        Meeting meeting = meetings.get(id);

        for (MeetingParticipant p : meeting.getMeetingParticipants()) {
            if (p.getPerson().getId() == person.getId()) {

                return "Participant is added already";
            }
        }

        if (!isParticipantAvailable(person, meetings, meeting)) {
            meeting.getMeetingParticipants().add(new MeetingParticipant(person, LocalDateTime.now()));
            writeToFile();

            return "Participant is added, but is in another meeting this time too";
        }

        meeting.getMeetingParticipants().add(new MeetingParticipant(person, LocalDateTime.now()));
        writeToFile();

        return "Participant added successfully";
    }

    private boolean isParticipantAvailable(Person person, Map<UUID, Meeting> meetings, Meeting meeting) {

        for (UUID id : meetings.keySet()) {
            for (MeetingParticipant participant : meetings.get(id).getMeetingParticipants()) {
                if (participant.getPerson().getId() == person.getId()) {
                    if (meeting.getStartDate().isAfter(meetings.get(id).getStartDate())
                            || meeting.getStartDate().equals(meetings.get(id).getStartDate())) {
                        if(meeting.getStartDate().isBefore(meetings.get(id).getEndDate())) {

                            return false;
                        }
                    } else if (meeting.getEndDate().isAfter(meetings.get(id).getStartDate())) {

                        return false;
                    }
                }
            }
        }

        return true;
    }

    public String deleteParticipantFromMeeting(Person person, UUID id)  {
        Meeting meeting = meetings.get(id);

        if (meeting.getResponsiblePerson().getId() == person.getId()) {

            return "Participant is responsible person. Can't delete.";
        }

        for (MeetingParticipant p : meeting.getMeetingParticipants()) {
            if (p.getPerson().getId() == person.getId()) {
                meeting.getMeetingParticipants().remove(p);
                writeToFile();

                return "Participant deleted successfully.";
            }
        }

        return "Ups, something goes wrong";
    }

    public Map<UUID, Meeting> sortedListOfMeetingsByDescription(String description) {

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getDescription().equals(description))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, Meeting> sortedListOfMeetingsByResponsiblePerson(Person person) {

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getResponsiblePerson().getId() == (person.getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, Meeting> sortedListOfMeetingsByCategory(Category category) {

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getCategory().equals(category))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, Meeting> sortedListOfMeetingsByType(Type type) {

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getType().equals(type))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, Meeting> sortedListOfMeetingsIsAfterDate(String dateTime){

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getStartDate().isAfter(LocalDateTime.parse(dateTime)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, Meeting> sortedListOfMeetingsIsBetween(String startDate, String endDate) {

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getStartDate().isAfter(LocalDateTime.parse(startDate))
                        && e.getValue().getStartDate().isBefore(LocalDateTime.parse(endDate)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, Meeting> sortedListOfMeetingsByParticipantNumber(int number){

        return meetings.entrySet()
                .stream()
                .filter(e -> e.getValue().getMeetingParticipants().size() == number)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void writeToFile() {

        try {
            objectMapper.writeValue(file, meetings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public   Map<UUID, Meeting> readFromFile() {

        try {
            return objectMapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


