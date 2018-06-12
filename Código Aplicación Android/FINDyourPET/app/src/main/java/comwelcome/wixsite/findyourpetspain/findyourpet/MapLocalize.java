/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/

package comwelcome.wixsite.findyourpetspain.findyourpet;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import findyourpet.Manage.GeocodingAddress;
import findyourpet.R;
import findyourpet.vistas.List_Gestion;
import findyourpet.vistas.List_Localize;
import findyourpet.vistas.Registro_dispositivo;
import findyourpet.vistas.SmsIsIn;

public class MapLocalize extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtiene el fragmento del mapa y observa si está listo para mostrarse
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Establece el tipo de mapa, añade un marcador y hace zoom en la camara
        LatLng posicion = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitud")), Double.parseDouble(getIntent().getStringExtra("longitud")));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.addMarker(new MarkerOptions().position(posicion).title("Marcador de " + getIntent().getStringExtra("nombreMascota")).icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_fyp_map)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion,16));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            final Intent listlocalize = new Intent(getApplicationContext(), List_Localize.class);

            listlocalize.putExtra("userId", getIntent().getIntExtra("userId", 0));

            Thread inicio = new Thread(){

                public void run () {

                    String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                    String op = getJSON(stringUrl, 1000000);

                    listlocalize.putExtra("devicesUser", op);

                    startActivity(listlocalize);
                    MapLocalize.this.finish();

                }
            };

            inicio.start();

            return true;

        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }

    public final String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setUseCaches(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }



}
