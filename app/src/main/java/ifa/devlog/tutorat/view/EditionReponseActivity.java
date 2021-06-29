package ifa.devlog.tutorat.view;



import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.loader.content.CursorLoader;

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
import java.io.FileNotFoundException;
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

public class EditionReponseActivity extends AppCompatActivity {

    TextView textSujet;
    TextView textExplication;
    TextView textUtilisateur;
    TextView textNiveau;
    Question question;
    BottomAppBar bottomAppBar;
    String bearer;
    String currentPhotoPath;
    Button buttonEnregistrementVocal;

    ActivityResultLauncher<Intent> activitySelectionImage;
    ActivityResultLauncher<Intent> activityTakePicture;

    boolean mStartRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init() {
        SharedPreferences preference = this.getSharedPreferences(
                this.getResources().getString(R.string.fichier_preference), 0); // 0 - for private mode
        this.bearer = "Bearer " + preference.getString("token","");
        setContentView(R.layout.activity_edition_reponse);
        initViewFromQuestion();
        initClickVoirContenu();
        initClickVoirContenuQuestion();
        initClickEnregistrementVocal();
        initClickPrendrePhoto();
        initActivitySelectionImage();
        initActivityTakePicture();
        initClickItems();
        initClickBottomBarEdition();
    }


    private void initViewFromQuestion() {
        textSujet = findViewById(R.id.text_QuestionSujet);
        textExplication = findViewById(R.id.text_QuestionExplication);
        textUtilisateur = findViewById(R.id.text_QuestionUtilisateur);
        textNiveau = findViewById(R.id.text_QuestionNiveau);
        if (getIntent().hasExtra("question")) {
            Serializable serializable = getIntent().getSerializableExtra("question");
            Question question = (Question) serializable;
            this.question = question;
            textExplication.setText(question.getExplication());
            textSujet.setText(this.question.getSujet());
            textUtilisateur.setText(this.question.getTexteDate_question());
            if (this.question.getNiveau() != null) textNiveau.setText(this.question.getNiveau().getDenomination());
            if (this.question.getReponse() == null) this.question.setReponse(new Reponse());
        } else {
            System.out.println("erreur: pas de question");
            finish();
        }
    }

    private void initClickVoirContenuQuestion() {
        Button buttonVoirQuestion = findViewById(R.id.button_reponse_contenu_question);
        buttonVoirQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoirContenuActivity.class);
            if (question.getOral() != null) intent.putExtra("audio", question.getOral());
            if (question.getPhoto() != null) intent.putExtra("image", question.getPhoto());
            startActivity(intent);
        });
    }

    private void initClickVoirContenu() {
        Button buttonVoirContenu = findViewById(R.id.button_reponse_contenu);
        buttonVoirContenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoirContenuActivity.class);
            if (question.getReponse().getOral() != null) intent.putExtra("audio", question.getReponse().getOral());
            if (question.getReponse().getPhoto() != null) intent.putExtra("image", question.getReponse().getPhoto());
            startActivity(intent);
        });
    }

    private void initClickPrendrePhoto() {
        Button buttonPrendrePhoto = findViewById(R.id.button_reponse_photo);
        buttonPrendrePhoto.setOnClickListener(v -> {
            ouvertureAppareilPhoto();
        });
    }

    private void initClickEnregistrementVocal() {
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecord.3gp";
        buttonEnregistrementVocal = findViewById(R.id.button_reponse_audio);
        buttonEnregistrementVocal.setOnClickListener(v -> {
            ouvertureEnregistreur();
        });
    }
    private void initClickItems() {
        FloatingActionButton fabEditionReponse = findViewById(R.id.fab_editionReponse);
        fabEditionReponse.setOnClickListener(v -> {
            QuestionController.getInstance().sauvegarder(
                    this,
                    question,
                    () -> finish(),
                    messageErreur -> Toast.makeText(this, messageErreur, Toast.LENGTH_LONG).show());
        });
    }

    private void initClickBottomBarEdition() {
        bottomAppBar = findViewById(R.id.bottomAppBar_EditionReponseActivity);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuItem_ajoutPhoto) {
                ouvertureAppareilPhoto();
            } else if (item.getItemId() == R.id.menuItem_ajoutFichier) {
                ouvertureSelecteurImage();
            } else if (item.getItemId() == R.id.menuItem_ajoutAudio) {
                ouvertureEnregistreur();
            } else if (item.getItemId() == R.id.menuItem_supprimerQuestion) {
                supression();
            }
            return true;
        });
    }

    private void supression() {
        StringRequestWithToken deleteRequest = new StringRequestWithToken(this,Request.Method.DELETE,
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

    /******************************* Galerie ******************************************************************/

    private void ouvertureSelecteurImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activitySelectionImage.launch(gallery);
    }

    public String responseParser(NetworkResponse networkResponse) {
        String response;
        try {
            response = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        } catch (UnsupportedEncodingException e) {
            response = new String(networkResponse.data);
        }
        return response;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String getFileExtension(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('.');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void initActivitySelectionImage() {
        activitySelectionImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result != null &&
                            result.getData() != null &&
                            result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        Uri uriImageSelectionne = data.getData();
                        try {
                            InputStream iStream =   getContentResolver().openInputStream(uriImageSelectionne);
                            byte[] inputData = getBytes(iStream);
                            String extention = getFileExtension(uriImageSelectionne);
                            MultipartRequest volleyMultipartRequest = new MultipartRequest(
                                    Request.Method.POST,
                                    this.getResources().getString(R.string.url_spring) + "test/upload-fichier",
                                    jsonResult -> {
                                        try {
                                            question.getReponse().updateFromUploadPhoto(new JSONObject(responseParser(jsonResult)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> {
                                        System.out.println("nok");
                                    }
                            ) {
                                @Override
                                protected Map<String, DataPart> getByteData() {
                                    Map<String, DataPart> params = new HashMap<>();
                                    params.put("Authorization", new DataPart(bearer, "".getBytes()));
                                    if (question.getId() != null) {
                                        params.put("questionID", new DataPart(question.getId().toString(), "".getBytes()));
                                    }
                                    params.put("file", new MultipartRequest.DataPart(
                                            "image."+extention, inputData));
                                    return params;
                                }
                            };
                            RequestManager.getInstance(this).addToRequestQueue(volleyMultipartRequest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /******************************* Photo ******************************************************************/

    private void initActivityTakePicture() {
        activityTakePicture= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    File file = new File(currentPhotoPath);
                    try {
                        byte[] fileContent = new byte[(int) file.length()];
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                        DataInputStream dis = new DataInputStream(bis);
                        dis.readFully(fileContent);
                        MultipartRequest volleyMultipartRequest = new MultipartRequest(
                                Request.Method.POST,
                                this.getResources().getString(R.string.url_spring) + "test/upload-fichier",
                                jsonResult -> {
                                    try {
                                        question.getReponse().updateFromUploadPhoto(new JSONObject(responseParser(jsonResult)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                },
                                error -> {
                                    System.out.println("nok");
                                }
                        ) {
                            @Override
                            protected Map<String, DataPart> getByteData() {
                                Map<String, DataPart> params = new HashMap<>();
                                params.put("Authorization", new DataPart(bearer, "".getBytes()));
                                if (question.getId() != null) {
                                    params.put("questionID", new DataPart(question.getId().toString(), "".getBytes()));
                                }
                                params.put("file", new MultipartRequest.DataPart(
                                        "photo.jpg", fileContent));
                                return params;
                            }
                        };
                        RequestManager.getInstance(this).addToRequestQueue(volleyMultipartRequest);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "photo";
        File storageDir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activityTakePicture.launch(takePictureIntent);
            }
        }
    }

    private void ouvertureAppareilPhoto() {
        dispatchTakePictureIntent();
    }


    /******************************* AUDIO ******************************************************************/
    private void ouvertureEnregistreur() {
        MenuItem itemAudio = bottomAppBar.getMenu().getItem(2);
        if (mStartRecording) {
            itemAudio.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_record_voice_over_24));
            buttonEnregistrementVocal.setBackgroundColor( getColor(R.color.design_default_color_primary));
            mStartRecording = false;
            stopRecording();
        } else {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.RECORD_AUDIO},
                        10 );
            } else {
                itemAudio.setIcon(ContextCompat.getDrawable(this, R. drawable.ic_baseline_stop_circle_24));
                buttonEnregistrementVocal.setBackgroundColor(0xFF00FF00);
                mStartRecording = true;
                startRecording();
            }
        }
    }

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder = null;
    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecord";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        System.out.println("record");
        System.out.println(recorder);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        File file = new File(fileName);
        try {
            byte[] fileContent = new byte[(int) file.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(fileContent);
            MultipartRequest volleyMultipartRequest = new MultipartRequest(
                    Request.Method.POST,
                    this.getResources().getString(R.string.url_spring) + "test/upload-fichier",
                    jsonResult -> {
                        try {
                            question.getReponse().updateFromUploadAudio(new JSONObject(responseParser(jsonResult)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        System.out.println("nok");
                    }
            ) {
                @Override
                protected Map<String, DataPart> getByteData() {
                    System.out.println("--------------------questionID------------------------"+question.getId());

                    Map<String, DataPart> params = new HashMap<>();
                    params.put("Authorization", new DataPart(bearer, "".getBytes()));
                    if (question.getId() != null) {
                        params.put("questionID", new DataPart(question.getId().toString(), "".getBytes()));
                    }
                    params.put("file", new MultipartRequest.DataPart(
                            "audio.3gp", fileContent));
                    return params;
                }
            };
            RequestManager.getInstance(this).addToRequestQueue(volleyMultipartRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}