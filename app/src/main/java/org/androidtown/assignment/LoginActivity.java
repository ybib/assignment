package org.androidtown.assignment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText edittext_id, edittext_pw;
    String TAG = "LoginActivity";
    ProgressBar login_progress;

    String st_Id;
    String st_Pw;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        login_progress = (ProgressBar) findViewById(R.id.login_progress);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        Button login_button = (Button) findViewById(R.id.login);
        Button register_button =(Button) findViewById(R.id.register);
        edittext_id = (EditText) findViewById(R.id.edittext_id);
        edittext_pw = (EditText) findViewById(R.id.edittext_password);

        /*button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(),MenuActivity.class));

            }
        });*/
        login_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               st_Id= edittext_id.getText().toString();
                st_Pw= edittext_pw.getText().toString();
                if(st_Id.isEmpty()||st_Id.equals("")||st_Pw.isEmpty()||st_Pw.equals("")){
                    Toast.makeText(LoginActivity.this,"입력주세요",Toast.LENGTH_SHORT).show();
                }else {
                    userLogin(st_Id, st_Pw);
                }
                Toast.makeText(LoginActivity.this,st_Id+","+st_Pw, Toast.LENGTH_SHORT).show();

            }
        });

        register_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                st_Id= edittext_id.getText().toString();
                st_Pw= edittext_pw.getText().toString();
                if(st_Id.isEmpty()||st_Id.equals("")||st_Pw.isEmpty()||st_Pw.equals("")){
                    Toast.makeText(LoginActivity.this,"입력주세요",Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(st_Id,st_Pw);
                }
               // Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
               // LoginActivity.this.startActivity(registerIntent);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void registerUser(String email, String password ){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        Toast.makeText(LoginActivity.this,"Authetication success",
                               Toast.LENGTH_SHORT).show();

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    private void userLogin(String email, String password){
        login_progress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());


                        if(task.isSuccessful()) {
                            login_progress.setVisibility(View.GONE);
                            Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
                            LoginActivity.this.startActivity(menuIntent);
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "login failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}