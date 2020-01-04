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
        int var1 = 2147483647;
        int var2 = -2147483648;
        int var3 = 2147483647;
        int var4 = -2147483648;

        for(int var5 = 0; var5 < this.cornerPoints.length; ++var5) {
            Point var6 = this.cornerPoints[var5];
            var1 = Math.min(var1, var6.x);
            var2 = Math.max(var2, var6.x);
            var3 = Math.min(var3, var6.y);
            var4 = Math.max(var4, var6.y);
        }

        return new Rect(var1, var3, var2, var4);
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
