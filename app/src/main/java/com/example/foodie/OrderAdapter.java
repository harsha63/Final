package com.example.foodie;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.StructuredQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrderAdapter extends  RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    DeliveryGuyOrder driverOrders;
    private List<Order> ordersList ;
    FirebaseAuth mFAuth;
    FirebaseFirestore fStore;
    String Email;
    String OTP;
    Order od;
    double dist;

    public OrderAdapter(Context context, List<Order> ordersList, double dist) {
        this.context = context;
        this.ordersList = ordersList;
        this.dist = dist;
    }
    public static int generateRandomDigits() {
        int m = (int) Math.pow(10, 3);
        return m + new Random().nextInt(9 * m);
    }

    public void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.delivery_order_card, null, false);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, final int position) {
        Order orders = ordersList.get(position);
        holder.textViewRest.setText(orders.getRest());
        holder.textViewuname.setText(orders.getuName());
        holder.textViewPhone.setText(orders.getuPhone());
        holder.textViewPrice.setText(String.valueOf(orders.getAmount()));

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), ordersList.get(position).getuName() + " " + "Selected!", Toast.LENGTH_SHORT).show();
                mFAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                final String userId = mFAuth.getUid();
                od = ordersList.get(position);
                String orderID = od.getId();
                DocumentReference documentReference = fStore.collection("Orders").document(orderID);
                Map<String,Object> user = new HashMap<>();
                user.put("DeliveryId",userId);
                user.put("Assigned",true);
                user.put("Status",3);
                user.put("Delivery Price", dist/10);
                documentReference.set(user, SetOptions.merge());

                Intent intent = new Intent(context, OrderSummary.class);
                intent.putExtra("orderID", orderID);
                context.startActivity(intent);


                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String Uid = document.getString("UserID");
                                DocumentReference doc = fStore.collection("Customers").document(Uid);
                                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot User = task.getResult();
                                            if(User.exists()){
                                                Email = User.getString("email");
                                                OTP = String.valueOf(generateRandomDigits());
                                                try{
                                                    enableStrictMode();

                                                    //Mailer.send("obliviousalwaysforever@gmail.com","wha1sup??",Email,"Order OTP",OTP+" Is yout OTP. Do you understand?");

                                                }
                                                catch(Exception e){
                                                    System.out.println(e.toString()+ "  Hallelujah");
                                                }
                                                DocumentReference documentReference = fStore.collection("Orders").document(od.getId());
                                                Map<String,Object> users = new HashMap<>();
                                                users.put("OTP",OTP);

                                                documentReference.set(users, SetOptions.merge());

                                            }
                                        }
                                    }
                                });


                            } else {

                            }
                        } else {

                        }
                    }
                });


            }
        });

        /*     holder.textViewPrice.setText((int) orders.getAmount());*/
    }


    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView textViewRest, textViewuname, textViewPhone, textViewPrice;
        Button decline, accept;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRest = itemView.findViewById(R.id.RestroName);
            textViewuname = itemView.findViewById(R.id.custName);
            textViewPhone = itemView.findViewById(R.id.custPhone);
            textViewPrice = itemView.findViewById(R.id.price);
            //decline = itemView.findViewById(R.id.decline);
            accept = itemView.findViewById(R.id.accept);



        }
    }
}
