package com.example.recyclercart;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.recyclercart.models.Product;
import com.example.recyclercart.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupProductList();

        WeightPicker weightPicker = new WeightPicker();
        weightPicker.show(this, new WeightPicker.OnWeightPickedListener() {
            @Override
            public void onWeightPicked(int kg, int g) {

                Toast.makeText(MainActivity.this, "The item quantity is "+kg+" in kgs",Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "quantity is "+kg+" kgs");
                Toast.makeText(MainActivity.this, "The item quantity is "+g+" in gms",Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "quantity is "+g+" gms");
            }

            @Override
            public void onWeightPickerCancelled() {

                Toast.makeText(MainActivity.this, "Order Cancelled",Toast.LENGTH_SHORT);
                Log.d(LOG_TAG, "order cancelled");
            }
        });
    }

    private void setupProductList() {
        //Create DataSet
        List<Product> product = new ArrayList<>(
                Arrays.asList(
                        new Product("Tomato",20)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                        ,new Product("Potato",30)
                        ,new Product("Apple",100)
                )
        );
        //Create Adapter object
        ProductAdapter adapter = new ProductAdapter(this,product);
        // Set the adapter & layoutManager to RV
        b.productID.setAdapter(adapter);
        b.productID.setLayoutManager(new LinearLayoutManager(this));
    }
}
