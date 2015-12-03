package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBTransactionDAO;

/**
 * Created by Shehan on 12/2/2015.
 */
public class DBExpenseManager extends ExpenseManager {

    /**
     * This is the concrete class of ExpenseManager
     */
    public DBExpenseManager() {
        setup();
    }

    @Override
    public void setup() {

        //initailize accountsHolder
        AccountDAO dbAccountDAO = new DBAccountDAO();// DBTransactionDAO uses sqlite to manage accounts
        setAccountsDAO(dbAccountDAO);

        //initailize transactionsHolder
        TransactionDAO dbTransactionDAO = new DBTransactionDAO();// DBTransactionDAO uses sqlite to manage transactions
        setTransactionsDAO(dbTransactionDAO);

    }
}
