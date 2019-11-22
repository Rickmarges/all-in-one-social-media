package com.example.dash.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.dash.data.model.LoggedInUser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String email, String password) {

        try {
            // TODO: handle loggedInUser authentication

//            Connection conn = null;
//            Log.w("Test", "test");
//            try {
//                String driver = "net.sourceforge.jtds.jdbc.driver";
//                Class.forName(driver).newInstance();
//                Log.w("Test", "test");
//                String connString = "jtds:jdbc:sqlserver://dash-sec.database.windows.net:1433;database=Dash;user=Dash@dash-sec;password=Appel-123" +
//                        ";encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
//                String username = "Dash";
//                String password1 = "Appel-123";
//                conn = DriverManager.getConnection(connString, username, password1);
//                Log.w("Connection", "open");
//                Statement stmt = conn.createStatement();
//                ResultSet reset = stmt.executeQuery("Select * from Users");
//
//                while(reset.next()){
//                    Log.w("Data:",reset.getString(3));
//                }
//                conn.close();
//
//            } catch (Exception e) {
//                Log.w("Error ", e.getMessage());
//            }

            class User {
                String email;
                String pass;

                User(){

                }

                User(String email, String pass){
                    this.email = email;
                    this.pass = pass;
                }
            }



            // Access a Firebase Database instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Log.w("Message", "test");

            Map<String, String> user = new HashMap<>();
            user.put("E-mail", "marynkaspers20 @gmail.com");
            user.put("Password", "1234567890");

//            User user = new User("Maryn", "appel");

            Log.w("Message", "made user");

            db.collection("Users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Message", "Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Warning", "Error adding document, " + e);
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.w("Warning", "Canceled");
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.w("Message", "Complete");
                        }
                    });

            Log.w("Message", "After write db");

            db.collection("Users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Message", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w("Error", "Error getting documents! " + task.getException());
                            }
                        }
                    });

            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
