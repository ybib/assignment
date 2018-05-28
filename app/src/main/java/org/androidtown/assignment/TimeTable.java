package org.androidtown.assignment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimeTable extends AppCompatActivity {

    private TextView mon[] = new TextView[14];
    private TextView tue[] = new TextView[14];
    private TextView wed[] = new TextView[14];
    private TextView thr[] = new TextView[14];
    private TextView fri[] = new TextView[14];
    private TextView sat[] = new TextView[14];
    private TextView sun[] = new TextView[14];
    String user_id;
    FirebaseDatabase database;
    String trainer;


    TextView txtDisplay;
    String TAG = "Timetable";
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        txtDisplay = (TextView) findViewById(R.id.mon0);
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_id = user.getEmail();
        }
        readtrainer();

       // Toast.makeText(TimeTable.this,"전달:"+trainer,Toast.LENGTH_SHORT).show();


    }

    private void readschedule() {
        DocumentReference contact = db.collection("customer").document(trainer);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Log.d(TAG, doc.getId() + " => " + doc.getData());

                    // txtDisplay.setText(data.toString());*/
                }
            }
        });
    }

    private void readtrainer() {
        DocumentReference contact = db.collection("customer").document(user_id);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Log.d(TAG, doc.getId() + " => " + doc.getData());
                    trainer = doc.getString("mappedtrainer");
                    Toast.makeText(TimeTable.this,trainer, Toast.LENGTH_SHORT).show();
                   // Toast.makeText(TimeTable.this,"전달:"+trainer,Toast.LENGTH_SHORT).show();
                   // txtDisplay.setText(data.toString());*/


                    DocumentReference contace2 = db.collection("customer").document(trainer).collection("scheduler").document("scheduler");
                    contace2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful());
                            DocumentSnapshot doc2 = task.getResult();
                            Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                            Log.d(TAG,"check:"+doc2.get("mon"));
                            String temp = doc2.get("mon").toString();
                            String monday[] = new String[14];

                            StringBuilder id  = new StringBuilder("");
                           for(int i=0;i<temp.length();i++){
                               if(temp.charAt(i)=='['|| temp.charAt(i)==']'|| temp.charAt(i)==' ')
                                   continue;
                               Log.d(TAG,temp.charAt(i)+" ");
                           }

                            Toast.makeText(TimeTable.this,"전달:"+monday[0],Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }
        });


    }
}

