package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobia on 16.02.2018.
 */

public class ActorInstanceIdentifierModel implements Serializable {

    private String actorId;

    private String actorInstanceId;

    public ActorInstanceIdentifierModel() {

    }

    public ActorInstanceIdentifierModel(TaskModel task) {
        this.actorId = task.getActorId();
        this.actorInstanceId = task.getActorInstanceId();
    }

    public String getActorId() {
        return actorId;
    }

    public String getActorInstanceId() {
        return actorInstanceId;
    }
}
