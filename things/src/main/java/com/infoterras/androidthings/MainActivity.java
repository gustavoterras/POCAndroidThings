package com.infoterras.androidthings;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infoterras.androidthings.model.Led;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String pinYellow = "BCM14";
    private static final String pinRed = "BCM15";
    private static final String pinBlue = "BCM18";

    private Led yellowLed;
    private Led redLed;
    private Led blueLed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yellowLed = new Led(pinYellow);
        redLed = new Led(pinRed);
        blueLed = new Led(pinBlue);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "signInAnonymously:success");
                            registerDataBaseObservable();
                        } else {
                            Log.i(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }

    public void registerDataBaseObservable() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("automation");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange: " + dataSnapshot.toString());

                Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                if (map == null) return;

                yellowLed.setStatus(map.getOrDefault("ledYellow", false));
                redLed.setStatus(map.getOrDefault("ledRed", false));
                blueLed.setStatus(map.getOrDefault("ledBlue", false));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        yellowLed.onDestroy();
        redLed.onDestroy();
        blueLed.onDestroy();
    }
}