package com.carpediemsolution.languagecards.model;

/**
 * Created by Юлия on 16.04.2017.
 */

public class User {

    private String username;
    private String password;
    private int person_id;
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


    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}
}
