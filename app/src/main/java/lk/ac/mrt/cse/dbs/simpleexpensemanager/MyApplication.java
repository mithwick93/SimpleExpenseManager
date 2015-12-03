package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shehan on 12/3/2015.
 */

/**
 * This class is used to get the context of the application to the DatabaseHandler.DataBaseHelper inner class which extends from SQLiteOpenHelper
 */
public class MyApplication extends Application {
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}