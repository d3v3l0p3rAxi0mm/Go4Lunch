package app.d3v3l.go4lunch.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.ItemUserBinding;
import app.d3v3l.go4lunch.model.User;

public class WormatesParticipationAdapter extends FirestoreRecyclerAdapter<User, WormatesParticipationAdapter.ViewHolder> {

    // *********************************************************************************************
    // ***************************** ADAPTER *******************************************************
    // *********************************************************************************************

    private final RequestManager glide;

    /**
     * Create constructor
     * @param options set Model and Queries
     * @param glide requestManager for glide (image management)
     */
    public WormatesParticipationAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemUserBinding itemUserBinding = ItemUserBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemUserBinding);
    }


    /**
     * Update viewHolder
     * @param holder the item (holder) in viewHolder
     * @param position position of item in the list (start to 0)
     * @param user User model to display in the holder
     */

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
        holder.bindView(user, this.glide);
    }

    // *********************************************************************************************
    // ***************************** VIEW HOLDER ***************************************************
    // *********************************************************************************************


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemUserBinding b;

        public ViewHolder(ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            b = itemUserBinding;
        }


        public void bindView(User user, RequestManager glide) {

            b.userName.setText(user.getUsername() + " is joining !");

            if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
                glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(b.avatar);
            } else {
                b.avatar.setImageResource(R.drawable.avatar);
            }


        }

    }

















}
