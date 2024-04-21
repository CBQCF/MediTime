package cbqcf.dim.meditime;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String LOG = "DIM";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "medications.db";


    // Common column names
    public static final String KEY_ID = "id";
    public static final String KEY_CREATED_AT = "created_at";

    // Table Names
    public static final String TABLE_MEDICATIONS = "medications";
    public static final String TABLE_TAKEN = "taken";

    // Medications Table - column names
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DELAY = "delay";
    public static final String KEY_ADAPTATION = "adaptation";

    // Taken Table - column names
    public static final String KEY_MEDICATION_ID = "medication_id";
    public static final String KEY_DATE = "date";


    // Table Create Statements

    // Medications table create statement
    private static final String CREATE_TABLE_MEDICATIONS =
            "CREATE TABLE " + TABLE_MEDICATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_DELAY + " INTEGER,"
            + KEY_ADAPTATION + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // Taken table create statement

    private static final String CREATE_TABLE_TAKEN =
        "CREATE TABLE " + TABLE_TAKEN + "("
        + KEY_MEDICATION_ID + " INTEGER,"
        + KEY_DATE + " DATETIME,"
        + "PRIMARY KEY (" + KEY_MEDICATION_ID + "," + KEY_DATE + "),"
        + "FOREIGN KEY (" + KEY_MEDICATION_ID + ") REFERENCES " + TABLE_MEDICATIONS + "(" + KEY_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG, "Creating database");
        db.execSQL(CREATE_TABLE_MEDICATIONS);
        db.execSQL(CREATE_TABLE_TAKEN);
        Log.i(LOG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG, "Upgrading database from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAKEN);
        onCreate(db);
    }
}
