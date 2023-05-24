package ro.pub.cs.systems.eim.practicaltest02.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientAsyncTask extends AsyncTask<String, Object, Void> {

    TextView abilitiesTextView;
    TextView tipesTextView;
    ImageView pokemonImage;
    TextView first20EntriesView;

    public ClientAsyncTask(TextView abilitiesTextView, TextView tipesTextView, ImageView pokemonImage, TextView first20EntriesView) {
        this.abilitiesTextView = abilitiesTextView;
        this.tipesTextView = tipesTextView;
        this.pokemonImage = pokemonImage;
        this.first20EntriesView = first20EntriesView;
    }

    @Override
    protected Void doInBackground(String... params) {
        Socket socket = null;
        try {
            String pokemonName = params[0];
            int serverPort = Constants.PORT;
            String serverAddress = "localhost";

            socket = new Socket(serverAddress, serverPort);
            if (socket == null) {
                Log.e(Constants.TAG, "Connection not established.");
                return null;
            }
            Log.i(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
            PrintWriter printWriter = Utilities.getWriter(socket);
            printWriter.println(pokemonName);
            printWriter.flush();

            BufferedReader bufferedReader = Utilities.getReader(socket);
            String abilities = bufferedReader.readLine();
            Log.i(Constants.TAG, "[CLIENT] Message: " + abilities);
            String types = bufferedReader.readLine();
            Log.i(Constants.TAG, "[CLIENT] Message: " + types);
            String imageUrl = bufferedReader.readLine();
            Log.i(Constants.TAG, "[CLIENT] Message: " + imageUrl);

            URL url = null;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String first20Pokemons = bufferedReader.readLine();


            publishProgress(abilities, types, bmp, first20Pokemons);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                Log.v(Constants.TAG, "Connection closed");
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        abilitiesTextView.setText("");
        tipesTextView.setText("");
        first20EntriesView.setText("");
    }

    @Override
    protected void onProgressUpdate(Object... progress) {
        abilitiesTextView.setText("Abilities: " + progress[0]);
        tipesTextView.setText("Types: " + progress[1]);
        pokemonImage.setImageBitmap((Bitmap) progress[2]);
        first20EntriesView.setText("Pokemons: " + progress[3]);
    }

    @Override
    protected void onPostExecute(Void result) {}

}
