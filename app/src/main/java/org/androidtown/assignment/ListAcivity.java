package org.androidtown.assignment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAcivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    LinearLayoutManager  mLayoutManager;
    FirebaseFirestore db;
    TextView txtDisplay;
    String user_id;
    FirebaseDatabase database;

    List<Member_list> mMember_list;
    ListAdapter mAdapter;
    String TAG = "ListAcivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_acivity);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_list);
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            user_id = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);
       // addNewContact();
       // readSingleContact();
        //updateData();
        //deleteData();
       // addRealtimeUpdate();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMember_list = new ArrayList<>();


        // specify an adapter (see also next example)
        //id와 email이 넘어가고.
        mAdapter = new ListAdapter(mMember_list);
        mRecyclerView.setAdapter(mAdapter);

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
