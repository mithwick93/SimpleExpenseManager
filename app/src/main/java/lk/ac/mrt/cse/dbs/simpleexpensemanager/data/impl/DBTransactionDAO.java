package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHandler;

/**
 * Created by Shehan on 12/2/2015.
 */

/**
 * This is the concrete implementation of TransactionDAO interface which uses sqlite as the database to manage data
 */
public class DBTransactionDAO implements TransactionDAO {

    public DBTransactionDAO() {
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = null;

        try {
            db = DatabaseHandler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_TRANSACTION_ACCOUNT_NO, accountNo); // account number
            values.put(DatabaseHandler.KEY_TRANSACTION_DATE, DatabaseHandler.getTimeString(date)); // date
            switch (expenseType) {
                case EXPENSE:
                    values.put(DatabaseHandler.KEY_TRANSACTION_TYPE, "e"); // type
                    break;
                case INCOME:
                    values.put(DatabaseHandler.KEY_TRANSACTION_TYPE, "i"); // type
                    break;
            }
            values.put(DatabaseHandler.KEY_TRANSACTION_AMOUNT, amount); //  amount

            // Inserting Row
            db.insert(DatabaseHandler.TRANSACTION_TABLE, null, values);// Log transaction to database
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.TRANSACTION_TABLE, null, null, null, null, null, null); //Get all transactions from database
            if (cursor.moveToFirst()) {// If records are found process them
                do {
                    /*
                    cursor.getString(0) - transaction id number
                     cursor.getString(1) - transaction account number
                     cursor.getString(2) - transaction date
                     cursor.getString(3) - transaction type
                     cursor.getString(4) - transaction amount
                     */
                    Transaction transaction = new Transaction(DatabaseHandler.getTimeValue(cursor.getString(2)), cursor.getString(1), null, Double.parseDouble(cursor.getString(4)));
                    switch (cursor.getString(3)) {
                        case "e":
                            transaction.setExpenseType(ExpenseType.EXPENSE);
                            break;
                        case "i":
                            transaction.setExpenseType(ExpenseType.INCOME);
                            break;
                    }
                    transactionsList.add(transaction);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return transactionsList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.TRANSACTION_TABLE, null, null, null, null, null, DatabaseHandler.KEY_TRANSACTION_ID + " DESC", String.valueOf(limit)); //Get specified number of transactions , order by descending order of date
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction(DatabaseHandler.getTimeValue(cursor.getString(2)), cursor.getString(1), null, Double.parseDouble(cursor.getString(4)));
                    switch (cursor.getString(3)) {
                        case "e":
                            transaction.setExpenseType(ExpenseType.EXPENSE);
                            break;
                        case "i":
                            transaction.setExpenseType(ExpenseType.INCOME);
                            break;
                    }
                    transactionsList.add(transaction);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return transactionsList;
    }
}
