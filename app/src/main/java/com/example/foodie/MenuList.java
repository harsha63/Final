package com.example.foodie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.view.menu.MenuAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuList extends AppCompatActivity {
    private static final String TAG = "HELLO";
    RecyclerView recyclerView;
    MenuAdapter adapter;
    List<MenuObject> Menul;
    FirebaseAuth mFAuth;
    FirebaseFirestore fStore;
    Button bt;
    String RestroID;

    public static int[] convertIntegers(List<Long> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menulist);
        fStore = FirebaseFirestore.getInstance();
        mFAuth = FirebaseAuth.getInstance();
        Menul = new ArrayList<MenuObject>();
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        bt = findViewById(R.id.order);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(Menul.size()>0){
            Menul.clear();
        }
        String userId = mFAuth.getUid();
        DocumentReference documentReference = fStore.collection("Orders").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        RestroID = document.getString("RestroID");
                        Log.d(TAG, RestroID+ " is the ID");
                        fStore.collection("RestaurantList").document(RestroID).collection("FoodItems").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @SuppressLint("RestrictedApi")
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData().getClass().getName());
                                            Map<String,Object> order;

                                            order = documentSnapshot.getData();
                                            for (Map.Entry mapElement : order.entrySet()) {
                                                int[] arr = {23,34,5,7};

                                                String key = (String)mapElement.getKey();

                                                // Add some bonus marks
                                                // to all the students and print it
                                                Log.d(TAG,mapElement.getValue().getClass().getName()+"Is the target");
                                                int[] arr1 = convertIntegers((List<Long>) mapElement.getValue());
                                                MenuObject restru = new MenuObject(key,arr1);
                                                Menul.add(restru);
                                                Log.d(TAG, "MenuListAdded");
                                            }

                                        }
                                        adapter = new com.example.foodie.MenuAdapter(MenuList.this,Menul);
                                        recyclerView.setAdapter(adapter);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MenuList.this, "failed :(", Toast.LENGTH_SHORT).show();
                                        Log.v("Failed", e.getMessage());
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CustomerLocation.class));
            }
        });



    }
}
