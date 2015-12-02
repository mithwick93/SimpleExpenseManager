package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBTransactionDAO;

/**
 * Created by Shehan on 12/2/2015.
 */
public class DBExpenseManager extends ExpenseManager {

    public DBExpenseManager() {
        setup();
    }

    @Override
    public void setup() {

        //initailize transactionsHolder
        TransactionDAO dbTransactionDAO = new DBTransactionDAO();
        setTransactionsDAO(dbTransactionDAO);

        //initailize accountsHolder
        AccountDAO dbAccountDAO = new DBAccountDAO();
        setAccountsDAO(dbAccountDAO);

    }
}
