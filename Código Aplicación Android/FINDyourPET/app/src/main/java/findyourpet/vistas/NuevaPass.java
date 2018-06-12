/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import findyourpet.R;
import findyourpet.sql.Usuario;


public class NuevaPass extends AppCompatActivity implements View.OnClickListener {

    private EditText password;
    private EditText passwordRepeat;
    private Button resetearPassword;
    private boolean cancel;
    View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_password);


        // pass = (TextView) findViewById(R.id.lbl_loginhere);
        password = (EditText) findViewById(R.id.password_new);
        passwordRepeat = (EditText) findViewById(R.id.password_repeat_new);

        resetearPassword = (Button) findViewById(R.id.boton_renovar);

        iniciarListeners();

    }

    private void iniciarListeners() {

        resetearPassword.setOnClickListener(this);

    }



    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.boton_renovar:
                if(!attemptLogin())
                    postDataToSQLite();
                break;

        }
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;

    }


    private boolean attemptLogin() {

        cancel = false;

        // Reset errors.
        password.setError(null);
        passwordRepeat.setError(null);

        if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError(getString(R.string.error_campo_obligatorio));
            focusView = password;
            cancel = true;
        }else if (!isPasswordValid(password.getText().toString().trim())) {
            password.setError(getString(R.string.Error_password_invalida));
            focusView = password;
            cancel = true;
        }else if (TextUtils.isEmpty(passwordRepeat.getText().toString().trim())) {
            passwordRepeat.setError(getString(R.string.Error_password_diferente));
            focusView = passwordRepeat;
            cancel = true;
        }else if (!password.getText().toString().trim().equals(passwordRepeat.getText().toString().trim())) {
            passwordRepeat.setError(getString(R.string.Error_password_diferente));
            focusView = passwordRepeat;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }

        return cancel;
    }


    private void postDataToSQLite() {

        Thread inicio = new Thread() {
            public void run() {
                String postUrl = "http://104.46.35.52:3000/users";
                Gson gson = new Gson();
                HttpURLConnection con = null;
                URL u = null;
                try {
                    u = new URL(postUrl);
                    con = (HttpURLConnection) u.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestMethod("PUT");

                    Usuario usuario = new Usuario();

                    usuario.setPassword(password.getText().toString().trim());
                    usuario.setEmail(getIntent().getStringExtra("userEmail"));

                    String user = gson.toJson(usuario);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(user);
                    wr.flush();

                    int status = con.getResponseCode();

                    switch (status) {
                        case 200:
                            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            Snackbar popup = Snackbar.make(resetearPassword, getString(R.string.registrado_password), Snackbar.LENGTH_LONG);
                            View sbView = popup.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.rgb(255, 255, 0));

                            popup.show();

                            final Intent loginClass = new Intent (getApplicationContext(), Login.class);

                            Thread inicio = new Thread(){

                                public void run () {
                                    try {

                                        sleep(2000);

                                    } catch (InterruptedException excepcion) {

                                        excepcion.printStackTrace();

                                    } finally {

                                        startActivity(loginClass);
                                        NuevaPass.this.finish();
                                    }
                                }
                            };

                            inicio.start();

                            break;
                        case 400:
                            Snackbar popup1 = Snackbar.make(resetearPassword, "Se ha producido un error en la modificacion", Snackbar.LENGTH_LONG);
                            View sbView1 = popup1.getView();
                            TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                            textView1.setTextColor(Color.rgb(255, 255, 0));

                            popup1.show();
                            break;
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        inicio.start();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
            NuevaPass.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


}
