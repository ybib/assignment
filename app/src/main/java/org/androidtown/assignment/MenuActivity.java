package org.androidtown.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        Button button4 = (Button) findViewById(R.id.button4);
        Intent intent = new Intent(getApplicationContext(),TimeTable.class);
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),TimeTable.class);
                startActivityForResult(intent,105);
            }
        });

        Button chat_button = (Button) findViewById(R.id.chat_button);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(MenuActivity.this, ChatActivity.class);
                MenuActivity.this.startActivity(chatIntent);
            }
        });

        Button posintgBoard_btn = (Button) findViewById(R.id.posintgBoard_btn);
        posintgBoard_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent postingintent = new Intent(MenuActivity.this, ListAcivity.class);
                MenuActivity.this.startActivity(postingintent);
            }

        });



        //Intent passedIntent = getIntent();
        //  processIntent(passedIntent);
    }

    private  void processIntent(Intent intent){
        if (intent != null){
            ArrayList<String> names=(ArrayList<String>)intent.getSerializableExtra("names");
            if (names  != null){
                Toast.makeText(getApplicationContext(), "전달받은 이름 갯수:" + names.size(), Toast.LENGTH_SHORT).show();
            }
            /*SimpleData data = intent.getParcelableExtra("data");
            if (data != null){
                Toast.makeText(getApplicationContext(), "전달받은 SimpleData:" + data.message, Toast.LENGTH_SHORT).show();
            }*/
        }
    }




}

