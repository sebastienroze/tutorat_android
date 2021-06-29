package ifa.devlog.tutorat.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.List;

import ifa.devlog.tutorat.R;
import ifa.devlog.tutorat.controller.UtilisateurController;
import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.model.Utilisateur;
import ifa.devlog.tutorat.utils.requestmanager.JsonArrayRequestWithToken;
import ifa.devlog.tutorat.utils.requestmanager.JsonObjectRequestWithToken;
import ifa.devlog.tutorat.utils.requestmanager.RequestManager;
import ifa.devlog.tutorat.view.adapter.ListQuestionAdapter;
import ifa.devlog.tutorat.view.adapter.ListQuestionAdapter.EcouteurClicQuestion;

public class ListeQuestionActivity extends AppCompatActivity {

    private FloatingActionButton buttonAjouterQuestion;
    private RecyclerView recyclerViewListeQuestion;
    private TextView textTitre;
    private boolean isAdmin=false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init() {
        setContentView(R.layout.activity_liste_question);
        recyclerViewListeQuestion = findViewById(R.id.liste_question);
        recyclerViewListeQuestion.setLayoutManager(new LinearLayoutManager(this));
        textTitre = findViewById(R.id.textView_titreQuestions);
        buttonAjouterQuestion = findViewById(R.id.button_ajouterQuestion);

        UtilisateurController.getInstance().getUtilisateurConnecte(
                this,
                (utilisateur -> {
                    isAdmin = utilisateur.isAdmin();
                    if (isAdmin) {
                        textTitre.setText("Questions pour " + utilisateur.getPseudo());
                        this.buttonAjouterQuestion.setVisibility(View.GONE);
                    } else {
                        textTitre.setText("Questions de " + utilisateur.getPseudo());
                        this.buttonAjouterQuestion.setOnClickListener((View v) -> {
                            startActivity(new Intent(this, EditionActivity.class));
                        });
                    }
                    List <Question> questions = null;
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequestWithToken(
                            this,
                            Request.Method.GET,
                            this.getResources().getString(R.string.url_spring) + "user/questions",
                            null,
                            jsonQuestions -> {
                                try {
                                    recyclerViewListeQuestion.setAdapter(
                                        new ListQuestionAdapter(Question.fromJsonArray(jsonQuestions),
                                                (questionClic) -> {
                                                    if (isAdmin) {
                                                        Intent intent = new Intent(this, EditionReponseActivity.class);
                                                        intent.putExtra("question", questionClic);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intent;
                                                        if (questionClic.getReponse()!=null) {
                                                            intent= new Intent(this, VoirReponseActivity.class);
                                                        } else {
                                                            intent= new Intent(this, EditionActivity.class);
                                                        }
                                                        intent.putExtra("question", questionClic);
                                                        startActivity(intent);
                                                    }
                                                }));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> Log.d("Erreur", error.toString())){
                    };
                    RequestManager.getInstance(this).addToRequestQueue(jsonArrayRequest);
                }));

    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }
}