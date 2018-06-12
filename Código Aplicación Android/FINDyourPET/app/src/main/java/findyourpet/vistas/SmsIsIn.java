/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import comwelcome.wixsite.findyourpetspain.findyourpet.MapLocalize;
import findyourpet.Manage.GeocodingAddress;
import findyourpet.R;

public class SmsIsIn extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readsms);

        TraducirSms();

    }

    public void TraducirSms() {

        String sms = getIntent().getStringExtra("content");
        String[] parts = sms.split("Dw:");
        String[] partes = parts[1].split(", Spain");
        final String direccion = partes[0];
        //String URLString = "https://maps.googleapis.com/maps/api/geocode/json?address="+direccion+",+ES&key=AIzaSyDqnE3AcfAH8UrW4nSWkSi7mb_jSD2P1ig";

        Thread inicio = new Thread() {
            public void run() {
                String stringUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="+direccion+",+ES&key=AIzaSyDqnE3AcfAH8UrW4nSWkSi7mb_jSD2P1ig";

                String resultado = getJSON(stringUrl,1000000);

                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    GeocodingAddress apiResposne = objectMapper.readValue(resultado, GeocodingAddress.class);

                    final Intent mapa = new Intent(getApplicationContext(), MapLocalize.class);

                    mapa.putExtra("latitud", apiResposne.getResults()[0].getGeometry().getLocation().getLat());
                    mapa.putExtra("longitud", apiResposne.getResults()[0].getGeometry().getLocation().getLng());
                    mapa.putExtra("userId", getIntent().getIntExtra("userId",0));
                    mapa.putExtra("nombreMascota", getIntent().getStringExtra("nombreMascota"));

                    startActivity(mapa);

                    SmsIsIn.this.finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        inicio.start();


    }

    public final String getJSON(String url, int timeout) {
        HttpsURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpsURLConnection) u.openConnection();
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
                    SmsIsIn.this.finish();

                }
            };

            inicio.start();

            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


}


