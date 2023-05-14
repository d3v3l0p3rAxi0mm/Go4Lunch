package app.d3v3l.go4lunch.Utils;

import java.util.List;

import app.d3v3l.go4lunch.model.Restaurant;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


// https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants&location=45%2C3&radius=10000&key=AIzaSyD4dihjkgbOfKsFgUVFu5mKq_9iC_tGY8U

public class RestaurantService {
    @GET("place/{username}/following")
    Call<List<Restaurant>> getFollowing(@Path("username") String username);
}
