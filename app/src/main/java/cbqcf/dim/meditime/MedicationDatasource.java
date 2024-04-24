package cbqcf.dim.meditime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Timestamp;

import android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MedicationDatasource {

    private static MedicationDatasource instance = null;
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final static String[] MedicationsColumns = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.KEY_NAME,
            DatabaseHelper.KEY_DESCRIPTION,
            DatabaseHelper.KEY_IS_FIXED_DELAY,
            DatabaseHelper.KEY_DELAY,
            DatabaseHelper.KEY_WEANING_MODE,
    };

    private MedicationDatasource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized MedicationDatasource getInstance(Context context) {
        if (instance == null) {
            instance = new MedicationDatasource(context);
        }
        return instance;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<Medication> loadMedications() {
        ArrayList<Medication> medications = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICATIONS, MedicationsColumns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Medication med = cursorToMedication(cursor);
                med.loadSpecialTimes();
                medications.add(med);
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
                values.put(DatabaseHelper.KEY_IS_FIXED_DELAY, medication.isFixedDelay() ? 1 : 0);
                values.put(DatabaseHelper.KEY_DELAY, medication.getDelay());
                values.put(DatabaseHelper.KEY_NAME, medication.getName());
                values.put(DatabaseHelper.KEY_DESCRIPTION, medication.getDescription());
                values.put(DatabaseHelper.KEY_WEANING_MODE, medication.getWeaningMode() ? 1 : 0);

                // Update existing row if medication with same ID exists
                int rowsAffected = database.update(DatabaseHelper.TABLE_MEDICATIONS, values, DatabaseHelper.KEY_ID + " = ?",
                        new String[]{String.valueOf(medication.getId())});

                // If no rows were affected, insert new row
                if (rowsAffected == 0) {
                    values.put(DatabaseHelper.KEY_NAME, medication.getName());
                    database.insert(DatabaseHelper.TABLE_MEDICATIONS, null, values);
                }

                updateSpecialTimes(medication);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void addMedication(Medication medication) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_IS_FIXED_DELAY, medication.isFixedDelay() ? 1 : 0);
        values.put(DatabaseHelper.KEY_DELAY, medication.getDelay());
        values.put(DatabaseHelper.KEY_NAME, medication.getName());
        values.put(DatabaseHelper.KEY_DESCRIPTION, medication.getDescription());
        values.put(DatabaseHelper.KEY_WEANING_MODE, medication.getWeaningMode() ? 1 : 0);
        medication.setId((int)database.insert(DatabaseHelper.TABLE_MEDICATIONS, null, values));
        updateSpecialTimes(medication);
    }
    public void updateMedication(Medication medication) {
        ContentValues values = new ContentValues();
        if(medication.getId() == -1){
            addMedication(medication);
            return;
        }
        values.put(DatabaseHelper.KEY_IS_FIXED_DELAY, medication.isFixedDelay() ? 1 : 0);
        values.put(DatabaseHelper.KEY_DELAY, medication.getDelay());
        values.put(DatabaseHelper.KEY_NAME, medication.getName());
        values.put(DatabaseHelper.KEY_DESCRIPTION, medication.getDescription());
        values.put(DatabaseHelper.KEY_WEANING_MODE, medication.getWeaningMode() ? 1 : 0);

        database.update(DatabaseHelper.TABLE_MEDICATIONS, values,"id=?",new String[]{String.valueOf(medication.id)});
        updateSpecialTimes(medication);
    }

    private void updateSpecialTimes(Medication medication) {
        database.delete(DatabaseHelper.TABLE_SPECIAL_TIMES, DatabaseHelper.KEY_MEDICATION_ID + " = ?", new String[]{String.valueOf(medication.getId())});
        for (SpecialTime t : medication.getSpecialTimes()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_MEDICATION_ID, medication.getId());
            values.put(DatabaseHelper.KEY_HOUR, t.Hour);
            values.put(DatabaseHelper.KEY_MINUTE, t.Minute);
            database.insert(DatabaseHelper.TABLE_SPECIAL_TIMES, null, values);
        }
    }

    public Medication cursorToMedication(Cursor cursor) {
        return new Medication(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_WEANING_MODE)) > 0,
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DELAY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_IS_FIXED_DELAY)) > 0
        );
    }

    public Timestamp getLastTaken(Medication medication){
        Cursor cursor = database.query(DatabaseHelper.TABLE_TAKEN, new String[]{DatabaseHelper.KEY_DATE}, DatabaseHelper.KEY_MEDICATION_ID + " = ?", new String[]{String.valueOf(medication.getId())}, null, null, DatabaseHelper.KEY_DATE + " DESC", "1");
        Timestamp result = new Timestamp(0);
        if (cursor.moveToFirst()) {
            result = new Timestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DATE)));
        }
        cursor.close();
        return result;
    }

    public Timestamp getLastAimedDate(Medication medication){
        Cursor cursor = database.query(DatabaseHelper.TABLE_TAKEN, new String[]{DatabaseHelper.KEY_AIMED_DATE}, DatabaseHelper.KEY_MEDICATION_ID + " = ?", new String[]{String.valueOf(medication.getId())}, null, null, DatabaseHelper.KEY_AIMED_DATE + " DESC", "1");
        Timestamp result = new Timestamp(0);
        if (cursor.moveToFirst()) {
            result = new Timestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_AIMED_DATE)));
        }
        cursor.close();
        return result;
    }

    public void TakeMedication(Medication medication){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_MEDICATION_ID, medication.getId());
        values.put(DatabaseHelper.KEY_DATE, System.currentTimeMillis());
        values.put(DatabaseHelper.KEY_AIMED_DATE, medication.getNextTime().getTime());
        database.insert(DatabaseHelper.TABLE_TAKEN, null, values);
    }

    public void deleteMedication(Medication medication) {

        int id = medication.getId();
        Log.i(DatabaseHelper.LOG , "Medication deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_MEDICATIONS, DatabaseHelper.KEY_ID
                + " = " + id, null);
        database.delete(DatabaseHelper.TABLE_TAKEN, DatabaseHelper.KEY_MEDICATION_ID
                + " = " + id, null);
        database.delete(DatabaseHelper.TABLE_SPECIAL_TIMES, DatabaseHelper.KEY_MEDICATION_ID
                + " = " + id, null);
    }

    public ArrayList<SpecialTime> getTimestamps(Medication medication) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_SPECIAL_TIMES, new String[]{DatabaseHelper.KEY_HOUR, DatabaseHelper.KEY_MINUTE}, DatabaseHelper.KEY_MEDICATION_ID + " = ?", new String[]{String.valueOf(medication.getId())}, null, null, null);
        ArrayList<SpecialTime> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                result.add(new SpecialTime(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HOUR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_MINUTE))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public void deleteTaken(Medication medication, Timestamp expectedTime, Timestamp actualTime){
        database.delete(DatabaseHelper.TABLE_TAKEN, DatabaseHelper.KEY_MEDICATION_ID + " = ? AND " + DatabaseHelper.KEY_DATE + " = ? AND " + DatabaseHelper.KEY_AIMED_DATE + " = ?", new String[]{String.valueOf(medication.getId()), String.valueOf(actualTime.getTime()), String.valueOf(expectedTime.getTime())});
    }

    public Medication getMedicationFromId(int id){
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICATIONS, MedicationsColumns, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if(cursor.moveToFirst()){
            return cursorToMedication(cursor);
        }
        return null;
    }

    public ArrayList<HistoryBubble> loadHistory(Medication filter, Context context){
        Cursor cursor = database.query(DatabaseHelper.TABLE_TAKEN, new String[]{DatabaseHelper.KEY_MEDICATION_ID, DatabaseHelper.KEY_DATE, DatabaseHelper.KEY_AIMED_DATE}, DatabaseHelper.KEY_MEDICATION_ID + " = ?", new String[]{String.valueOf(filter.getId())}, null, null, DatabaseHelper.KEY_DATE + " DESC");
        ArrayList<HistoryBubble> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                result.add(new HistoryBubble(
                        context,
                        getMedicationFromId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_MEDICATION_ID))),
                        new Timestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DATE))),
                        new Timestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_AIMED_DATE)))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return result;
    }
}
