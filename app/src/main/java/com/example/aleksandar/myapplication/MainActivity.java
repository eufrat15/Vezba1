package com.example.aleksandar.myapplication;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

;
import com.example.aleksandar.myapplication.db.DatabaseHelper;
import com.example.aleksandar.myapplication.db.model.Glumac;
import com.example.aleksandar.myapplication.dialogs.AboutDialog;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                final EditText glumacBiografija = (EditText) dialog.findViewById(R.id.glumac_biografija);
                final EditText glumacOcena = (EditText) dialog.findViewById(R.id.glumac_ocena);
                final EditText glumacDatumRodjenja = (EditText) dialog.findViewById(R.id.glumac_datum_rodjenja);

                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String ime = glumacIme.getText().toString();
                        if (ime.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Ne sme biti prazno", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String biografija = glumacBiografija.getText().toString();
                        if (ime.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Ne sme biti prazno", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double ocena = 0;
                        try {
                            ocena = Double.parseDouble(glumacOcena.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Ocena mora biti broj.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                        Date datum = null;
                        try {
                            datum = sdf.parse(glumacDatumRodjenja.getText().toString());
                        } catch (ParseException e) {
                            Toast.makeText(MainActivity.this, "Datum mora biti u formatu dd.mm.yyyy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Glumac glumac = new Glumac();
                        glumac.setGlumacIme(ime);
                        glumac.setGlumacBiografija(biografija);
                        glumac.setGlumacOcena(ocena);
                        glumac.setGlumacDatumRodjenja(datum);


                        try {
                            getDatabaseHelper().getmGlumacDao().create(glumac);

                            boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
                            boolean status = preferences.getBoolean(NOTIF_STATUS, false);

                            if (toast) {
                                Toast.makeText(MainActivity.this, "Novi glumac je dodat", Toast.LENGTH_SHORT).show();
                            }

                            if (status) {
                                showStatusMesage("Novi glumac je dodat");
                            }
                            refresh();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }

                });

                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { dialog.dismiss(); }

                });

                dialog.show();

                break;
            case R.id.action_settings:
                Intent preferences = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(preferences);
                break;

            case R.id.action_about:
                if (dialogAlert == null) {
                    dialogAlert = new AboutDialog(MainActivity.this).prepareDialog();
                } else {
                    if (dialogAlert.isShowing()) {
                        dialogAlert.dismiss();
                    }
                }
                dialogAlert.show();
                break;

        }

        return  super.onOptionsItemSelected(item);

    }

    public DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.listMainActivity);
        if (listview != null) {
            ArrayAdapter<Glumac> adapter = (ArrayAdapter<Glumac>) listview.getAdapter();
            if (adapter != null) {
                adapter.clear();
                try {
                    List<Glumac> list = getDatabaseHelper().getmGlumacDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
