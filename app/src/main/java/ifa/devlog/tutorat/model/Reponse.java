package ifa.devlog.tutorat.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Reponse implements Serializable {
    private Integer id;
    private String oral;
    private String photo;
    private String date_reponse;

    public Reponse() {}
    public Reponse(Integer id, String oral, String photo,
                   String date_reponse) {
        if ("null".equals(date_reponse)) date_reponse = null;
        this.id = id;
        this.oral = oral;
        this.photo = photo;
        this.date_reponse = date_reponse;
    }
    public String getOral() {return oral;}
    public void setOral(String oral) {this.oral = oral;}

    public String getPhoto() {return photo;}
    public void setPhoto(String photo) {this.photo = photo;}

    public String getDate_reponse() {
        return date_reponse;
    }

    public void setDate_reponse(String date_reponse) {
        this.date_reponse = date_reponse;
    }

    public static List<Reponse> fromJsonArray(JSONArray jsonArray)   throws JSONException {
        List<Reponse> reponses = new ArrayList<Reponse>();
        for(int j = 0; j < jsonArray.length(); j++) {
            JSONObject jsonReponse = jsonArray.getJSONObject(j);
            Reponse reponse;
            reponse = Reponse.fromJson(jsonReponse);
            reponses.add(reponse);
        }
        return reponses;
    }

    public static Reponse fromJson(JSONObject json) throws JSONException {
        return new Reponse(
                json.getInt("id"),
                json.getString("oral"),
                json.getString("photo"),
                json.getString("date_reponse")
        );
    }

    public static JSONObject toJson(Reponse reponse) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", reponse.getId());
        return json;
    }

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}


    public void updateFromUploadPhoto(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.setPhoto(json.getString("file"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateFromUploadAudio(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.setOral(json.getString("file"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
