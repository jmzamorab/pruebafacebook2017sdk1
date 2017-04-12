package org.jordi.pruebafacebook2017sdk1;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.BitmapFactory.decodeResource;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "1upGedXidBGuacfhfvUOMRldz";
    private static final String TWITTER_SECRET = "9U6Ze7AZM6oNYfDxGcs1Ckga83MFuySiPQ5TJ41kuiGrZf1vKy";

    private String tag = "MainActivity";
    private Bitmap bitmapOriginal = null;
    private Bitmap bitmapGrises = null;
    private Bitmap bitmapSepia = null;
    private Bitmap bitmapMarco_1 = null;
    private ImageView ivDisplay = null;
    private MenuItem mnuItem;
    private Menu mnu;
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private Button shareFacebookBtn;
    private Button shareTwitterBtn;
    private Bitmap photo;


    static {
        System.loadLibrary("prueba");
    }

    // funciones de Código nativo
    public native void convertirGrises(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void convertirSepia(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void creaMarco(Bitmap bitmapIn, Bitmap bitmapOut);

    public native void creaMarcoCallBack(Bitmap bitmapIn, Bitmap bitmapOut);

    public static boolean hayPixel(int x, int y) {
        return x % 10 == y % 10;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Log.i(tag, "Imagen antes de modificar");
        ivDisplay = (ImageView) findViewById(R.id.ivDisplay);
        shareFacebookBtn = (Button) findViewById(R.id.shareFacebookBtn);
        shareTwitterBtn = (Button) findViewById(R.id.shareTwitterBtn);
        shareFacebookBtn.setEnabled(true);
        shareTwitterBtn.setEnabled(true);
        setColor(true);
        shareFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(MainActivity.this, FacebookActivity.class);
                i.putExtra("photo",bitmapOriginal);
                startActivity(i);*/
                photo=((BitmapDrawable)ivDisplay.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] byteArray = stream.toByteArray();
                Intent i = new Intent(MainActivity.this, FacebookActivity.class);
                i.putExtra("photo",byteArray);
                startActivity(i);
            }
        });

        shareTwitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap bitmap = ((BitmapDrawable)ivDisplay.getDrawable()).getBitmap();
                //escalaImagen(bitmap);
                //ivDisplay.setDrawingCacheEnabled(true);
                //ivDisplay.buildDrawingCache();
                //photo = ivDisplay.getDrawingCache();
                photo=((BitmapDrawable)ivDisplay.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] byteArray = stream.toByteArray();
                Intent i = new Intent(MainActivity.this, TwiterActivity.class);
                i.putExtra("photo",byteArray);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void setColor(Boolean imgOrigen) {
        Log.i(tag, "Resetear Imagen");
        if (imgOrigen) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Asegurar que la imagen tiene 24 bits de color
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmapOriginal = BitmapFactory.decodeResource(this.getResources(), R.drawable.sampleimage, options);
            if (bitmapOriginal != null){
                ivDisplay.setImageBitmap(bitmapOriginal);
                //escalaImagen(bitmapOriginal);
            }
        }
        ivDisplay.setImageBitmap(bitmapOriginal);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_reset:
                setColor(true);
                break;
            case R.id.color:
                setColor(false);
                break;
            case R.id.gray:
                Log.i(tag, "Conversion a escala de grises");
                bitmapGrises = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
                convertirGrises(bitmapOriginal, bitmapGrises);
                ivDisplay.setImageBitmap(bitmapGrises);
                break;
            case R.id.sepia:
                Log.i(tag, "Conversion a escala a Sepia");
                bitmapSepia = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
                convertirSepia(bitmapOriginal, bitmapSepia);
                ivDisplay.setImageBitmap(bitmapSepia);
                break;
            case R.id.marco_1:
                Log.i(tag, "Creacion de Marco_1");
                bitmapMarco_1 = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
                creaMarco(bitmapOriginal, bitmapMarco_1);
                ivDisplay.setImageBitmap(bitmapMarco_1);
                break;
            case R.id.marco_2:
                Log.i(tag, "Creacion de Marco_2");
                bitmapMarco_1 = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
                creaMarcoCallBack(bitmapOriginal, bitmapMarco_1);
                ivDisplay.setImageBitmap(bitmapMarco_1);
                break;
            case R.id.galery:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                int code = SELECT_PICTURE;
                startActivityForResult(intent, code);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e("pasa por aquí", "sii");

        if (permissions[0] == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Permiso denegado para acceder a la cámara", Toast.LENGTH_SHORT);
        }
        else  takePhoto();
    }

    public void onTakePhoto(View v) {
        //tomarFoto();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }


    }

    private void takePhoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int code = TAKE_PICTURE;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, code);}
    }


    // Al tomar fotografía, comprobamos si resultado correcto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                escalaImagen((Bitmap) extras.get("data"));
        } else if (requestCode == SELECT_PICTURE) {
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                escalaImagen(bitmap);

            } catch (FileNotFoundException e) {
            }
        }
    }

    // Escalamos la fotografía, y la mostramos
    private void escalaImagen(Bitmap bitmap) {
        int targetW = 500;//ivDisplay.getWidth();
        int targetH = 500;//ivDisplay.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bitmapOriginal = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
        ivDisplay.setImageBitmap(bitmapOriginal);
    }
}


