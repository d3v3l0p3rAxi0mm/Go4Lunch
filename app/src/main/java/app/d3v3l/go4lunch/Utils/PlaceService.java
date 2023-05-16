package app.d3v3l.go4lunch.Utils;

import java.util.List;
import java.util.Properties;

import app.d3v3l.go4lunch.BuildConfig;
import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.model.Location;
import app.d3v3l.go4lunch.model.Place;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface PlaceService {

    @GET("textsearch/json?key=" + BuildConfig.MAPS_API_KEY)
    // see documentation at https://developers.google.com/maps/documentation/places/web-service/search-text?hl=fr
    Call<Place> getPlaces(@Query("query") String query, @Query("location") String location, @Query("radius") int radius);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
