package giannhsrak.activitiestracker;

import android.content.Context;
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

public class RegisterFragment extends Fragment implements View.OnClickListener,Executor{
    //Αυτή η κλάση αποτελεί το Register fragment
    //Καλείται όταν ο χρήστης θέλει να κάνει Register
    //η κληση γίνεται από το Main Activity
    //Το container του fragment βρίσκεται στο RegisterActivity.java

    //Θα αποθηκεύουν το περιεχόμενο κάθε EditText
    //Για να γίνεται ο έλεγχος να μην είναι null
    //κατά το register
    private String usernameText = "";
    private String passwordText = "";
    private String firstnameText = "";
    private String lastnameText = "";
    private String emailText = "";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText emailEditText;
    private Button confirmRegBtn;
    private Button cancelBtn;
    private FrameLayout frameLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database;
    private DatabaseReference myUsersRef;

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

                    //Γράφουμε στη βάση με ένα Java Object που
                    //περιέχει τα στοιχεία του χρήστη
                    User customUser = new User();

                    //Χρησιμοποιούμε τους setters από την User.java για να δώσουμε τιμές
                    //στα πεδία του customUser object
                    //και να τα γράψουμε στη βάση
                    customUser.setUsername(usernameEditText.getText().toString());
                    customUser.setPassword(passwordEditText.getText().toString());
                    customUser.setFirstname(firstnameEditText.getText().toString());
                    customUser.setLastname(lastnameEditText.getText().toString());
                    customUser.setEmail(emailEditText.getText().toString());

                    //Γράφουμε στο μονοπάτι root/users/ user-uid /..
                    myUsersRef.child("users").child(user.getUid()).setValue(customUser);

                    //Μετάβαση στη Main Activity
                    startMainActivity();
                } else {
                    // User is signed out
                    //Δείξει ότι κανείς δεν είναι συνδεδεμένος
                    Toast.makeText(getActivity(),"No one is signed in.",Toast.LENGTH_LONG).show();
                    Log.d("1", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_register, container, false);

        //Views
        usernameEditText = (EditText) frameLayout.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) frameLayout.findViewById(R.id.passwordEditText);
        firstnameEditText = (EditText) frameLayout.findViewById(R.id.firstnameEditText);
        lastnameEditText = (EditText) frameLayout.findViewById(R.id.lastnameEditText);
        emailEditText = (EditText) frameLayout.findViewById(R.id.emailEditText);

        //Buttons
        cancelBtn = (Button) frameLayout.findViewById(R.id.cancelBtn);
        confirmRegBtn = (Button) frameLayout.findViewById(R.id.confirmRegBtn);

        confirmRegBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        return frameLayout;
    }

    //Click event handler
    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.confirmRegBtn:

                    usernameText = usernameEditText.getText().toString();
                    passwordText = passwordEditText.getText().toString();
                    firstnameText = firstnameEditText.getText().toString();
                    lastnameText = lastnameEditText.getText().toString();
                    emailText = emailEditText.getText().toString();

                    if ( getUserInput(usernameText,passwordText,firstnameText,lastnameText,emailText) ){

                        createAccount(usernameText,passwordText,firstnameText,lastnameText,emailText);

                    }

                    break;
            case R.id.cancelBtn:
                startMainActivity();
                break;
            default:
                break;
        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void execute(@NonNull Runnable command) {

    }

    //Returns true if input is correct
    //Χρησιμοποιείται για να μην επιτραπεί στον χρήστη
    //Να προσθέσει κενά πεδία ( " " )κατά την εγγραφή του
    public boolean getUserInput(String usernameText,String passwordText,String firstnameText,String lastnameText,String emailText){
        if( usernameText.equals("") || passwordText.equals("") || firstnameText.equals("") || lastnameText.equals("") ||
                emailText.equals("")){
             Toast.makeText(getContext(),"Fill all the fields.",Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    //Δημιουργείται το account του χρήστη με το email
    //και το password που έδωσε
    //όταν η εγγραφή τελειώσει
    //το αντιλαμβάνεται ο listener onAuthStateChanged
    private void createAccount(final String username, final String password,
                               final String firstname, final String lastname,final String email){

        //Κάνουμε το confirm button disabled
        //Για να μην υπάρχει κίνδυνος διπλοεγγραφής κτλ.
        //όσο περιμένουμε για τη μετάβαση στη Main Activity
        confirmRegBtn.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)  {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getContext(), R.string.auth_failed  + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //Handle exceptions
        //Δείχνει μηνύματα λάθους στον χρήστη
        mAuth.createUserWithEmailAndPassword(email,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to create user", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}