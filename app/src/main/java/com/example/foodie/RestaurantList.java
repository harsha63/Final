package com.example.foodie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantList extends AppCompatActivity {
    RecyclerView recyclerView;
    RestaurantAdapter adapter;
    List<Restaurant>  RestroList;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Button bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list);
        fStore = FirebaseFirestore.getInstance();
        RestroList = new ArrayList<>();
        recyclerView = findViewById(R.id.Recycler_restro);
        recyclerView.setHasFixedSize(true);
        bt = findViewById(R.id.button3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(RestroList.size()>0){
            RestroList.clear();
        }

        fStore.collection("RestaurantList").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                            Restaurant restru = new Restaurant(documentSnapshot.getString("Name"),documentSnapshot.getId());
                            RestroList.add(restru);
                        }
                        adapter = new RestaurantAdapter(RestaurantList.this, RestroList);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RestaurantList.this, "failed :(", Toast.LENGTH_SHORT).show();
                        Log.v("Failed", e.getMessage());
                    }
                });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MenuList.class));
            }
        });



    }
}
