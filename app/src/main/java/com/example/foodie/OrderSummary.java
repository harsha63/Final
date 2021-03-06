package com.example.foodie;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class OrderSummary extends AppCompatActivity{
    private static final String TAG ="HIIIIIIIIIIIIIIIIIIII";
    Button otp;
    //String UserId;
    TextView price, name, ph,rest,food, delPrice;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    //String UserId =  getIntent().getStringExtra("orderID");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_summary);
        otp = findViewById(R.id.otp);
        price = findViewById(R.id.price11);
        name= findViewById(R.id.custName1);
        ph=findViewById(R.id.custPhone1);
        food=findViewById(R.id.textView8);
        rest=findViewById(R.id.rest11);
        delPrice = findViewById(R.id.price22);
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        //UserId = fAuth.getUid();

        Log.d(TAG, "getIncomingIntent: found intent extra");
        String UserId =  getIntent().getStringExtra("orderID");

        Log.d(TAG, "onCreate: userID: " + UserId);


         DocumentReference documentReference = fStore.collection("Orders").document(UserId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                price.setText(String.valueOf(documentSnapshot.getDouble("Price")));
                food.setText(documentSnapshot.getString("MenuItem"));
                rest.setText(documentSnapshot.getString("Restaurant"));
                name.setText(documentSnapshot.getString("UserName"));
                delPrice.setText(String.valueOf(documentSnapshot.getDouble("Delivery Price")));
            }
        });



        DocumentReference documentReference1 = fStore.collection("Customers").document(UserId);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name.setText(documentSnapshot.getString("fname"));
                Log.d(TAG,documentSnapshot.getString("fName")+"Hello");
                ph.setText(documentSnapshot.getString("Phone number"));
            }
        });

        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderSummary.this,OTP.class));
            }
        });

}}


