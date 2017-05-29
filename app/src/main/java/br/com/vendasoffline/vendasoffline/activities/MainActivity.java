package br.com.vendasoffline.vendasoffline.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.fragments.FragmentCadastroCliente;
import br.com.vendasoffline.vendasoffline.model.User;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String diretorio = "/sdcard/Prodest/Imagens/";

    private User usuario;
    private ImageView imgvUsuario;
    private TextView txtNomeUsuario;
    private DatabaseHelper databaseHelper;

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

        registerForContextMenu(imgvUsuario);

        /*imgvUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "foiiiiiiiii", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.imgvFotoUsuario) {
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            // Utilizado para vibrar o celular.
            Vibrator vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(50);

            String[] menuItems = new String[] {"Câmera","Galeria"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){  // Câmera
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, 0);
        }else if (menuItemIndex == 1){ // Galeria
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                /*File tempFile = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
                Uri tempUri = Uri.fromFile(tempFile);

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                pickPhoto.putExtra("crop", "true");
                pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT,
                        tempUri);
                pickPhoto.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(pickPhoto, 1);*/
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    /*Uri selectedImage = data.getData();
                    imgvUsuario.setImageURI(selectedImage);*/
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    //Set<String> keys = data.getExtras().keySet();
                    imgvUsuario.setImageBitmap(photo);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imgvUsuario.setImageURI(selectedImage);
                    /*Bitmap photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.jpg");
                    imgvUsuario.setImageBitmap(photo);*/

                }
                break;
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

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
