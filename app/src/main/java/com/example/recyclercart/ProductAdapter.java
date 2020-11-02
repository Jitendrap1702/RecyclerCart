package com.example.recyclercart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclercart.databinding.ProductItemBinding;
import com.example.recyclercart.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product>productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //1. Inflate the layout for product_item.xml
        ProductItemBinding b = ProductItemBinding.inflate(
                LayoutInflater.from(context)
                ,parent
                ,false
        );
        //2. Create ViewHolder object and return
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //1. Get the data at position
        Product product = productList.get(position);

        //2. Bind the data
        holder.b.name.setText(String.format("%s (Rs. %d)",product.name,product.price));

        //Quantity

        holder.b.quantity.setText(product.qty+"");

        //visibility of button

        holder.b.decrement.setVisibility(product.qty>0?View.VISIBLE:View.GONE);
        holder.b.quantity.setVisibility(product.qty>0? View.VISIBLE: View.GONE);

        //increment button

        holder.b.increment.setOnClickListener(v -> {
            product.qty++;
            notifyItemChanged(position);
        });

        //decrement button

        holder.b.decrement.setOnClickListener(v -> {
            product.qty--;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final ProductItemBinding b;
        public ViewHolder(@NonNull ProductItemBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}