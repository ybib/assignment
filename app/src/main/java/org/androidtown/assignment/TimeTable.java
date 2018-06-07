package org.androidtown.assignment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import me.grantland.widget.AutofitTextView;

public class TimeTable extends AppCompatActivity {

    private AutofitTextView auto_temp;
    private TextView mon[] = new TextView[14];
    private TextView tue[] = new TextView[14];
    private TextView wed[] = new TextView[14];
    private TextView thr[] = new TextView[14];
    private TextView fri[] = new TextView[14];
    private TextView sat[] = new TextView[14];
    private TextView sun[] = new TextView[14];
    String user_id;
    String user_name;
    FirebaseDatabase database;
    String trainer;
    int temp; // 임시 전역변수 - singleChoiceItems 에서 선택항목 저장시 사용

    // 다이얼로그의 ID를 보기 좋은 상수로 선언해서 사용한다
    final int DIALOG_TEXT = 1;
    final int DIALOG_LIST = 2; // 리스트 형식의 다이얼로그 ID
    final int DIALOG_RADIO= 3; // 하나만 선택할 수 있는 다이얼로그 ID



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


        for(int i=0; i<14;i++) {
            mon[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v.getId());
                }
            });
            tue[i].setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                   // new AlertDialog.Builder(TimeTable.this).setTitle("확인").show();
                    showDialog(v.getId());
                }
            });
            wed[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new AlertDialog.Builder(TimeTable.this).setTitle("확인").show();
                    showDialog(v.getId());
                }
            });
            thr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  // new AlertDialog.Builder(TimeTable.this).setTitle("확인").show();
                    showDialog(v.getId());
                }
            });
            fri[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // new AlertDialog.Builder(TimeTable.this).setTitle("확인").show();
                    showDialog(v.getId());
                }
            });
            sat[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // new AlertDialog.Builder(TimeTable.this).setTitle("확인").show();
                    showDialog(v.getId());
                }
            });
            sun[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // new AlertDialog.Builder(TimeTable.this).setTitle("확인").show();
                    showDialog(v.getId());
                }
            });

        }



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
                    user_name = doc.getString("name");
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

                            String thursday[] = new String[14];
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
                            readscheduler(sunday,temp);

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
                                if(monday[i].length() > 3){
                                   // mon[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    mon[i].setBackgroundColor(getResources().getColor(R.color.colorpink));
                                }


                                tue[i].setText(tuseday[i]);
                                tue[i].setTextColor(Color.parseColor("#000000"));
                                if(tuseday[i].length() > 3){
                                   // tue[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    tue[i].setBackgroundColor(getResources().getColor(R.color.colorpink));

                                }

                                wed[i].setText(wednesday[i]);
                                wed[i].setTextColor(Color.parseColor("#000000"));
                                if(wednesday[i].length() > 3){
                                   // wed[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    wed[i].setBackgroundColor(getResources().getColor(R.color.colorpink));
                                }

                                thr[i].setText(thursday[i]);
                                thr[i].setTextColor(Color.parseColor("#000000"));
                                if(thursday[i].length() > 3){
                                   // thr[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    thr[i].setBackgroundColor(getResources().getColor(R.color.colorpink));
                                }

                                fri[i].setText(friday[i]);
                                fri[i].setTextColor(Color.parseColor("#000000"));
                                if(friday[i].length() > 3){
                                    //fri[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    fri[i].setBackgroundColor(getResources().getColor(R.color.colorpink));
                                }

                                sat[i].setText(saturday[i]);
                                sat[i].setTextColor(Color.parseColor("#000000"));
                                if(saturday[i].length() > 3){
                                   // sat[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    sat[i].setBackgroundColor(getResources().getColor(R.color.colorpink));
                                }

                                sun[i].setText(sunday[i]);
                                sun[i].setTextColor(Color.parseColor("#000000"));
                                if(sunday[i].length() > 3){
                                   // sun[i].setBackgroundColor(Color.parseColor("#FF0000"));
                                    sun[i].setBackgroundColor(getResources().getColor(R.color.colorpink));
                                }

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

        thr[0] = (TextView) findViewById(R.id.thu0);
        thr[1] = (TextView) findViewById(R.id.thu1);
        thr[2] = (TextView) findViewById(R.id.thu2);
        thr[3] = (TextView) findViewById(R.id.thu3);
        thr[4] = (TextView) findViewById(R.id.thu4);
        thr[5] = (TextView) findViewById(R.id.thu5);
        thr[6] = (TextView) findViewById(R.id.thu6);
        thr[7] = (TextView) findViewById(R.id.thu7);
        thr[8] = (TextView) findViewById(R.id.thu8);
        thr[9] = (TextView) findViewById(R.id.thu9);
        thr[10] = (TextView) findViewById(R.id.thu10);
        thr[11] = (TextView) findViewById(R.id.thu11);
        thr[12] = (TextView) findViewById(R.id.thu12);
        thr[13] = (TextView) findViewById(R.id.thu13);

        fri[0] = (TextView) findViewById(R.id.fri0);
        fri[1] = (TextView) findViewById(R.id.fri1);
        fri[2] = (TextView) findViewById(R.id.fri2);
        fri[3] = (TextView) findViewById(R.id.fri3);
        fri[4] = (TextView) findViewById(R.id.fri4);
        fri[5] = (TextView) findViewById(R.id.fri5);
        fri[6] = (TextView) findViewById(R.id.fri6);
        fri[7] = (TextView) findViewById(R.id.fri7);
        fri[8] = (TextView) findViewById(R.id.fri8);
        fri[9] = (TextView) findViewById(R.id.fri9);
        fri[10] = (TextView) findViewById(R.id.fri10);
        fri[11] = (TextView) findViewById(R.id.fri11);
        fri[12] = (TextView) findViewById(R.id.fri12);
        fri[13] = (TextView) findViewById(R.id.fri13);

        sat[0] = (TextView) findViewById(R.id.sat0);
        sat[1] = (TextView) findViewById(R.id.sat1);
        sat[2] = (TextView) findViewById(R.id.sat2);
        sat[3] = (TextView) findViewById(R.id.sat3);
        sat[4] = (TextView) findViewById(R.id.sat4);
        sat[5] = (TextView) findViewById(R.id.sat5);
        sat[6] = (TextView) findViewById(R.id.sat6);
        sat[7] = (TextView) findViewById(R.id.sat7);
        sat[8] = (TextView) findViewById(R.id.sat8);
        sat[9] = (TextView) findViewById(R.id.sat9);
        sat[10] = (TextView) findViewById(R.id.sat10);
        sat[11] = (TextView) findViewById(R.id.sat11);
        sat[12] = (TextView) findViewById(R.id.sat12);
        sat[13] = (TextView) findViewById(R.id.sat13);

        sun[0] = (TextView) findViewById(R.id.sun0);
        sun[1] = (TextView) findViewById(R.id.sun1);
        sun[2] = (TextView) findViewById(R.id.sun2);
        sun[3] = (TextView) findViewById(R.id.sun3);
        sun[4] = (TextView) findViewById(R.id.sun4);
        sun[5] = (TextView) findViewById(R.id.sun5);
        sun[6] = (TextView) findViewById(R.id.sun6);
        sun[7] = (TextView) findViewById(R.id.sun7);
        sun[8] = (TextView) findViewById(R.id.sun8);
        sun[9] = (TextView) findViewById(R.id.sun9);
        sun[10] = (TextView) findViewById(R.id.sun10);
        sun[11] = (TextView) findViewById(R.id.sun11);
        sun[12] = (TextView) findViewById(R.id.sun12);
        sun[13] = (TextView) findViewById(R.id.sun13);


    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(final int id) {
        // 다이얼로그를 처음 생성할 때 호출됨
        Log.d("test", "onCreateDialog");

        // id 값에 따라서 다이얼로그를 구분해서 띄워줌
                // 버튼 클릭시 AlertDialog 를 띄우기
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(TimeTable.this);
                builder
                        .setMessage("예약하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                temp = which;
                                Toast.makeText(getApplicationContext(),
                                        user_name + "으로 예약하겠슴",
                                        Toast.LENGTH_SHORT).show();
                                TextView txt = (TextView) findViewById(id);
                                txt.setText(user_name);
                                txt.setBackgroundColor(getResources().getColor(R.color.colorpink));


                            }
                        })
                        .setNegativeButton("예약취소", null)
                        .setNeutralButton("취소", null);

                return builder.create();
        //return super.onCreateDialog(id);
    }



}
