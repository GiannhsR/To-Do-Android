package giannhsrak.activitiestracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.Executor;
import static android.content.ContentValues.TAG;


public class LoginFragment extends Fragment implements View.OnClickListener,Executor {

    //Αυτή η κλάση αποτελεί το Login fragment
    //Καλείται όταν ο χρήστης θέλει να κάνει Log in
    //η κληση γίνεται από το Main Activity
    //Το container του fragment βρίσκεται στο RegisterActivity.java

    private EditText usernameLoginEditText;
    private EditText passwordLoginEditText;
    private Button loginBtn;

    private String usernameText = "";
    private String passwordText = "";

    private FrameLayout frameLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database;
    private DatabaseReference myUsersRef;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frameLayout = null;
        database = FirebaseDatabase.getInstance();
        myUsersRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();//Παίρνουμε στιγμιότυπο για το firebaseAuth
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();//Παίρνουμε τον τωρινό χρήστη
                if (user != null) {
                    // User is signed in
                    Log.d("0", "onAuthStateChanged:signed_in:" + user.getUid());
                    //Όταν ο χρήστης συνδεθέι/sign in ,
                    //αρχίζει πλαι το Main Activity
                    startMainActivity();
                } else {
                    // User is signed out
                    Toast.makeText(getActivity(),"No one is signed in.",Toast.LENGTH_LONG).show();
                    Log.d("1", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_login, container, false);

        //Views
        usernameLoginEditText = (EditText) frameLayout.findViewById(R.id.usernameLoginEditText);
        passwordLoginEditText = (EditText) frameLayout.findViewById(R.id.passwordLoginEditText);

        //Buttons
        loginBtn = (Button) frameLayout.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        return frameLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Click event handler
    @Override
    public void onClick(View v) {
        switch ( v.getId()){
            case R.id.loginBtn:
                usernameText = usernameLoginEditText.getText().toString();
                passwordText = passwordLoginEditText.getText().toString();

                if( usernameText.equals("") || passwordText.equals("")){
                    //
                }else{
                    signIn(usernameText,passwordText);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void execute(@NonNull Runnable command) {

    }

    //Μέθοδος που καλείται για να συνδεθεί/sign in ο χρήστης
    //στο σύστημα
    private void signIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Handle exceptions
        //Δείχνει μηνύματα λάθους στον χρήστη
        mAuth.signInWithEmailAndPassword(email,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to login user", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

}
