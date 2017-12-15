package com.peermountain.core.persistence;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.peermountain.core.model.guarded.Contact;

/**
 * Created by Galeen on 10/31/2017.
 */

 class ShareHelper {

    @WorkerThread
    static Bitmap getQrCode(Contact contact, int imageSize, int qrColor) {
        QRCodeWriter writer = new QRCodeWriter();
//            String contentContact = new Gson().toJson(contacts[0]);
        String data = setData(contact);
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, imageSize, imageSize);

            Bitmap bmp = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.RGB_565);
            for (int x = 0; x < imageSize; x++) {
                for (int y = 0; y < imageSize; y++) {
                    if (bitMatrix.get(x, y))
                        bmp.setPixel(x, y, qrColor);
                    else
                        bmp.setPixel(x, y, Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            Log.e("QR ERROR", "" + e);
        }
        return null;
    }

    @NonNull
    static String setData(Contact contact) {
        StringBuilder data = new StringBuilder();
        String divider = "@#@";
        data.append(contact.getNames()).append(divider);
        data.append(contact.getDob()).append(divider);
        data.append(contact.getPob()).append(divider);
        data.append(contact.getPhone()).append(divider);
        data.append(contact.getMail());//.append(d);
//            data.append(c.getPictureUrl());
        return data.toString();
    }

    static Contact handleResult(Result rawResult) {
        final String qrcode = rawResult.getText();
        if (rawResult.getText().length() > 0) {
//            Contact contact = new Gson().fromJson(qrcode,Contact.class);
            Contact contact = new Contact();
            String[] data = qrcode.split("@#@");
            if (data.length > 0)
                contact.setNames(data[0].trim());
            if (data.length > 1)
                contact.setDob(data[1].trim());
            if (data.length > 2)
                contact.setPob(data[2].trim());
            if (data.length > 3)
                contact.setPhone(data[3].trim());
            if (data.length > 4)
                contact.setMail(data[4].trim());
            return contact;
        }
        return null;
    }

}
