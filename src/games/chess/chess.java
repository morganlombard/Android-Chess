package games.chess;

import android.app.Activity;
import android.os.Bundle;


// TODO: move the logic into a separate class
public class chess extends Activity {

	private chessCore core;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}