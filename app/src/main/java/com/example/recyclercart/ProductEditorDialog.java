package com.example.recyclercart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.regex.Pattern;

import com.example.recyclercart.databinding.DialogProductEditBinding;
import com.example.recyclercart.models.Product;

public class ProductEditorDialog {
    private DialogProductEditBinding b;
    private Product product;

    void show(Context context, Product product, final onProductEditedListener listener){
        // inflate
        b = DialogProductEditBinding.inflate(LayoutInflater.from(context));
        this.product = product;

        // Create dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Product")
                .setView(b.getRoot())
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (areProductDetailsValid()) {
                            listener.onProductEdited(ProductEditorDialog.this.product);
                        }
                        else{
                            Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelled();
                    }
                })
                .show();
        setupRadioGroup();
        preFillPreviousDetails();

    }

    private void preFillPreviousDetails() {
        // Set name
        b.name.setText(product.name);

        // Change RadioGroup selection
        b.productType.check(product.type == Product.WEIGHT_BASED
                ? R.id.weight_based_rbtn : R.id.variants_based_rbtn);

        // Setup views according to type
        if (product.type == Product.WEIGHT_BASED){
            b.price.setText(product.pricePerKg + "");
            b.minQty.setText(product.minQtyToString());
        }
        else{
            b.variants.setText(product.variantsString());
        }
    }

    // check if all variants are valid or not
        private boolean areProductDetailsValid() {
            // Check name
            String name = b.name.getText().toString().trim();
            if (name.isEmpty()) {
                return false;
            }
            product.name = name;

            switch (b.productType.getCheckedRadioButtonId()){
                case R.id.weight_based_rbtn:

                    // Get values from views
                    String pricePerKg = b.price.getText().toString().trim(),
                            minQty = b.minQty.getText().toString().trim();

                    // Check inputs
                    if (pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)")) {
                        return false;
                    }

                    // All good set values of product
                    product = new Product(name
                            , Integer.parseInt(pricePerKg)
                            , extractMinQtyFromString(minQty));

                    return true;

                case R.id.variants_based_rbtn:

                    //Get values from views
                    String variants = b.variants.getText().toString().trim();

                    // Create Product
                    product = new Product(name);

                    return areVariantsValid(variants);
            }

            return false;
        }

    private float extractMinQtyFromString(String minQty) {
        if(minQty.contains("kg"))
            return Integer.parseInt(minQty.replace("kg", ""));
        else
            return Integer.parseInt(minQty.replace("g", "")) / 1000f;
    }

    private boolean areVariantsValid(String variants) {
        if(variants.length() == 0){
            return true;
        }
        String[] vs = variants.split("\n");

        ////Check for each variant format using RegEx
        Pattern pattern = Pattern.compile("^\\w+(\\s|\\w)+,\\s*\\d+$");
        for (String variant : vs)
            if (!pattern.matcher(variant).matches())
                return false;

        // Extracts variants from string[]
        product.fromVariantStrings(vs);

        return true;
    }

    // Change visibility of views based on productType selection
    private void setupRadioGroup(){
        //b.productType.clearCheck();
        b.productType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.weight_based_rbtn){
                    b.weightBasedRoot.setVisibility(View.VISIBLE);
                    b.variantsRoot.setVisibility(View.GONE);
                }
                else{
                    b.weightBasedRoot.setVisibility(View.GONE);
                    b.variantsRoot.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    // Listener Interface to notifiy Activity of Dialog
    interface onProductEditedListener{
        void onProductEdited(Product product);
        void onCancelled();
    }
}
