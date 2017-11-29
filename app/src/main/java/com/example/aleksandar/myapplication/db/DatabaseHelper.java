package com.example.aleksandar.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.aleksandar.myapplication.db.model.Film;
import com.example.aleksandar.myapplication.db.model.Glumac;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Aleksandar on 28-Nov-17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME = "ormlite1.db";

    private static final int DATABASE_VERSION = 2;

    private Dao<Glumac, Integer> mGlumacDao = null;
    private Dao<Film, Integer> mFilmDao = null;

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Film.class);
            TableUtils.createTable(connectionSource, Glumac.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Film.class, true);
            TableUtils.dropTable(connectionSource, Glumac.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Glumac, Integer> getmGlumacDao() throws SQLException {
        if(mGlumacDao == null) {
            mGlumacDao = getDao(Glumac.class);
        }
        return mGlumacDao;
    }

    public Dao<Film, Integer> getmFilmDao() throws SQLException {
        if(mFilmDao == null) {
            mFilmDao = getDao(Film.class);
        }
        return mFilmDao;
    }
    @Override
    public void close() {
        mGlumacDao = null;
        mFilmDao = null;

        super.close();

    }
}
