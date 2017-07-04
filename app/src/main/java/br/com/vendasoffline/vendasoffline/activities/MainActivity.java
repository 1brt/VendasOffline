package br.com.vendasoffline.vendasoffline.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.classes.GetJson;
import br.com.vendasoffline.vendasoffline.classes.Permission;
import br.com.vendasoffline.vendasoffline.model.User;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int MY_PERMISSIONS_REQUEST_ACTION_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACTION_PICK = 2;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final String diretorio = Environment.getExternalStorageDirectory()+"/Prodest/Imagens/";

    private User usuario;
    private ImageView imgvUsuario;
    private DatabaseHelper databaseHelper;
    private ProgressDialog load;
    private Permission permis;
    private ImageView imgView01;
    private ImageView imgView02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences prefs = getSharedPreferences("br.com.vendasoffline.vendasoffline", MODE_PRIVATE);

        permis = new Permission(MainActivity.this, prefs);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        Intent it = getIntent();

        if (it.hasExtra("Usuario")) {
            String user = it.getExtras().getString("Usuario");
            usuario = databaseHelper.getUser(user);
        }

        View hView =  navigationView.getHeaderView(0);
        imgvUsuario = (ImageView) hView.findViewById(R.id.imgvFotoUsuario);

        imgView01 = (ImageView) findViewById(R.id.imgView01);
        imgView02 = (ImageView) findViewById(R.id.imgView02);

        new ImageLoadTask("http://chart.googleapis.com/chart?cht=lc&chco=FF6342%2CADDE63%2C63C6DE&chs=420x250&chd=t%3A12.92%2C8.98%2C17.5%2C11.6%7C7.5%2C10.12%2C12.3%2C15.25%7C8.76%2C11.25%2C13.02%2C14.33&chl=6%20meses%7C1%20ano%7C1a%206m%7C2%20anos&chdl=Peso%20Min.%7CPeso%20M%C3%A1x.%7C%20Peso",imgView01).execute();

        TextView txtNomeUsuario = (TextView) hView.findViewById(R.id.txtNomeUsuario);
        TextView txtNomeEmail = (TextView) hView.findViewById(R.id.txtNomeEmail);

        if (usuario != null){
            txtNomeUsuario.setText(usuario.getNome());
            txtNomeEmail.setText(usuario.getEmail());
        }

        carregaImagemUsuario();
        registerForContextMenu(imgvUsuario);

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()==R.id.imgvFotoUsuario) {

            permis.requestContentPermissions();

            if (permis.hasContentPermissions()){
                menu.setHeaderTitle("Selecione uma Opção");
                String[] menuItems = new String[] {"Câmera","Galeria"};
                for (int i = 0; i<menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){  // Câmera
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

                }

                break;
            case MY_PERMISSIONS_REQUEST_ACTION_PICK:
                if(resultCode == RESULT_OK){
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
                if (permis.hasContentPermissions()){
                    File folder = new File(diretorio);
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
            Intent intentRegister = new Intent(getApplicationContext(), ListPedido.class);
            startActivity(intentRegister);
        } else if (id == R.id.nav_sincronizar) {
            // Faz sincronização com o webservice.
            (new GetJson(MainActivity.this)).execute();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void carregaImagemUsuario(){
        File imgFile = new  File(diretorio,usuario.getUsuario() + ".jpg");
        if(imgFile.exists() && permis.hasExternalPermission()){

            Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imgvUsuario.setImageBitmap(photo);

        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
