package ru.akazankov.digitreader;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;

import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class Digit extends AbstractSafeParcelable {

    public Point[] cornerPoints;
    public String rawValue;
    public String displayValue;

    public Digit() {
    }

    public Digit(Parcel source) {
        cornerPoints = (Point[]) source.readArray(Point.class.getClassLoader());
        rawValue = source.readString();
        displayValue = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(cornerPoints);
        dest.writeString(rawValue);
        dest.writeString(displayValue);
    }

    public Rect getBoundingBox() {
        int x1 = 2147483647;
        int x2 = -2147483648;
        int y1 = 2147483647;
        int y2 = -2147483648;

        for (Point point : this.cornerPoints) {
            x1 = Math.min(x1, point.x);
            x2 = Math.max(x2, point.x);
            y1 = Math.min(y1, point.y);
            y2 = Math.max(y2, point.y);
        }

        return new Rect(x1, y1, x2, y2);
    }


    public static final Creator<Digit> CREATOR = new Creator<Digit>() {
        @Override
        public Digit createFromParcel(Parcel source) {
            return new Digit(source);
        }

        @Override
        public Digit[] newArray(int size) {
            return new Digit[size];
        }
    };
}
