/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.Manage;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import findyourpet.R;
import findyourpet.sql.Dispositivos;
import findyourpet.vistas.List_Gestion;

public class Adaptador_gestion extends BaseAdapter {

    private Activity activity; //Activity desde el cual se hace referencia al llenado de la lista
    private ArrayList<Dispositivos> arrayItems; // Lista de items
    private List_Gestion vistaGestion;

    // Constructor con parámetros que recibe la Acvity y los datos de los items.
    public Adaptador_gestion(Activity activity, ArrayList<Dispositivos> listaItems, List_Gestion instancia){
        super();
        this.activity = activity;
        this.arrayItems = new ArrayList<Dispositivos>(listaItems);
        this.vistaGestion = instancia;
    }

    // Retorna el número de items de la lista
    @Override
    public int getCount() {
        return arrayItems.size();
    }

    // Retorna el objeto Dispositivos de la lista
    @Override
    public Object getItem(int position) {
        return arrayItems.get(position);
    }

    // Retorna la posición del item en la lista
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    Clase estática que contiene los elementos de la lista
    */
    public static class Fila
    {
        TextView NombreDispositivo;
        TextView NumeroDispositivo;
        ImageView img;
        ImageButton botonEliminar;
    }

    // Método que retorna la vista formateada
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fila view = new Fila();
        LayoutInflater inflator = activity.getLayoutInflater();
        final Dispositivos itm = arrayItems.get(position);
 /*
 Condicional para recrear la vista y no distorcionar el número de elementos
 */
        if(convertView==null)
        {
            convertView = inflator.inflate(R.layout.lista_dispositivos_gestion, parent, false);
            view.NombreDispositivo = (TextView) convertView.findViewById(R.id.txtTitle);
            view.NumeroDispositivo = (TextView) convertView.findViewById(R.id.txtDescription);
            view.img = (ImageView)convertView.findViewById(R.id.imgItem);
            view.botonEliminar = (ImageButton) convertView.findViewById(R.id.deleteImageButton);
            convertView.setTag(view);
        }
        else
        {
            view = (Fila)convertView.getTag();
        }

        // Se asigna el dato proveniente del objeto Dispositivos
        view.NombreDispositivo.setText(itm.getNombre());
        view.NumeroDispositivo.setText(itm.getTelefono());
        view.img.setImageResource(itm.getImg());

        //Listener del boton eliminar
        final Fila finalView = view;
        view.botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(vistaGestion)
                        .setIcon(R.drawable.ic_delete_blue)
                        .setTitle(R.string.eliminardispositivo)
                        .setMessage("¿Estás seguro que deseas eliminar el dispositivos " + itm.getNombre() + "?")
                        .setNegativeButton(R.string.cancelar, null)
                        .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                final int IdDispositivo = itm.getId();

                                Thread inicio = new Thread(){

                                    public void run () {

                                        String stringUrl = "http://104.46.35.52:3000/users/devices/" + IdDispositivo;

                                        String op = getJSON(stringUrl, 1000000);

                                        //Actualizamos la vista de gestión
                                        vistaGestion.onUpdate();

                                    }
                                };

                                inicio.start();
                            }
                        })
                        .show();
            }
        });


        // Retornamos la vista
        return convertView;
    }


    public final String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("DELETE");
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
