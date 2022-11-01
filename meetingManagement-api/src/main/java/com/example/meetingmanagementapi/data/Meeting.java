package com.example.meetingmanagementapi.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Meeting {
    @NotBlank
    String title;
    @NotBlank
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Category category;
    Type type;
    Person responsiblePerson;
    List<MeetingParticipant> meetingParticipants;


    public Meeting(String title, String description, LocalDateTime startDate,LocalDateTime entDate, Category category, Type type) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = entDate;
        this.category = category;
        this.type = type;
    }
}
