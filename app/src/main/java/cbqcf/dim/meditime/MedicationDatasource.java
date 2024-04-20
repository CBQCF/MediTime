package cbqcf.dim.meditime;


import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MedicationDatasource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private final static String[] allColumns = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.KEY_NAME,
            DatabaseHelper.KEY_DESCRIPTION,
            DatabaseHelper.KEY_DELAY,
            DatabaseHelper.KEY_ADAPTATION,
    };

    public MedicationDatasource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<Medication> loadMedications() {
        List<Medication> medications = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICATIONS, allColumns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                medications.add(cursorToMedication(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medications;
    }

    public void saveMedications(List<Medication> medications) {
        database.beginTransaction();
        try {
            for (Medication medication : medications) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.KEY_DELAY, medication.getDelay());
                values.put(DatabaseHelper.KEY_DESCRIPTION, medication.getDescription());
                values.put(DatabaseHelper.KEY_ADAPTATION, medication.getAdaptation() ? 1 : 0);

                // Update existing row if medication with same ID exists
                int rowsAffected = database.update(DatabaseHelper.TABLE_MEDICATIONS, values, DatabaseHelper.KEY_ID + " = ?",
                        new String[]{String.valueOf(medication.getId())});

                // If no rows were affected, insert new row
                if (rowsAffected == 0) {
                    values.put(DatabaseHelper.KEY_NAME, medication.getName());
                    database.insert(DatabaseHelper.TABLE_MEDICATIONS, null, values);
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void addMedication(Medication medication) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_DELAY, medication.getDelay());
        values.put(DatabaseHelper.KEY_NAME, medication.getName());
        values.put(DatabaseHelper.KEY_DESCRIPTION, medication.getDescription());
        values.put(DatabaseHelper.KEY_ADAPTATION, medication.getAdaptation() ? 1 : 0);
        database.insert(DatabaseHelper.TABLE_MEDICATIONS, null, values);
    }
    public Medication cursorToMedication(Cursor cursor) {
        return new Medication(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ADAPTATION)) > 0,
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DELAY))
        );
    }
    public Medication newMedication(String id, String name, String description, boolean adaptation, long delay){
        Medication newMedication = new Medication(id, name, description, adaptation, delay);
        addMedication(newMedication);
        return newMedication;
    }

    public Timestamp getLastTaken(Medication medication){
        Cursor cursor = database.query(DatabaseHelper.TABLE_TAKEN, new String[]{DatabaseHelper.KEY_DATE}, DatabaseHelper.KEY_MEDICATION_ID + " = ?", new String[]{medication.getId()}, null, null, DatabaseHelper.KEY_DATE + " DESC", "1");
        Timestamp result = null;
        if (cursor.moveToFirst()) {
            result = new Timestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DATE)));
        }
        cursor.close();
        return result;
    }
}
