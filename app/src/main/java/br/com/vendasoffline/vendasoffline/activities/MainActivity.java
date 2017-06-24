package br.com.vendasoffline.vendasoffline.activities;

import android.Manifest;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.fragments.FragmentCadastroCliente;
import br.com.vendasoffline.vendasoffline.helpers.Utils;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.model.User;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int MY_PERMISSIONS_REQUEST_ACTION_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACTION_PICK = 2;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private static final String diretorio = Environment.getExternalStorageDirectory()+"/Prodest/Imagens/";

    private User usuario;
    private ImageView imgvUsuario;
    private TextView txtNomeUsuario;
    private DatabaseHelper databaseHelper;
    private File folder;
    private SharedPreferences prefs;
    private ProgressDialog load;
    private GetJson download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Fragment fragment = new FragmentCadastroCliente();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.drawer_layout, fragment, fragment.getClass().getSimpleName()).addToBackStack("cadastroCliente").commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefs = getSharedPreferences("br.com.vendasoffline.vendasoffline", MODE_PRIVATE);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        Intent it = getIntent();

        if (it.hasExtra("Usuario")) {
            String user = it.getExtras().getString("Usuario");
            usuario = databaseHelper.getUser(user);
        }

        /*Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera= new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uriSavedImage=Uri.fromFile(new File(String.format(Locale.ENGLISH,"%s%s%s",diretorio,usuario,".png")));
                camera.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(camera, 0);
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });*/

        View hView =  navigationView.getHeaderView(0);
        imgvUsuario = (ImageView) hView.findViewById(R.id.imgvFotoUsuario);
        txtNomeUsuario = (TextView) hView.findViewById(R.id.txtNomeUsuario);

        if (usuario != null){
            txtNomeUsuario.setText(usuario.getNome());
        }

        carregaImagemUsuario();
        registerForContextMenu(imgvUsuario);

        /*imgvUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "foiiiiiiiii", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()==R.id.imgvFotoUsuario) {

            requestAllPermissions();

            if (hasAllPermissions()){
                vibrate();
                String[] menuItems = new String[] {"Câmera","Galeria"};
                for (int i = 0; i<menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){  // Câmera
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /*File image = new File(folder, usuario.getUsuario() + ".jpg");
            Uri uriSavedImage = Uri.fromFile(image);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);*/
            startActivityForResult(takePicture, MY_PERMISSIONS_REQUEST_ACTION_IMAGE_CAPTURE);
        }else if (menuItemIndex == 1){ // Galeria
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {

                File tempFile = new File(diretorio + usuario.getUsuario() + ".jpg");
                Uri tempUri = Uri.fromFile(tempFile);

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                pickPhoto.putExtra("crop", "true");
                pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT,
                        tempUri);
                pickPhoto.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(pickPhoto, MY_PERMISSIONS_REQUEST_ACTION_PICK);
                /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);*/
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_ACTION_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    /*Uri selectedImage = data.getData();
                    imgvUsuario.setImageURI(selectedImage);*/
                    //Set<String> keys = data.getExtras().keySet();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgvUsuario.setImageBitmap(photo);
                    try {
                        File tempFile = new File(diretorio + usuario.getUsuario() + ".jpg");
                        FileOutputStream out = new FileOutputStream(tempFile);
                        photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /*File imgFile = new  File(diretorio,usuario.getUsuario() + ".jpg");

                    if(imgFile.exists()){

                        Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        imgvUsuario.setImageBitmap(photo);

                    }*/
                }

                break;
            case MY_PERMISSIONS_REQUEST_ACTION_PICK:
                if(resultCode == RESULT_OK){
                    /*Uri selectedImage = data.getData();
                    imgvUsuario.setImageURI(selectedImage);*/
                    Bitmap photo = BitmapFactory.decodeFile(diretorio + usuario.getUsuario() + ".jpg");
                    imgvUsuario.setImageBitmap(photo);

                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST){

           if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (hasAllPermissions()){
                    showMessage("Todas as permissões foram concedidas!");
                    folder = new File(diretorio);
                    if (!folder.mkdir()){
                        folder.mkdirs();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent intentRegister = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentRegister);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            Intent intentRegister = new Intent(getApplicationContext(), ListCliente.class);
            startActivity(intentRegister);
        } else if (id == R.id.nav_pedidos) {

        } else if (id == R.id.nav_sincronizar) {
            // Faz sincronização com o webservice.
            (new GetJson()).execute();

        }/* else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission){
        boolean retorno = true;

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        return retorno;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasAllPermissions(){
        boolean retorno = true;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        return retorno;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestAllPermissions(){
        String permis = "Você precisa permitir o acesso ao(s) seguinte(s) recurso(s): \n";
        Boolean comPermis = true;
        Boolean firstRun = false;

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            firstRun = true;
            if (hasAllPermissions()){
                prefs.edit().putBoolean("firstrun", false).commit();
            }
        }

        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !firstRun) {
            permis = permis + "Armazenamento Externo \n";
            comPermis = false;
        }

        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && !hasPermission(Manifest.permission.CAMERA) && !firstRun){
            permis = permis + "Câmera \n";
            comPermis = false;
        }

        if (!comPermis){
            showMessage(permis);
            return;
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            vibrate();
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST);
            return;
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            vibrate();
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            vibrate();
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
    }

    public void vibrate(){
        // Utilizado para vibrar o celular.
        Vibrator vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50);
    }

    public void showMessage(String message){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void carregaImagemUsuario(){
        File imgFile = new  File(diretorio,usuario.getUsuario() + ".jpg");
            if(imgFile.exists() && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                imgvUsuario.setImageBitmap(photo);

            }
    }

    private class GetJson extends AsyncTask<Void, Void, Customer> {

        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(MainActivity.this, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }

        @Override
        protected Customer doInBackground(Void... params) {

            Utils util = new Utils();
            Customer c;
            c=util.getInformacao("https://randomuser.me/api/");
            c.setPais("sdioasjd");
            c.setTipoPessoa("J");

            databaseHelper.addCustomer(c);
            return c;
        }

        @Override
        protected void onPostExecute(Customer cliente){
            String nome;
            String endereco;
            String cidade;
            String uf;

            nome = cliente.getNome().substring(0,1).toUpperCase() + cliente.getNome().substring(1);

            endereco = cliente.getEndereco();
            cidade = cliente.getCidade().substring(0,1).toUpperCase()+cliente.getCidade().substring(1);
            uf = cliente.getUf();

            load.dismiss();
        }
    }
}
