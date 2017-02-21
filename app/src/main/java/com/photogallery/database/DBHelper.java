package com.photogallery.database;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;

import com.photogallery.util.DebugLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "photogallery.db";
    private static final int SCHEMA_VERSION = 1;

    private final Context mContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 1; i <= SCHEMA_VERSION; i++) {
            applySqlFile(db, i);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = (oldVersion + 1); i <= newVersion; i++) {
            applySqlFile(db, i);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void applySqlFile(SQLiteDatabase db, int version) {
        BufferedReader reader = null;

        try {
            String filename = String.format(Locale.US, "%s.%d.sql", DATABASE_NAME, version);
            final InputStream inputStream = mContext.getAssets().open(filename);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            final StringBuilder statement = new StringBuilder();

            for (String line; (line = reader.readLine()) != null; ) {
                DebugLogger.d(TAG, "Reading line -> " + line);

                // Ignore empty lines
                if (!TextUtils.isEmpty(line) && !line.startsWith("--")) {
                    statement.append(line.trim());
                }

                if (line.endsWith(";")) {
                    DebugLogger.d(TAG, "Running statement " + statement);

                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }

        } catch (IOException e) {
            DebugLogger.e(TAG, "Could not apply SQL file: " + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    DebugLogger.w(TAG, "Could not close reader: " + e.toString());
                }
            }
        }
    }
}
