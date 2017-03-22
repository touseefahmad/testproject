package com.example.enigma_pc.smartmailforblinds.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by enigma-pc on 3/13/2017.
 */

public class User {


    @SerializedName("smart_id")
    @Expose
    private Integer smartId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email_id")
    @Expose
    private String emailId;

    public Integer getSmartId() {
        return smartId;
    }

    public void setSmartId(Integer smartId) {
        this.smartId = smartId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
