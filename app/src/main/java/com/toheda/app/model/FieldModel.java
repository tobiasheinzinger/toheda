package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobias on 18.02.2018.
 */

public class FieldModel implements Serializable {

    private String id;

    private String name;

    private String value;

    public FieldModel() {

    }

    public FieldModel(final String id, final String name, final String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
