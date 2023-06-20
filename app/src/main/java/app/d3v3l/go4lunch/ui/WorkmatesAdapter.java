package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.res.ColorStateList;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private String stringDate;

        public ViewHolder(WorkmateItemBinding workmateItemBinding) {
            super(workmateItemBinding.getRoot());
            b = workmateItemBinding;
        }

        public void bindView(User user, RequestManager glide) {

            String uid = user.getUid();
            Date date_of_today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            this.stringDate= format.format(date_of_today);

            b.workmatesName.setText(user.getUsername() + " hasn't decided yet !");

            db.collection("restaurants")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String placeId = document.getId();
                                    String name = document.getData().get("name").toString();
                                    Log.d("NameResto", name);

                                    db.collection("restaurants").document(placeId).collection("usersOn" + stringDate)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                                            Log.d("UIDUser", uid + " = " + userDocument.getData().get("uid").toString() + " ? ");
                                                            if (uid.equals(userDocument.getData().get("uid").toString())) {
                                                                Log.d("CheckMatch", "YES");
                                                                b.workmatesName.setText(user.getUsername() + " is eating at " + name);
                                                                b.workmatesName.setTextColor(ContextCompat.getColor(b.workmatesName.getContext(), R.color.black));
                                                            }
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });





                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
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
