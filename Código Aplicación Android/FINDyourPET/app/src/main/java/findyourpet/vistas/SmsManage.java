/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import findyourpet.R;

public class SmsManage extends AppCompatActivity {

    private ImageView imagenView;
    private EditText txtNombreDestino;
    private EditText txtNumeroTelefono;
    private EditText txtMensaje;
    Button EnviarMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localize);

        iniciarVistas();

    }


    private void iniciarVistas() {

        imagenView = (ImageView) findViewById(R.id.logo_corto_sms);
        txtNombreDestino = (EditText) findViewById(R.id.txtNombreDestino);
        txtNumeroTelefono = (EditText) findViewById(R.id.txtNumeroTelefono);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        EnviarMensaje = (Button) findViewById(R.id.btnEnviar);

    }

    public void EnviarMensaje(View v){


        String numTel = txtNumeroTelefono.getText().toString();
        String datosCompletos = txtNombreDestino.getText().toString() + " " + txtMensaje.getText().toString();

        try{

            int chequear_permisos1 = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            int chequear_permisos2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

            if (chequear_permisos1 != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(getApplicationContext(), "No se tiene permiso para enviar sms", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);

            }
            if (chequear_permisos2 != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(getApplicationContext(), "No se tiene permiso para el estado del telefono", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

            }

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numTel, null, datosCompletos, null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();



        }

        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos." + e.getMessage().toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



}
