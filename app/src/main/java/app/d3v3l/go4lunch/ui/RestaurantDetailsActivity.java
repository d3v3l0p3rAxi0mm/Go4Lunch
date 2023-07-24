package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.d3v3l.go4lunch.BuildConfig;
import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import app.d3v3l.go4lunch.manager.UserManager;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placeDetails.DetailsContainer;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placeDetails.Result;
import app.d3v3l.go4lunch.model.User;

public class RestaurantDetailsActivity extends AppCompatActivity implements PlaceCalls.PlaceDetailsCallbacks {

    private ActivityRestaurantDetailsBinding b;
    private String placeId;
    private UserManager userManager = null;

    private WormatesParticipationAdapter wormatesParticipationAdapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers;

    private String stringDate;

    RatingBar ratingBar;

    private boolean isUserHasJoined;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Date date_of_today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.stringDate= format.format(date_of_today);

        userManager = UserManager.getInstance();

        b = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Intent intent = getIntent();
        if (intent.hasExtra("PLACEID")){
            String PLACEID = intent.getStringExtra("PLACEID");
            placeId = PLACEID;
            collectionUsers = db.collection("restaurants").document(placeId).collection("usersOn" + stringDate);
        }

        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RatingBar simpleRatingBar = b.ratingBar; // initiate a rating bar
        simpleRatingBar.setRating((float) 2.5); // set default rating

        ratingBar = b.rating; // initiate a rating bar
        ratingBar.setRating((float) 2.5); // set default rating

        setRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        executeHttpRequestForRestaurantDetails();

        FirebaseUser user = userManager.getCurrentUser();

        Query query = db.collection("restaurants").document(placeId).collection("usersOn" + stringDate).whereEqualTo("uid", user.getUid());
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    if (snapshot.getCount()==0) {
                        b.floatingOkBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
                    } else {
                        b.floatingOkBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                    }
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });

        b.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG,"Click on ratingBar");
                    b.notationLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        b.submitScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Your rating is " + ratingBar.getRating());
                b.notationLayout.setVisibility(View.GONE);
            }
        });

        b.floatingOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = userManager.getCurrentUser();

                Log.d(TAG,user.getUid());

                // request to determine if user has already selected this restaurant
                Query query = db.collection("restaurants").document(placeId).collection("usersOn" + stringDate).whereEqualTo("uid", user.getUid());
                AggregateQuery countQuery = query.count();
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Count fetched successfully
                            AggregateQuerySnapshot snapshot = task.getResult();
                            Log.d(TAG, "Count: " + snapshot.getCount());

                            // User hasn't choose this restaurant
                            if (snapshot.getCount()==0) {
                                // Access a Firestore instance
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                // if user joined another restaurant, we find its Id
                                DocumentReference docRef = db.collection("history").document(stringDate).collection("users").document(user.getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                //delete user record for this restaurant
                                                db.collection("restaurants").document(document.getData().get("placeId").toString()).collection("usersOn" + stringDate).document(user.getUid())
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                /*//Delete all history daily entries for this user
                                                                db.collection("history").document(stringDate).collection("users").document(user.getUid())
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        });*/
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error deleting document", e);
                                                            }
                                                        });

                                            } else {
                                                Log.d(TAG, "No such document");

                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });





                                // Add user in the restaurant joiners list
                                Map<String, Object> users = new HashMap<>();
                                users.put("uid", user.getUid());
                                users.put("username", user.getDisplayName());
                                users.put("urlPicture", user.getPhotoUrl());

                                db.collection("restaurants").document(placeId).collection("usersOn" + stringDate).document(user.getUid())
                                        .set(users)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // add an entry in History collection
                                                Map<String, Object> lunch = new HashMap<>();
                                                lunch.put("placeId", placeId);
                                                db.collection("history").document(stringDate).collection("users").document(user.getUid())
                                                        .set(lunch)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                b.floatingOkBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error writing document", e);
                                                                //TODO Remove document in usersOn{today} of the current Restaurant
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });



                            // User has already choose this restaurant => delete datas
                            } else {
                                db.collection("restaurants").document(placeId).collection("usersOn" + stringDate).document(user.getUid())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.collection("history").document(stringDate).collection("users").document(user.getUid())
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                b.floatingOkBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                //TODO recreate user in collection usersOn{today} of current restaurant
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Count failed: ", task.getException());
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        wormatesParticipationAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        wormatesParticipationAdapter.stopListening();
    }

    // Execute HTTP request and update UI
    private void executeHttpRequestForRestaurantDetails(){
        PlaceCalls.fetchRestaurantDetail(this, placeId);
    }



    private void setRecyclerView() {

        RecyclerView recyclerViewWorkMatesParticipation = b.workmatesRecyclerView;
        Query query = collectionUsers;
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        wormatesParticipationAdapter = new WormatesParticipationAdapter(options, Glide.with(this));
        recyclerViewWorkMatesParticipation.setHasFixedSize(true);
        recyclerViewWorkMatesParticipation.setAdapter(wormatesParticipationAdapter);
        recyclerViewWorkMatesParticipation.setLayoutManager(new LinearLayoutManager(this));

    }



    @Override
    public void onDetailsResponse(@Nullable DetailsContainer places) {
        Result place = places.getResult();
        b.restoName.setText(place.getName());
        b.restoAddress.setText(place.getFormattedAddress());
        if (place.getPhotos()!= null) {
            String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=" + place.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY;
            Glide.with(b.restoPhoto.getContext())
                    .load(urlPhoto)
                    .apply(RequestOptions.centerCropTransform())
                    .into(b.restoPhoto);
        }

        if (place.getInternationalPhoneNumber() != null) {
            b.callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Call to" + place.getInternationalPhoneNumber(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            b.callBtn.setTextColor(getColor(R.color.grey));
            b.callBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
        }

        if (place.getWebsite() != null) {
            b.webBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (place.getWebsite() != null) {
                        String url = place.getWebsite();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
            });
        } else {
            b.webBtn.setTextColor(getColor(R.color.grey));
            b.webBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
        }

    }

    @Override
    public void onDetailsFailure() {

    }
}