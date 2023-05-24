package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {

    private Socket socket;

    public CommunicationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Log.i(Constants.TAG, "[COM THREAD] Connection opened to " + socket.getLocalAddress() + ":" + socket.getLocalPort()+ " from " + socket.getInetAddress());

            BufferedReader bufferedReader = Utilities.getReader(socket);
            String pokemonName = bufferedReader.readLine();
            Log.i(Constants.TAG, "[COM THREAD] Pokemon name: " + pokemonName);

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("https://pokeapi.co/api/v2/pokemon/" + pokemonName);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpGetEntity = httpGetResponse.getEntity();
            String result = EntityUtils.toString(httpGetEntity);
            if (httpGetEntity != null) {
                Log.i(Constants.TAG, result);
            }

            JSONObject jsonObject = new JSONObject(result);
            List<String> abilities = new ArrayList<>();
            List<String> types = new ArrayList<>();
            JSONArray abilitiesJson = jsonObject.getJSONArray("abilities");
            for (int i = 0; i < abilitiesJson.length(); i++) {
                abilities.add(abilitiesJson.getJSONObject(i).getJSONObject("ability").getString("name"));
            }

            Log.i(Constants.TAG, abilities.toString());

            JSONArray typesJson = jsonObject.getJSONArray("types");
            for (int i = 0; i < typesJson.length(); i++) {
                types.add(typesJson.getJSONObject(i).getJSONObject("type").getString("name"));
            }
            Log.i(Constants.TAG, types.toString());

            String imageUrl = jsonObject.getJSONObject("sprites").getString("front_default");
            Log.i(Constants.TAG, imageUrl);

            PrintWriter printWriter = Utilities.getWriter(socket);
            printWriter.println(String.join(",", abilities));
            printWriter.flush();

            printWriter.println(String.join(",", types));
            printWriter.flush();

            printWriter.println(imageUrl);
            printWriter.flush();

            socket.close();
            Log.v(Constants.TAG, "[COM THREAD] Connection closed");
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COM THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}
