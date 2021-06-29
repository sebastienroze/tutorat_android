package ifa.devlog.tutorat.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import ifa.devlog.tutorat.R;
import ifa.devlog.tutorat.utils.requestmanager.JsonObjectRequestWithToken;
import ifa.devlog.tutorat.utils.requestmanager.RequestManager;
import ifa.devlog.tutorat.model.Utilisateur;

public class UtilisateurController {

    private static UtilisateurController instance = null;

    private UtilisateurController() {
    }

    public static UtilisateurController getInstance() {
        if(instance == null) {
            instance = new UtilisateurController();
        }
        return instance;
    }

    public interface TelechargementUtilisateurListener {
        void onUtilisateurEstTelecharge(Utilisateur utilisateur);
    }

    public void getUtilisateurConnecte(Context context, TelechargementUtilisateurListener evenement) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithToken(
                context,
                Request.Method.GET,
                context.getResources().getString(R.string.url_spring) + "user/utilisateur-connecte",
                null,
                jsonUtilisateur -> {
                    try {
                        Utilisateur utilisateur = Utilisateur.fromJson(jsonUtilisateur);
                        evenement.onUtilisateurEstTelecharge(utilisateur);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Erreur", error.toString())){
        };
        RequestManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
