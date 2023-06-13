package app.d3v3l.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import app.d3v3l.go4lunch.model.User;


public class UserRepositoryTest {

    UserRepository userRepository;
    FirebaseAuth instance = FirebaseAuth.getInstance();


    @Before
    public void setUp() {
        //userRepository = UserRepository.getInstance();
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void getCurrentUser() {

    }

    @Test
    public void getCurrentUserIsNull() {
        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
        instance = firebaseAuth;
        doReturn(null).when(firebaseAuth).getCurrentUser();
        assertNull(getUID());
    }

    public String getUID() {
        return instance.getUid();
    }

    @Test
    public void signOut() {
    }

    @Test
    public void deleteUser() {
    }

    @Test
    public void createUser() {
    }

    @Test
    public void getUserData() {
    }

    @Test
    public void deleteUserFromFirestore() {
    }



}

