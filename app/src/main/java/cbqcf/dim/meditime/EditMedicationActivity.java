package cbqcf.dim.meditime;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class EditMedicationActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextDelay;
    private CheckBox checkBoxAdaptation;
    private CheckBox checkBoxHasBeenTaken;

    private Medication medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication);

        editTextName = findViewById(R.id.editTextMedicationName);
        editTextDescription = findViewById(R.id.editTextMedicationDescription);
        editTextDelay = findViewById(R.id.editTextMedicationDelay);
        checkBoxAdaptation = findViewById(R.id.checkBoxAdaptation);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        // Suppose medication is passed as a serializable extra

        String medicationId = getIntent().getStringExtra("MEDICATION_ID");
         medication = MedicationManager.getInstance().getMedication(medicationId);


        loadData();

        buttonSave.setOnClickListener(view -> saveData());
        buttonCancel.setOnClickListener(view -> finish());
    }
    private String getTimeString(long millis) {
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
    long timeSelected = -1;
    public void getInputTime(View v) {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                 timeSelected = (hourOfDay * 3600 + minute * 60) * 1000;  // Corrected the calculation
                editTextDelay.setText(getTimeString(timeSelected));
            }
        }, 0, 0, true);
        dialog.show();
    }
    private void loadData() {
        editTextName.setText(medication.getName());
        editTextDescription.setText(medication.getDescription());
        editTextDelay.setText(String.valueOf(medication.getDelay()));
        checkBoxAdaptation.setChecked(medication.getAdaptation());

    }

    private void saveData() {
        medication.setName(editTextName.getText().toString());
        medication.setDescription(editTextDescription.getText().toString());
        medication.setDelay(timeSelected);
        medication.setAdaptation(checkBoxAdaptation.isChecked());


        // Logic to update medication in database or return result
        setResult(RESULT_OK); // You can also pass the updated medication back
        finish();
    }
}
