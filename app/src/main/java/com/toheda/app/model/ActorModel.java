package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobia on 16.02.2018.
 */

public class ActorModel implements Serializable {

    private String actorId;

    private String name;

    private String imageUrl;

    private int activeTaskCount;

    private boolean startable;

    public String getActorId() {
        return actorId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getActiveTaskCount() {
        return activeTaskCount;
    }

    public boolean isStartable() {
        return startable;
    }
}
