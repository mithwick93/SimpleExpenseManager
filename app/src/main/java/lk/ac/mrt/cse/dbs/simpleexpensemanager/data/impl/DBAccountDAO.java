package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHandler;

/**
 * Created by Shehan on 12/2/2015.
 */

/**
 * This is the concrete implementation of AccountDAO interface which uses sqlite as the database to manage data
 */
public class DBAccountDAO implements AccountDAO {
    public DBAccountDAO() {
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accountNumbers = new ArrayList<>(); //Store the account numbers

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.ACCOUNT_TABLE, new String[]{DatabaseHandler.KEY_ACCOUNT_NO}, null, null, null, null, null); // Get account numbers from the database
            if (cursor.moveToFirst()) { // If records are found process them
                do {
                     /*
                    cursor.getString(0) - account number
                     */
                    accountNumbers.add(cursor.getString(0));// Add account numbers to the ArrayList
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
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> accountList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.ACCOUNT_TABLE, null, null, null, null, null, null);// Get all accounts from the database
            if (cursor.moveToFirst()) {// If records are found process them
                do {
                    /*
                    cursor.getString(0) -  account number
                     cursor.getString(1) -  bank name
                     cursor.getString(2) -  account holder name
                     cursor.getString(3) -  balance
                     */
                    Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)));
                    accountList.add(account);
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
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = DatabaseHandler.getWritableDatabase();

            cursor = db.query(DatabaseHandler.ACCOUNT_TABLE, null, DatabaseHandler.KEY_ACCOUNT_NO + " = ?", new String[]{accountNo}, null, null, null);// Get specific account using account number
            if (cursor != null) {
                cursor.moveToFirst();
                 /*
                     cursor.getString(0) -  account number
                     cursor.getString(1) -  bank name
                     cursor.getString(2) -  account holder name
                     cursor.getString(3) -  balance
                  */
                return new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)));

            } else {
                String msg = "Account " + accountNo + " is invalid."; //If account not found
                throw new InvalidAccountException(msg);
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
        return null;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = null;

        try {
            db = DatabaseHandler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_ACCOUNT_NO, account.getAccountNo()); // account number
            values.put(DatabaseHandler.KEY_BANK_NAME, account.getBankName()); // bank name
            values.put(DatabaseHandler.KEY_CUSTOMER_NAME, account.getAccountHolderName()); // customer name
            values.put(DatabaseHandler.KEY_BALANCE, account.getBalance()); // initial balance

            // Inserting Row
            db.insert(DatabaseHandler.ACCOUNT_TABLE, null, values); //Add new account to database
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = null;

        try {
            db = DatabaseHandler.getWritableDatabase();

            db.delete(DatabaseHandler.ACCOUNT_TABLE, DatabaseHandler.KEY_ACCOUNT_NO + " = ?", new String[]{accountNo}); //Remove account specified by account number

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = null;
        try {
            Account account = getAccount(accountNo); //Get the account details from database
            if (account != null) { //If the account exist then proceed
                db = DatabaseHandler.getWritableDatabase();

                ContentValues values = new ContentValues();
                switch (expenseType) {
                    case EXPENSE:
                        //if (account.getBalance() - amount < 0) { // check if new balance is >0
                        //    String msg = "Account balance " + accountNo + " would be < 0.";
                        //    throw new InvalidAccountException(msg);
                        //}
                        values.put(DatabaseHandler.KEY_BALANCE, account.getBalance() - amount);// If type is expense then reduce amount from account
                        break;
                    case INCOME:
                        values.put(DatabaseHandler.KEY_BALANCE, account.getBalance() + amount);// If type is income then add amount to account
                        break;
                }
                db.update(DatabaseHandler.ACCOUNT_TABLE, values, DatabaseHandler.KEY_ACCOUNT_NO + " = ?", new String[]{accountNo});// Update account balance
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
