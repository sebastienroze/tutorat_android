package ifa.devlog.tutorat.view;



import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ifa.devlog.tutorat.R;
import ifa.devlog.tutorat.controller.QuestionController;
import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.model.Reponse;
import ifa.devlog.tutorat.utils.requestmanager.MultipartRequest;
import ifa.devlog.tutorat.utils.requestmanager.RequestManager;
import ifa.devlog.tutorat.utils.requestmanager.StringRequestWithToken;

public class VoirReponseActivity extends AppCompatActivity {


    Question question;
    String bearer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init() {
        setContentView(R.layout.activity_affiche_reponse);
        initViewFromQuestion();
        initClickVoirContenu();
        initClickVoirContenuQuestion();
        initClickSupprimer();
    }


    private void initViewFromQuestion() {
        TextView textSujet = findViewById(R.id.text_reponseSujet);
        TextView textExplication = findViewById(R.id.text_reponseExplication);
        TextView textUtilisateur = findViewById(R.id.text_reponseUtilisateur);
        TextView textNiveau = findViewById(R.id.text_VoirNiveau);
        if (getIntent().hasExtra("question")) {
            Serializable serializable = getIntent().getSerializableExtra("question");
            Question question = (Question) serializable;
            this.question = question;
            textExplication.setText(question.getExplication());
            textSujet.setText(this.question.getSujet());
            textUtilisateur.setText(this.question.getTexteDate_question());
            if (this.question.getNiveau() != null) textNiveau.setText(this.question.getNiveau().getDenomination());
            if (this.question.getReponse() == null) {
                System.out.println("erreur: pas de question");
                finish();
            }
        } else {
            System.out.println("erreur: pas de question");
            finish();
        }
    }
    private void initClickSupprimer() {
        Button buttonSupprimer = findViewById(R.id.button_supprimer);
        buttonSupprimer.setOnClickListener(v->{
            supression();
        });
    }
    private void initClickVoirContenuQuestion() {
        Button buttonVoirQuestion = findViewById(R.id.button_affiche_contenu_question);
        buttonVoirQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoirContenuActivity.class);
            if (question.getOral() != null) intent.putExtra("audio", question.getOral());
            if (question.getPhoto() != null) intent.putExtra("image", question.getPhoto());
            startActivity(intent);
        });
    }

    private void initClickVoirContenu() {
        Button buttonVoirContenu = findViewById(R.id.button_affiche_contenu_reponse);
        buttonVoirContenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoirContenuActivity.class);
            if (question.getReponse().getOral() != null) intent.putExtra("audio", question.getReponse().getOral());
            if (question.getReponse().getPhoto() != null) intent.putExtra("image", question.getReponse().getPhoto());
            startActivity(intent);
        });
    }

    private void supression() {
        StringRequestWithToken deleteRequest = new StringRequestWithToken(this, Request.Method.DELETE,
                this.getResources().getString(R.string.url_spring) + "user/question/"+question.getId(),
                jsonResult -> {
                    Toast.makeText(this, "Supprimé !", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Erreur de supression !", Toast.LENGTH_SHORT).show();
                }
        );
        RequestManager.getInstance(this).addToRequestQueue(deleteRequest);
    }

}