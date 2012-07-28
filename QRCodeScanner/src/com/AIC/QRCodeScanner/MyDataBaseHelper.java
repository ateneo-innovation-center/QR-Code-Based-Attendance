package com.AIC.QRCodeScanner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper{
    //singleton/ single instance reference of database instance

    private static MyDataBaseHelper _dbHelper;
    //The Android's default system path of your application database.
    //private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";
    private static String DB_PATH = "/data/data/com.AIC.QRCodeScanner/databases/";
    //database name
    private static String DB_NAME = "androidTest";
    //reference of database
    private SQLiteDatabase _androidTestSqliteDb;
    //
    private String _searchToken;
 
    //
    private final Context _context;
    //Contains search result when user search with any text
    private Vector<Object> _searchResultVec;
    MyDataBaseHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
        this._context = context;
        _searchResultVec = new Vector<Object>();
        _searchToken = "";
    }
    public static MyDataBaseHelper getInstance(Context context)
    {
        if(_dbHelper == null)
        {
            _dbHelper = new MyDataBaseHelper(context);
        }
        return _dbHelper;
    }
    public String getLastSearchToken()
    {
        return _searchToken;
    }
    public void createDataBase() throws IOException
    {
        boolean dbExist = checkDataBase();
        if(dbExist)
        {
            //do nothing - database already exist
        }else{
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;
        try
        {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e)
        {
            //database does't exist yet.
        }
        if(checkDB != null)
        {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    private void copyDataBase() throws IOException
    {
        //Open your local db as the input stream
        InputStream myInput = _context.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0)
        {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
    public Vector<Object> search(String text)
    {
        //list of Versioni, search result with query text
        _searchResultVec = new Vector<Object>();
        _searchToken = text;
        try
        {
            //open database to query
            openDataBase();
            Cursor cursor = _androidTestSqliteDb.query("TextTable",
                    new String[] { "Column1"},
                    "Column1" + " like '%"+text+"%'",
                    null ,
                    null,
                    null,
                    null);
            //mapped all rows to data object
            if (cursor.moveToFirst())
            {
                do
                {
                    //create objects
                } while (cursor.moveToNext());
            }
            //close cursor
            cursor.close();
        }
        catch(Exception ex)
        {
            System.out.println("DatabaseHelper.search()- : ex " + ex.getClass() +", "+ ex.getMessage());
        }
        //
        return _searchResultVec;
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
	
	public void openDataBase() throws SQLException{
		 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
        _androidTestSqliteDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
}
