package com.CaLLIek.androidhelp.barcodedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.CaLLIek.androidhelp.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class BarCodeDetection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bcd_activity_bar_code_detection);

        Button btn = findViewById(R.id.bcd_btn);

        final TextView textView = findViewById(R.id.bcd_txtContent);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = findViewById(R.id.bcd_imgView);
                Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.bcd_qr);
                imageView.setImageBitmap(bitmap);

                BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();

                if(!barcodeDetector.isOperational())
                {
                    textView.setText("Could not set up the detector");
                }

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> barcodeSparseArray = barcodeDetector.detect(frame);

                Barcode thisCode = barcodeSparseArray.valueAt(0);
                textView.setText(thisCode.rawValue);
            }
        });
    }
}
