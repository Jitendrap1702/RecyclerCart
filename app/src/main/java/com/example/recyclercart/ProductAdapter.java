package com.example.recyclercart;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclercart.databinding.ProductItemBinding;
import com.example.recyclercart.databinding.VariantBasedProductBinding;
import com.example.recyclercart.databinding.WeightBasedProductBinding;
import com.example.recyclercart.models.Product;

import java.util.List;

//public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
//
//    private Context context;
//    private List<Product> productList;
//
//    public ProductAdapter(Context context, List<Product>productList) {
//        this.context = context;
//        this.productList = productList;
//    }

//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        //1. Inflate the layout for product_item.xml
//        ProductItemBinding b = ProductItemBinding.inflate(
//                LayoutInflater.from(context)
//                ,parent
//                ,false
//        );
//        //2. Create ViewHolder object and return
//        return new ViewHolder(b);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        //1. Get the data at position
//        Product product = productList.get(position);
//
//        //2. Bind the data
//        holder.b.name.setText(String.format("%s (Rs. %d)",product.name,product.price));
//
//        //Quantity
//
//        holder.b.quantity.setText(product.qty+"");
//
//        //visibility of button
//
//        holder.b.decrement.setVisibility(product.qty>0?View.VISIBLE:View.GONE);
//        holder.b.quantity.setVisibility(product.qty>0? View.VISIBLE: View.GONE);
//
//        //increment button
//
//        holder.b.increment.setOnClickListener(v -> {
//            product.qty++;
//            notifyItemChanged(position);
//        });
//
//        //decrement button
//
//        holder.b.decrement.setOnClickListener(v -> {
//            product.qty--;
//            notifyItemChanged(position);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    // creating view holder
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        public final ProductItemBinding b;
//        public ViewHolder(@NonNull ProductItemBinding b) {
//            super(b.getRoot());
//            this.b = b;
//        }
//    }
//}

// Adapter for List of products
  public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    // Needed for inflating layout
    private Context context;

    // List of data
    private List<Product> productList;

    int lastSelectedItemPosition;

    public ProductAdapter(Context context, List<Product> productList){
        this.context = context;
        this.productList = productList;
    }

    //Inflate the view for item and create a ViewHolder object based on viewType
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Product.WEIGHT_BASED){
            // Inflate WeightBasedProduct Layout
            WeightBasedProductBinding b = WeightBasedProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    ,false
                    );

            // Create object of view holder WeightBasedProduct and return
            return new WeightBasedProductVH(b);
        }
        else{
            // Inflate VariantBasedProduct Layout
            VariantBasedProductBinding b = VariantBasedProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );

            // Create object of view holder VariantBasedProduct and return
            return new VariantBasedProductVH(b);
        }
    }

    // Return viewType based on position


    @Override
    public int getItemViewType(int position) {
        return productList.get(position).type;
    }

    // Binds the data to view

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Get the data at position
        final Product product = productList.get(position);

        if (product.type == Product.WEIGHT_BASED){

            // Get binding
            WeightBasedProductBinding b = ((WeightBasedProductVH) holder).b;

            // Bind data
            b.name.setText(product.name);
            b.pricePerKg.setText("Rs. " + product.pricePerKg);
            b.minQty.setText("MinQty - " + product.minQty);

            //Setup Contextual Menu inflation
            setupContextMenu(b.getRoot());
        }
        else{

            // Get Binding
            VariantBasedProductBinding b = ((VariantBasedProductVH) holder).b;

            // Bind data
            b.name.setText(product.name);
            b.variants.setText(product.variantsString());

            //Setup Contextual Menu inflation
            setupContextMenu(b.getRoot());
        }
        //Save dynamic position of selected item to access it in Activity
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });
    }

    private void setupContextMenu(ConstraintLayout root) {
        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if(! (context instanceof MainActivity))
                    return;

                ((MainActivity) context)
                        .getMenuInflater().inflate(R.menu.product_contextual_menu, menu);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // View holder for WeightBasedProduct
    public static class WeightBasedProductVH extends RecyclerView.ViewHolder{

        WeightBasedProductBinding b;

        public WeightBasedProductVH(@NonNull WeightBasedProductBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    // View holder for VariantBasedProduct
    public static class VariantBasedProductVH extends RecyclerView.ViewHolder{

        VariantBasedProductBinding b;

        public VariantBasedProductVH(@NonNull VariantBasedProductBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}