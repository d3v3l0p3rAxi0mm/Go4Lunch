
package app.d3v3l.go4lunch.model;

public class Restaurant {

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String distanceFromUser;

    public Restaurant(String name, String address, Double latitude, Double longitude, String distanceFromUser) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceFromUser = distanceFromUser;
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
}
