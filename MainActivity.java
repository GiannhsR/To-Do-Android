package giannhsrak.activitiestracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button registerBtn;
    private Button loginBtn;
    private TextView userSignedInLabel;
    private Button todoListBtn;
    private Button logoutBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database;
    private DatabaseReference myUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        myUsersRef = database.getReference();

        //Views
        userSignedInLabel = (TextView) findViewById(R.id.userSignedInLabel);

        //Buttons
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        todoListBtn = (Button) findViewById(R.id.todolistBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        //Δηλώνουμε τους listener
        logoutBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        todoListBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();//Παίρνουμε στιγμιότυπο για το Firebase Auth
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();//Παίρνουμε τον συνδεδεμένο χρήστη
                if (user != null) {
                    // User is signed in
                    //Το uId κάνει την ταυτοποίηση του χρήστη
                    String uId = user.getUid();

                    //Ακούμε τη βάση δεδομένων
                    //Μόνο για μία φορά και δείχνουμε
                    //το όνομα του χρήστη
                    listenToDatabase(uId);

                    registerBtn.setVisibility(View.GONE);//Να μην καλύπτει χώρο όταν κάποιος είναι signed in
                    loginBtn.setVisibility(View.GONE);//Να μην καλύπτει χώρο όταν κάποιος είναι signed in
                    todoListBtn.setVisibility(View.VISIBLE);//Να μη φαίνεται όταν κάποιος είναι signed in
                    logoutBtn.setVisibility(View.VISIBLE);//Να μη φαίνεται όταν κάποιος είναι signed in

                    Log.d("0", "onAuthStateChanged:signed_in:" + uId);

                } else {
                    // User is signed out
                    todoListBtn.setVisibility(View.GONE);//Να μην καλύπτει χώρο όταν κάποιος είναι signed out
                    logoutBtn.setVisibility(View.GONE);//Να μην καλύπτει χώρο όταν κάποιος είναι signed out
                    userSignedInLabel.setText("No user is signed in.\nPlease log in or Register a new account.");
                    Log.d("1", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //Ο "observer" για κάθε state του χρήστη
        //Δηλώνεται στην OnStart
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    //Event listener
    //Για τα clicks του χρήστη
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerBtn:
                startOtherActivity("register");
                break;
            case R.id.loginBtn:
                startOtherActivity("login");
                break;
            case R.id.todolistBtn:
                startOtherActivity("to_do_list");
                break;
            case R.id.logoutBtn:
                logOutUser();
                break;
            default:
                break;
        }
    }

    //Με αυτή τη μέθοδο ακούμε μόνο μια φορά
    //για να πάρουμε το username του χρήστη
    //και να το προβάλουμε στην αρχική οθόνη
    //userSignedInLabel.setText(value.getUsername())
    private void listenToDatabase(String uId){
        //Παίρνουμε το path της βάσης δεδομένων
        //που από εκεί θέλουμε δεδομένα
        //root/users/ user-uid /
        myUsersRef.child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value
                User value = dataSnapshot.getValue(User.class);
                Log.d("1", "Value is: " + value);
                //Αλλάζουμε το μήνυμα εισόδου
                //με το όνομα του χρήστη
                userSignedInLabel.setText(value.getUsername());//Παίρνουμε το username του χρήστη από τη βάση δεδομένων
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("-1", "Failed to read value.", error.toException());
            }
        });
    }

    //Αποσυνδέει τον χρήστη
    private void logOutUser(){
        registerBtn.setVisibility(View.VISIBLE);//Κάνει το κουμπί visible
        loginBtn.setVisibility(View.VISIBLE);//Κάνει το κουμπί visible
        mAuth.signOut();
    }

    private void startOtherActivity(String activityFunctionality){
        //Αρχίζει κάποιο activity ανάλογα με το String που πήρε ως όρισμα
        Intent myIntent;
        if( activityFunctionality.equals("register")){
            myIntent = new Intent(this,RegisterActivity.class);
            myIntent.putExtra("START_REGISTER",activityFunctionality);
            startActivity(myIntent);
        }
        if( activityFunctionality.equals("login")){
            myIntent = new Intent(this,RegisterActivity.class);
            myIntent.putExtra("START_LOGIN",activityFunctionality);
            startActivity(myIntent);
        }
        if( activityFunctionality.equals("to_do_list")){
            myIntent = new Intent(this,UserActivityList.class);
            startActivity(myIntent);
            finish();//κλείνει αυτό το Activity(MainActivity)
        }
    }
}
