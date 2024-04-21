package cbqcf.dim.meditime;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class EditMedicationActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextDelay;

    private EditText editTextMedicationDelay;

    private LinearLayout checkBoxLayout;
    private CheckBox checkBoxMorning;
    private CheckBox checkBoxNoon;
    private CheckBox checkBoxEvening;
    private CheckBox checkBoxNight;

    private Medication medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication);

        editTextName = findViewById(R.id.editTextMedicationName);
        editTextDescription = findViewById(R.id.editTextMedicationDescription);
        editTextDelay = findViewById(R.id.editTextMedicationDelay);
        checkBoxMorning = findViewById(R.id.checkBoxMorning);
        checkBoxNoon = findViewById(R.id.checkBoxNoon);
        checkBoxEvening = findViewById(R.id.checkBoxEvening);
        checkBoxNight = findViewById(R.id.checkBoxNight);
        checkBoxLayout = findViewById(R.id.linearLayoutTimeSlots);
        EditMedicationActivity.this.setTitle("Edit Medication");
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        SwitchMaterial switchInterval = findViewById(R.id.switchMedicationDelay);
        // Suppose medication is passed as a serializable extra
        Intent intent = getIntent();
        int medicationId = intent.getIntExtra("MEDICATION_ID" , -1 );
         medication = MedicationManager.getInstance().getMedication(String.valueOf(medicationId));


        loadData();
        switchInterval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextDelay.setVisibility(View.VISIBLE);
                checkBoxLayout.setVisibility(View.GONE);
            } else {
                editTextDelay.setVisibility(View.GONE);
                checkBoxLayout.setVisibility(View.VISIBLE);
            }
        });
        buttonSave.setOnClickListener(view -> saveData());

    }

    private void deleteMedic(){
        MedicationDatasource.getInstance(getApplicationContext()).deleteMedication(medication);
        returnToMainActivity(null);
    }
    public void showConfirmationDialog(View v ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer la suppression");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer ce médicament ?");

        // Bouton OUI
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code pour supprimer le médicament
                deleteMedic();
            }
        });

        // Bouton NON
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog and do nothing
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
    }
    public void returnToMainActivity(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ACTIVITY_CLEAR_TASK will clear any existing task that would be associated with the activity
        // FLAG_ACTIVITY_NEW_TASK will start a new task with MainActivity as the root
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void saveData() {
        medication.setName(editTextName.getText().toString());
        medication.setDescription(editTextDescription.getText().toString());
        medication.setDelay(timeSelected);

        // Logic to update medication in database or return result
        MedicationDatasource.getInstance(getApplicationContext()).updateMedication(medication);
        setResult(RESULT_OK); // You can also pass the updated medication back
        returnToMainActivity(null);
    }
}
