package cbqcf.dim.meditime;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.TimeUnit;

public class EditMedicationActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private LinearLayout linearLayoutDelay;
    private TimePicker timePicker;
    private RelativeLayout timeGrid;
    private LinearLayout SpecialTimesGrid;
    private SwitchMaterial switchInterval;
    private SwitchMaterial addictionSwitch;
    private Medication medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication);
        EditMedicationActivity.this.setTitle(R.string.edit_medication_title);
        findViews();
        findMedication();

        timePicker.setIs24HourView(true);
        switchInterval.setOnCheckedChangeListener(this::onSwitchInterval);
        loadData();
    }

    private void findViews(){
        editTextName = findViewById(R.id.editTextMedicationName);
        editTextDescription = findViewById(R.id.editTextMedicationDescription);
        linearLayoutDelay = findViewById(R.id.linearLayoutDelay);
        timePicker = findViewById(R.id.timePicker);
        timeGrid = findViewById(R.id.time_grid_layout);
        SpecialTimesGrid = findViewById(R.id.grid);
        switchInterval = findViewById(R.id.switchMedicationDelay);
        addictionSwitch = findViewById(R.id.addiction_switch);
    }
    private void onSwitchInterval(View buttonView, boolean isChecked){
        if (isChecked) {
            linearLayoutDelay.setVisibility(View.VISIBLE);
            timeGrid.setVisibility(View.GONE);
        } else {
            linearLayoutDelay.setVisibility(View.GONE);
            timeGrid.setVisibility(View.VISIBLE);
        }
    }
    private void findMedication(){
        Intent intent = getIntent();
        int medicationId = intent.getIntExtra("MEDICATION_ID" , -1 );
        if(medicationId != -1) medication = MedicationManager.getInstance().getMedication(String.valueOf(medicationId));
        else medication= new Medication(-1,intent.getStringExtra("MEDICATION_NAME"),intent.getStringExtra("MEDICATION_DESCRIPTION"),false,0, false);

    }
    private void deleteMedic(){
        MedicationDatasource.getInstance(getApplicationContext()).deleteMedication(medication);
        NotifManager.cancelNotification(getApplicationContext(), medication);
        returnToMainActivity(null);
    }
    public void showConfirmationDialog(View v ) {
        new AlertDialog.Builder(this)
        .setTitle(R.string.confirm_delete_medication)
        .setMessage(R.string.confirm_delete_medication_message)
        .setPositiveButton(R.string.yes, (dialog, which) -> deleteMedic())
        .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss()).show();
    }

    private Long getFixedDelay() {
        return TimeUnit.HOURS.toMillis(timePicker.getHour()) + TimeUnit.MINUTES.toMillis(timePicker.getMinute());
    }

    private void loadData() {
        editTextName.setText(medication.getName());
        editTextDescription.setText(medication.getDescription());
        switchInterval.setChecked(medication.isFixedDelay());
        addictionSwitch.setChecked(medication.getWeaningMode());
        timePicker.setHour((int)TimeUnit.MILLISECONDS.toHours(medication.getDelay()));
        timePicker.setMinute((int)TimeUnit.MILLISECONDS.toMinutes(medication.getDelay()) % 60);
        for(SpecialTime specialTime : medication.getSpecialTimes()) {
            SpecialTimesGrid.addView(new SpecialTimes(this, specialTime));
        }
    }
    public void returnToMainActivity(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ACTIVITY_CLEAR_TASK will clear any existing task that would be associated with the activity
        // FLAG_ACTIVITY_NEW_TASK will start a new task with MainActivity as the root
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void saveData(View v) {
        if(!switchInterval.isChecked() && medication.getSpecialTimes().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.no_special_time, Toast.LENGTH_SHORT).show();
            return;
        }
        medication.setName(editTextName.getText().toString());
        medication.setDescription(editTextDescription.getText().toString());
        medication.setWeaningMode(addictionSwitch.isChecked());
        medication.setFixedDelay(switchInterval.isChecked());
        medication.setDelay(getFixedDelay());

        // Logic to update medication in database or return result
        MedicationDatasource.getInstance(getApplicationContext()).updateMedication(medication);
        setResult(RESULT_OK); // You can also pass the updated medication back
        returnToMainActivity(null);
    }

    public void addTime(View view) {
        TimePicker pickTime = new TimePicker(this);
        new AlertDialog.Builder(this)
            .setView(pickTime)
            .setPositiveButton(R.string.validate, (dialog, which) -> {
                // Add a special time instance
                SpecialTime time = new SpecialTime(pickTime.getHour(), pickTime.getMinute());
                medication.addSpecialTime(time);
                SpecialTimesGrid.addView(new SpecialTimes(this, time));
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    public Medication getMedication(){
        return medication;
    }
}
