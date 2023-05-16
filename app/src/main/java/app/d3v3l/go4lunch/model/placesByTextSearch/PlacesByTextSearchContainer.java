
package app.d3v3l.go4lunch.model.placesByTextSearch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlacesByTextSearchContainer {

    @SerializedName("results")
    @Expose
    private List<Place> places;


    public List<Place> getResults() {
        return places;
    }


}
