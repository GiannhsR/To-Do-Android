package giannhsrak.activitiestracker;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserActivityList extends AppCompatActivity implements View.OnClickListener{

    private Button addActivityBtn;
    private TextView userSignedInLabel;
    private ListView activitiesListView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database;
    private DatabaseReference myUsersRef;

    //HashMap(String,String) για το ζεύγος (key,value) ---> ( activity-key , activity-value)
    private HashMap<String,String> handleActivities = new HashMap<>();

    private UserActivityAdapter adapter;

    //Αποθηκεύονται τα activities που επιστρέφει η βάση δεδομένων
    //και μετά προβάλλονται στον χρήστη
    private List<String> activitiesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        database = FirebaseDatabase.getInstance();
        myUsersRef = database.getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_name);//Αλλάζει το αρχικό image του fab σε βελάκι
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startMainActivity();
            }
        });

        //Views
        activitiesListView = (ListView) findViewById(R.id.listView);
        userSignedInLabel = (TextView) findViewById(R.id.userSignedInLabel);

        //Buttons
        addActivityBtn = (Button) findViewById(R.id.addActivityBtn);
        addActivityBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();//Παίρνουμε τον συνδεδεμένο χρήστη
                if (user != null) {
                    // User is signed in

                    String uId = user.getUid();//Το uId κάνει την ταυτοποίηση του χρήστη

                    Log.d("0", "onAuthStateChanged:signed_in:" + uId);

                    listenToDatabase(uId);//Ακούμε τη βάση δεδομένων

                } else {
                    // User is signed out
                    Toast.makeText(getApplicationContext(), " is signed out",Toast.LENGTH_LONG).show();
                    Log.d("1", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    //Click event handler
    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.addActivityBtn:
                addActivity(addActivityBtn,FirebaseAuth.getInstance().getCurrentUser().getUid());
                break;
            default:
                break;
        }
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

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Χρησιμοποιείται για να ακούμε αλλαγές στα δεδομένα/παιδιά στο συγκεκριμένο path
    // listRef = database.getReference("users/" + uid + "/list/");
    // "users/" + uid + "/list/"
    //Δηλαδή στις δραστηριότητες/activities του χρήστη
    private void childChanges(String uid,final String key, final String activity){
        DatabaseReference listRef = database.getReference("users/" + uid + "/list/");

        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                handleActivities.put(key,activity);
                //Toast.makeText(UserActivityList.this, "Called from onChildAdded" +  handleActivities.get(key), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Ακούμε τη βάση δεδομένων στο συγκεκριμένο path
    // DatabaseReference listRef = database.getReference("users/" + uid + "/list/");
    //"users/" + uid + "/list/"
    //Δηλαδή στις δραστηριότητες/activities του χρήστη
    //Αν προϋπάρχουν activities , θα εμφανιστόυν μέσα σε κάποιο TextView(R.layout.item_layout.xml) μέσα σε ένα ListView
    //που γίνεται populate/παίρνει entries από to adapter object
    private void listenToDatabase(String uid){
        DatabaseReference listRef = database.getReference("users/" + uid + "/list/");

        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //This method is called once with the initial value and again
                //whenever data at this location is updated.

                /* ----------------Used for debugging -------------------
                Toast.makeText(getApplicationContext(),"Number of /list/ children are: "
                        + dataSnapshot.getChildrenCount(),Toast.LENGTH_LONG).show();

                Toast.makeText(UserActivityList.this, "dataSnapshot.getvalue() is : "
                        + dataSnapshot.getValue(), Toast.LENGTH_LONG).show();
                 /* -------------------------------------------------------*/

                for (DataSnapshot listSnapshot: dataSnapshot.getChildren()) {

                    String key = listSnapshot.getKey();

                    String value = (String) listSnapshot.getValue();

                    handleActivities.put(key,value);

                    activitiesList.add(value);

                    adapter = new UserActivityAdapter(UserActivityList.this,R.layout.item_layout,activitiesList);

                    activitiesListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("-1", "Failed to read value.", databaseError.toException());
            }
        });
    }

    //Χρησιμοποιείται για να προσθέτει ο χρήστης δραστηριότητες/activities
    //Εμφανίζεται ένα pop up windo με 2 buttons και ένα EditText που προέρχονται
    //από το fragment_add_activity
    private void addActivity(View anchor,final String uId){
        View popupView = getLayoutInflater().inflate(R.layout.fragment_add_activity, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Βρες τα view που θες να εμφανίζονται μέσα στο popupView
        final EditText activityEditText = (EditText) popupView.findViewById(R.id.activityEditText);
        Button btn1 = (Button) popupView.findViewById(R.id.confirmBtn);
        Button btn2 = (Button) popupView.findViewById(R.id.cancelBtn);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> activityMap = new HashMap<>();
                //Το activity που θέλει ο χρήστης να προσθέσει στη βάση
                String myActivity = activityEditText.getText().toString();

                //Αν είναι κενό το activity
                //να μην γράφεται στη βάση
                if( !myActivity.equals("")){

                    Toast.makeText(getApplicationContext(),"Activity : " + myActivity + " added.",Toast.LENGTH_LONG).show();

                    String key = myUsersRef.child("users").child(uId).child("list").push().getKey();

                    activityMap.put("users/" + uId + "/list/" + key, myActivity);

                    childChanges( uId, key ,myActivity );

                    activitiesList.add(myActivity);

                    myUsersRef.updateChildren(activityMap);

                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Καθαρίζει το input field για το όνομα
                //του activity που θέλει να προσθέσει
                //ο χρήστης
                activityEditText.setText("");
            }
        });

        //Κάνε το popupView focusable
        popupWindow.setFocusable(true);

        //Όταν πατάς στο background το popupView φεύγει
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        //Η θέση του popupView είναι κάτω από εκεί που έγινε το click
        //δηλαδή στο ADD ACTIVITY button
        anchor.getLocationOnScreen(location);

        //Η θέση του popupView είναι κάτω από εκεί που έγινε το click
        popupWindow.showAtLocation( anchor, Gravity.NO_GRAVITY,
                location[0], location[1] +  anchor.getHeight());

    }
}
