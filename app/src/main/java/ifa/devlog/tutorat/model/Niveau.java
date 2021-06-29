package ifa.devlog.tutorat.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Niveau implements Serializable {
    private Integer id;
    private String denomination;

    public Niveau(Integer id, String nom) {
        this.id = id;
        this.denomination = nom;
    }

    public static List<Niveau> fromJsonArray(JSONArray jsonArray)  throws JSONException {
        List<Niveau> niveaux = new ArrayList<Niveau>();
        for(int j = 0; j < jsonArray.length(); j++) {
            JSONObject json = jsonArray.getJSONObject(j);
            Niveau niveau = Niveau.fromJson(json);
            niveaux.add(niveau);
        }
        return niveaux;
    }
    public static Niveau fromJson(JSONObject jsonRole) throws JSONException {
        return new Niveau(
                jsonRole.getInt("id"),
                jsonRole.getString("denomination")
        );
    }

    public static JSONObject toJson(Niveau niveau) throws JSONException {
        JSONObject json = new JSONObject();
        if (niveau!=null) {
            json.put("id", niveau.getId());
            json.put("denomination", niveau.getDenomination());
        }
        return json;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDenomination() {
        return denomination;
    }
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    @Override
    public String toString() {
        return denomination;
    }
}
