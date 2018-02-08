package giannhsrak.activitiestracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class RegisterActivity extends AppCompatActivity {

    //Αυτό το Activity περιέχει 2 fragments
    //Ποιό fragment θα εμφανιστεί κάθε φορα
    //Καθορίζεται απο το όρισμα που έχει το intent
    //consIntent = getIntent();
    //consIntent.getStringExtra
    private Intent consIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        consIntent = getIntent();
        showLoginFragment();
        showRegisterFragment();
    }

    private void showLoginFragment(){
        //Αν το consIntent.getStringExtra = start_login
        //Εμφάνισε το login fragment
        try{
            String intentString = consIntent.getStringExtra("START_LOGIN");
            if( intentString == null){
                //
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.linearLayout1,new LoginFragment()).commit();
            }
        }catch(NullPointerException  e){
            e.printStackTrace();
            Log.d("Exception : ", Log.getStackTraceString(e.getCause()));
        }
    }

    private void showRegisterFragment(){
        //Αν το consIntent.getStringExtra = start_register
        //Εμφάνισε το register fragment
        try{
            String intentString = consIntent.getStringExtra("START_REGISTER");
            if( intentString == null){
                //
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.linearLayout1,new RegisterFragment()).commit();
            }
        }catch(NullPointerException e){
            e.printStackTrace();
            Log.d("Exception : ", Log.getStackTraceString(e.getCause()));
        }
    }
}

