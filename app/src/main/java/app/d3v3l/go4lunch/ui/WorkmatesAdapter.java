package app.d3v3l.go4lunch.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.WorkmateItemBinding;
import app.d3v3l.go4lunch.model.User;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.ViewHolder> {

    // *********************************************************************************************
    // ***************************** ADAPTER *******************************************************
    // *********************************************************************************************

    private final RequestManager glide;

    /**
     * Create constructor
     * @param options set Model and Queries
     * @param glide requestManager for glide (image management)
     */
    public WorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WorkmateItemBinding workmateItemBinding = WorkmateItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(workmateItemBinding);
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

        private final WorkmateItemBinding b;

        public ViewHolder(WorkmateItemBinding workmateItemBinding) {
            super(workmateItemBinding.getRoot());
            b = workmateItemBinding;
        }

        public void bindView(User user, RequestManager glide) {
            b.workmatesName.setText(user.getUsername());

            if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
                glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(b.workmatesPhoto);
            } else {
                b.workmatesPhoto.setImageResource(R.drawable.avatar);
            }


        }

    }

















}
