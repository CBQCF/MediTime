package cbqcf.dim.meditime;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity  extends AppCompatActivity {

    public GridLayout mainGrid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.button3);
        b.setText(getApplicationContext().getPackageName());

        mainGrid = findViewById(R.id.grid);
        mainGrid.addView(new MedicPanel(getApplicationContext() , "Doliprane" , 4 ));


    }

}
