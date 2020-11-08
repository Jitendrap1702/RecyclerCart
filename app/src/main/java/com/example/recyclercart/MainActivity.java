package com.example.recyclercart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.recyclercart.models.Product;
import com.example.recyclercart.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding b;
    private ArrayList<Product> products;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupProductList();

//        // it's for selecting weight from weightPicker
//        WeightPicker weightPicker = new WeightPicker();
//        weightPicker.show(this, new WeightPicker.OnWeightPickedListener() {
//            @Override
//            public void onWeightPicked(int kg, int g) {
//
//                Toast.makeText(MainActivity.this, "The item quantity is "+kg+" in kgs",Toast.LENGTH_SHORT).show();
//                Log.d(LOG_TAG, "quantity is "+kg+" kgs");
//                Toast.makeText(MainActivity.this, "The item quantity is "+g+" in gms",Toast.LENGTH_SHORT).show();
//                Log.d(LOG_TAG, "quantity is "+g+" gms");
//            }
//
//            @Override
//            public void onWeightPickerCancelled() {
//
//                Toast.makeText(MainActivity.this, "Order Cancelled",Toast.LENGTH_SHORT);
//                Log.d(LOG_TAG, "order cancelled");
//            }
//        });
   }

    private void setupProductList() {
//        //Create DataSet
//        List<Product> product = new ArrayList<>(
//                Arrays.asList(
//                        new Product("Tomato",20)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                        ,new Product("Potato",30)
//                        ,new Product("Apple",100)
//                )
//        );
//        //Create Adapter object
//        ProductAdapter adapter = new ProductAdapter(this,product);
//        // Set the adapter & layoutManager to RV
//        b.productID.setAdapter(adapter);
//        b.productID.setLayoutManager(new LinearLayoutManager(this));

        //  ******  setupProductList() method in case of options menu  *****

        // Create DataSet
        products = new ArrayList<>();

        // Create adapter object
        adapter = new ProductAdapter(this, products);

        // Set the adapter & layoutManager to RV
        b.productID.setAdapter(adapter);
        b.productID.setLayoutManager(new LinearLayoutManager(this));
        b.productID.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
    }
    // OPTIONS MENU

    // inflates the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_catalog_options, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // OnItem Click Listener for Option Menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_item){
            showProductEditorDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // OnClick handler for ContextualMenu of product

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.product_edit:
                editLastSelectedItem();
                return true;

            case R.id.product_remove:
                removeLastSelectedItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void removeLastSelectedItem() {
        new AlertDialog.Builder(this)
                .setTitle("Do You Want to remove?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        products.remove(adapter.lastSelectedItemPosition);
                        adapter.notifyItemRemoved(adapter.lastSelectedItemPosition);

                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void editLastSelectedItem() {
        // Get data to be edited
        Product lastSelectedProduct = products.get(adapter.lastSelectedItemPosition);

        // Show Editor Dialog
        new ProductEditorDialog()
                .show(this, lastSelectedProduct, new ProductEditorDialog.onProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        // Replace old data
                        products.set(adapter.lastSelectedItemPosition, product);

                        // Update view
                        adapter.notifyItemChanged(adapter.lastSelectedItemPosition);
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // it will show dialog for adding new item in the list
    private void showProductEditorDialog() {
        new ProductEditorDialog()
                .show(this, new Product(), new ProductEditorDialog.onProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        products.add(product);
                        adapter.notifyItemInserted(products.size() - 1);
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
