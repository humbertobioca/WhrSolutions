package com.humbertobioca.whr.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.humbertobioca.whr.Components.Alerta;
import com.humbertobioca.whr.Entidades.User;
import com.humbertobioca.whr.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfilerActivity extends AppCompatActivity {

    private BootstrapEditText edtNameProfiler;
    private BootstrapEditText edtEmailProfiler;
    private BootstrapEditText edtPasswordProfiler;
    private BootstrapEditText edtConfirmPasswordProfiler;

    private BootstrapButton btnUpdate;
    private BootstrapButton btnCancel;

    private RadioButton rbMasc;
    private RadioButton rbFem;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser userAuth;

    private ImageView imgPerfil;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Alerta alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiler);

        edtNameProfiler = (BootstrapEditText) findViewById(R.id.edtNameProfiler);
        edtEmailProfiler = (BootstrapEditText) findViewById(R.id.edtEmailProfiler);
        edtPasswordProfiler = (BootstrapEditText) findViewById(R.id.edtPasswordProfiler);
        edtConfirmPasswordProfiler = (BootstrapEditText) findViewById(R.id.edtConfirmPasswordProfiler);

        btnUpdate = (BootstrapButton) findViewById(R.id.btnUpdate);
        btnCancel = (BootstrapButton) findViewById(R.id.btnCancelProfiler);

        rbMasc = (RadioButton) findViewById(R.id.rbMascProfiler);
        rbFem = (RadioButton) findViewById(R.id.rbFemProfiler);

        imgPerfil = (ImageView) findViewById(R.id.imgPerfil2);
        alerta = new Alerta();

        preencherImagemPerfil();

        popularDadosUsuario();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();

                String emailCurrentUser = mAuth.getCurrentUser().getEmail();
                reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").orderByChild("email").equalTo(emailCurrentUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);

                            if(rbFem.isChecked()){
                                updateUser(user.getEmail(), user.getKeyUser(), edtNameProfiler.getText().toString(), "Feminino",  user.getUid());
                            }else if(rbMasc.isChecked()){
                                updateUser(user.getEmail(), user.getKeyUser(), edtNameProfiler.getText().toString(), "Masculino",  user.getUid());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void popularDadosUsuario() {

        final Alerta alerta = new Alerta();
        alerta.loading("Aguarde um momento...", ProfilerActivity.this);

        mAuth = FirebaseAuth.getInstance();

        userAuth = mAuth.getCurrentUser();
        String emailCurrentUser = userAuth.getEmail();
        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").orderByChild("email").equalTo(emailCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    User user = userSnapshot.getValue(User.class);
                    edtNameProfiler.setText(user.getName());
                    edtEmailProfiler.setText(user.getEmail());

                    if (user.getSex().equals("Masculino")) {
                        rbMasc.setChecked(true);
                    } else if (user.getSex().equals("Feminino")) {
                        rbFem.setChecked(true);
                    }
                }

                alerta.fecharDialog();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                alerta.fecharDialog();
            }
        });

    }

    private void updateUser(String email, String keyUser, String name,  String sex,  String uid) {

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users");

        User user = new User(email, keyUser,name, sex, uid);

        Map<String, Object> userValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + keyUser, userValues);

        reference.updateChildren(childUpdates);

    }


    private void preencherImagemPerfil() {

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        String uid = userAuth.getUid();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef.child("profile/" + uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                imgPerfil.setBackgroundColor(Color.TRANSPARENT);
                Picasso.get().load(uri.toString()).into(imgPerfil);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
