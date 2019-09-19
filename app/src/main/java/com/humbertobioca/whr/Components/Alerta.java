package com.humbertobioca.whr.Components;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.humbertobioca.whr.R;

import androidx.annotation.NonNull;

public class Alerta {

    private Dialog dialog;

    private TextView txtDialogMessage;

    private BootstrapButton btnCancelAlert;
    private BootstrapButton btnSendEmail;
    private BootstrapEditText edtEmailAlert;

    private TextView txtDialogMessageConfirm;
    private Button btnOk;


    private FirebaseAuth mAuth;

    public Alerta() {

    }

    public void dialogEmail(final Activity activity) {
        dialog = new Dialog(activity);

        dialog.setContentView(R.layout.alert_recovery_password);

        //Alert
        btnSendEmail = (BootstrapButton) dialog.findViewById(R.id.btnSendEmail);
        btnCancelAlert = (BootstrapButton) dialog.findViewById(R.id.btnCancelAlert);
        edtEmailAlert = (BootstrapEditText) dialog.findViewById(R.id.edtEmailAlert);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envio de email para restauracao de senha.
                enviarEmail(edtEmailAlert.getText().toString(), activity);
                dialog.dismiss();
            }
        });

        btnCancelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void enviarEmail(String emailAddress, final Activity activity) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Email enviado com sucesso", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "Email nao existe.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void critica(String critica, Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.alert_critica);
        dialog.setCancelable(false);

        txtDialogMessageConfirm = (TextView) dialog.findViewById(R.id.txtDialogMessageCritica);
        btnOk = (Button) dialog.findViewById(R.id.btnOk);

        txtDialogMessageConfirm.setText(critica);


        dialog.show();


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void loading(String message, Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.alert_loading);
        dialog.setCancelable(false);

        txtDialogMessage = (TextView) dialog.findViewById(R.id.txtDialogMessage);
        txtDialogMessage.setText(message);

        dialog.show();

    }

    public void fecharDialog() {
        dialog.dismiss();
    }
}


