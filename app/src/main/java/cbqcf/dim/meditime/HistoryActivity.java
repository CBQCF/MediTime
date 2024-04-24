package cbqcf.dim.meditime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private LinearLayout historyGrid;
    private Spinner filterSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        setTitle(R.string.history_title);
        historyGrid = findViewById(R.id.grid);
        filterSpinner = findViewById(R.id.filter);
        filterSpinner.setOnItemSelectedListener(this);
        loadFilter();
        loadHistory();
    }

    private void loadFilter() {
        ArrayAdapter<Medication> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(MedicationDatasource.getInstance(this).loadMedications()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        reloadHistory();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        reloadHistory();
    }

    public Medication getFilter() {
        return (Medication) filterSpinner.getSelectedItem();
    }

    public void loadHistory() {
        for (HistoryBubble bubble : MedicationDatasource.getInstance(this).loadHistory(getFilter(), this)) {
            addHistoryBubble(bubble);
        }
    }

    public void addHistoryBubble(HistoryBubble bubble) {
        historyGrid.addView(bubble);
    }

    public void clearView() {
           historyGrid.removeAllViews();
    }

    public void reloadHistory() {
        clearView();
        loadHistory();
    }

    public void returnToMainActivity(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ACTIVITY_CLEAR_TASK will clear any existing task that would be associated with the activity
        // FLAG_ACTIVITY_NEW_TASK will start a new task with MainActivity as the root
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
