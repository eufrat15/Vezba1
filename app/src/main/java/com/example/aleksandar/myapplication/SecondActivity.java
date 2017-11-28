package com.example.aleksandar.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aleksandar.myapplication.db.DatabaseHelper;
import com.example.aleksandar.myapplication.db.model.Film;
import com.example.aleksandar.myapplication.db.model.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.aleksandar.myapplication.MainActivity.NOTIF_STATUS;
import static com.example.aleksandar.myapplication.MainActivity.NOTIF_TOAST;

public class SecondActivity extends AppCompatActivity {

    private int position = 0;

    private DatabaseHelper databaseHelper;
    private Glumac glumac;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toolbar toolbar = (Toolbar) findViewById(R.id.second_toolbar);
        setSupportActionBar(toolbar);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        try {
            glumac = getDatabaseHelper().getmGlumacDao().queryForId(position);
            String ime = glumac.getGlumacIme();
            String biografija = glumac.getGlumacBiografija();
            double ocena = glumac.getGlumacOcena();
            String stringOcena = Double.toString(ocena);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String datum = sdf.format(glumac.getGlumacDatumRodjenja());

            TextView imeGlumac = (TextView) findViewById(R.id.imeGlumac);
            imeGlumac.setText(ime);

            TextView datumGlumac = (TextView) findViewById(R.id.inputDatumRodjenjaGlumac);
            datumGlumac.setText(datum);

            TextView biografijaGlumac = (TextView) findViewById(R.id.inputBiografijaGlumac);
            biografijaGlumac.setText(biografija);

            TextView ocenaGlumac = (TextView) findViewById(R.id.inputOcenaGlumac);
            ocenaGlumac.setText(stringOcena);

            final ListView listView = (ListView) findViewById(R.id.inputListaFilmovaGlumac);

            List<Film> filmovi = getDatabaseHelper().getmFilmDao().queryBuilder().where().eq(Film.POLJE_GLUMAC, position).query();

            List<String> filmoviNazivi = new ArrayList<>();
            for (Film f : filmovi) {
                filmoviNazivi.add(f.getFilmNaziv());
            }
            ListAdapter adapter = new ArrayAdapter<String>(SecondActivity.this, R.layout.list_item_film, filmoviNazivi);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Film f = (Film) listView.getItemAtPosition(position);
                    Toast.makeText(SecondActivity.this, "FILM: " + f.getFilmNaziv() + "\n" +
                                    "ZANR: " + f.getFilmZanr() + "\n" +
                                    "GODINA: " + f.getFilmGodinaIzlaska() + "\n" +
                                    "GLUMAC: " + f.getGlumac(),
                                    Toast.LENGTH_LONG).show();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {

        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
        boolean status = preferences.getBoolean(NOTIF_STATUS, false);

        if (toast) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        if (status) {
            showStatusMesage(message);
        }
    }

    private void showStatusMesage(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Ispit");
        builder.setContentText(message);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_movietable);
        builder.setLargeIcon(bm);

        notificationManager.notify(1, builder.build());
    }

    //

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    // osvezavanje baze
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
