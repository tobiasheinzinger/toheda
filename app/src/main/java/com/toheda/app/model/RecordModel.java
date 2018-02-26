package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobia on 16.02.2018.
 */

public class RecordModel implements Serializable {

    private String timestamp;

    private String actorinstancetitle;

    private String userid;

    private String actorinstanceid;

    private String actorid;

    private String serial;

    private String log;

    private String actorname;

    public String getTimestamp() {
        return timestamp;
    }

    public String getActorinstancetitle() {
        return actorinstancetitle;
    }

    public String getUserid() {
        return userid;
    }

    public String getActorinstanceid() {
        return actorinstanceid;
    }

    public String getActorid() {
        return actorid;
    }

    public String getSerial() {
        return serial;
    }

    public String getLog() {
        return log;
    }

    public String getActorname() {
        return actorname;
    }
}
