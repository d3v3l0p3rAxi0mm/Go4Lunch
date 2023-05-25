
package app.d3v3l.go4lunch.model;

import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Photo;

public class Restaurant {

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String distanceFromUser;
    private Photo photo;


    public Restaurant(String name, String address, Double latitude, Double longitude, String distanceFromUser, Photo photo) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceFromUser = distanceFromUser;
        this.photo = photo;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(String distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
