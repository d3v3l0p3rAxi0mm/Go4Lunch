package app.d3v3l.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.FragmentWorkmatesBinding;
import app.d3v3l.go4lunch.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding b;
    private WorkmatesAdapter workmatesAdapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionUsers = db.collection("users");

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment WorkmatesFragment.
     */
    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //change title of toolbar
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.available_workmates);

        b = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        setRecyclerView();
        return b.getRoot();
    }


    /**
     * RecyclerView configuration
     * Configure RecyclerView, Adapter, LayoutManager & glue it
     */

    // Source : https://firebaseopensource.com/projects/firebase/firebaseui-android/firestore/readme/

    private void setRecyclerView() {

        RecyclerView recyclerViewWorkMates = b.WorkmatesRecyclerView;

        Query query = collectionUsers.orderBy("username", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        workmatesAdapter = new WorkmatesAdapter(options, Glide.with(this), getContext());
        recyclerViewWorkMates.setHasFixedSize(true);
        recyclerViewWorkMates.setAdapter(workmatesAdapter);
        recyclerViewWorkMates.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();
        workmatesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        workmatesAdapter.stopListening();
    }

}

