package ifa.devlog.tutorat.controller;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import ifa.devlog.tutorat.R;
import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.utils.requestmanager.RequestManager;
import ifa.devlog.tutorat.utils.requestmanager.StringRequestWithToken;

public final class QuestionController {

    private static QuestionController instance = null;

    private QuestionController() {
    }

    public static QuestionController getInstance() {

        if(instance == null) {
            instance = new QuestionController();
        }

        return instance;
    }

    public interface SuccesEcouteur {
        void onSuccesSauvegarde();
    }

    public interface ErreurEcouteur {
        void onErreurSauvegarde(String messageErreur);
    }

    public void sauvegarder(
            Context context,
            Question question,
            SuccesEcouteur ecouteurSucces,
            ErreurEcouteur ecouteurErreur) {

        String fin_url;

        fin_url = "user/question";

        StringRequestWithToken stringRequest = new StringRequestWithToken (
                context,
                Request.Method.POST,  context.getResources().getString(R.string.url_spring) + fin_url,
                token -> {
                    ecouteurSucces.onSuccesSauvegarde();
                },
                error -> {
                    ecouteurErreur.onErreurSauvegarde("Impossible de sauvegarder");
                }
        ){

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {

                    JSONObject jsonBody;

                        jsonBody = Question.toJson(question);

                    return jsonBody.toString().getBytes(StandardCharsets.UTF_8);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        RequestManager.getInstance(context).addToRequestQueue(stringRequest);

    }

}
