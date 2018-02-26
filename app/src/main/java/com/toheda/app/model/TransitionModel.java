package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobia on 16.02.2018.
 */

public class TransitionModel implements Serializable {

    private String id;

    private String name;

    private String valid;

    private String validationError;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValid() {
        return valid;
    }

    public String getValidationError() {
        return validationError;
    }
}
