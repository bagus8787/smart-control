package com.example.smart_control.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class BarcodeImage {
    protected Bitmap image;
    protected String barcodeString;

    public BarcodeImage(String barcode) {
        if (barcode.length()>0) {
            byte[] decodedString = Base64.decode(barcode.replace("data:image/png;base64,", ""), Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        barcodeString = barcode;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getBarcodeString() {
        return barcodeString;
    }
}
