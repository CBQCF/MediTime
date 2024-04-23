package cbqcf.dim.meditime;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class SpecialTimes extends LinearLayout {
    private RelativeLayout layout;
    private TextView textClock;
    private ImageButton imageButton;
    private SpecialTime time;

    public SpecialTimes(Context context, SpecialTime time) {
        super(context);
        this.time = time;
        initializeUI(context);
        layout = findViewById(R.id.time_layout);
        layout.setOnClickListener(v -> onClick());
    }

    private void initializeUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.specialtimes, this, true);
        textClock = findViewById(R.id.clock);
        imageButton = findViewById(R.id.delete);
        imageButton.setOnClickListener(v -> Delete());
        textClock.setText(time.toString());
    }

    public void Delete() {
        ((EditMedicationActivity)getContext()).getMedication().removeTimestamp(time);
        ((LinearLayout) getParent()).removeView(this);
    }

    public void onClick() {
        TimePicker pickTime = new TimePicker(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.modif_time_alert_title)
                .setView(pickTime)
                .setPositiveButton(R.string.validate, (dialog, which) -> {
                    time.setTime(pickTime.getHour(), pickTime.getMinute());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public SpecialTime getTime() {
        return time;
    }
}
