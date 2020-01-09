package ru.akazankov.digitreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DigitDetector extends Detector<Digit> {
    private static final int SQUARE_WIDTH = 28;

    private Context context;
    private Interpreter interpreter;
    private float[][][][] dataForModel = new float[1][SQUARE_WIDTH][SQUARE_WIDTH][1];
    private float[][] output = new float[1][10];

    public DigitDetector(Context context) {

        try {
            this.context = context;
            InputStream input = context.getResources().openRawResource(R.raw.mnist_model);
            File model = new File(context.getCacheDir()+ File.separator+"model.tflite");
            model.createNewFile();
            copyInputStreamToFile(input, model);
            this.interpreter = new Interpreter(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }

    @Override
    public SparseArray<Digit> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        } else {
            int width = frame.getMetadata().getWidth();
            int height = frame.getMetadata().getHeight();
            int min = Math.min(width,height);

            try {
                Bitmap bitmap = null;
                if(bitmap==null) {
                    YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(),
                            ImageFormat.NV21, width, height, null);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, byteArrayOutputStream);
                    byte[] jpegArray = byteArrayOutputStream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);
                }

                try {
                    interpreter.run(getDataForModel(frame, bitmap), output);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SparseArray<Digit> resultArray = new SparseArray<>();

                Digit digit = new Digit();
                int indexOfmax = getIndexOfmax(output[0]);
                digit.displayValue = String.valueOf(indexOfmax);
                digit.rawValue = digit.displayValue;
                Point[] points = new Point[4];
                points[0] = new Point(0, 0);
                points[1] = new Point(min, 0);
                points[2] = new Point(min, min);
                points[3] = new Point(0, min);
                digit.cornerPoints = points;

                resultArray.append(digit.rawValue.hashCode(), digit);

                return resultArray;
            } catch (Exception e){
                e.printStackTrace();
            }
            return new SparseArray<>();
        }
    }

    private int[] buffer;
    private float[][][][] getDataForModel(Frame frame, Bitmap bitmap) {
        int pixelPerPixel = Math.min(frame.getMetadata().getWidth(), frame.getMetadata().getHeight()) / SQUARE_WIDTH;
        if(buffer == null)
             buffer = new int[pixelPerPixel*pixelPerPixel];

        for(int i = 0; i < SQUARE_WIDTH; i++) {
            for (int j = 0; j < SQUARE_WIDTH; j++) {
                bitmap.getPixels(buffer, 0, pixelPerPixel,
                        i*pixelPerPixel, j*pixelPerPixel, pixelPerPixel, pixelPerPixel);
                dataForModel[0][i][SQUARE_WIDTH - j - 1][0] = getNormalizedAvgGrayScale(buffer);
            }
        }
//        printPixelArray(dataForModel);
        return dataForModel;
    }

    /**
     * for debugging
     * */
    private void printPixelArray(float[][][][] inp) {
        for(int i = 0; i < SQUARE_WIDTH; i++) {
            for(int j = 0; j < SQUARE_WIDTH; j++) {
                int next = ((int)(inp[0][i][j][0] * 100));
                if((next / 10) == 0)
                    System.out.print(".");
                if(next > 80)
                    System.out.print("@@");
                else
                    System.out.print(next);
                System.out.print(".");
            }
            System.out.println();
        }
    }

    private float getNormalizedAvgGrayScale(int[] pixels){
        double sum = 0;
        for(int pixel: pixels){
            sum += ((double)(Color.blue(pixel) + Color.green(pixel) + Color.red(pixel)))/(255d*3d);
        }
        return (float) (sum/((double) pixels.length));
    }

    private int getIndexOfmax(float[] array) {
        int resultIndex = 0;
        float max = array[0];
        for(int i = 1; i < array.length; i++){
            if(array[i] > max){
                max = array[i];
                resultIndex = i;
            }
        }
        return resultIndex;
    }

}
