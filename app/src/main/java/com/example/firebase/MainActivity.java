package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1000;
    Button button;
    EditText editText;
    ListView listView;
    ArrayList<String>notes;
    FirebaseUser firebaseUser;
    IdpResponse response;

    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.btn);
        editText=findViewById(R.id.text);
        listView=findViewById(R.id.list);
        notes=new ArrayList<>();





        arrayAdapter= new ArrayAdapter<>(this,R.layout.item_row,R.id.tViewList,notes);
        listView.setAdapter(arrayAdapter);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser !=null) {
            AddListener();
        }
        else
        {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()
                                    ))
                            .build(),
                    RC_SIGN_IN);
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                AddListener();

                Log.i("result is",""+firebaseUser.getDisplayName()+"       "+firebaseUser.getUid());
            }

            } else {
                // Sign in failed

            if (response == null) {
                    // User pressed back button

                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                    return;
                }

            }
        }

        public void AddListener()
        {
            final DatabaseReference dbref= FirebaseDatabase.getInstance().getReference();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String note=editText.getText().toString();
                    dbref.child("note").child(firebaseUser.getUid()).push().setValue(note);
                    // dbref.child("todo").push().setValue(note);

                }
            });

            dbref.child("note").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String data=dataSnapshot.getValue(String.class);
                    notes.add(data);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

