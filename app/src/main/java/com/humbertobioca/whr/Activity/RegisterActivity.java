package com.humbertobioca.whr.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.humbertobioca.whr.Components.AlertDialog;
import com.humbertobioca.whr.Entidades.User;
import com.humbertobioca.whr.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private BootstrapEditText edtNome;
    private BootstrapEditText edtEmail;
    private BootstrapEditText edtPassword;
    private BootstrapEditText edtConfirmPassword;

    private BootstrapButton btnRegister;
    private BootstrapButton btnCancel;

    private RadioButton rbMasc;
    private RadioButton rbFem;

    private Dialog dialog;
    private ImageView imgPictureProfile;
    private LinearLayout takePicture;
    private LinearLayout uploadPicture;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profilerRef;
    private FirebaseUser userAuth;


    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        user = new User();

        edtNome = (BootstrapEditText) findViewById(R.id.edtNome);
        edtEmail = (BootstrapEditText) findViewById(R.id.edtEmail);
        edtPassword = (BootstrapEditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (BootstrapEditText) findViewById(R.id.edtConfirmPassword);

        btnRegister = (BootstrapButton) findViewById(R.id.btnRegister);
        btnCancel = (BootstrapButton) findViewById(R.id.btnCancelRegister);

        rbMasc = (RadioButton) findViewById(R.id.rbMasc);
        rbFem = (RadioButton) findViewById(R.id.rbFem);

        imgPictureProfile = (ImageView) findViewById(R.id.imgPictureProfile);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                    user.setEmail(edtEmail.getText().toString());
                    user.setName(edtNome.getText().toString());
                    user.setPassword(edtPassword.getText().toString());

                    if (rbMasc.isChecked()) {
                        user.setSex("Masculino");
                    } else if (rbFem.isChecked()) {
                        user.setSex("Feminino");
                    }

                    criarConta(user);

                } else {
                    Toast.makeText(RegisterActivity.this, "Senha não corresponde", Toast.LENGTH_LONG).show();
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirLoginActvity();

            }
        });

        imgPictureProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDialog();
            }
        });
    }

    private void criarConta(final User user) {
        final AlertDialog alertDialog = new AlertDialog();
        alertDialog.abrirLoading("Criando usuário...", RegisterActivity.this);

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userAuth = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = userAuth.getUid();
                            user.setUid(uid);

                            storageRef = storage.getReference();
                            profilerRef = storageRef.child("profile/" + uid + ".jpg");

                            // Get the data from an ImageView as bytes
                            imgPictureProfile.setDrawingCacheEnabled(true);
                            imgPictureProfile.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) imgPictureProfile.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = profilerRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(RegisterActivity.this, "Falha ao Enviar Imagem!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");

                                    Toast.makeText(RegisterActivity.this, "Cadastro Efetuado!", Toast.LENGTH_SHORT).show();
                                    inserirUsuarioDatabase(user);
                                    alertDialog.fecharLoading();
                                    abrirMainActivity();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            alertDialog.fecharLoading();
                        }

                        // ...
                    }
                });
    }

    private void abrirMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void inserirUsuarioDatabase(User user) {
        myRef = database.getReference("users");

        String key = myRef.child("users").push().getKey();
        user.setKeyUser(key);

        myRef.child(key).setValue(user);

    }

    private void abrirLoginActvity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void fazerUploadFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 123);
    }

    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void abrirDialog() {
        dialog = new Dialog(this);

        dialog.setContentView(R.layout.alert_picture);

        uploadPicture = (LinearLayout) dialog.findViewById(R.id.TakePicture);
        takePicture = (LinearLayout) dialog.findViewById(R.id.UploadPicture);

        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerUploadFoto();
                dialog.dismiss();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tirarFoto();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 123) {
                imgPictureProfile.setBackgroundColor(Color.TRANSPARENT);
                Uri image = data.getData();
                Picasso.get().load(image.toString()).into(imgPictureProfile);

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imgPictureProfile.setBackgroundColor(Color.TRANSPARENT);
                Bundle extra = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extra.get("data");
                imgPictureProfile.setImageBitmap(imageBitmap);
            }
        }
    }
}
