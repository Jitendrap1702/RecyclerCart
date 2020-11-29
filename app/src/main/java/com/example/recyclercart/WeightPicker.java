package com.example.recyclercart;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;

import com.example.recyclercart.databinding.WeightPickerDialogBinding;

class WeightPicker {

    /**
     * There are 9 TODOs in this file, locate them using the window for it given at the bottom.
     * Also, complete them in order.
     *
     * TODO 1 : Design layout weight_picker_dialog.xml for this WeightPicker Dialog
     *          with 2 NumberPickers (for kg & g)
     */

    public static void show(Context context, final OnWeightPickedListener listener, ProductAdapter adapter, final int KG, final int GM){
        WeightPickerDialogBinding b = WeightPickerDialogBinding.inflate(
                LayoutInflater.from(context)
        );

        new AlertDialog.Builder(context)
                .setTitle("Pick Weight")
                .setView(b.getRoot())
                .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO 3 : Replace 0s & assign kg & g values from respective NumberPickers
                        int kg , g ;
                        kg = b.kgPicker.getValue();
                        g = b.gmPicker.getValue()*50;
                        //TODO 4 : Add GuardCode to prevent user from selecting 0kg 0g. If so, then return
                        if (kg == 0 && g == 0){
                            return;
                        }
                        listener.onWeightPicked(kg, g);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onWeightPickerCancelled();
                    }
                })
                .show();

        setupNumberPickers(b.kgPicker, b.gmPicker);

        //TODO 5 : Call new WeightPicker().show() in MainActivity and pass (this, new OnWeight...)

        //TODO 6 : Show toast of selected weight in onWeightPicked() method

        //TODO 7 : Find appropriate solution for : NumberPicker not formatting the first item

        //TODO 8 : Test your code :)

        //TODO 9 : Try to understand the flow as to how our Listener interface is working
    }

    private static void setupNumberPickers(NumberPicker kgPicker, NumberPicker gmPicker) {
        //TODO 2 : Define this method to setup kg & g NumberPickers as per the given ranges

        //kg Range - 0kg to 10kg
        kgPicker.setMinValue(0);
        kgPicker.setMaxValue(10);
        kgPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value + " kg";
            }
        });

        //g Range - 0g to 950g
        gmPicker.setMinValue(0);
        gmPicker.setMaxValue(19);
        gmPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return 50*value + " g";
            }
        });
        // if value is not null then make it visible on number picker
        View firstItemKg = kgPicker.getChildAt(0);
        if (firstItemKg != null){
            firstItemKg.setVisibility(View.INVISIBLE);
        }

        View firstItemGm = gmPicker.getChildAt(0);
        if (firstItemGm != null){
            firstItemGm.setVisibility(View.INVISIBLE);
        }
    }

    interface OnWeightPickedListener{
        void onWeightPicked(int kg, int g);
        void onWeightPickerCancelled();
    }

}
