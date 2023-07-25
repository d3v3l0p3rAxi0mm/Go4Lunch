package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.WorkmateItemBinding;
import app.d3v3l.go4lunch.model.User;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.ViewHolder> {

    // *********************************************************************************************
    // ***************************** ADAPTER *******************************************************
    // *********************************************************************************************

    private final RequestManager glide;
    private final Context context;

    /**
     * Create constructor
     * @param options set Model and Queries
     * @param glide requestManager for glide (image management)
     */
    public WorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide, Context context) {
        super(options);
        this.context = context;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WorkmateItemBinding workmateItemBinding = WorkmateItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(workmateItemBinding, context);
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
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private String stringDate;
        private final Context context;


        public ViewHolder(WorkmateItemBinding workmateItemBinding, Context context) {
            super(workmateItemBinding.getRoot());
            this.context = context;
            b = workmateItemBinding;
        }

        public void bindView(User user, RequestManager glide) {

            String uid = user.getUid();
            Date date_of_today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            this.stringDate= format.format(date_of_today);

            String s = user.getUsername() + " " + context.getResources().getString(R.string.has_not_decided);
            b.workmatesName.setText(s);

            db.collection("restaurants")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String placeId = document.getId();
                                String name = Objects.requireNonNull(document.getData().get("name")).toString();

                                db.collection("restaurants").document(placeId).collection("usersOn" + stringDate)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                for (QueryDocumentSnapshot userDocument : task1.getResult()) {
                                                    if (uid.equals(Objects.requireNonNull(userDocument.getData().get("uid")).toString())) {
                                                        String d = context.getResources().getString(R.string.is_eating_at);
                                                        String s1 = user.getUsername() + " " + d + " " + name;
                                                        b.workmatesName.setText(s1);
                                                        b.workmatesName.setTextColor(ContextCompat.getColor(b.workmatesName.getContext(), R.color.black));
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task1.getException());
                                            }
                                        });





                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

            if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
                glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(b.workmatesPhoto);
            } else {
                b.workmatesPhoto.setImageResource(R.drawable.avatar);
            }


        }

    }

















}
