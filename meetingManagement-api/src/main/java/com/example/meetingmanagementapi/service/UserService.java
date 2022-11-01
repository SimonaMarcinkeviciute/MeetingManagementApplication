package com.example.meetingmanagementapi.service;

import com.example.meetingmanagementapi.data.Person;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final List<Person> persons;

    public UserService(ObjectMapper objectMapper) throws IOException {
        File file = new File("persons.json");
        persons = objectMapper.readValue(file, new TypeReference<>(){});
    }

    public Person getPerson(String userName) {
        String[] splitUserName = userName.split(" ");

        for (Person p : persons) {
            if(p.getName().equals(splitUserName[0]) && p.getSurname().equals(splitUserName[1])) {

                return p;
            }
        }

        return null;
    }
}
