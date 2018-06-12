/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import findyourpet.R;


public class Main extends AppCompatActivity {

    //Animation slide;
    private FrameLayout portada;
    private ImageView findyourpet_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        portada = (FrameLayout) findViewById(R.id.Portada);
        findyourpet_logo = (ImageView) findViewById(R.id.logo_fyp);

        Animation animacion_principal = AnimationUtils.loadAnimation(this, R.anim.transicion_inicio);

        portada.startAnimation(animacion_principal);
        findyourpet_logo.startAnimation(animacion_principal);

        final Intent login = new Intent(this, Login.class);

        Thread inicio = new Thread() {

            public void run() {
                try {

                    // Esperamos 4 segundos hasta que finalice la transición.
                    sleep(4000);

                } catch (InterruptedException excepcion) {

                    excepcion.printStackTrace();

                } finally {

                    startActivity(login);
                    finish();
                }
            }
        };

        inicio.start();
    }
}
