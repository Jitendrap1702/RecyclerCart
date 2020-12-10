package com.example.recyclercart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclercart.adapters.AllOrdersAdapter;
import com.example.recyclercart.constants.Constants;
import com.example.recyclercart.databinding.ActivityAllOrdersBinding;
import com.example.recyclercart.fcmsender.FCMSender;
import com.example.recyclercart.fcmsender.MessageFormatter;
import com.example.recyclercart.models.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AllOrdersActivity extends AppCompatActivity {

    ActivityAllOrdersBinding binding;

    private Context context;

    List<Order> orderList = new ArrayList<>();

    MyApp myApp;

    AllOrdersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAllOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myApp = (MyApp) getApplicationContext();

        fetchAllOrdersFromDB();

    }
    // fetch all orders from firestore
    private void fetchAllOrdersFromDB() {
        myApp.db.collection(Constants.ORDERS)
                .orderBy(Constants.ORDER_TIME, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Order order = document.toObject(Order.class);
                            orderList.add(order);
                        }
                        setupAdapter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AllOrdersActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
        // set up adapter
    private void setupAdapter() {
        adapter = new AllOrdersAdapter(this, orderList);
        binding.recyclerViewAllOrders.setAdapter(adapter);

        binding.recyclerViewAllOrders.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.recyclerViewAllOrders.addItemDecoration(itemDecor);

    }

    // setup for order status contextual menu

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.declined:
                declineOrder();
                return true;

            case R.id.delivered:
                delieverOrder();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void delieverOrder() {
        Order orderToBeDeliver = adapter.orderList.get(adapter.lastSelectedItemPosition);
        orderToBeDeliver.action = Order.OrderStatus.DELIVERED;
        adapter.notifyDataSetChanged();
        sendNotificationToUser("DELIVERED", orderToBeDeliver);
        updateFirestore(orderToBeDeliver);
    }


    private void declineOrder() {
        Order orderToBeDeclined = adapter.orderList.get(adapter.lastSelectedItemPosition);
        orderToBeDeclined.action = Order.OrderStatus.DECLINED;
        adapter.notifyDataSetChanged();
        sendNotificationToUser("DECLINED", orderToBeDeclined);
        updateFirestore(orderToBeDeclined);
    }

    private void sendNotificationToUser(String message, Order order) {
        new FCMSender().send(MessageFormatter.getSampleMessage("users", "Your Order!", message), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("bxj", "Failure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("cjsc", "Success");
            }
        });

    }



    private void updateFirestore(Order order) {
        myApp.db.collection(Constants.ORDERS).document(order.orderID)
                .set(order, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AllOrdersActivity.this, "Order status updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AllOrdersActivity.this, "Order status failed to update!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}