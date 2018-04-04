package com.akshathakadri.knowyourgovernment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akshathakadri on 3/29/18.
 */

public class Official implements Serializable{

    private String name;
    private String position;
    private String party;
    private String photoURL;
    private String address;
    private String phone;
    private String email;
    private String website;
    private Map<String, String> channels;

    private static final String NO_DATA = "No Data Provided";

    public Official(String name, String party, String photoURL) {
        this.name = name;
        this.party = party;
        this.photoURL = photoURL;
        this.address = NO_DATA;
        this.phone = NO_DATA;
        this.email = NO_DATA;
        this.website = NO_DATA;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Map<String, String> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, String> channels) {
        this.channels = channels;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
