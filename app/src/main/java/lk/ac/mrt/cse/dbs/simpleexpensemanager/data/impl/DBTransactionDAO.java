package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHandler;

/**
 * Created by Shehan on 12/2/2015.
 */
public class DBTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;

    public DBTransactionDAO() {
        transactions = new LinkedList<>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        //add a transaction to database
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        SQLiteDatabase db = null;

        try {
            db = DatabaseHandler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_TRANSACTION_ACCOUNT_NO, accountNo); // account number
            values.put(DatabaseHandler.KEY_TRANSACTION_DATE, getTimeString(date)); // date
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
            db.insert(DatabaseHandler.TRANSACTION_TABLE, null, values);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date getTimeValue(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    @Override
    public List<Transaction> getAllTransactionLogs() {
        //get all transactions from database
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.TRANSACTION_TABLE, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction(getTimeValue(cursor.getString(2)), cursor.getString(1), null, Double.parseDouble(cursor.getString(4)));
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
        //get limited no of transactions from database
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.TRANSACTION_TABLE, null, null, null, null, null, DatabaseHandler.KEY_TRANSACTION_DATE + " DESC", String.valueOf(limit));
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction(getTimeValue(cursor.getString(2)), cursor.getString(1), null, Double.parseDouble(cursor.getString(4)));
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
