package com.example.recyclercart;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclercart.constants.Constants;
import com.example.recyclercart.databinding.ActivityMainBinding;
import com.example.recyclercart.models.Inventory;
import com.example.recyclercart.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

     ActivityMainBinding b;
     List<Product> products;
     ProductAdapter adapter;
    private SearchView searchView;

    // SharedPreferences
    private SharedPreferences mSharedPref;
    private final String MY_DATA = "myData";
    private MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setup();
        loadSavedData();
        setupProductList();
    }
    private void setup() {
        app = (MyApp) getApplicationContext();
    }

    // Data save and Reload
    private void saveDataToCloud(){
        if (app.isOffline()){
            app.showToast(this, "Unable to save data, You are offline");
            return;
        }
        app.showLoadingDialog(this);
        Inventory inventory = new Inventory(products);

        // Save on cloud
        app.db.collection(Constants.INVENTORY)
                .document(Constants.PRODUCTS)
                .set(inventory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                        saveLocally();
                        app.hideLoadingDialog();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to save on cloud", Toast.LENGTH_SHORT).show();
                        app.hideLoadingDialog();
                        finish();
                    }
                });
    }
    private void saveLocally() {
        SharedPreferences preferences = getSharedPreferences("products_data", MODE_PRIVATE);
        preferences.edit()
                .putString("data", new Gson().toJson(products))
                .apply();
    }

    /** Shared Preferences**/
    private void loadSavedData() {
        // Try to use sharedPreferences
        Gson gson = new Gson();
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        String json =  mSharedPref.getString(MY_DATA,null);

        if(json!=null){
            products = gson.fromJson(json,new TypeToken<List<Product>>(){}.getType());
            setupProductList();
        }
        else{
            fetchFromCloud();
        }
    }

    private void fetchFromCloud() {
        if(app.isOffline()){
            app.showToast(this, "Unable to save. You are offline!");
            return;
        }

        app.showLoadingDialog(this);

        app.db.collection(Constants.INVENTORY)
                .document(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Inventory inventory = documentSnapshot.toObject(Inventory.class);
                            products = inventory.products;

                        } else
                            products = new ArrayList<>();
                        setupProductList();
                        saveLocally();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to save on cloud", Toast.LENGTH_SHORT).show();
                        app.hideLoadingDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Unsaved changes")
                .setMessage("Do you want to save?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveDataToCloud();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    // Trying to use SharedPreferences
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataLocally();
    }
    /*@Override
    protected void onPause() {
        super.onPause();
    }*/
    private void saveDataLocally() {
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        Gson gson = new Gson();
        mSharedPref.edit()
                .putString(MY_DATA,gson.toJson(adapter.visibleProducts))
                .apply();
    }


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
        products.add(new Product("Apple", 180, 1));
        products.add(new Product("Banana", 30, 2));
        products.add(new Product("grapes", 100, 1));

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

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("MyLog", "onQueryTextSubmit : " +  query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.filter(query);
                return true;
            }

        });
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

            case R.id.weigth_picker:
                showWeightPickerForWBP(adapter.visibleProducts.get(adapter.lastSelectedItemPosition).type);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    // Weight Picker Dialog for weightBased Product
    private void showWeightPickerForWBP(int type) {
        if (type==0){
            float minQ = adapter.visibleProducts.get(adapter.lastSelectedItemPosition).minQty;
            final int KG = extractCredentialsFromFloat(minQ).get(0);
            final int GM = extractCredentialsFromFloat(minQ).get(1);

            WeightPicker.show(MainActivity.this, new WeightPicker.OnWeightPickedListener() {
                @Override
                public void onWeightPicked(int kg, int g) {

                    Toast.makeText(MainActivity.this,"Picked values\n"+kg+" kg and "+g+" gm",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onWeightPickerCancelled() {
                    Toast.makeText(MainActivity.this,"Cancelled!!",Toast.LENGTH_SHORT).show();

                }
            },adapter,KG,GM);
        }
        else{
            Toast.makeText(MainActivity.this,"Not Available for Variant Based Product",Toast.LENGTH_SHORT).show();
        }

    }
    private static ArrayList<Integer> extractCredentialsFromFloat(float minQ){
        ArrayList<Integer> cred = new ArrayList<>();
        final int KG ,GM;
        if (minQ<0){
            KG=0;
            cred.add(KG);
            GM=(int)(minQ*1000);
            cred.add(GM);
            return cred;
        }else{
            KG=(int)(minQ);
            cred.add(KG);
            GM=(int)((minQ-KG)*1000);
            cred.add(GM);
            return cred;
        }
    }


    private void removeLastSelectedItem() {
        new AlertDialog.Builder(this)
                .setTitle("Do You Want to remove?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get the item to be removed
                        Product productToBeRemoved = adapter.visibleProducts.get(adapter.lastSelectedItemPosition);
                        // remove item
                        adapter.visibleProducts.remove(productToBeRemoved);
                        adapter.allProducts.remove(productToBeRemoved);
                        // notify adapter
                        adapter.notifyItemRemoved(adapter.lastSelectedItemPosition);

                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void editLastSelectedItem() {
        // Get data to be edited
        Product lastSelectedProduct = adapter.visibleProducts.get(adapter.lastSelectedItemPosition);

        ProductEditorDialog productEditorDialog = new ProductEditorDialog(ProductEditorDialog.PRODUCT_EDIT);

        // Set lastSelectedProduct to product
        productEditorDialog.product = lastSelectedProduct;

        // Show editor dialog
        productEditorDialog
                .show(this, lastSelectedProduct, new ProductEditorDialog.onProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        // Replace old Data
                        products.set(adapter.lastSelectedItemPosition,product);

                        // Update View
                        adapter.notifyItemChanged(adapter.lastSelectedItemPosition);
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(MainActivity.this, "Cancelled!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // it will show dialog for adding new item in the list
    private void showProductEditorDialog() {
       new ProductEditorDialog(ProductEditorDialog.PRODUCT_ADD)
               .show(this, new Product(), new ProductEditorDialog.onProductEditedListener() {
                   @Override
                   public void onProductEdited(Product product) {
                       adapter.allProducts.add(product);
                       adapter.visibleProducts.add(product);
                       adapter.notifyItemInserted(products.size() - 1);
                       Toast.makeText(MainActivity.this,"Item Added!",Toast.LENGTH_SHORT).show();

                   }

                   @Override
                   public void onCancelled() {
                       Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                   }
               });
    }
    private boolean isNameInQuery(String name) {
        try{
        String query = searchView.getQuery().toString().toLowerCase();
        return name.toLowerCase().contains(query);}
        catch (NullPointerException exception){
            return true;
        }
    }

}