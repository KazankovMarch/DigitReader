/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.akazankov.digitreader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


import ru.akazankov.digitreader.ui.camera.GraphicOverlay;

/**
 * Graphic instance for rendering digit position, size, and ID within an associated graphic
 * overlay view.
 */
public class DigitGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Digit mDigit;

    DigitGraphic(GraphicOverlay overlay) {
        super(overlay);

        final int selectedColor = Color.GREEN;

        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(300.0f);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Digit getDigit() {
        return mDigit;
    }

    /**
     * Updates the digit instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Digit digit) {
        mDigit = digit;
        postInvalidate();
    }

    /**
     * Draws the digit annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Digit digit = mDigit;
        if (digit == null) {
            return;
        }

        // Draws the bounding box around the digit.
        RectF rect = new RectF(digit.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, mRectPaint);

        // Draws a label at the bottom of the digit indicate the digit value that was detected.
        canvas.drawText(digit.rawValue, rect.left, rect.bottom, mTextPaint);
    }
}
