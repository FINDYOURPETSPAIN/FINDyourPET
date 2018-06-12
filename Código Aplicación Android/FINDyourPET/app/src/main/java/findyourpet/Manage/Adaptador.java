/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/

package findyourpet.Manage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import findyourpet.R;
import findyourpet.sql.Dispositivos;

public class Adaptador extends BaseAdapter {

    private Activity activity; //Activity desde el cual se hace referencia al llenado de la lista
    private ArrayList<Dispositivos> arrayItems; // Lista de items

    // Constructor con parámetros que recibe la Acvity y los datos de los items.
    public Adaptador(Activity activity, ArrayList<Dispositivos> listaItems){
        super();
        this.activity = activity;
        this.arrayItems = new ArrayList<Dispositivos>(listaItems);
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
    }

    // Método que retorna la vista formateada
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fila view = new Fila();
        LayoutInflater inflator = activity.getLayoutInflater();
        Dispositivos itm = arrayItems.get(position);
         /*
         Condicional para recrear la vista y no distorcionar el número de elementos
         */
        if(convertView==null)
        {
            convertView = inflator.inflate(R.layout.lista_dispositivos, parent, false);
            view.NombreDispositivo = (TextView) convertView.findViewById(R.id.txtTitle);
            view.NumeroDispositivo = (TextView) convertView.findViewById(R.id.txtDescription);
            view.img = (ImageView)convertView.findViewById(R.id.imgItem);
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
        // Retornamos la vista
        return convertView;
    }
}
