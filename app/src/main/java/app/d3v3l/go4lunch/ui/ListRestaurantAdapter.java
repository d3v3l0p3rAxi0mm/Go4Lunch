package app.d3v3l.go4lunch.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        holder.bindView(result);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRestaurantBinding b;

        public ViewHolder(ItemRestaurantBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        public void bindView(Restaurant result) {
            b.NameTextView.setText(result.getName());
            b.AddressTextView.setText(result.getAddress());
            b.DistanceTextView.setText(result.getDistanceFromUser());
        }
    }

}
