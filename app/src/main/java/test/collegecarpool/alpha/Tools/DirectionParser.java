package test.collegecarpool.alpha.Tools;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DirectionParser {

    private final static String TAG = "DIRECTION PASRSER";
    private String jsonString = "";

    DirectionParser(String jsonString){
        this.jsonString = jsonString;
    }

    public ArrayList<LatLng> getDirectionsAsList(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        }
        catch(JSONException e){
            Log.d(TAG, "MALFORMED JSON");
        }
        ArrayList<LatLng> latLngArray = new ArrayList<>();
        /*Structure of JSON Document From Google Directions*/
        JSONArray routes;
        JSONArray legs;
        JSONArray steps;
        String routeLine;

        try{
            /*When you look at JSON URL, you can see bound, set Map view to be incorporate these bounds*/
            if(null != jsonObject)
                routes = jsonObject.getJSONArray("routes");
            else
                return null;
            /*PATH IS 3 "LAYERS" DEEP*/
            for(int i = 0; i < routes.length(); i++){
                legs = ((JSONObject) routes.get(i)).getJSONArray("legs");
                for(int j = 0; j < legs.length(); j++){
                    steps = ((JSONObject) legs.get(j)).getJSONArray("steps");
                    for(int k = 0; k < steps.length(); k++){
                        routeLine = (String) ((JSONObject)((JSONObject)steps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = PolyUtil.decode(routeLine);
                        for(int l = 0; l < list.size(); l++){
                            LatLng latLng = new LatLng((list.get(l)).latitude,(list.get(l)).longitude);
                            latLngArray.add(latLng);
                        }
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSON Error");
        }
        return latLngArray;
    }
}
