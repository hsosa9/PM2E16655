package com.example.pm2e16655;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pm2e16655.transacciones.Transacciones;


public class ActivityActualizar extends AppCompatActivity {

    String nombre, nota,ID;
    int id;
    int telefono;
    EditText nom, tel, note;

    Button btnEditar,btnTomarFoto, btnSubirFoto, btnatras;
    ImageView foto;
    SQLiteConexion conexion;

    private Spinner spinner;
    private String[] arraycontenido;
    private AdaptadorSpinner adapter;

    static final int PETICION_ACCESO_CAM = 102;
    static final int TAKE_PIC_REQUEST = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        spinner = findViewById(R.id.sppaices);
        arraycontenido = new String[]{"Honduras(504)", "Costa Rica(506)", "Guatemala(502)", "El Salvador(503)"};
        adapter = new AdaptadorSpinner(this, arraycontenido);
        spinner.setAdapter(adapter);

        nom = (EditText) findViewById(R.id.txtnombre);
        tel = (EditText) findViewById(R.id.txttelefono);
        note = (EditText) findViewById(R.id.txtnota);
        btnEditar = (Button) findViewById(R.id.btnEditar);
        btnatras = (Button) findViewById(R.id.btnatras);
        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnSubirFoto = (Button) findViewById(R.id.btnSubirFoto);
        foto = (ImageView) findViewById(R.id.foto);

        recibirDatos();

        // -- BOTON PARA REGRESAR AL ActivityList --
        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }
        });

        // -- BOTON PARA TOMAR LA FOTOGRAFIA --
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisosCamara();
            }
        });

        // -- BOTON PARA CARGAR LA IMAGEN DE LA GALERIA --
        btnSubirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarImagen();
            }
        });

        // -- BOTON PARA ACTUALIZAR EL CONTACTO EN LA BD --
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actualizar();
            }
        });

    }

    // ----- INICIO DE METODOS -----

    // -- METODO PARA RECIBIR LOS DATOS DEL ActivityListView
    public void recibirDatos(){
        Bundle extras = getIntent().getExtras();

        //Pais  = extras.getString("pais");
        id  = extras.getInt("id");
        nombre  = extras.getString("nombre");
        telefono  = extras.getInt("telefono");
        nota = extras.getString("nota");

        ID = String.valueOf(id);
        nom.setText(nombre);
        tel.setText(""+telefono);
        note.setText(nota);


    }

    // -- METODO PARA ACTUALIZAR EL CONTACTO EN LA BASE DE DATOS
    private void Actualizar() {
        conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String [] params = {ID};

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.pais, spinner.getSelectedItem().toString());
        valores.put(Transacciones.nombre, nom.getText().toString());
        valores.put(Transacciones.telefono, tel.getText().toString());
        valores.put(Transacciones.nota, note.getText().toString());

        if (validar() == true) {
            db.update(Transacciones.tablausuarios, valores, Transacciones.id + "=?", params);
            Toast.makeText(getApplicationContext(), "Dato Actualizado" + ID, Toast.LENGTH_LONG).show();
        }
    }

    // -- METODO PARA VALIDAR CAMPOS VACIOS Y MOSTRAR ALERTAS --
    public boolean validar(){
        boolean retorno= true;

        String name= nom.getText().toString();
        String cel= tel.getText().toString();
        String nt= note.getText().toString();

        if(name.isEmpty()){
            nom.setError("DEBE INGRESAR EL NOMBRE");
            retorno = false;
        }
        if(cel.isEmpty()){
            tel.setError("DEBE INGRESAR EL NUMERO TELEFONICO");
            retorno = false;
        }
        if(nt.isEmpty()){
            note.setError("DEBE INGRESAR UNA NOTA DE RECORDATORIO");
            retorno = false;
        }

        return retorno;
    }

    // -- METODO PARA DAR PERMISOS PARA USAR LA CAMARA --
    private void permisosCamara(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_ACCESO_CAM);
        }else{
            tomarFoto();
            //dispatchTakePictureIntent();
        }
    }

    // -- METODO PARA LA RESPUESTA AL PERMISO A LA CAMARA
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_ACCESO_CAM){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                tomarFoto();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Necesita permiso de acceso a la camara",Toast.LENGTH_LONG).show();
        }
    }

    // -- METODO PARA IR A CAPTURAR LA FOTOGRAFIA --
    private void tomarFoto(){
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takepic.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takepic,TAKE_PIC_REQUEST);
        }
    }

    // -- METODO PARA CARGAR LA IMAGEN DE LA GALERIA DEL SISTEMA --
    private void cargarImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicación"),10);
    }

    // -- METODO PARA OBTENER LA FOTOGRAFIA Y MOSTRARLA EN LA APP --
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if ( resultCode == RESULT_OK) {
            Uri path=data.getData();
            foto.setImageURI(path);

            if (requestCode == TAKE_PIC_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap img = (Bitmap) extras.get("data");
                foto.setImageBitmap(img);
            }
        }
    }

}