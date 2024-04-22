package cbqcf.dim.meditime;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class EditMedicationActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private LinearLayout linearLayoutDelay;
    private TimePicker timePicker;

    private GridLayout checkBoxLayout;
    private CheckBox checkBoxMorning;
    private CheckBox checkBoxNoon;
    private CheckBox checkBoxEvening;
    private CheckBox checkBoxNight;
    private SwitchMaterial switchInterval;
    private Switch addictionSwitch;

    private Medication medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication);

        editTextName = findViewById(R.id.editTextMedicationName);
        editTextDescription = findViewById(R.id.editTextMedicationDescription);
        linearLayoutDelay = findViewById(R.id.linearLayoutDelay);
        timePicker = findViewById(R.id.timePicker);
        checkBoxMorning = findViewById(R.id.checkBoxMorning);
        checkBoxNoon = findViewById(R.id.checkBoxNoon);
        checkBoxEvening = findViewById(R.id.checkBoxEvening);
        checkBoxNight = findViewById(R.id.checkBoxNight);
        checkBoxLayout = findViewById(R.id.linearLayoutTimeSlots);
        EditMedicationActivity.this.setTitle("Edit Medication");
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        switchInterval = findViewById(R.id.switchMedicationDelay);
        addictionSwitch = findViewById(R.id.addiction_switch);
        // Suppose medication is passed as a serializable extra
        Intent intent = getIntent();
        int medicationId = intent.getIntExtra("MEDICATION_ID" , -1 );
        if(medicationId != -1)
         medication = MedicationManager.getInstance().getMedication(String.valueOf(medicationId));
        else
            medication= new Medication(-1,intent.getStringExtra("MEDICATION_NAME"),intent.getStringExtra("MEDICATION_DESCRIPTION"),intent.getBooleanExtra("MEDICATION_ADAPTATION", true),0);
        timePicker.setIs24HourView(true);
        switchInterval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                linearLayoutDelay.setVisibility(View.VISIBLE);
                checkBoxLayout.setVisibility(View.GONE);
            } else {
                linearLayoutDelay.setVisibility(View.GONE);
                checkBoxLayout.setVisibility(View.VISIBLE);
            }
        });
        buttonSave.setOnClickListener(view -> saveData());
        loadData();
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
    public void getInputTime() {
        if(switchInterval.isChecked()){
            showTimePicker();
        } else {
            showTimeSlotPicker();
        }
    }
    private void showTimePicker() {
        timeSelected = (long)timePicker.getHour() * 3600 * 1000 + (long)timePicker.getMinute() * 60 * 1000;
        if(timeSelected <= 0b1111)
            timeSelected = -1;
    }

    private void showTimeSlotPicker() {
        timeSelected = 0;
        if(checkBoxMorning.isChecked()) {
            timeSelected |= 0b1000;
        }
        if(checkBoxNoon.isChecked()) {
            timeSelected |= 0b0100;
        }
        if(checkBoxEvening.isChecked()) {
            timeSelected |= 0b0010;
        }
        if(checkBoxNight.isChecked()) {
            timeSelected |= 0b0001;
        }
    }

    private void loadData() {
        editTextName.setText(medication.getName());
        editTextDescription.setText(medication.getDescription());
        timeSelected = medication.getDelay();
        if (timeSelected > 0 && timeSelected <= 0b1111){
            checkBoxMorning.setChecked((timeSelected & 0b1000) == 0b1000);
            checkBoxNoon.setChecked((timeSelected & 0b0100) == 0b0100);
            checkBoxEvening.setChecked((timeSelected & 0b0010) == 0b0010);
            checkBoxNight.setChecked((timeSelected & 0b0001) == 0b0001);
            switchInterval.setChecked(false);
        } else {
            Time timestamp = new Time(timeSelected);
            timestamp.setTime(timeSelected);
            timePicker.setHour((int) (timeSelected / 3600 / 1000));
            timePicker.setMinute((int) ((timeSelected / 60 / 1000) % 60));
            switchInterval.setChecked(true);
        }
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
        medication.setAdaptation(addictionSwitch.isChecked());
        getInputTime();
        if(timeSelected == -1) {
            Toast.makeText(this, "Veuillez sélectionner un horaire", Toast.LENGTH_SHORT).show();
            return;
        }
        else
            medication.setDelay(timeSelected);

        // Logic to update medication in database or return result
        MedicationDatasource.getInstance(getApplicationContext()).updateMedication(medication);
        setResult(RESULT_OK); // You can also pass the updated medication back
        returnToMainActivity(null);
    }
}
