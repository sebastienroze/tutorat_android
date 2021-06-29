package ifa.devlog.tutorat.view;



import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.Serializable;

import ifa.devlog.tutorat.R;
import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.utils.DownloadImageTask;
import ifa.devlog.tutorat.utils.requestmanager.RequestManager;
import ifa.devlog.tutorat.utils.requestmanager.StringRequestWithToken;

public class VoirContenuActivity extends AppCompatActivity {

    Button buttonEcouter;
    Button buttonTourner;
    ImageView imageContenu;
    TextView textUtilisateur;
    String urlImage=null;
    String urlAudio=null;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onBackPressed() {
        stopMedia();
        finish();
        return;

    }

    private void init() {
        initViewFormVoir();
    }

    private void stopMedia() {
        if (mediaPlayer!=null)
            try {
                mediaPlayer.reset();
                mediaPlayer.prepare();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }


    private void initViewFormVoir() {
        setContentView(R.layout.activity_affiche_contenu);
        buttonEcouter = findViewById(R.id.button_ecouter_contenu);
        buttonTourner = findViewById(R.id.button_tourner_contenu);
        imageContenu = findViewById(R.id.imageView_contenu);

        textUtilisateur = findViewById(R.id.text_QuestionUtilisateur);
        if (getIntent().hasExtra("image")) {
            Serializable serializable = getIntent().getSerializableExtra("image");
            String urlImage = (String) serializable;
            if (!"null".equals(urlImage)) this.urlImage = urlImage;
        }
        if (getIntent().hasExtra("audio")) {
            Serializable serializable = getIntent().getSerializableExtra("audio");
            String urlAudio = (String) serializable;
            if (!"null".equals(urlAudio)) this.urlAudio = urlAudio;
        }
        String Url = this.getResources().getString(R.string.url_spring) + "test/download/";

        if (urlImage!=null) {
            new DownloadImageTask(imageContenu)
                    .execute(Url + urlImage);
            buttonTourner.setOnClickListener(v -> {
                imageContenu.setRotation(imageContenu.getRotation()+90);
            });
        } else {
            buttonTourner.setOnClickListener(v -> {
                Toast.makeText(this, "Pas de photo !", Toast.LENGTH_LONG).show();
            });
            buttonTourner.setAlpha(0.2f);
        }

        if (urlAudio!=null) {
            buttonEcouter.setOnClickListener(v -> {
                playAudio(Url + urlAudio);
            });
        playAudio(Url+urlAudio);
        } else {
            buttonEcouter.setOnClickListener(v -> {
                Toast.makeText(this, "Pas d'enregisrement vocal !", Toast.LENGTH_LONG).show();
            });
            buttonEcouter.setAlpha(0.2f);
        }
    }

    private void playAudio(String audioUrl) {

        // initializing media player
        stopMedia();
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch ( IOException e) {
            e.printStackTrace();
        }
        // below line is use to display a toast message.
    }


}