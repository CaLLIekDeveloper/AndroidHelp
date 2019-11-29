package com.CaLLIek.androidhelp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//Клас для работы с Базой данных SqLite
public class DataBaseHelper extends SQLiteOpenHelper {
    //        Как инициоровать работу с классом
    //        sqlHelper = new DataBaseHelper(this);
    //        sqlHelper.create_db();

    public static String DB_NAME = "Tracks.db";
    private static final int SCHEMA = 2;
    private static String DB_PATH;
    public SQLiteDatabase database;
    private Context myContext;

    static public String TABLE_NAME = "Tracks";

    static public String COLUMN_ID = "id";
    static public String COLUMN_PERFORMER= "performer";
    static public String COLUMN_TRACK_NAME = "trackName";
    static public String COLUMN_TIME = "time";

    String path;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext = context;
        DB_PATH = "/data/data/com.CaLLIek.androidhelp/databases/";
        path = DB_PATH + DB_NAME;
    }
    public void create_db() {

        //обьявляю переменные для входного и выходного потоков
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            //создаю файл
            File file = new File(DB_PATH + DB_NAME);
            //если файл не существует
            if (!file.exists()) {
                //получаю БД
                Log.e("БД","Не существует");
                this.getWritableDatabase();
                //открываю поток для чтения
                myInput = myContext.getAssets().open(DB_NAME);
                String outFileName = DB_PATH + DB_NAME;
                //открываю поток для записи
                myOutput = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int length;
                //записываю данные в БД в цикле пока буфер не будет пустым
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
            //onUpgrade(database);
        } catch (IOException ex) {

        }
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        onUpgrade(database,database.getVersion(),SCHEMA);
        createTable();
    }

    public void createTable(){
        open();
        // создаем таблицу с полями
        try {
            database.execSQL("create table "+TABLE_NAME+" ("
                    + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
                    + COLUMN_PERFORMER+" text,"
                    + COLUMN_TRACK_NAME+" text,"
                    + COLUMN_TIME+" text" + ");");
        } catch (Exception ex) {
        }
        close();
    }

    public void open() throws SQLException {
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }
    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("UPGRADE","UPGRADE "+newVersion);
        if(newVersion>oldVersion) {
            db.execSQL("DROP TABLE " + TABLE_NAME);
            onCreate(db);
        }
    }


}