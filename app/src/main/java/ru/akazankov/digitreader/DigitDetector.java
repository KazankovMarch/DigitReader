package ru.akazankov.digitreader;

import android.content.Context;
import android.graphics.Point;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;

import java.nio.ByteBuffer;

public class DigitDetector extends Detector<Digit> {

    private Context context;

    public DigitDetector(Context context) {
        this.context = context;
    }

    @Override
    public SparseArray<Digit> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        } else {

            SparseArray<Digit> resultArray = new SparseArray<>();

            Digit digit = new Digit();
            digit.displayValue = "1d";
            digit.rawValue = "1r";
            Point[] points = new Point[4];
            points[0] = new Point(10, 10);
            points[1] = new Point( 40, 10);
            points[2] = new Point(40, 40);
            points[3] = new Point(10, 40);
            digit.cornerPoints = points;

            resultArray.append(digit.rawValue.hashCode(), digit);


            return resultArray;
        }
    }

}
