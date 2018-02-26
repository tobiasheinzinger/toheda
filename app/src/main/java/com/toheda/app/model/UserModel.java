package com.toheda.app.model;

import java.io.Serializable;

/**
 * Created by tobia on 16.02.2018.
 */

public class UserModel implements Serializable {
    private String userId;

    private String email;

    private String username;

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
