package ifa.devlog.tutorat.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question  implements Serializable {
    private Integer id;
    private Niveau niveau;
    private String sujet;
    private String explication;
    private String oral;
    private String photo;
    private String date_question;
    private Utilisateur utilisateur;
    private Reponse reponse;

    public Question() {}
    public Question(Integer id,Niveau niveau,String sujet,String explication,String oral,String photo,
                    String date_question,Utilisateur utilisateur,Reponse reponse) {
        if ("null".equals(sujet)) sujet = "";
        if ("null".equals(explication)) explication = "";
        if ("null".equals(date_question)) date_question = null;
        this.id = id;
        this.niveau = niveau;
        this.sujet = sujet;
        this.explication = explication;
        this.oral = oral;
        this.photo = photo;
        this.date_question = date_question;
        this.utilisateur = utilisateur;
        this.reponse = reponse;
    }

    public static List<Question> fromJsonArray(JSONArray jsonArray)   throws JSONException {
        List<Question> questions = new ArrayList<Question>();
        for(int j = 0; j < jsonArray.length(); j++) {
            JSONObject jsonQuestion = jsonArray.getJSONObject(j);
            Question question;
            question = Question.fromJson(jsonQuestion);
            questions.add(question);
        }
        return questions;
    }

    public static Question fromJson(JSONObject json) throws JSONException {
        Niveau niveauQuestion = null;
        if (!json.isNull("niveau")) {
            JSONObject niveauJSON = json.getJSONObject("niveau");
            niveauQuestion = Niveau.fromJson(niveauJSON);
        }
        Reponse reponseQuestion = null;
        if (!json.isNull("reponse")) {
            JSONObject reponseJSON = json.getJSONObject("reponse");
            reponseQuestion = Reponse.fromJson(reponseJSON);
        }

        return new Question(
                json.getInt("id"),
                niveauQuestion,
                json.getString("sujet"),
                json.getString("explication"),
                json.getString("oral"),
                json.getString("photo"),
                json.getString("date_question"),
                Utilisateur.fromJson(json.getJSONObject("utilisateur")),
                reponseQuestion
        );
    }

    public static JSONObject toJson(Question question) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", question.getId());
        json.put("sujet", question.getSujet());
        json.put("explication", question.getExplication());
        json.put("niveau", Niveau.toJson(question.getNiveau()));
        return json;
    }

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public String getSujet() {return sujet;}
    public void setSujet(String sujet) {this.sujet = sujet;}

    public String getExplication() {return explication;}
    public void setExplication(String explication) {this.explication = explication;}

    public String getOral() {return oral;}
    public void setOral(String oral) {this.oral = oral;}

    public String getPhoto() {return photo;}
    public void setPhoto(String photo) {this.photo = photo;}

    public Niveau getNiveau() {return niveau;}
    public void setNiveau(Niveau niveau) {this.niveau = niveau;}

    public Reponse getReponse() {return reponse;}
    public void setReponse(Reponse reponse) {this.reponse = reponse;}

    //    public LocalDate getDate_question() {return date_question;}
//    public void setDate_question(LocalDate date_question) {this.date_question = date_question;}
    public String getTexteDate_question() {
        StringBuffer texteDate = new StringBuffer();
        if (date_question!=null) {
            texteDate.append("Le " + date_question);
        }
        if (utilisateur!=null) {
            texteDate.append(" par ");
            texteDate.append(utilisateur.getPseudo());
        }
        if (reponse!=null && reponse.getDate_reponse()!=null) {
            texteDate.append(" répondue le "+reponse.getDate_reponse());
        }
        return texteDate.toString();
    }

    public void updateFromUploadPhoto(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.setPhoto(json.getString("file"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************************************************");
        System.out.println("*********************Apres photo***************************");
        System.out.println(this.id);
    }

    public void updateFromUploadAudio(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.setOral(json.getString("file"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************************************************");
        System.out.println("*********************Apres audio***************************");
        System.out.println(this.id);
    }

    public boolean estRepondue() {
        if (this.reponse==null) return false;
        if (this.reponse.getDate_reponse()==null) return false;
        return true;
    }

}
