package app.d3v3l.go4lunch.model.placeDetails;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PlaceDetailsContainer {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions;
    @SerializedName("result")
    @Expose
    private PlaceWithDetails placeWithDetails;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceWithDetails getResult() {
        return placeWithDetails;
    }

    public void setResult(PlaceWithDetails placeWithDetails) {
        this.placeWithDetails = placeWithDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
