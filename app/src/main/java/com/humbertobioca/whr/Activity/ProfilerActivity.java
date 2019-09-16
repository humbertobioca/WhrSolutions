package com.humbertobioca.whr.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.humbertobioca.whr.Entidades.User;
import com.humbertobioca.whr.R;

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

        popularDadosUsuario();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void popularDadosUsuario(){
        mAuth = FirebaseAuth.getInstance();

        userAuth  = mAuth.getCurrentUser();
        String emailCurrentUser = userAuth.getEmail();
        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").orderByChild("email").equalTo(emailCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                    User user = userSnapshot.getValue(User.class);
                    edtNameProfiler.setText(user.getName());
                    edtEmailProfiler.setText(user.getEmail());

                    if(user.getSex().equals("Masculino")){
                        rbMasc.setChecked(true);
                    }else if (user.getSex().equals("Feminino")){
                        rbFem.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
