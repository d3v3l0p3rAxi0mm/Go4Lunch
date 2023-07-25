package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import app.d3v3l.go4lunch.BuildConfig;
import app.d3v3l.go4lunch.databinding.ItemRestaurantBinding;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Result;
import app.d3v3l.go4lunch.model.Restaurant;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ViewHolder> {

    private final List<Restaurant> mResults;

    public ListRestaurantAdapter(List<Restaurant> items) {
        mResults = items;
    }

    @NonNull
    @Override
    public ListRestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRestaurantBinding itemRestaurantBinding = ItemRestaurantBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemRestaurantBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant result = mResults.get(position);
        holder.bindView(result, position);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRestaurantBinding b;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(ItemRestaurantBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        public void bindView(Restaurant result, int position) {
            String placeId = result.getPlaceId();
            String name = result.getName();
            b.NameTextView.setText(name);
            b.AddressTextView.setText(result.getAddress());
            b.DistanceTextView.setText(result.getDistanceFromUser());

            // Collect the score
            DocumentReference docRef = db.collection("restaurants").document(placeId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            float score = 0F;
                            if (document.getData().get("score") != null) {
                                score = Float.parseFloat(document.getData().get("score").toString());
                            }
                            b.ratingBar.setRating((float) score);

                        } else {
                            Log.d(TAG, "No such document");

                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            if (result.getPhoto()!= null) {
                String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=80&photo_reference=" + result.getPhoto().getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY;
                Glide.with(b.imageRestaurant.getContext())
                        .load(urlPhoto)
                        .apply(RequestOptions.centerCropTransform())
                        .into(b.imageRestaurant);
            }

            b.itemRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(b.itemRestaurant.getContext(), RestaurantDetailsActivity.class);
                    intent.putExtra("PLACEID", result.getPlaceId());
                    v.getContext().startActivity(intent);
                }
            });


        }
    }

}
