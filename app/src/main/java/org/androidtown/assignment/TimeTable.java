package org.androidtown.assignment;

import android.graphics.Color;
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

        readTextView();
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
                            readscheduler(monday,temp);

                            String tuseday[] = new String[14];
                            temp = doc2.get("tue").toString();
                            readscheduler(tuseday,temp);

                            String wednesday[] = new String[14];
                            temp = doc2.get("wed").toString();
                            readscheduler(wednesday,temp);

                         /*   String thursday[] = new String[14];
                            temp = doc2.get("thr").toString();
                            readscheduler(thursday,temp);

                            String friday[] = new String[14];
                            temp = doc2.get("fri").toString();
                            readscheduler(friday,temp);

                            String saturday[] = new String[14];
                            temp = doc2.get("sat").toString();
                            readscheduler(saturday,temp);

                            String sunday[] = new String[14];
                            temp = doc2.get("sun").toString();
                            readscheduler(sunday,temp);*/

                         /*   StringBuilder id  = new StringBuilder("");
                           for(int i=0;i<temp.length();i++){
                               if(temp.charAt(i)=='['|| temp.charAt(i)==']'|| temp.charAt(i)==' ')
                                   continue;
                               Log.d(TAG,temp.charAt(i)+" ");
                           }*/

                        /*   int j=0;
                           for(int i=0;i<14;i++){
                               StringBuilder reservation = new StringBuilder("");
                               for(;j<temp.length();j++){
                                   if(temp.charAt(j)=='['|| temp.charAt(j)==']'){
                                       continue;
                                   }

                                  else if(temp.charAt(j) !=','){
                                       reservation.append(temp.charAt(j));
                                   }

                               }
                               monday[i] = reservation.toString();
                           }*/

                           for(int i=0;i<14;i++) {
                               Log.d(TAG,monday[i]);
                               mon[i].setText(monday[i]);
                               mon[i].setTextColor(Color.parseColor("#000000"));

                               tue[i].setText(tuseday[i]);
                               tue[i].setTextColor(Color.parseColor("#000000"));

                               wed[i].setText(wednesday[i]);
                               wed[i].setTextColor(Color.parseColor("#000000"));

                           }

                        }
                    });


                }
            }
        });


    }

    private void readscheduler(String[] day,String temp){
        int j=0;
        for(int i=0;i<14;i++){
            StringBuilder reservation = new StringBuilder("");

            for(;j<temp.length();j++){
                if(temp.charAt(j)=='['|| temp.charAt(j)==']'){
                    continue;
                }
                if(temp.charAt(j)==','){
                    j++;
                    break;
                }
                else if(temp.charAt(j) !=',' || temp.charAt(j)==' ' ){
                    reservation.append(temp.charAt(j));
                }

            }
            day[i] = reservation.toString();
        }
    }
    private void readTextView(){
        mon[0] = (TextView) findViewById(R.id.mon0);
        mon[1] = (TextView) findViewById(R.id.mon1);
        mon[2] = (TextView) findViewById(R.id.mon2);
        mon[3] = (TextView) findViewById(R.id.mon3);
        mon[4] = (TextView) findViewById(R.id.mon4);
        mon[5] = (TextView) findViewById(R.id.mon5);
        mon[6] = (TextView) findViewById(R.id.mon6);
        mon[7] = (TextView) findViewById(R.id.mon7);
        mon[8] = (TextView) findViewById(R.id.mon8);
        mon[9] = (TextView) findViewById(R.id.mon9);
        mon[10] = (TextView) findViewById(R.id.mon10);
        mon[11] = (TextView) findViewById(R.id.mon11);
        mon[12] = (TextView) findViewById(R.id.mon12);
        mon[13] = (TextView) findViewById(R.id.mon13);

        tue[0] = (TextView) findViewById(R.id.tue0);
        tue[1] = (TextView) findViewById(R.id.tue1);
        tue[2] = (TextView) findViewById(R.id.tue2);
        tue[3] = (TextView) findViewById(R.id.tue3);
        tue[4] = (TextView) findViewById(R.id.tue4);
        tue[5] = (TextView) findViewById(R.id.tue5);
        tue[6] = (TextView) findViewById(R.id.tue6);
        tue[7] = (TextView) findViewById(R.id.tue7);
        tue[8] = (TextView) findViewById(R.id.tue8);
        tue[9] = (TextView) findViewById(R.id.tue9);
        tue[10] = (TextView) findViewById(R.id.tue10);
        tue[11] = (TextView) findViewById(R.id.tue11);
        tue[12] = (TextView) findViewById(R.id.tue12);
        tue[13] = (TextView) findViewById(R.id.tue13);

        wed[0] = (TextView) findViewById(R.id.wed0);
        wed[1] = (TextView) findViewById(R.id.wed1);
        wed[2] = (TextView) findViewById(R.id.wed2);
        wed[3] = (TextView) findViewById(R.id.wed3);
        wed[4] = (TextView) findViewById(R.id.wed4);
        wed[5] = (TextView) findViewById(R.id.wed5);
        wed[6] = (TextView) findViewById(R.id.wed6);
        wed[7] = (TextView) findViewById(R.id.wed7);
        wed[8] = (TextView) findViewById(R.id.wed8);
        wed[9] = (TextView) findViewById(R.id.wed9);
        wed[10] = (TextView) findViewById(R.id.wed10);
        wed[11] = (TextView) findViewById(R.id.wed11);
        wed[12] = (TextView) findViewById(R.id.wed12);
        wed[13] = (TextView) findViewById(R.id.wed13);



    }

}

