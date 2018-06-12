/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import findyourpet.R;

public class SmsIncoming extends AppCompatActivity /*implements View.OnClickListener*/ {

    TextView wait;
    int id = 0;
    String numero;
    Boolean encontrado = false;
    Uri smsUri = Uri.parse("content://sms/");
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espera);

        this.numero = getIntent().getStringExtra("numberPhone");

        this.cursor = SmsIncoming.this.getContentResolver().query(smsUri, null, null, null, null);

        Thread inicio = new Thread(){

            public void run () {
                try {

                    sleep(10);

                } catch (InterruptedException excepcion) {

                    excepcion.printStackTrace();

                } finally {

                    WaitSMS();
                }
            }
        };

        inicio.start();

    }



    public void WaitSMS() {


        //leer el ultimo id de mensaje
        if (!this.cursor.moveToFirst()) { /* false = cursor is empty */
            return;
        }

        this.id = Integer.parseInt(this.cursor.getString(0)) + 1;

        while(!encontrado){

            this.cursor = SmsIncoming.this.getContentResolver().query(smsUri, null, null, null, null);
            if (!this.cursor.moveToFirst()) { /* false = cursor is empty */
                return;
            }
            int id2 = Integer.parseInt(this.cursor.getString(0));
            if(id2 == this.id){

                encontrado = true;

            }
        }

        final Intent mostrarSMS = new Intent(getApplicationContext(), SmsIsIn.class);

        mostrarSMS.putExtra("userId", getIntent().getIntExtra("userId",0));
        mostrarSMS.putExtra("Id", this.cursor.getString(0));
        mostrarSMS.putExtra("numberPhone",this.cursor.getString(2));
        mostrarSMS.putExtra("content", this.cursor.getString(12));
        mostrarSMS.putExtra("nombreMascota", getIntent().getStringExtra("nombreMascota"));

        this.cursor.close();

        startActivity(mostrarSMS);
        SmsIncoming.this.finish();
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
                    SmsIncoming.this.finish();

                }
            };

            inicio.start();

            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }
}