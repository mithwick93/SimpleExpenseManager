package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.MyApplication;

/**
 * Created by Shehan on 12/2/2015.
 */

/**
 * this singleton class is used to get connection to SQLiteDatabase
 */
public class DatabaseHandler {

    // Database Name
    public static final String DATABASE_NAME = "130650U";

    //Database Version (Increase one if want to also upgrade your database)
    public static final int DATABASE_VERSION = 1;// started at 1


    // Table names
    public static final String ACCOUNT_TABLE = "account";
    public static final String TRANSACTION_TABLE = "account_transaction";

    // ACCOUNT Table Columns names
    public static final String KEY_ACCOUNT_NO = "account_no";
    public static final String KEY_BANK_NAME = "bank_name";
    public static final String KEY_CUSTOMER_NAME = "customer_name";
    public static final String KEY_BALANCE = "balance";

    // TRANSACTION Table Columns names
    public static final String KEY_TRANSACTION_ID= "transaction_id";
    public static final String KEY_TRANSACTION_ACCOUNT_NO = "account_no";
    public static final String KEY_TRANSACTION_DATE = "trans_date";
    public static final String KEY_TRANSACTION_TYPE = "trans_type";
    public static final String KEY_TRANSACTION_AMOUNT = "amount";

    //Set all table with comma separated like USER_TABLE,ABC_TABLE
    private static final String[] ALL_TABLES = {ACCOUNT_TABLE, TRANSACTION_TABLE};

    //Create table syntax
    private static final String ACCOUNT_CREATE =
            "CREATE TABLE account (\n" +
                    "\n" +
                    "  account_no TEXT NOT NULL,\n" +
                    "  bank_name TEXT NOT NULL,\n" +
                    "  customer_name TEXT NOT NULL,\n" +
                    "  balance REAL NOT NULL CHECK (balance >= 0),\n" +
                    "  \n" +
                    "  PRIMARY KEY (account_no)\n" +
                    ");";
    private static final String TRANSACTION_CREATE =
            "CREATE TABLE account_transaction (\n" +
                    "\n" +
                    "  transaction_id INTEGER NOT NULL,\n" +
                    "  account_no TEXT NOT NULL,\n" +
                    "  trans_date TEXT NOT NULL,\n" +
                    "  trans_type TEXT NOT NULL CHECK (trans_type =='e' OR trans_type =='i'),\n" +
                    "  amount REAL NULL CHECK (amount > 0),\n" +
                    "  \n" +
                    "  PRIMARY KEY (transaction_id),\n" +
                    "  \n" +
                    "  CONSTRAINT fk_transaction_account1 FOREIGN KEY (account_no) REFERENCES account (account_no) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ");";
    //Used to open database in syncronized way (singleton)
    private static DataBaseHelper DBHelper = null;

    //Do not instantiate this class
    private DatabaseHandler() {
    }


    /**
     * Open database for insert,update,delete in syncronized manner
     *
     * @return - SQLiteDatabase
     * @throws SQLiteException
     */
    public static SQLiteDatabase getWritableDatabase() throws SQLiteException {
        if (DBHelper == null) {
            synchronized (DatabaseHandler.class) {
                DBHelper = new DataBaseHelper();
            }
        }
        return DBHelper.getWritableDatabase();
    }

    /**
     * Convert Date to sqlite compatible string
     *
     * @param date - Date to be converted to string
     * @return - Returns String representation of Date
     */
    public static String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * Convert sqlite compatible date string to java Date object
     *
     * @param str String to be converted to Date
     * @return Returns Date object
     */
    public static Date getTimeValue(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    /**
     * Inner class to actually handle Database
     */
    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper() {
            super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                //Create the tables on the first run
                db.execSQL(ACCOUNT_CREATE);
                db.execSQL(TRANSACTION_CREATE);

            } catch (Exception exception) {
                Log.i("DatabaseHandler", "Exception onCreate() exception : " + exception.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (String table : ALL_TABLES) {
                db.execSQL("DROP TABLE IF EXISTS " + table); //On upgrade drop tables
            }
            onCreate(db);
        }
    }
}
