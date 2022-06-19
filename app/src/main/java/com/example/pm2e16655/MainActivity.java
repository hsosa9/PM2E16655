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

public class MainActivity extends AppCompatActivity {

    EditText nombre, telefono, nota;
    Spinner pais;
    ImageView imagen;
    Button btnTomarFoto, btnSubirFoto, btnsalvar, btncontactos;

    private Spinner spinner;
    private String[] arraycontenido;
    private AdaptadorSpinner adapter;

    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.sppaices);
        arraycontenido = new String[]{"Honduras(504)", "Costa Rica(506)", "Guatemala(502)", "El Salvador(503)"};
        adapter = new AdaptadorSpinner(this, arraycontenido);
        spinner.setAdapter(adapter);

        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnSubirFoto = (Button) findViewById(R.id.btnSubirFoto);
        btnsalvar = (Button)findViewById(R.id.btnEditar);
        btncontactos = (Button)findViewById(R.id.btncontactos);
        pais = (Spinner)findViewById(R.id.sppaices);
        nombre = (EditText)findViewById(R.id.txtnombre);
        telefono = (EditText)findViewById(R.id.txttelefono);
        nota = (EditText)findViewById(R.id.txtnota);
        imagen = (ImageView) findViewById(R.id.imagen);

        // -- BOTON PARA AGREGAR CONTACTO --
        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarUsuario();
            }
        });

        // -- BOTON PARA VER LA LISTA DE CONTACTOS --
        btncontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    // ----- INICIO DE LOS METODOS -----

    // -- METODO PARA AGREGAR UN CONTACTO --
    private void AgregarUsuario() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores =  new ContentValues();
        valores.put(Transacciones.pais, pais.getSelectedItem().toString());
        valores.put(Transacciones.nombre, nombre.getText().toString());
        valores.put(Transacciones.telefono, telefono.getText().toString());
        valores.put(Transacciones.nota, nota.getText().toString());

        if (validar() == true){
            Long resultado = db.insert(Transacciones.tablausuarios, Transacciones.nombre, valores);
            Toast.makeText(getApplicationContext(), "Registro Ingresado Correctamente: " + resultado, Toast.LENGTH_LONG).show();
            db.close();
            ClearScreen();
        }

    }

    // -- METODO PARA VALIDAR CAMPOS VACIOS Y MOSTRAR ALERTAS --
    public boolean validar(){
        boolean retorno= true;

        String nom= nombre.getText().toString();
        String tel= telefono.getText().toString();
        String nt= nota.getText().toString();

        if(nom.isEmpty()){
            nombre.setError("DEBE INGRESAR EL NOMBRE");
            retorno = false;
        }
        if(tel.isEmpty()){
            telefono.setError("DEBE INGRESAR EL NUMERO TELEFONICO");
            retorno = false;
        }
        if(nt.isEmpty()){
            nota.setError("DEBE INGRESAR UNA NOTA DE RECORDATORIO");
            retorno = false;
        }

        return retorno;
    }

    // -- METODO PARA LIMPIAR PANTALLA --
    private void ClearScreen() {
        nombre.setText("");
        telefono.setText("");
        nota.setText("");
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
            imagen.setImageURI(path);

            if (requestCode == TAKE_PIC_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap img = (Bitmap) extras.get("data");
                imagen.setImageBitmap(img);
            }
        }
    }

}