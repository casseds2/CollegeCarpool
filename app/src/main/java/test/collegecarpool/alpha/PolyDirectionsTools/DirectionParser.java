package test.collegecarpool.alpha.PolyDirectionsTools;

import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import test.collegecarpool.alpha.MapsUtilities.DirectionStep;


public class DirectionParser {

    private final static String TAG = "DIRECTION PASRSER";
    private String jsonString = "";
    private ArrayList<DirectionStep> directionSteps;

    DirectionParser(String jsonString){
        this.jsonString = jsonString;
        directionSteps = new ArrayList<>();
    }

    public ArrayList<DirectionStep> getDirectionSteps(){
        return directionSteps;
    }

    public String toString(){
        return jsonString;
    }

    ArrayList<LatLng> getDirectionsAsList(){
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
        int distance;
        int duration;
        String htmlInstructions;
        String maneuver ;
        LatLng endLocation;
        LatLng startLocation;
        String encodedPolyStep;

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
                        distance = (int) ((JSONObject)((JSONObject)steps.get(k)).get("distance")).get("value");
                        //Log.d(TAG, "Distance: " + distance);
                        duration = (int) ((JSONObject)((JSONObject)steps.get(k)).get("duration")).get("value");
                        //Log.d(TAG, "Duration: " + duration);
                        htmlInstructions = (String) ((JSONObject)steps.get(k)).get("html_instructions");
                        htmlInstructions = Html.fromHtml(htmlInstructions).toString();
                        htmlInstructions = htmlInstructions.replaceAll("\\s+", " ");
                        //Log.d(TAG, "Html: " + htmlInstructions);
                        if(((JSONObject) steps.get(k)).has("maneuver")) {  //Check If there is actually an element of type
                            maneuver = (String)((JSONObject) steps.get(k)).get("maneuver");
                            //Log.d(TAG, "Maneuver: " + maneuver);
                        }
                        else
                            maneuver = null;
                        double startLat = (double) ((JSONObject)((JSONObject)steps.get(k)).get("start_location")).get("lat");
                        double startLng = (double) ((JSONObject)((JSONObject)steps.get(k)).get("start_location")).get("lng");
                        double endLat = (double) ((JSONObject)((JSONObject)steps.get(k)).get("end_location")).get("lat");
                        double endLng = (double) ((JSONObject)((JSONObject)steps.get(k)).get("end_location")).get("lng");
                        startLocation = new LatLng(startLat, startLng);
                        //Log.d(TAG, "Start: " + startLocation.toString());
                        endLocation = new LatLng(endLat, endLng);
                        //Log.d(TAG, "End: " + endLocation.toString());
                        encodedPolyStep = (String) ((JSONObject)((JSONObject)steps.get(k)).get("polyline")).get("points");
                        ArrayList<LatLng> list = (ArrayList<LatLng>) PolyUtil.decode(encodedPolyStep);
                        DirectionStep directionStep = new DirectionStep(distance, duration, startLocation, endLocation, maneuver, htmlInstructions, list);
                        directionSteps.add(directionStep);
                        for(int l = 0; l < list.size(); l++){
                            LatLng latLng = new LatLng((list.get(l)).latitude,(list.get(l)).longitude);
                            latLngArray.add(latLng);
                        }
                    }
                }
            }
            Log.d(TAG, "DIRECTION PARSER FINISHED BUILDING");
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSON Error");
        }
        return latLngArray;
    }
}
