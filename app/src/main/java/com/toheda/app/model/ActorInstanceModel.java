package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobia on 16.02.2018.
 */

public class ActorInstanceModel implements Serializable {

    private String actorInstanceId;

    private String actorId;

    private String actorVersion;

    private String title;

    private String contextId;

    private String userId;

    private String startDate;

    private String endDate;

    private String activeState;

    private String activeStateName;

    private String activeStateType;

    public String getActorInstanceId() {
        return actorInstanceId;
    }

    public String getActorId() {
        return actorId;
    }

    public String getActorVersion() {
        return actorVersion;
    }

    public String getTitle() {
        return title;
    }

    public String getContextId() {
        return contextId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getActiveState() {
        return activeState;
    }

    public String getActiveStateName() {
        return activeStateName;
    }

    public String getActiveStateType() {
        return activeStateType;
    }
}
