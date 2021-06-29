package ifa.devlog.tutorat.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Utilisateur implements Serializable {

    private int id;

    private String pseudo;

    private String motDePasse;

    private List<Question> listeQuestion = new ArrayList<>();

    private List<Role> listeRole = new ArrayList<>();

    public Utilisateur(int id, String pseudo) {
        this.id = id;
        this.pseudo = pseudo;
    }

    public static Utilisateur fromJson(JSONObject jsonUtilisateur) throws JSONException {
        Utilisateur utilisateur = new Utilisateur(
                jsonUtilisateur.getInt("id"),
                jsonUtilisateur.getString("pseudo"));
        if (jsonUtilisateur.has("listeRole")) {
            JSONArray jsonListeRole = jsonUtilisateur.getJSONArray("listeRole");
            for (int j = 0; j < jsonListeRole.length(); j++) {
                JSONObject jsonRole = jsonListeRole.getJSONObject(j);
                Role role = Role.fromJson(jsonRole);
                utilisateur.getListeRole().add(role);
            }
        }
/*        JSONArray jsonListeQuestion = jsonUtilisateur.getJSONArray("listeQuestion");
        for(int j = 0; j < jsonListeQuestion.length(); j++) {
            JSONObject jsonQuestion = jsonListeQuestion.getJSONObject(j);
            Question question;
            System.out.println(jsonQuestion);
            question = Question.fromJson(jsonQuestion);
            System.out.println(question.getSujet());
            utilisateur.getListeQuestion().add(question);
        }
 */
        return utilisateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public List<Question> getListeQuestion() {
        return listeQuestion;
    }

    public void setListeQuestion(List<Question> listeQuestion) {
        this.listeQuestion = listeQuestion;
    }

    public List<Role> getListeRole() {
        return listeRole;
    }

    public void setListeRole(List<Role> listeRole) {
        this.listeRole = listeRole;
    }

    public boolean isAdmin() {
        for (Role role : getListeRole() ) {
            if ("ROLE_ADMINISTRATEUR".equals(role.getDenomination())) {
                return true;
            }
        }
        return false;
    }

}
