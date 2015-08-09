package com.stpi.campus.entity.navigate;

/**
 * Created by lyl on 2014/8/31.
 */
public class Location {
    private Integer locationId;
    private String locationName;
    private String locationPic;
    private Double locationLongi;
    private Double locationLati;

    public Location() {

    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationPic() {
        return locationPic;
    }

    public void setLocationPic(String locationPic) {
        this.locationPic = locationPic;
    }

    public Double getLocationLongi() {
        return locationLongi;
    }

    public void setLocationLongi(Double locationLongi) {
        this.locationLongi = locationLongi;
    }

    public Double getLocationLati() {
        return locationLati;
    }

    public void setLocationLati(Double locationLati) {
        this.locationLati = locationLati;
    }
}
