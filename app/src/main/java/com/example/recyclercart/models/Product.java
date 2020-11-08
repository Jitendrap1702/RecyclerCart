package com.example.recyclercart.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//public class Product implements Serializable {
//    public String name;
//    public int qty, price;
//
//    public Product(String name, int price) {
//        this.name = name;
//        this.price = price;
//    }
//}

public class Product implements Serializable{

    public static final byte WEIGHT_BASED = 0, VARIANTS_BASED=1;

    // Compulsory
    public String name;
    public byte type;

    // Weight Based
    public int pricePerKg;
    public float minQty;

    // OR

    // Variants Based
    public List<Variant> variants;

    // Weight Based
    public Product(String name, int pricePerKg, float minQty){
        type = WEIGHT_BASED;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.minQty = minQty;
    }

    // Variants Based
    public Product(String name){
        type = VARIANTS_BASED;
        this.name = name;
    }
    public Product(){

    }

    // Extracts and sets variants from String[]
    public void fromVariantStrings(String[] vs){
        variants = new ArrayList<>();
        for(String s : vs){
            //["variant name", "price"]
            String[] v = s.split(",\\s*");
            variants.add(new Variant(v[0], Integer.parseInt(v[1])));
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", pricePerKg=" + pricePerKg +
                ", minQty=" + minQty +
                ", variants=" + variants +
                '}';
    }

    public String variantsString(){
        String variantsString = variants.toString();
        return variantsString
                .replaceFirst("\\[", "")
                .replaceFirst("]", "")
                .replaceAll(",", "\n");
    }

    public String minQtyToString() {
        //float (2.0) -> String (2kg)
        //float (0.050) -> String (50g)

        if(minQty < 1){
            int g = (int) (minQty * 1000);
            return g + "g";
        }
        return ((int) minQty) + "kg";
    }
}
