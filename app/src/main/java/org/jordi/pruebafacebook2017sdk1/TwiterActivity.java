package org.jordi.pruebafacebook2017sdk1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.support.v4.app.ActivityCompat;


import com.twitter.sdk.android.Twitter;
        import com.twitter.sdk.android.core.Callback;
        import com.twitter.sdk.android.core.Result;
        import com.twitter.sdk.android.core.TwitterApiException;
        import com.twitter.sdk.android.core.TwitterAuthConfig;
        import com.twitter.sdk.android.core.TwitterException;
        import com.twitter.sdk.android.core.TwitterSession;
        import com.twitter.sdk.android.core.identity.TwitterLoginButton;
        import com.twitter.sdk.android.core.models.Media;
        import com.twitter.sdk.android.core.models.Tweet;
        import com.twitter.sdk.android.core.services.MediaService;
        import com.twitter.sdk.android.core.services.StatusesService;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.Date;

        import io.fabric.sdk.android.Fabric;
        import okhttp3.MediaType;
        import retrofit.mime.TypedFile;
        import retrofit2.Call;

        import static android.R.attr.bitmap;
       // import static es.upv.master.pruebatwiter.R.drawable.carnival;


//import static android.icu.text.RelativeDateTimeFormatter.Direction.THIS;

 public class TwiterActivity extends AppCompatActivity {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "	1upGedXidBGuacfhfvUOMRldz";
    private static final String TWITTER_SECRET = "	9U6Ze7AZM6oNYfDxGcs1Ckga83MFuySiPQ5TJ41kuiGrZf1vKy";
    TwitterLoginButton btnTwiter;
    private final Activity THIS = this;
    private TextView txtShare;
    private TwitterSession myTwitterSession;
    private ImageView ivDisplay = null;
    private Bitmap bitmapOriginal = null;
    private String mCurrentPhotoPath;
    File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.twiter_layout);
        Bundle extras = getIntent().getExtras();
        bitmapOriginal = (Bitmap) extras.get("photo");
        ivDisplay = (ImageView) findViewById(R.id.imgView);
        ivDisplay.setImageBitmap(bitmapOriginal);
        txtShare = (TextView) findViewById(R.id.edtTweet);
        btnTwiter = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        setColor(true);
        btnTwiter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(THIS, "Autenticado en twitter: " + result.data.getUserName(), Toast.LENGTH_LONG).show();
                myTwitterSession = result.data;
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(THIS, "Fallo en autentificación: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setColor(Boolean imgOrigen)
    {
     /*   Log.i("VER IMG", "Resetear Imagen");
        if (imgOrigen) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Asegurar que la imagen tiene 24 bits de color
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmapOriginal = BitmapFactory.decodeResource(this.getResources(), R.drawable.twitter_logo, options);
            if (bitmapOriginal != null)
                ivDisplay.setImageBitmap(bitmapOriginal);
        }
        ivDisplay.setImageBitmap(bitmapOriginal);*/
    }

    private void escalaImagen()
    {
    /*    int targetW = ivDisplay.getWidth();
        int targetH = ivDisplay.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOriginal = BitmapFactory.decodeResource(this.getResources(), carnival, bmOptions);
        //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //Bitmap resizedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath), targetW, targetH, true);
        //  bitmapOriginal = resizedBitmap;
        ivDisplay.setImageBitmap(bitmapOriginal);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        btnTwiter.onActivityResult(requestCode, resultCode, data);
    }


    public void call_compartir(View quien) {
        StatusesService statusesService = Twitter.getApiClient(myTwitterSession).getStatusesService();
        Call<Tweet> call = statusesService.update(txtShare.getText().toString(), null, null, null, null, null, null, null, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(THIS, "Tweet publicado: " + result.response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(THIS, "No se pudo publicar el tweet: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private File createImageFile() throws IOException {
       // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";

        String imageFileName = "FicPruebaTwiter";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix*/
                ".png",         /* suffix*/
                storageDir      /* directory*/
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            getPhoto();
        else
            ActivityCompat.requestPermissions(TwiterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("** SUBIR IMAGEN ", "requestPermission Result");

        if (permissions[0] == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(TwiterActivity.this, "Permiso denegado para acceder a la tarjeta SD", Toast.LENGTH_SHORT);
        } else getPhoto();
    }

    private File convertBitmapToFile() throws IOException {
        File f = createImageFile();
     //   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapOriginal);
        //Bitmap bitmap = BitmapFactory.decodeFile()
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapOriginal.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private void getPhoto()
    {
        try {
            photo  = new File("///storage/209E-9627/DCIM/Camera/carnival.jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(photo.getPath());
            ivDisplay.setImageBitmap(bitmap);
            Log.d ("** SUBIR IMAGEN ", "enviarImagen_async(): can read: " + photo.canRead());
            Log.d("** SUBIR IMAGEN ", "enviarImagen_async(): absolute path: " + photo.getAbsolutePath());
            Log.d("** SUBIR IMAGEN ", "enviarImagen_async(): length: " + photo.length());
            Log.d("** SUBIR IMAGEN ", "enviarImagen_async(): Exist: " + photo.exists());

        } catch (Exception e) {
            Log.d("miApp", "enviarImagen : excepcion: " + e.getMessage());
            return;
        }
    }

    public void share_image(View quien) {
        //checkPermission();
        try {
           photo =  convertBitmapToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TypedFile typedFile = new TypedFile("image/jpg", bitmapOriginal.compress());
        TypedFile typedFile = new TypedFile("image/jpg", photo);
        MediaService ms = Twitter.getApiClient(myTwitterSession).getMediaService();
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MediaType.parse("image/jpg"), photo);
        // 4. con el media service: enviamos la foto a Twitter
        Call<Media> call1 = ms.upload(
                requestBody, // foto que enviamos
                null,
                null);
        call1.enqueue(new Callback<Media>() {
            @Override
            public void success(Result<Media> mediaResult) {
// he tenido éxito:
                Log.d("** SUBIR IMAGEN ", "Dentro de Succes de call1.enqueue");
                Log.d("** SUBIR IMAGEN ", "imagen publicada: " + mediaResult.response.toString());
                Toast.makeText(THIS, "imagen publicada: " + mediaResult.response.toString(), Toast.LENGTH_LONG);
                StatusesService statusesService = Twitter.getApiClient(myTwitterSession).getStatusesService();
                Call<Tweet> call2 = statusesService.update("imagen subida OK",null,
                        false,
                        null,
                        null,
                        null,
                        true,
                        false,
                        ""+mediaResult.data.mediaId);

                call2.enqueue(
                        new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                Toast.makeText(THIS, "Tweet publicado: " + result.response.message().toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(TwitterException e) {
                                //Toast.makeText(THIS, "No se pudo publicar el tweet: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                //Log.d(" ** SUBIR IMAGEN ", "Failure llamada 2 " + e.getMessage());
                                TwitterApiException apiException = (TwitterApiException) e;
                                Log.d("** SUBIR IMAGEN", "ERROR => " + apiException.getErrorMessage());
                                Log.d("** SUBIR IMAGEN", apiException.getMessage());
                                Toast.makeText(THIS, "No se pudo publicar el tweet: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }

            @Override
            public void failure(TwitterException e) {
// failure de call1
                Toast.makeText(THIS, "No se pudo publicar el tweet: " + e.getMessage(), Toast.LENGTH_LONG).show();
                //Log.d(" ** SUBIR IMAGEN ", "Failure llamada 1 " + e.getMessage());
                //TwitterApiException apiException = (TwitterApiException) e;
                //Log.d(" ** SUBIR IMAGEN ", e.getErrorMessage());
                Log.d("** SUBIR IMAGEN ", e.getMessage());
            }
        });
    }
}