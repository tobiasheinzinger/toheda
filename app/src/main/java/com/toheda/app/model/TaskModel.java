package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobias on 18.02.2018.
 */

public class TaskModel implements Serializable {

    private String actorId;

    private String actorName;

    private String stateName;

    private String actorInstanceId;

    private String startDate;

    private String title;

    public String getActorId() {
        return actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getActorInstanceId() {
        return actorInstanceId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }
}
