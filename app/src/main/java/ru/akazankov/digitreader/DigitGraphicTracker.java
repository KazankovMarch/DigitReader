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

import android.content.Context;
import androidx.annotation.UiThread;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;

import ru.akazankov.digitreader.ui.camera.GraphicOverlay;

/**
 * Generic tracker which is used for tracking or reading a digit (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
public class DigitGraphicTracker extends Tracker<Digit> {
    private GraphicOverlay<DigitGraphic> mOverlay;
    private DigitGraphic mGraphic;

    private DigitUpdateListener mDigitUpdateListener;

    /**
     * Consume the item instance detected from an Activity or Fragment level by implementing the
     * DigitUpdateListener interface method onDigitDetected.
     */
    public interface DigitUpdateListener {
        @UiThread
        void onDigitDetected(Digit digit);
    }

    DigitGraphicTracker(GraphicOverlay<DigitGraphic> mOverlay, DigitGraphic mGraphic,
                        Context context) {
        this.mOverlay = mOverlay;
        this.mGraphic = mGraphic;
        if (context instanceof DigitUpdateListener) {
            this.mDigitUpdateListener = (DigitUpdateListener) context;
        } else {
            throw new RuntimeException("Hosting activity must implement DigitUpdateListener");
        }
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, Digit item) {
        mGraphic.setId(id);
        mDigitUpdateListener.onDigitDetected(item);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<Digit> detectionResults, Digit item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<Digit> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }
}
