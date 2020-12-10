package com.example.recyclercart.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclercart.AllOrdersActivity;
import com.example.recyclercart.R;
import com.example.recyclercart.databinding.OrderDetailLayoutBinding;
import com.example.recyclercart.databinding.OrderItemsListBinding;
import com.example.recyclercart.models.Order;

import java.util.List;

public class AllOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    public List<Order> orderList;
    public int lastSelectedItemPosition;

    // pass data to constructor
    public AllOrdersAdapter(Context context, List<Order> orderList){
        this.context = context;
        this.orderList = orderList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderDetailLayoutBinding b = OrderDetailLayoutBinding.inflate(
                LayoutInflater.from(context)
                ,parent
                ,false
        );
        return new AllOrdersViewHolder(b);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind data
        Order order = orderList.get(position);
        OrderDetailLayoutBinding b = ((AllOrdersViewHolder) holder).binding;
        b.orderUserName.setText("" + order.name);
        b.orderUserPhoneNo.setText("" + order.phoneNo);
        b.orderTotalItems.setText("Items: " + order.total_items);
        b.orderTotalPrice.setText("Rs: " + order.total_price);
        setupOrderStatus(b, order.action);
        setupProductsView(b, order);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }
    @SuppressLint("SetTextI18n")
    private void setupProductsView(OrderDetailLayoutBinding b, Order order) {
        b.allOrderItems.removeAllViews();
        for (int i = 0; i< order.orderItems.size(); i++) {
            OrderItemsListBinding bn = OrderItemsListBinding.inflate(LayoutInflater.from(context));
            bn.itemName.setText("" + order.orderItems.get(i).name);
            bn.itemQuantity.setText("Total Items: " + order.orderItems.get(i).quantity);
            bn.itemPrice.setText("Rs. " + order.orderItems.get(i).price);

            b.allOrderItems.addView(bn.getRoot());
        }

    }

    @SuppressLint("SetTextI18n")
    private void setupOrderStatus(OrderDetailLayoutBinding b, int action){
        if (action == Order.OrderStatus.PLACED) {
            b.orderStatus.setText("PLACED");
            b.orderStatus.setTextColor(Color.GREEN);
            showContextMenu(b.getRoot(), action);
        } else if (action == Order.OrderStatus.DECLINED) {
            b.orderStatus.setText("DECLINED");
            b.orderStatus.setTextColor(Color.RED);
        } else {
            b.orderStatus.setText("DELIVERED");
            b.orderStatus.setTextColor(Color.YELLOW);
        }
    }

    private void showContextMenu(ConstraintLayout root, int action) {
        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (!(context instanceof AllOrdersActivity)) {
                    return;
                }

                if (action == Order.OrderStatus.PLACED) {
                    AllOrdersActivity activity = ((AllOrdersActivity) context);
                    activity.getMenuInflater().inflate(R.menu.order_status_contextual_menu, menu);
                }
            }
        });
    }


    public static class AllOrdersViewHolder extends RecyclerView.ViewHolder {
        OrderDetailLayoutBinding binding;

        public AllOrdersViewHolder(@NonNull OrderDetailLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
