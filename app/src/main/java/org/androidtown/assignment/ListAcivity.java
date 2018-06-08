package org.androidtown.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ListAcivity extends AppCompatActivity {

    FirebaseFirestore db;
    TextView txtDisplay;
    String user_id;
    FirebaseDatabase database;
    String partener;
    String TAG = "ListAcivity";
    String partner_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_acivity);

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            user_id = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);

        readmemberlist();

        txtDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ListAcivity.this, ChatActivity.class);
                in.putExtra("name",partener);
                ListAcivity.this.startActivity(in);
            }
        });


    }

    private void readmemberlist() {
        DocumentReference contact = db.collection("customerList").document(user_id).collection("memberlist").document("memberlist");
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        StringBuilder data = new StringBuilder("");
                        data.append("member: ").append(doc.getString("member1"));
                        partener = doc.getString("member1");
                        partner_name = doc.getString("name");
                        txtDisplay.setText(partner_name);
                    }
                    else{
                        readSingleContact();
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

    }


    private void readSingleContact() {
        DocumentReference contact = db.collection("customerList").document(user_id);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    StringBuilder data = new StringBuilder("");
                    data.append("Trainer: ").append(doc.getString("trainer"));
                    // data.append("\nEmail:").append(doc.getString("email"));
                    //  data.append("\nPhone:").append(doc.getString("phone"));
                    partener = doc.getString("trainer");
                    partner_name = doc.getString("name");
                    txtDisplay.setText(partner_name);
                }
            }
        });
    }

}