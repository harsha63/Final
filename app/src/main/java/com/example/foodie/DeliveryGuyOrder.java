package com.example.foodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.List;

public class DeliveryGuyOrder extends AppCompatActivity {
    private static final String TAG = "DDDDDDDDDDDD" ;
    RecyclerView recyclerView;
    OrderAdapter adapter;
    List<Order>  ordersList,orderTemporary;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Button ordersummary;
    double lat1,long1,lat2,long2, Radius;
    ArrayList<Double>latitude = new ArrayList<>();
    ArrayList<Double>longitude = new ArrayList<>();
    String Cust, DeliveryUser;
    Order orders;
    int count = 0;
    int count1 = 0;
    Button accept;

    public static void wait(int ms){
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliveryboy_orders);
        //accept=findViewById(R.id.accept1);
        Log.d(TAG,"The distance that comes out is this");
        fStore = FirebaseFirestore.getInstance();
        ordersList = new ArrayList<>();
        orderTemporary = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //ordersummary=findViewById(R.id.button7);

        DeliveryUser = FirebaseAuth.getInstance().getUid();
        if(ordersList.size()>0){
            ordersList.clear();
        }

        DocumentReference doc = fStore.collection("Customers").document(DeliveryUser);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot f = task.getResult();
                lat2 = f.getDouble("Latitude");
                long2 = f.getDouble("Longitude");
                Radius = f.getDouble("Radius");
                Log.d(TAG,"But, the secondary value is: "+lat2+" "+long2);
            }
        });
        fStore.collection("Orders").whereEqualTo("Assigned",false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            orders = new Order(documentSnapshot.getString("UserID"),
                                    documentSnapshot.getString("Restaurant"), documentSnapshot.getString("UserName"),
                                    documentSnapshot.getString("UserPhone"), documentSnapshot.getDouble("Price"),
                                    documentSnapshot.getBoolean("Assigned"));
                            orderTemporary.add(orders);
                            Log.d(TAG,"Order object name: "+orders.getuName());
                            String Rid = documentSnapshot.getString("RestroID");
                            DocumentReference documentReference = fStore.collection("RestaurantList").document(Rid);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            lat1 = document.getDouble("Latitude");
                                            latitude.add(lat1);
                                            long1 = document.getDouble("Longitude");
                                            longitude.add(long1);
                                            Log.d(TAG,"But, the initial value is: "+lat1+" "+long1);
                                            count++;

                                            final String restuuu = document.getString("Name");
                                            DocumentReference doc = fStore.collection("Customers").document(DeliveryUser);
                                            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot docu = task.getResult();
                                                        if (docu.exists()) {

                                                            String name = docu.getString("fName");
                                                            Location RestLoc = new Location("Rest");
                                                            RestLoc.setLatitude(latitude.get(count1));
                                                            RestLoc.setLongitude(longitude.get(count1));
                                                            Location CustLoc = new Location("Cust");
                                                            CustLoc.setLatitude(lat2);
                                                            CustLoc.setLongitude(long2);

                                                            Log.d(TAG, "The distance that comes out is " + CustLoc.distanceTo(RestLoc) / 1000 + " Yes. " + count1 + " " + orders.getRest());
                                                            double dist = CustLoc.distanceTo(RestLoc)/1000;

                                                            if (CustLoc.distanceTo(RestLoc) / 1000 < Radius) {
                                                                ordersList.add(orderTemporary.get(count1));
                                                                Log.d(TAG, "The distance that comes out is " + CustLoc.distanceTo(RestLoc) / 1000 + " Yes. " + count1 + " " + orders.getRest());
                                                                Log.d(TAG,long1+" "+lat1);

                                                            }
                                                            count1++;
                                                            if(count1==count) {
                                                                Log.d(TAG,"We are in the end");
                                                                adapter = new OrderAdapter(DeliveryGuyOrder.this, ordersList, dist);
                                                                recyclerView.setAdapter(adapter);
                                                            }
                                                        }
                                                    }
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

                       /*     if(documentSnapshot.getBoolean("Assigned") == false){
                                ordersList.add(orders);
                            }*/

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DeliveryGuyOrder.this, "failed :(", Toast.LENGTH_SHORT).show();
                        Log.v("Failed", e.getMessage());
                    }
                });


        /*CollectionReference listCollectionReference = fStore.collection("Orders");
        Query listQuery = listCollectionReference
                .whereEqualTo("Assigned",false);
        listQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        List list = document.toObject(List.class);
                        ordersList.add((Order) list);
                    }
                }
            }
        });
*/
/*        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OrderSummary.class));
            }
        });*/

    }
}
