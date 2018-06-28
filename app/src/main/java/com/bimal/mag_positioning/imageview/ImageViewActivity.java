package com.bimal.mag_positioning.imageview;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.bimal.mag_positioning.DBHelper;
import com.bimal.mag_positioning.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wmcs on 7/21/2017.
 */

public class ImageViewActivity extends FragmentActivity {

    private BlueDotView mImageView;
    public Float a;
    public Float b;
    public Float c;
    public Float d;
    public Float total;
    PointF get;

    private SensorEventListener sensorEvent=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Float z;
            Float a1;
            Float b1;
            Float cc;

            a = event.values[0];
            b = event.values[1];
            c = event.values[2];
            d = (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2));

            SQLiteDatabase db;
            db = openOrCreateDatabase(
                    "Mag_Positioning.db"
                    , SQLiteDatabase.CREATE_IF_NECESSARY
                    , null
            );
            db.setVersion(1);
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);

            Cursor cur = DBHelper.getInstance().getAllData();
            cur.moveToFirst();
            HashMap<PointF, Float> difference = new HashMap<>();

            if (cur.isLast() == false) {
                do {

                    PointF location = new PointF(cur.getInt(2), cur.getInt(3));
                    a1 = Float.valueOf(cur.getString(4));
                    b1 = Float.valueOf(cur.getString(5));
                    cc = Float.valueOf(cur.getString(6));
                    z = Float.valueOf(cur.getString(7));
                    total = Float.valueOf((float) Math.sqrt(Math.pow(((Math.pow((a - a1), 2) + Math.pow((b - b1), 2) + Math.pow((c - cc), 2) + Math.pow((d - z), 2)) / 4), 2)));
                    difference.put(location, total);

                } while (cur.moveToNext());

            }

            Map.Entry<PointF, Float> min = Collections.min(difference.entrySet(), new Comparator<Map.Entry<PointF, Float>>() {
                @Override
                public int compare(Map.Entry<PointF, Float> entry1, Map.Entry<PointF, Float> entry2) {
                    return entry1.getValue().compareTo(entry2.getValue());
                }
            });
            get = min.getKey();
            mImageView.setDotCenter(get);
            mImageView.postInvalidate();
            cur.close();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        findViewById(android.R.id.content).setKeepScreenOn(true);

        mImageView = (BlueDotView) findViewById(R.id.imageView);
    }
}
