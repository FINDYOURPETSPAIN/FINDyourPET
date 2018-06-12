/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import findyourpet.R;


public class Responder_preguntas extends AppCompatActivity implements View.OnClickListener {

    private TextView preguntaText;
    private EditText respuestaText;
    private Button botonRespuesta;
    View focusView = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_pregunta);

        iniciarVistas();
        iniciarListeners();

    }



    private void iniciarVistas() {

        preguntaText = (TextView) findViewById(R.id.pregunta_pass);
        respuestaText = (EditText) findViewById(R.id.respuesta_pass);
        botonRespuesta = (Button) findViewById(R.id.boton_continuar);

        preguntaText.setText(getIntent().getStringExtra("userPregunta"));

    }

    /**
     * Este metodo inicializa los listeners
     */

    private void iniciarListeners() {

        botonRespuesta.setOnClickListener(this);

    }


    /**
     * Listener para los botones
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_continuar:
                postDataToSQLite();
            break;

        }
    }



    /**
     * acciones de la BBDD pa;ra añadir usuarios, cuando clicamos en el boton register pasará por aqui
     *
     *
     */

    @SuppressLint("ResourceAsColor")
    private void postDataToSQLite() {

        if (respuestaText.getText().toString().trim().equalsIgnoreCase(getIntent().getStringExtra("userRespuesta"))) {

            final Intent nuevaPass = new Intent (getApplicationContext(), NuevaPass.class);

            nuevaPass.putExtra("userEmail", getIntent().getStringExtra("userEmail"));

            startActivity(nuevaPass);

            Responder_preguntas.this.finish();


        } else {

            respuestaText.setError("La respuesta no es correcta");
            focusView = respuestaText;
            focusView.requestFocus();

        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
            Responder_preguntas.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }



}
