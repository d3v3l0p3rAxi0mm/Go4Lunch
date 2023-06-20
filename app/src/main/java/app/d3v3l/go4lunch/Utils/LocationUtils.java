package app.d3v3l.go4lunch.Utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;

public class LocationUtils {

    public static LatLng getCoordinate(LatLng middleCoord, double d) {
        double lat = middleCoord.latitude + (180 / Math.PI) * (d / 2 / 6378137);
        double lng = middleCoord.longitude + (180 / Math.PI) * (d / 2 / 6378137) / Math.cos(middleCoord.latitude);
        LatLng coord = new LatLng(lat, lng);
        Log.d("Bound", coord.toString());
        return coord;
    }

    public static RectangularBounds getBoundsFromLatLng(LatLng coord, double d) {
        return RectangularBounds.newInstance(
                getCoordinate(coord, d*-1),
                getCoordinate(coord, d));
    }

}
