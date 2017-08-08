package com.carpediemsolution.languagecards.model;

/**
 * Created by Юлия on 16.04.2017.
 */

public class User {

    private String username;
    private String password;
    private int personId;
    private String email;
    private String token;

    public String getPassword() {
        return password;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}
}
