package com.akshathakadri.knowyourgovernment;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akshathakadri on 3/29/18.
 */

//API key: AIzaSyBVUf7a-A_FlSURLjDGeeN2OnVfI-Zf11o
public class AsyncLoader extends AsyncTask<String, Integer, GovtObject> {
    private static final String TAG = "AsyncLoader";

    private static final String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBVUf7a-A_FlSURLjDGeeN2OnVfI-Zf11o&address=";

    public MainActivity responseHandler;

    public AsyncLoader(MainActivity mainActivity) {
        this.responseHandler = mainActivity;
    }
    @Override
    protected void onPreExecute() {
        //Toast.makeText(mainActivity, "AsyncTask onPreExecute", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected GovtObject doInBackground(String... city) {
        if(city == null) {
            return null;
        }
        Log.d(TAG, "doInBackground: " );
        return loadDataFromNet(city[0]);
    }

    @Override
    protected void onPostExecute(GovtObject s) {
        responseHandler.processFinish(s);
    }


    private GovtObject loadDataFromNet(String city) {

        String urlToUse = CIVIC_URL+city.trim();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));


            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            if (sb.toString().isEmpty()) {
                Toast.makeText(responseHandler, "No data is available for the specified location", Toast.LENGTH_SHORT).show();
                return null;
            } else {
                return parseStockJSON(sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(responseHandler, "CivicInfo Service is Unavailable", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private GovtObject parseStockJSON(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray officials = jObjMain.getJSONArray("officials");

            List<Official> officialList = new ArrayList<>();
            Map<Integer,Official> officialMap = new HashMap<>();
            for (int i = 0; i < officials.length(); i++) {

                JSONObject jsonObject = officials.getJSONObject(i);
                String name = jsonObject.getString("name");

                String party = "Unknown", photoUrl=null;
                if(jsonObject.has("party")){
                    party = jsonObject.getString("party");
                    if (!party.equalsIgnoreCase("Republican")
                        && !party.equalsIgnoreCase("Democratic")
                            && !party.equalsIgnoreCase("Democrat")) {
                        party = "Unknown";
                    }
                }
                if(jsonObject.has("photoUrl")) {
                    photoUrl = jsonObject.getString("photoUrl");
                }
                Official official = new Official(name, party, photoUrl);

                if(jsonObject.has("urls")) {
                    JSONArray urls = jsonObject.getJSONArray("urls");
                    if (urls != null && urls.get(0) != null)
                        official.setWebsite(urls.get(0).toString());
                }
                //Address
                if(jsonObject.has("address")) {
                    JSONArray jAddresses = jsonObject.getJSONArray("address");
                    if (jAddresses != null && jAddresses.get(0) != null) {
                        JSONObject jAddress = jAddresses.getJSONObject(0);
                        StringBuilder address = new StringBuilder();
                        if (jAddress.has("line1"))
                            address.append(jAddress.get("line1")+"\n");
                        if (jAddress.has("line2"))
                            address.append(jAddress.get("line2")+"\n");
                        if (jAddress.has("line3"))
                            address.append(jAddress.get("line3")+"\n");
                        if (jAddress.has("city"))
                            address.append(jAddress.get("city")+", ");
                        if (jAddress.has("state"))
                            address.append(jAddress.get("state")+" ");
                        if (jAddress.has("zip"))
                            address.append(jAddress.get("zip"));
                        official.setAddress(address.toString());
                    }
                }
                if(jsonObject.has("phones")) {
                    JSONArray phones = jsonObject.getJSONArray("phones");
                    if (phones != null && phones.get(0) != null)
                        official.setPhone(phones.get(0).toString());
                }

                if(jsonObject.has("channels")) {
                    JSONArray channels = jsonObject.getJSONArray("channels");
                    if (channels != null) {
                        Map<String, String> channelMap = new HashMap<>();
                        for (int j = 0; j < channels.length(); j++) {
                            JSONObject obj = channels.getJSONObject(j);
                            String type = obj.getString("type");
                            String id = obj.getString("id");
                            channelMap.put(type, id);
                        }
                        official.setChannels(channelMap);
                    }
                }
                officialList.add(official);
                officialMap.put(i,official);
            }

            JSONArray offices = jObjMain.getJSONArray("offices");
            for (int i = 0; i < offices.length(); i++) {
                JSONObject jsonObject = offices.getJSONObject(i);

                String position = jsonObject.getString("name");
                if(jsonObject.has("officialIndices")) {
                    JSONArray officialIndices = jsonObject.getJSONArray("officialIndices");
                    for (int j = 0; j < officialIndices.length(); j++) {
                        int officialId = officialIndices.getInt(j);
                        Official official = officialMap.get(officialId);
                        official.setPosition(position);
                    }
                }
            }

            String location="";
            JSONObject locationData = jObjMain.getJSONObject("normalizedInput");
            if(locationData.has("city") && !locationData.get("city").toString().isEmpty())
                location=locationData.get("city").toString()+",";
            if(locationData.has("state") && !locationData.get("state").toString().isEmpty())
                location=location+locationData.get("state").toString()+",";
            if(locationData.has("zip") && !locationData.get("zip").toString().isEmpty())
                location=location+locationData.get("zip").toString()+",";

            location = location.substring(0, location.lastIndexOf(","));

            return new GovtObject(officialList, location);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(responseHandler, "Unknown parsing error!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
