package com.dogukan.hizolcer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SelectOption extends AppCompatActivity {
        private Button button;
        private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrackSpeed();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://designproject-adec5-default-rtdb.firebaseio.com");
        DatabaseReference myRef = database.getReference("Anlık Hız");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(int.class);
                Log.i("Hız","Hedef kişinin anlık hızı: " +value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Hız", "Bulut sisteminden hız verisi okunamadı." +
                        "Bir sorun oluştu.", error.toException());
            }
        });

    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openTrackSpeed() {
        Intent intent1 = new Intent(this, TrackSpeed.class);
        startActivity(intent1);
    }
}