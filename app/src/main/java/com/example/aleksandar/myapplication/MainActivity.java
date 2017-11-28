package com.example.aleksandar.myapplication;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.aleksandar.myapplication.db.DatabaseHelper;
import com.example.aleksandar.myapplication.db.model.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private AlertDialog dialogAlert;
    private SharedPreferences preferences;

    public static String NOTIF_TOAST = "pref_toast";
    public static String NOTIF_STATUS = "pref_notification";

    private static final String TAG = "PERMISSIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.first_toolbar);
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        List<Glumac> glumci = new ArrayList<Glumac>();
            try {
                glumci = getDatabaseHelper().getmGlumacDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        List<String> glumciIme = new ArrayList<String>();
        for(Glumac i : glumci) {
            glumciIme.add(i.getGlumacIme());
        }

        final ListView listView = (ListView) findViewById(R.id.listMainActivity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, glumciIme);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Glumac glumac = (Glumac) listView.getItemAtPosition(position);
                Intent intentGlumac = new Intent(MainActivity.this, SecondActivity.class);
                intentGlumac.putExtra("position", glumac.getGlumacId());
                startActivity(intentGlumac);
            }
        });

    }

    private void showStatusMesage(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Prupremni zadatak");
        builder.setContentText(message);
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add:

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_glumac);

                final EditText glumacIme = (EditText) dialog.findViewById(R.id.glumac_ime);

        }

    }

    public DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
