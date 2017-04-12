package org.jordi.pruebafacebook2017sdk1;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FacebookActivity extends AppCompatActivity {

    private TextView elTextoDeBienvenida;
    private Button botonEnviarFoto;
    private Button botonCompartir;
    private ImageView prueba;
    private Bitmap photo = null;
    private ProfilePictureView profilePictureView;
    private TextView txtMessage;
    private Button btnCompartirBasico;
    private Button btnCompartiFotoBasico;

    LoginButton loginButtonOficial;

    private ShareDialog elShareDialog;
    private CallbackManager elCallbackManagerDeFacebook;
    private final Activity THIS = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_layout);
       // Bundle extras = getIntent().getExtras();
       // photo = (Bitmap) extras.get("photo");
        //
        byte[] param = getIntent().getExtras().getByteArray("photo");
        photo = BitmapFactory.decodeByteArray(param, 0, param.length);

                //
        prueba = (ImageView) findViewById(R.id.pruebaFoto);
        prueba.setImageBitmap(photo);
        botonCompartir = (Button) findViewById(R.id.boton_EnviarAFB);
        loginButtonOficial = (LoginButton) findViewById(R.id.login_button);
        loginButtonOficial.setPublishPermissions("publish_actions");
        // Uno u otro, no se puede los 2 a la vez
        //loginButtonOficial.setReadPermissions("public_profile");
        botonEnviarFoto = (Button) findViewById(R.id.boton_EnviarFoto);
        txtMessage = (EditText) findViewById(R.id.txt_mensajeFB);
        btnCompartirBasico = (Button) findViewById(R.id.boton_EnviarBasico);
        btnCompartiFotoBasico = (Button) findViewById(R.id.boton_EnviarFotoBasico);

        botonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!puedoUtilizarShareDialogParaPublicarMensaje()) {
                    return;
                }
                ShareLinkContent content = new ShareLinkContent.Builder().build();
                elShareDialog.show(content);
            }
        });

        botonEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!puedoUtilizarShareDialogParaPublicarFoto()) {
                    Log.e("publicarFoto", "no puedo utilizar shareDialog");
                    return;
                }

                Log.e("publicarFoto", "si puedo utilizar shareDialog");
                SharePhoto sharePhoto = new SharePhoto
                        .Builder()
                        .setBitmap(photo)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                elShareDialog.show(content);

            }
        });
        this.elCallbackManagerDeFacebook = CallbackManager.Factory.create();
        this.elShareDialog = new ShareDialog(this);

        LoginManager.getInstance().registerCallback(this.elCallbackManagerDeFacebook,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Toast.makeText(THIS, "Login onSuccess()", Toast.LENGTH_LONG).show();
                        actualizarVentanita();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(THIS, "Login onCancel()", Toast.LENGTH_LONG).show();
                        actualizarVentanita();
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        Toast.makeText(THIS, "Login onError(): " + exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                        actualizarVentanita();
                    }
                });


        this.elShareDialog.registerCallback(this.elCallbackManagerDeFacebook, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(THIS, "Sharer onSuccess()", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(THIS, "Sharer onError(): " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });


        this.actualizarVentanita();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.elCallbackManagerDeFacebook.onActivityResult(requestCode, resultCode, data);
    }


    private void actualizarVentanita() {
        Log.d("updateWindow()", "empiezo ");

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d("updateWindow()", "no hay sesion, deshabilito");


            this.botonCompartir.setEnabled(false);
            this.botonEnviarFoto.setEnabled(false);
            btnCompartiFotoBasico.setEnabled(false);
            btnCompartirBasico.setEnabled(false);
            txtMessage.setEnabled(false);
            return;
        }

        Log.d("updateWindow()", "hay sesion habilito");


        this.botonCompartir.setEnabled(true);
        this.botonEnviarFoto.setEnabled(true);
        btnCompartiFotoBasico.setEnabled(true);
        btnCompartirBasico.setEnabled(true);
        txtMessage.setEnabled(true);

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Toast.makeText(getApplicationContext(), "Bienvenido " + profile.getName(),Toast.LENGTH_SHORT).show();
        }
    }


    private boolean puedoUtilizarShareDialogParaPublicarMensaje() {
        return puedoUtilizarShareDialogParaPublicarLink();
    }


    private boolean puedoUtilizarShareDialogParaPublicarLink() {
        return ShareDialog.canShow(ShareLinkContent.class);
    }


    private boolean puedoUtilizarShareDialogParaPublicarFoto() {
        return ShareDialog.canShow(SharePhotoContent.class);
    }


    private boolean sePuedePublicar() {
// compruebo la red
        if (!this.hayRed()) {
            Toast.makeText(this, "¿no hay red? No puedo publicar", Toast.LENGTH_LONG).show();
            return false;
        }
// compruebo permisos
        if (!this.tengoPermisoParaPublicar()) {
            Toast.makeText(this, "¿no tengo permisos para publicar? Los pido.", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
            return false;
        }
        return true;
    }


    private AccessToken obtenerAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }


    private boolean tengoPermisoParaPublicar() {
        AccessToken accessToken = this.obtenerAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private boolean hayRed() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
// http://stackoverflow.com/questions/15091591/post-on-facebook-wall-without-showing-dialog-on-android
// comprobar que estamos conetactos a internet, antes de hacer el login con
// facebook. Si no: da problemas.

    }


    public void enviarFotoAFacebook_async(Bitmap image, String comentario) {
        Log.d("updateWindow()", "llamado");
        if (image == null) {
            Toast.makeText(this, "Enviar foto: la imagen está vacía.", Toast.LENGTH_LONG).show();
            Log.d("updateWindow()", "acabo porque la imagen es null");
            return;
        }

        if (!sePuedePublicar()) {
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);

        final byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
        }

        Bundle params = new Bundle();
        params.putByteArray("source", byteArray);
        params.putString("caption", comentario);
        // si se quisiera publicar una imagen de internet: params.putString("url", "{image-url}");
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/photos",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Toast.makeText(THIS, "" + byteArray.length + " Foto enviada: " + response.toString(), Toast.LENGTH_LONG).show();
                        //txtMessage.setText(response.toString());
                    }
                }
        );
        request.executeAsync();
    }

    public void boton_EnviarFoto_pulsado(View quien) {
        Toast.makeText(this, "Enviar Foto", Toast.LENGTH_SHORT).show();

        String mensaje = "msg:" + txtMessage.getText() + " :"
                + System.currentTimeMillis();

        txtMessage.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.txtMessage.getWindowToken(), 0);

        enviarFotoAFacebook_async(photo, mensaje);
    }

    public void enviarTextoAFacebook_async(final String textoQueEnviar) {

        if (!sePuedePublicar()) {
            return;
        }

        Bundle params = new Bundle();
        params.putString("message", textoQueEnviar);
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Toast.makeText(THIS, "PublicaciÃ³n realizada: " +
                                textoQueEnviar, Toast.LENGTH_LONG).show();
                    }
                }
        );
        request.executeAsync();
    }

    public void boton_enviarTextoAFB_pulsado(View v) {

        String mensaje = "msg:" + this.txtMessage.getText() + " :"
                + System.currentTimeMillis();

        this.txtMessage.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.txtMessage.getWindowToken(), 0);

        enviarTextoAFacebook_async(mensaje);
    }
}
