package com.example.meetingmanagementapi;

public interface ApplicationPath {

    String userName = "userName";
    String meetingId = "meetingId";
    String description = "description";
    String category = "category";
    String type = "type";
    String startDate = "startDate";
    String endDate = "endDate";
    String number = "number";

    String ROOT = "/api";
    String MEETINGS = ROOT + "/meetings";
    String CREATE_MEETING = "/{" + userName + "}";
    String DELETE_MEETING = "/{" + userName + "}" + "/{" + meetingId + "}";
    String ADD_PARTICIPANT = DELETE_MEETING;
    String DELETE_PERSON = "/delete" + "/{" + userName + "}" + "/{" + meetingId + "}";
    String SEARCH_BY_DESCRIPTION = "/searchByDescription" + "/{" + description + "}";
    String SEARCH_BY_PERSON = "/searchByUser" + "/{" + userName + "}";
    String SEARCH_BY_CATEGORY = "/searchByCategory" + "/{" + category + "}";
    String SEARCH_BY_TYPE = "/searchByType" + "/{" + type + "}";
    String SEARCH_IS_AFTER_DATE = "/searchByDate" + "/{" + startDate + "}";
    String SEARCH_IS_BETWEEN_DATE = "/searchByPeriod" + "/{" + startDate + "}" + "/{" + endDate + "}";
    String SEARCH_BY_PARTICIPANT_NUMBER = "/searchQuantity" + "/{" + number + "}";
}
