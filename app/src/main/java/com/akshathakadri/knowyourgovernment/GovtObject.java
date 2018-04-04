package com.akshathakadri.knowyourgovernment;

import java.util.List;

/**
 * Created by akshathakadri on 4/3/18.
 */

public class GovtObject {
    private List<Official> officialList;
    private String location;

    public GovtObject(List<Official> officialList, String location) {
        this.officialList = officialList;
        this.location = location;
    }

    public List<Official> getOfficialList() {
        return officialList;
    }

    public void setOfficialList(List<Official> officialList) {
        this.officialList = officialList;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
