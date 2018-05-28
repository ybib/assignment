package org.androidtown.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class ListAcivity extends AppCompatActivity {

    FirebaseFirestore db;
    TextView txtDisplay;
    String user_id;
    FirebaseDatabase database;
    String partener;
    String TAG = "ListAcivity";

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
        //txtDisplay.setOnClickListener();
       // addNewContact();
       // readSingleContact();
        //updateData();
        //deleteData();
        //addRealtimeUpdate();
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
           DocumentReference contact = db.collection("customer").document(user_id).collection("memberlist").document("memberlist");
           contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.isSuccessful()) {
                       DocumentSnapshot doc = task.getResult();
                       if(doc.exists()) {
                           StringBuilder data = new StringBuilder("");
                           data.append("member: ").append(doc.getString("member1"));
                           partener = doc.getString("member1");
                           txtDisplay.setText(data.toString());
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
    private void addRealtimeUpdate() {
        DocumentReference contactListen = db.collection("AddressBook").document("1");
        contactListen.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("ERROR",e.getMessage());
                    return;
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    Toast.makeText(ListAcivity.this,"Current data:"+documentSnapshot.getData(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deleteData() {
         db.collection("AddressBook").document("1")
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ListAcivity.this,"Delete",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListAcivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateData() {
        //Get Document
        DocumentReference contact = db.collection("AddressBook").document("1");
        contact.update("name","EddyLee")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ListAcivity.this,"Updated!!!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void readSingleContact() {
        DocumentReference contact = db.collection("customer").document(user_id);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    StringBuilder data = new StringBuilder("");
                    data.append("mappedtrainer: ").append(doc.getString("mappedtrainer"));
                    // data.append("\nEmail:").append(doc.getString("email"));
                    //  data.append("\nPhone:").append(doc.getString("phone"));
                    partener = doc.getString("mappedtrainer");
                    txtDisplay.setText(data.toString());
                }
            }
        });
    }
    private void addNewContact(){
        Map<String, Object> newContact = new HashMap<>();
        newContact.put("name","EddyDN");
        newContact.put("email","eddydn@gamil.com");
        newContact.put("phone","000-000-0000");

        db.collection("AddressBook").document("1").set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ListAcivity.this,"Added new contact",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR",e.getMessage());
                    }
                });
    }

}
