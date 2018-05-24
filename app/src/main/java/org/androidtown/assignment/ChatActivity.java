package org.androidtown.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText chat_space;
    Button btn_send;
    String user_id;
    String user_key;
    List<Chatdata> mChatdata;
    FirebaseDatabase database;
    FirebaseFirestore db;
    String partener;
    String partener_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        partener=intent.getStringExtra("name");
        Toast.makeText(this, partener+"와 대화", Toast.LENGTH_SHORT).show();

        chat_space = (EditText) findViewById(R.id.chat_space);
        btn_send = (Button) findViewById(R.id.btn_send);

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            user_id = user.getEmail();
            user_key = user.getUid();
        }

        DocumentReference contact = db.collection("customer").document(partener);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    partener_key = doc.getString("id");
                }
            }
        });



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send_text = chat_space.getText().toString();
                if(send_text.equals("")||send_text.isEmpty()){
                    Toast.makeText(ChatActivity.this,"내용 입력!",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ChatActivity.this, send_text + ", " + user_id, Toast.LENGTH_SHORT).show();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"));
                    String formattedDate = df.format(c.getTime());
                    partener = user_id;

                    //DatabaseReference myRef = database.getReference("chatdata").child(formattedDate);
                    DatabaseReference myRef = database.getReference("users").child(user_key).child("chatdata").child(formattedDate);
                   // DatabaseReference myRef = database.getReference("users").child(partener).child("chatdata").child(formattedDate);
                    //DatabaseReference myRef = database.getReference("users").child(stChatid).child("chat").child(formattedDate);
                    Hashtable<String, String> chat
                            = new Hashtable<String, String>();
                    chat.put("ID", user_id);
                    chat.put("text",send_text);
                    myRef.setValue(chat);
                    chat_space.setText("");

                    DatabaseReference myRef2 = database.getReference("users").child(partener_key).child("chatdata").child(formattedDate);
                    Hashtable<String, String> chat2
                            = new Hashtable<String, String>();
                    chat2.put("ID", user_id);
                    chat2.put("text",send_text);
                    myRef2.setValue(chat2);

                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChatdata = new ArrayList<>();


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mChatdata,user_id);
        mRecyclerView.setAdapter(mAdapter);

       // DatabaseReference myRef = database.getReference("chatdata");
       // DatabaseReference myRef = database.getReference("users").child(partener).child("chatdata");
        DatabaseReference myRef = database.getReference("users").child(user_key).child("chatdata");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chatdata chatdata = dataSnapshot.getValue(Chatdata.class);

                // [START_EXCLUDE]
                // Update RecyclerView

                mChatdata.add(chatdata);
                mAdapter.notifyItemInserted(mChatdata.size() - 1);
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
}
