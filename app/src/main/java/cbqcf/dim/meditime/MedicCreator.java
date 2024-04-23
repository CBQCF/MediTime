package cbqcf.dim.meditime;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MedicCreator extends AppCompatActivity {

    TextView nameText;
    LinearLayout mainGrid;
    private Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_creator);

        nameText = findViewById(R.id.medication_name);
        mainGrid = findViewById(R.id.grid);
        ImageButton searchButton = findViewById(R.id.search);

        searchButton.setOnClickListener(v -> {
            mainGrid.removeAllViews();
            FirestoreHelper.getInstance().fetchMedicationByName(nameText.getText().toString().toLowerCase(), new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(String name, String description) {
                    Medication medication = new Medication(-1, name, description, false, 0, false);
                    mainGrid.addView(new SearchResult(context, medication));
                }
            });
        });

        ImageButton customButton = findViewById(R.id.custom);
        customButton.setOnClickListener(v -> addMedication());
    }

    private void addMedication() {
        Intent intent = new Intent(this, EditMedicationActivity.class);
        intent.putExtra("MEDICATION_NAME", nameText.getText().toString());
        intent.putExtra("MEDICATION_DESCRIPTION", "");
        intent.putExtra("MEDICATION_ADAPTATION", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void returnToMainActivity(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ACTIVITY_CLEAR_TASK will clear any existing task that would be associated with the activity
        // FLAG_ACTIVITY_NEW_TASK will start a new task with MainActivity as the root
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String getTimeString(long millis) {
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

}
