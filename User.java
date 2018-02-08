package giannhsrak.activitiestracker;

import java.util.HashMap;

/**
 * Created by Giannhs on 17/5/2017.
 * Η κλάση αυτή δημιουργεί στιγμιότυπα
 * για κάθε χρήστη που θέλει να εγγραφεί στο σύστημα
 *
 */

public class User {

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private HashMap<String,Object> list;

    public User(){

    }

    public User(String username,String password,String firstname,String lastname,String email){
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public HashMap<String, Object> getList(String key) {
        return list;
    }

    public void setList(HashMap<String, Object> list) {
        this.list = list;
    }

}
