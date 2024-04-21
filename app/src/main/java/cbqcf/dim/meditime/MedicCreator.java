package cbqcf.dim.meditime;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MedicCreator extends AppCompatActivity {

    TextView nameText, timeText;
    private Context context ;
    private long timeSelected = -1;  // Default to -1 to detect if time is not set

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_creator);

        nameText = findViewById(R.id.creator_name);
        timeText = findViewById(R.id.creator_time);
        Button addButton = findViewById(R.id.add);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedication();
            }
        });
    }

    private void addMedication() {
        String name = nameText.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a medication name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeSelected == -1) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        Medication medication = new Medication(-1,name , "" , false , timeSelected);
        MedicationDatasource.getInstance(context).addMedication(medication);
        // Process the name and timeSelected here (e.g., save to database or send to another activity)
        Toast.makeText(this, "Medication added: " + name + " at " + getTimeString(timeSelected), Toast.LENGTH_LONG).show();
        returnToMainActivity(null);
    }

    public void getInputTime(View v) {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeSelected = (hourOfDay * 3600 + minute * 60) * 1000;  // Corrected the calculation
                timeText.setText(getTimeString(timeSelected));
            }
        }, 0, 0, true);
        dialog.show();
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
