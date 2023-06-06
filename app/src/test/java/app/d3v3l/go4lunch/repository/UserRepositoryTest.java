package app.d3v3l.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import app.d3v3l.go4lunch.model.User;


public class UserRepositoryTest {

    UserRepository userRepository;

    @Mock
    FirebaseAuth firebaseAuth;

    @Before
    public void setUp() {
        userRepository = UserRepository.getInstance();
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void getCurrentUser() {

    }

    @Test
    public void getCurrentUserIsNull() {
        given(firebaseAuth.getCurrentUser()).willReturn(null);
        assertNull(userRepository.getCurrentUser());
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