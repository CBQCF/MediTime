package cbqcf.dim.meditime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity  extends AppCompatActivity {
    private FirestoreHelper FS;
    private MedicationDatasource DS;
    private Button addMedication;
    public GridLayout mainGrid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FS = new FirestoreHelper();
        DS = new MedicationDatasource(null);

        Button b = findViewById(R.id.button3);
        b.setText(getApplicationContext().getPackageName());

        mainGrid = findViewById(R.id.grid);
        mainGrid.addView(new MedicPanel(getApplicationContext() , new Medication("45","Doliprane" , "" , false , false , 5000 , System.currentTimeMillis())));

        addMedication = findViewById(R.id.button4);
        addMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedication(v);
            }
        });
    }

    public void addMedication(Medication med){
        DS.addMedication(med);
    }

    public void addMedication(View v){
        Toast.makeText(this, "Retour Main", Toast.LENGTH_SHORT).show();
        Intent startActivity = new Intent(this, MedicCreator.class);
        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startActivity);
    }


}
