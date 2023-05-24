package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientAsyncTask;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText pokemonNameEditText;
    Button getInfoButton;
    Button startServerButton;

    TextView abilitiesTextView;
    TextView tipesTextView;
    ImageView pokemonImage;
    private ServerThread serverThread;

    Button getFirst20Entries;
    TextView first20EntriesView;

    private GetInfoListener getInfoListener = new GetInfoListener();
    private class GetInfoListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String pokemonName = pokemonNameEditText.getText().toString();

            Log.i(Constants.TAG, "[UI] Pokemon Name: " + pokemonName);

            ClientAsyncTask clientAsyncTask = new ClientAsyncTask(abilitiesTextView, tipesTextView, pokemonImage, first20EntriesView);
            clientAsyncTask.execute(pokemonName);
        }
    }

    private StartServerListener startServerListener = new StartServerListener();
    private class StartServerListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            serverThread = new ServerThread(Integer.parseInt(Constants.PORT.toString()));
            serverThread.startServer();
        }
    }

    private PrintFirstEntries printFirstEntries = new PrintFirstEntries();
    private class PrintFirstEntries implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            first20EntriesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        pokemonNameEditText = findViewById(R.id.editTextPokemonName);
        getInfoButton = findViewById(R.id.getInfoButton);
        getInfoButton.setOnClickListener(getInfoListener);

        abilitiesTextView = findViewById(R.id.textViewAbilities);
        tipesTextView = findViewById(R.id.textViewPokemonTypes);
        pokemonImage = findViewById(R.id.imageView);
        startServerButton = findViewById(R.id.startServerButton);
        startServerButton.setOnClickListener(startServerListener);

        getFirst20Entries = findViewById(R.id.getFirst20Button);
        getFirst20Entries.setOnClickListener(printFirstEntries);
        first20EntriesView = findViewById(R.id.textViewFirst20);

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopServer();
        }
        super.onDestroy();
    }
}