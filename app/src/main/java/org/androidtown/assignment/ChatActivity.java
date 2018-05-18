package org.androidtown.assignment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText chat_space;
    Button btn_send;
    String user_id;

    String[] myDataset = {"안녕","밥은 먹었니?","영화볼래?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_space = (EditText) findViewById(R.id.chat_space);
        btn_send = (Button) findViewById(R.id.btn_send);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            user_id = user.getUid();
        }

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

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("chatdata").child(formattedDate);

                    Hashtable<String, String> chat
                            = new Hashtable<String, String>();
                    chat.put("ID", user_id);
                    chat.put("text",send_text);
                    myRef.setValue(chat);

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

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }
}
