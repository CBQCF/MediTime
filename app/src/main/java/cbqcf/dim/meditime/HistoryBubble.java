package cbqcf.dim.meditime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

public class HistoryBubble extends LinearLayout {
    private LinearLayout layout;
    private TextView nameView, timeView, dateView;
    private Medication medication;
    private Timestamp actualTime, expectedTime;

    public HistoryBubble(Context context, Medication result, Timestamp actualTime, Timestamp expectedTime) {
        super(context);
        this.medication = result;
        this.actualTime = actualTime;
        this.expectedTime = expectedTime;
        initializeUI(context);
        layout = findViewById(R.id.history_bubble);
        layout.setOnLongClickListener(this::onLongClick);
    }

    private void initializeUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.historybubble, this, true);
        nameView = findViewById(R.id.history_name);
        timeView = findViewById(R.id.history_time);
        dateView = findViewById(R.id.history_date);

        nameView.setText(medication.getName());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(actualTime.getTime());
        if(!medication.isFixedDelay()) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(expectedTime.getTime());
            timeView.setText(String.format(Locale.getDefault(), "%02d:%02d (%02d:%02d)", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal2.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.MINUTE)));
        } else
            timeView.setText(String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
        dateView.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
    }

    public boolean onLongClick(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.content_delete_button)
                .setMessage(R.string.content_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    MedicationDatasource.getInstance(getContext()).deleteTaken(medication, expectedTime, actualTime);
                    ((HistoryActivity)getContext()).reloadHistory();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
        return true;
    }
}
