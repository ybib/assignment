package org.androidtown.assignment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class TimeTable extends AppCompatActivity {

    private Button[][] buttons=new Button[13][7];
    String user_id;
    String user_name;
    FirebaseDatabase database;
    String trainer;
    String trainerName;
    Button sup;
    String TAG="Timetable";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        readButtons();
        db=FirebaseFirestore.getInstance();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            user_id=user.getEmail();

            DocumentReference cont=db.collection("customerList").document(user_id);
            cont.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot docm=task.getResult();
                        Log.d(TAG, docm.getId() + "=>" + docm.getData());
                        user_name=docm.getString("name");
                    }
                }
            });
        }
        readTrainer();
        setButtonsListener();
    }

    private void makeReservation(final String wd, int rt, String temp) {
        StringBuilder sb=new StringBuilder("");
        StringBuilder cusName=new StringBuilder("");
        int dotCount=0;
        int charCount=0;
        sb.append(temp);
        int yoil;

        if(wd=="Mon") {
            yoil=0;
        } else if(wd=="Tue") {
            yoil=1;
        } else if(wd=="Wed") {
            yoil=2;
        } else if(wd=="Thu") {
            yoil=3;
        } else if(wd=="Fri") {
            yoil=4;
        } else if(wd=="Sat") {
            yoil=5;
        } else {
            yoil=6;
        }

        for(int i=0; i<temp.length(); i++) {
            if(dotCount==rt) {
                for(int j=i; j<temp.length(); j++) {
                    if(sb.charAt(j)!=',') {
                        if(sb.charAt(j)!=' ') {
                            cusName.append(sb.charAt(j));
                        }
                        charCount++;
                    } else {
                        break;
                    }
                }

                String sName=cusName.toString();
                if(charCount>1) {
                    if(!sName.equals(user_name)) {
                        new AlertDialog.Builder(TimeTable.this).setTitle("Already Reserved").show();
                        return;
                    } else {
                        int Count=0;
                        for(int k=0; k<temp.length(); k++) {
                            if (Count == rt) {
                                sb.delete(k, k + sName.length());
                                break;
                            }
                            if(sb.charAt(k)==',') {
                                Count++;
                            }
                        }

                        final String guichan=sb.toString();
                        final DocumentReference DRef=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(DRef);
                                transaction.update(DRef, wd, guichan);

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Transaction success!" + guichan);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Transaction failure.", e);
                                    }
                                });


                        buttons[rt][yoil].setText(" ");
                        buttons[rt][yoil].setBackgroundColor(Color.parseColor("#40000000"));
                        new AlertDialog.Builder(TimeTable.this).setTitle("Reservation Canceled").show();
                        return;
                    }
                }
                sb.insert(i, user_name);
                break;
            }
            if(sb.charAt(i)==',') {
                dotCount++;
            }
        }

        //

        final String makeRSV=sb.toString();
        final DocumentReference col=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(col);
                transaction.update(col, wd, makeRSV);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!" + makeRSV);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });


        buttons[rt][yoil].setText(user_name);
        buttons[rt][yoil].setTextColor(Color.parseColor("#eeeeee"));
        buttons[rt][yoil].setBackgroundColor(getResources().getColor(R.color.colorAccent));

        new AlertDialog.Builder(TimeTable.this).setTitle("You Reserved").show();
    }

    private void readTrainer() {
        DocumentReference contact=db.collection("customerList").document(user_id);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc=task.getResult();
                    Log.d(TAG, doc.getId() + "=>" + doc.getData());
                    trainer=doc.getString("trainer");

                    DocumentReference ref=db.collection("customerList").document(trainer);
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful());
                            DocumentSnapshot docC=task.getResult();
                            Log.d(TAG, docC.getId() + " => " + docC.getData());
                            trainerName=docC.getString("name");

                            String s="Trainer: " + trainerName;
                            TextView showTrainer=(TextView)findViewById(R.id.textViewTrainer);
                            showTrainer.setText(s);
                        }
                    });

                    DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                    contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful());
                            DocumentSnapshot doc2=task.getResult();
                            Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                            Log.d(TAG,"check:"+doc2.get("mon"));
                            String temp;
                            String weekDay[][] = new String[7][13];

                            temp= doc2.get("Mon").toString();
                            readSchedule(weekDay[0],temp);

                            temp= doc2.get("Tue").toString();
                            readSchedule(weekDay[1],temp);

                            temp= doc2.get("Wed").toString();
                            readSchedule(weekDay[2],temp);

                            temp= doc2.get("Thu").toString();
                            readSchedule(weekDay[3],temp);

                            temp= doc2.get("Fri").toString();
                            readSchedule(weekDay[4],temp);

                            temp= doc2.get("Sat").toString();
                            readSchedule(weekDay[5],temp);

                            temp= doc2.get("Sun").toString();
                            readSchedule(weekDay[6],temp);

                            for(int i=0; i<13; i++) {
                                for(int j=0; j<7; j++) {
                                    buttons[i][j].setText(weekDay[j][i]);
                                    buttons[i][j].setTextColor(Color.parseColor("#eeeeee")); //
                                    if(weekDay[j][i].length()>1) {
                                        buttons[i][j].setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void readSchedule(String[] weekDay, String temp) {
        int j=0;
        for(int i=0; i<13; i++) {
            StringBuilder member=new StringBuilder("");

            for(; j<temp.length(); j++) {
                if(temp.charAt(j)=='[' || temp.charAt(j)==']') {
                    continue;
                }
                if(temp.charAt(j)==',') {
                    j++;
                    break;
                }
                else {
                    member.append(temp.charAt(j));
                }
            }
            weekDay[i] = member.toString();
        }
    }

    private void readButtons() {

        buttons[0][0]=(Button)findViewById(R.id.buttonMon8);
        buttons[0][1]=(Button)findViewById(R.id.buttonTue8);
        buttons[0][2]=(Button)findViewById(R.id.buttonWed8);
        buttons[0][3]=(Button)findViewById(R.id.buttonThu8);
        buttons[0][4]=(Button)findViewById(R.id.buttonFri8);
        buttons[0][5]=(Button)findViewById(R.id.buttonSat8);
        buttons[0][6]=(Button)findViewById(R.id.buttonSun8);

        buttons[1][0]=(Button)findViewById(R.id.buttonMon9);
        buttons[1][1]=(Button)findViewById(R.id.buttonTue9);
        buttons[1][2]=(Button)findViewById(R.id.buttonWed9);
        buttons[1][3]=(Button)findViewById(R.id.buttonThu9);
        buttons[1][4]=(Button)findViewById(R.id.buttonFri9);
        buttons[1][5]=(Button)findViewById(R.id.buttonSat9);
        buttons[1][6]=(Button)findViewById(R.id.buttonSun9);

        buttons[2][0]=(Button)findViewById(R.id.buttonMon10);
        buttons[2][1]=(Button)findViewById(R.id.buttonTue10);
        buttons[2][2]=(Button)findViewById(R.id.buttonWed10);
        buttons[2][3]=(Button)findViewById(R.id.buttonThu10);
        buttons[2][4]=(Button)findViewById(R.id.buttonFri10);
        buttons[2][5]=(Button)findViewById(R.id.buttonSat10);
        buttons[2][6]=(Button)findViewById(R.id.buttonSun10);

        buttons[3][0]=(Button)findViewById(R.id.buttonMon11);
        buttons[3][1]=(Button)findViewById(R.id.buttonTue11);
        buttons[3][2]=(Button)findViewById(R.id.buttonWed11);
        buttons[3][3]=(Button)findViewById(R.id.buttonThu11);
        buttons[3][4]=(Button)findViewById(R.id.buttonFri11);
        buttons[3][5]=(Button)findViewById(R.id.buttonSat11);
        buttons[3][6]=(Button)findViewById(R.id.buttonSun11);

        buttons[4][0]=(Button)findViewById(R.id.buttonMon12);
        buttons[4][1]=(Button)findViewById(R.id.buttonTue12);
        buttons[4][2]=(Button)findViewById(R.id.buttonWed12);
        buttons[4][3]=(Button)findViewById(R.id.buttonThu12);
        buttons[4][4]=(Button)findViewById(R.id.buttonFri12);
        buttons[4][5]=(Button)findViewById(R.id.buttonSat12);
        buttons[4][6]=(Button)findViewById(R.id.buttonSun12);

        buttons[5][0]=(Button)findViewById(R.id.buttonMon13);
        buttons[5][1]=(Button)findViewById(R.id.buttonTue13);
        buttons[5][2]=(Button)findViewById(R.id.buttonWed13);
        buttons[5][3]=(Button)findViewById(R.id.buttonThu13);
        buttons[5][4]=(Button)findViewById(R.id.buttonFri13);
        buttons[5][5]=(Button)findViewById(R.id.buttonSat13);
        buttons[5][6]=(Button)findViewById(R.id.buttonSun13);

        buttons[6][0]=(Button)findViewById(R.id.buttonMon14);
        buttons[6][1]=(Button)findViewById(R.id.buttonTue14);
        buttons[6][2]=(Button)findViewById(R.id.buttonWed14);
        buttons[6][3]=(Button)findViewById(R.id.buttonThu14);
        buttons[6][4]=(Button)findViewById(R.id.buttonFri14);
        buttons[6][5]=(Button)findViewById(R.id.buttonSat14);
        buttons[6][6]=(Button)findViewById(R.id.buttonSun14);

        buttons[7][0]=(Button)findViewById(R.id.buttonMon15);
        buttons[7][1]=(Button)findViewById(R.id.buttonTue15);
        buttons[7][2]=(Button)findViewById(R.id.buttonWed15);
        buttons[7][3]=(Button)findViewById(R.id.buttonThu15);
        buttons[7][4]=(Button)findViewById(R.id.buttonFri15);
        buttons[7][5]=(Button)findViewById(R.id.buttonSat15);
        buttons[7][6]=(Button)findViewById(R.id.buttonSun15);

        buttons[8][0]=(Button)findViewById(R.id.buttonMon16);
        buttons[8][1]=(Button)findViewById(R.id.buttonTue16);
        buttons[8][2]=(Button)findViewById(R.id.buttonWed16);
        buttons[8][3]=(Button)findViewById(R.id.buttonThu16);
        buttons[8][4]=(Button)findViewById(R.id.buttonFri16);
        buttons[8][5]=(Button)findViewById(R.id.buttonSat16);
        buttons[8][6]=(Button)findViewById(R.id.buttonSun16);

        buttons[9][0]=(Button)findViewById(R.id.buttonMon17);
        buttons[9][1]=(Button)findViewById(R.id.buttonTue17);
        buttons[9][2]=(Button)findViewById(R.id.buttonWed17);
        buttons[9][3]=(Button)findViewById(R.id.buttonThu17);
        buttons[9][4]=(Button)findViewById(R.id.buttonFri17);
        buttons[9][5]=(Button)findViewById(R.id.buttonSat17);
        buttons[9][6]=(Button)findViewById(R.id.buttonSun17);

        buttons[10][0]=(Button)findViewById(R.id.buttonMon18);
        buttons[10][1]=(Button)findViewById(R.id.buttonTue18);
        buttons[10][2]=(Button)findViewById(R.id.buttonWed18);
        buttons[10][3]=(Button)findViewById(R.id.buttonThu18);
        buttons[10][4]=(Button)findViewById(R.id.buttonFri18);
        buttons[10][5]=(Button)findViewById(R.id.buttonSat18);
        buttons[10][6]=(Button)findViewById(R.id.buttonSun18);

        buttons[11][0]=(Button)findViewById(R.id.buttonMon19);
        buttons[11][1]=(Button)findViewById(R.id.buttonTue19);
        buttons[11][2]=(Button)findViewById(R.id.buttonWed19);
        buttons[11][3]=(Button)findViewById(R.id.buttonThu19);
        buttons[11][4]=(Button)findViewById(R.id.buttonFri19);
        buttons[11][5]=(Button)findViewById(R.id.buttonSat19);
        buttons[11][6]=(Button)findViewById(R.id.buttonSun19);

        buttons[12][0]=(Button)findViewById(R.id.buttonMon20);
        buttons[12][1]=(Button)findViewById(R.id.buttonTue20);
        buttons[12][2]=(Button)findViewById(R.id.buttonWed20);
        buttons[12][3]=(Button)findViewById(R.id.buttonThu20);
        buttons[12][4]=(Button)findViewById(R.id.buttonFri20);
        buttons[12][5]=(Button)findViewById(R.id.buttonSat20);
        buttons[12][6]=(Button)findViewById(R.id.buttonSun20);

    }

    private void setButtonsListener() {
        buttons[0][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Mon").toString();
                                        makeReservation("Mon", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttons[0][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Tue").toString();
                                        makeReservation("Tue", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttons[0][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Wed").toString();
                                        makeReservation("Wed", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttons[0][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Thu").toString();
                                        makeReservation("Thu", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttons[0][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Fri").toString();
                                        makeReservation("Fri", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttons[0][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sat").toString();
                                        makeReservation("Sat", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttons[0][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 0, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[1][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 1, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[2][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 2, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[3][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 3, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[4][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 4, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[5][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 5, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[6][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 6, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[7][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 7, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[8][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 8, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[9][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 9, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[10][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 10, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[11][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 11, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        buttons[12][6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference contact=db.collection("customerList").document(user_id);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc=task.getResult();
                            Log.d(TAG, doc.getId() + "=>" + doc.getData());
                            trainer=doc.getString("trainer");

                            DocumentReference contact2=db.collection("customerList").document(trainer).collection("schedule").document("schedule");
                            contact2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc2 = task.getResult();
                                        Log.d(TAG, doc2.getId() + " => " + doc2.getData());
                                        String temp;

                                        temp = doc2.get("Sun").toString();
                                        makeReservation("Sun", 12, temp);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}