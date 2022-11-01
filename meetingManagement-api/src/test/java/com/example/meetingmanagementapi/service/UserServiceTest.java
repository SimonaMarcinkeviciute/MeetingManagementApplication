package com.example.meetingmanagementapi.service;

import com.example.meetingmanagementapi.data.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private ObjectMapper om;

    @Test
    public void testGetPerson() throws IOException {
        UserService us = new UserService(om);
        Person p = us.getPerson("Julius Crawford");

        assertTrue(p.getName().equals("Julius") && p.getSurname().equals("Crawford"));
    }

    @Test
    public void testGetPersonNull() throws IOException {
        UserService us = new UserService(om);
        Person p = us.getPerson("Julius Crawford4");

        assertNull(p);
    }
}