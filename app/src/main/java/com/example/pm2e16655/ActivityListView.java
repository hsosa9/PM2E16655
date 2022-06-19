package com.example.pm2e16655;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.pm2e16655.tablas.Contactos;
import com.example.pm2e16655.transacciones.Transacciones;

import java.util.ArrayList;

public class ActivityListView extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    SQLiteConexion conexion;
    ListView listausuarios;
    ArrayList<Contactos> lista;
    ArrayList<String> ArregloUsuarios;
    EditText nombre, telefono,nota, buscar;
    public String Pais,Nombre,Nota;
    public int ID,Telefono;
    public String id_contacto;
    Button btnactualizar, btncompartir, btnatras, btneliminar,btnimagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        btnatras = (Button)findViewById(R.id.btnatras);
        btneliminar = (Button)findViewById(R.id.btneliminar);
        btnactualizar = (Button)findViewById(R.id.btnactualizar);
        btncompartir = (Button) findViewById(R.id.btncompartir);
        listausuarios = (ListView)findViewById(R.id.listausuarios);
        buscar = (EditText)findViewById(R.id.txtbusqueda);

        // -- CONEXION A LA BASE DE DATOS
        conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);

        ObtenerListaUsuarios();

        // -- ADAPTA LA MATRIZ DE OBJETOS COMO FUENTE DE DATOS --
        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ArregloUsuarios);
        listausuarios.setAdapter(adp);

        // --- BARRA DEL BUSCADOR ---
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adp.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // -- BOTÓN ATRÁS PARA REGRESAR AL MENÚ PRINCIPAL --
        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.example.pm2e16655.MainActivity.class);
                startActivity(intent);
            }
        });

        // -- EFECTO DE SELECCIÓN --
        listausuarios.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // -- OBTENER ITEM SELECCIONADO --
        listausuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ID = lista.get(position).getId();
                Pais = lista.get(position).getPais();
                Nombre = lista.get(position).getNombre();
                Telefono = lista.get(position).getTelefono();
                Nota = lista.get(position).getNota();

                id_contacto = String.valueOf(ID);

            }
        });

        // -- EVENTO DOBLE CLIC EN LA LISTA PARA ABRIR ALERT DIALOG
        listausuarios.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    AlertDialog();
                    //Toast.makeText(getApplicationContext(),"Doble clic", Toast.LENGTH_SHORT).show();
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        // -- BOTÓN PARA COMPARTIR LOS DATOS CON OTRA APLICACIÓN DEL SISTEMA --
        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Nombre == null && Telefono <=0 && Nota == null){
                    Toast.makeText(ActivityListView.this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
                }else {
                    Compartir();
                }
            }
        });

        // -- BOTÓN ACTUALIZAR PARA ENVIAR LOS DATOS SELECCIONADOS AL ActivityActualizar --
        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Nombre == null && Telefono <=0 && Nota == null){
                    Toast.makeText(ActivityListView.this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), com.example.pm2e16655.ActivityActualizar.class);
                    intent.putExtra("id", ID);
                    intent.putExtra("pais", Pais);
                    intent.putExtra("nombre", Nombre);
                    intent.putExtra("telefono", Telefono);
                    intent.putExtra("nota", Nota);
                    //intent.putExtra("Dato4",correo);
                    startActivity(intent);
                }
            }
        });

        // -- BOTÓN ELIMINAR PARA BORRAR UN REGISTRO SELECCIONADO --
        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Nombre == null && Telefono <=0 && Nota == null) {
                    Toast.makeText(ActivityListView.this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
                }
                else {
                    android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder(ActivityListView.this);
                    builder.setMessage("Desea eliminar a "+ Nombre);
                    builder.setTitle("Eliminar");

                    builder.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Eliminar();

                            Intent intent = new Intent(ActivityListView.this, ActivityListView.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    // ----- INICIO DE METODOS -----

    // -- METODO PARA REALIZAR UNA LLAMADA --
    public void Llamar(){

        if (ContextCompat.checkSelfPermission(ActivityListView.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ActivityListView.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
        }else{
            String dial = "tel:"+Telefono;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    // -- METODO PARA OBTENER PERMISO PARA REALIZAR LLAMADAS --
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Llamar();
            }else{
                Toast.makeText(this,"Permiso Denegado",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // -- METODO PARA ALERT DIALOG QUE PERMITA O NO REALIZAR LA LLAMADA --
    public void AlertDialog() {
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setTitle("Accion");
        myBuild.setMessage("Desea realizar la llamada?");
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Llamar();
            }
        });
        myBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = myBuild.create();
        dialog.show();
    }

    // -- METODO PARA COMPARTIR UN TEXTO A OTRA APLICACION DEL SISTEMA --
    public void Compartir(){
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,Nombre+" "+Telefono);
        Intent eleccion = Intent.createChooser(intent,"Compartir usando");
        startActivity(eleccion);
    }

    // -- METODO PARA OBTENER LOS REGISTROS DE LA TABLA EN LA BASE DE DATOS --
    private void ObtenerListaUsuarios() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        Contactos listContactos = null;
        lista = new ArrayList<Contactos>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tablausuarios, null);
        while (cursor.moveToNext()){
            listContactos = new Contactos();
            listContactos.setId(cursor.getInt(0));
            listContactos.setPais(cursor.getString(1));
            listContactos.setNombre(cursor.getString(2));
            listContactos.setTelefono(cursor.getInt(3));
            listContactos.setNota(cursor.getString(4));
            lista.add(listContactos);
        }


        cursor.close();
        fillList();
    }

    // -- METODO PARA RELLENAR LA LISTA --
    private void fillList() {

        ArregloUsuarios = new ArrayList<String>();
        for (int i = 0; i<lista.size(); i++){
            ArregloUsuarios.add(
                            lista.get(i).getId() +" | "+
                            lista.get(i).getPais() +" | "+
                            lista.get(i).getNombre() +" | "+
                            lista.get(i).getTelefono());
        }
        System.out.println("ID"+ID);
    }

    // -- METODO PARA ELIMINAR UN REGISTRO DE LA BASE DE DATOS --
    private void Eliminar() {
        conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String [] params = {id_contacto};

        db.delete(Transacciones.tablausuarios,Transacciones.id+"=?",params);
        Toast.makeText(getApplicationContext(), "Dato Eliminado", Toast.LENGTH_LONG).show();
    }

    // -- METODO PARA LIMPIAR PANTALLA --
    private void ClearScreen() {
        nombre.setText("");
        telefono.setText("");
        nota.setText("");
    }
}