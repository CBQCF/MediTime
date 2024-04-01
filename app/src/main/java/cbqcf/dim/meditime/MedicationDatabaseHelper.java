package cbqcf.dim.meditime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class MedicationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medications.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MEDICATIONS = "medications";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LAST_TIME = "last_time";
    private static final String COLUMN_DELAY = "delay";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_ADAPTATION = "adaptation";
    private static final String COLUMN_HAS_BEEN_TAKEN = "has_been_taken";

    public MedicationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_MEDICATIONS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_LAST_TIME + " INTEGER, " +
                COLUMN_DELAY + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_ADAPTATION + " INTEGER, " +
                COLUMN_HAS_BEEN_TAKEN + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        onCreate(db);
    }

    public List<Medication> loadMedications() {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICATIONS, null);
        if (cursor.moveToFirst()) {
            do {
                Medication medication = new Medication(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ADAPTATION)) > 0,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HAS_BEEN_TAKEN)) > 0,
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELAY)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LAST_TIME))
                );
                medications.add(medication);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return medications;
    }

    public void saveMedications(List<Medication> medications) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Medication medication : medications) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_LAST_TIME, medication.getLastTime());
                values.put(COLUMN_DELAY, medication.getDelay());
                values.put(COLUMN_DESCRIPTION, medication.getDescription());
                values.put(COLUMN_ADAPTATION, medication.getAdaptation() ? 1 : 0);
                values.put(COLUMN_HAS_BEEN_TAKEN, medication.getHasBeenTaken() ? 1 : 0);

                // Update existing row if medication with same ID exists
                int rowsAffected = db.update(TABLE_MEDICATIONS, values, COLUMN_ID + " = ?",
                        new String[]{String.valueOf(medication.getId())});

                // If no rows were affected, insert new row
                if (rowsAffected == 0) {
                    values.put(COLUMN_NAME, medication.getName());
                    db.insert(TABLE_MEDICATIONS, null, values);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void appendMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_TIME, medication.getLastTime());
        values.put(COLUMN_DELAY, medication.getDelay());
        values.put(COLUMN_NAME, medication.getName());
        values.put(COLUMN_DESCRIPTION, medication.getDescription());
        values.put(COLUMN_ADAPTATION, medication.getAdaptation() ? 1 : 0);
        values.put(COLUMN_HAS_BEEN_TAKEN, medication.getHasBeenTaken() ? 1 : 0);
        db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
    }

    public Medication newMedication(String id, String name, String description, boolean adaptation, long delay){
        Medication newMedication = new Medication(id, name, description, adaptation, false, delay, System.currentTimeMillis());
        appendMedication(newMedication);
        return newMedication;
    }
}
