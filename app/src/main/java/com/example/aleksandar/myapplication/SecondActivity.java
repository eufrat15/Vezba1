package com.example.aleksandar.myapplication;

import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aleksandar.myapplication.db.DatabaseHelper;
import com.example.aleksandar.myapplication.db.model.Film;
import com.example.aleksandar.myapplication.db.model.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            RatingBar ocenaBar = (RatingBar) findViewById(R.id.glumac_ocena_bar);
            ocenaBar.setRating(glumac.getGlumacOcenabar());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.second_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_edit:

                //edit();  // pozivamo metodu edit()

                break;

            // kada pritisnemo ikonicu za brisanje
            case R.id.action_delete:
                try {
                    getDatabaseHelper().getmGlumacDao().delete(glumac);

                    showMessage("Glumac je obrisan");

                    finish();


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            // kada pritisnemo ikonicu za dodavanje filma
            case R.id.action_add_film:

                final Dialog dialog = new Dialog(SecondActivity.this);
                dialog.setContentView(R.layout.dialog_film);


                final EditText filmNaziv = (EditText) dialog.findViewById(R.id.film_naziv);
                final EditText filmZanr = (EditText) dialog.findViewById(R.id.film_zanr);
                final EditText filmGodina = (EditText) dialog.findViewById(R.id.film_godina);


                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String naziv = filmNaziv.getText().toString();
                        if (naziv.isEmpty()) {
                            Toast.makeText(SecondActivity.this, "Ime filma ne sme biti prazno", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String zanr = filmZanr.getText().toString();
                        if (zanr.isEmpty()) {
                            Toast.makeText(SecondActivity.this, "Zanr filma ne sme biti prazan", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int godina = 0;
                        try {
                            godina = Integer.parseInt(filmGodina.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Godina mora biti broj.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Film film = new Film();
                        film.setFilmNaziv(naziv);
                        film.setFilmZanr(zanr);
                        film.setFilmGodinaIzlaska(godina);
                        film.setGlumac(glumac);


                        try {
                            getDatabaseHelper().getmFilmDao().create(film);

                            refresh();

                            showMessage("Novi film je dodan");

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }

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

    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.inputListaFilmovaGlumac);
        if (listview != null) {
            ArrayAdapter<Film> adapter = (ArrayAdapter<Film>) listview.getAdapter();
            if (adapter != null) {

                try {
                    adapter.clear();

                    // konstruisemo QueryBuilder
                    List<Film> films = getDatabaseHelper().getmFilmDao()
                            .queryBuilder()
                            .where()
                            .eq(Film.POLJE_GLUMAC, glumac.getGlumacId())
                            .query();


                    adapter.addAll(films);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    private void edit() {

        final Dialog dialog = new Dialog(SecondActivity.this);
        dialog.setContentView(R.layout.dialog_glumac_edit);

        if (glumac != null) {

            final EditText glumacIme = (EditText) dialog.findViewById(R.id.glumac_ime);
            final EditText glumacBiografija = (EditText) dialog.findViewById(R.id.glumac_biografija);
            final EditText glumacOcena = (EditText) dialog.findViewById(R.id.glumac_ocena);
            final EditText glumacOcenabar = (EditText) dialog.findViewById(R.id.glumac_ocena_bar);
            final EditText glumacDatumRodjenja = (EditText) dialog.findViewById(R.id.glumac_datum_rodjenja);

            glumacIme.setText(glumac.getGlumacIme());
            glumacBiografija.setText(glumac.getGlumacBiografija());

            double ocena = glumac.getGlumacOcena();
            String stringOcena = Double.toString(ocena);
            glumacOcena.setText(stringOcena);

            float ocenaBar = glumac.getGlumacOcenabar();
            String stringOcenabar = Float.toString(ocenaBar);
            glumacOcenabar.setText(stringOcenabar);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String datum = sdf.format(glumac.getGlumacDatumRodjenja());
            glumacDatumRodjenja.setText(datum);

            Button ok = (Button) dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String ime = glumacIme.getText().toString();
                    if (ime.isEmpty()) {
                        Toast.makeText(SecondActivity.this, "Ne sme biti prazno", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String biografija = glumacBiografija.getText().toString();
                    if (biografija.isEmpty()) {
                        Toast.makeText(SecondActivity.this, "Ne sme biti prazno", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double ocena = 0;
                    try {
                        ocena = Double.parseDouble(glumacOcena.getText().toString());
                    }   catch (NumberFormatException e) {
                        Toast.makeText(SecondActivity.this, "Ocena mora biti broj", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float ocenabar = 0;
                    try {
                        ocenabar = Float.parseFloat(glumacOcenabar.getText().toString());
                    }   catch (NumberFormatException e) {
                        Toast.makeText(SecondActivity.this, "Mora biti broj od 1-5", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                    Date datum = null;
                    try {
                        datum = sdf.parse(glumacDatumRodjenja.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(SecondActivity.this, "Datum mora biti u formatu...", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    glumac.setGlumacIme(ime);
                    glumac.setGlumacBiografija(biografija);
                    glumac.setGlumacOcena(ocena);
                    glumac.setGlumacOcenabar(ocenabar);
                    glumac.setGlumacDatumRodjenja(datum);

                    try {
                        getDatabaseHelper().getmGlumacDao().update(glumac);

                        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
                        boolean status = preferences.getBoolean(NOTIF_STATUS, false);

                        if (toast) {
                            Toast.makeText(SecondActivity.this, "Podaci o glumcu su promenjeni", Toast.LENGTH_SHORT).show();
                        }

                        if (status) {
                            Toast.makeText(SecondActivity.this, "Podaci o glumcu su promenjeni", Toast.LENGTH_SHORT).show();
                        }

                        refresh();

                        finish();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
            });

            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();


        }
    }


}
