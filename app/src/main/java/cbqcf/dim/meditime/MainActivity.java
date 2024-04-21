package cbqcf.dim.meditime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity  extends AppCompatActivity {
    private FirestoreHelper FS;
    private MedicationDatasource DS;
    private Button addMedication;
    public LinearLayout mainGrid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FS = new FirestoreHelper();
        DS = MedicationDatasource.getInstance(getApplicationContext());
        DS.open();

        mainGrid = findViewById(R.id.grid);
        loadMedications();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        DS.close();
    }

    public void addMedication(Medication med){
        DS.addMedication(med);
        reloadMedications();
    }

    public void addMedication(View v){
        Toast.makeText(this, "Retour Main", Toast.LENGTH_SHORT).show();
        Intent startActivity = new Intent(this, MedicCreator.class);
        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startActivity);
    }

    public void openEditor(View v )
    {
        MedicPanel medicPanel = (MedicPanel)v ;

        medicPanel.openEditMedicationActivity(medicPanel.getMedication());
    }

    public void loadMedications(){
        for (Medication med : DS.loadMedications()){
            MedicPanel panel = new MedicPanel(getApplicationContext(), med);
            mainGrid.addView(panel);
        }
    }

    public void reloadMedications(){
        mainGrid.removeAllViews();
        loadMedications();
    }

    public void ClearMedications(View v){
        for(Medication med : DS.loadMedications()){
            DS.deleteMedication(med);
        }
        reloadMedications();
    }
}
