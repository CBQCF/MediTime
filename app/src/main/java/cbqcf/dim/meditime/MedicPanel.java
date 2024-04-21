package cbqcf.dim.meditime;

import android.content.Context;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class MedicPanel extends LinearLayout {
    private static final int UPDATE_INTERVAL_MS = 1000; // Interval for updating the UI
    private long time;
    private long ecart = 5000;
    private TextView nameView, timeView;
    private ProgressBar progressBar;
    private LinearLayout mediLayout;
    private Medication medication;

    private Handler mHandler = new Handler();
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            updateTime();
            updateLayout();
            mHandler.postDelayed(this, UPDATE_INTERVAL_MS);
        }
    };

    public MedicPanel(Context context, Medication medication) {
        super(context);
        initializeUI(context);
        this.medication = medication;
        this.ecart = medication.getDelay();
        Timestamp lastTaken = medication.getLastTaken();
        this.time = 0;
        if(lastTaken != null) {
            this.time = lastTaken.getTime();
        }
        revalidateComponent();
        mediLayout.setOnClickListener(v->openEditor(v));
    }

    public MedicPanel(Context context) {
        super(context);
        initializeUI(context);
        resetTime();
    }


    public Medication getMedication() {
        return medication;
    }
    public void openEditor(View v )
    {


       openEditMedicationActivity(getMedication());
    }
    public MedicPanel(Context context, String name, int EcartH) {
        this(context);
        this.ecart = TimeUnit.HOURS.toMillis(EcartH);
        nameView.setText(name);
        revalidateComponent();
    }

    private void initializeUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.medicpanel, this, true);
        nameView = findViewById(R.id.Name);
        timeView = findViewById(R.id.textTimer);
        progressBar = findViewById(R.id.progressBar);
        mediLayout = findViewById(R.id.MediLayout);
        progressBar.setMin(0);
        progressBar.setMax((int) (ecart / 1000));
        mHandler.postDelayed(update, 1);
    }

    public void openEditMedicationActivity(Medication medication) {
        MedicationManager.getInstance().updateMedication(medication );
        Intent intent = new Intent(getContext(), EditMedicationActivity.class);
        intent.putExtra("MEDICATION_ID", medication.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    private void revalidateComponent() {
        progressBar.setMax((int) (ecart / 1000));
        if (medication != null) {
            nameView.setText(medication.getName());
        }
    }

    public String getName() {
        return nameView.getText().toString();
    }

    public long getEcart() {
        return ecart;
    }

    public void resetTime() {
        time = System.currentTimeMillis();
    }

    private long getTimeSinceLast() {
        return System.currentTimeMillis() - time;
    }

    private String LongToHmsFormat(long t) {
        long hours = TimeUnit.MILLISECONDS.toHours(t) % 100;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(t) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(t) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateTime() {
        timeView.setText(LongToHmsFormat(getTimeSinceLast()));
    }

    private void updateLayout() {
        int backgroundColor = Color.YELLOW;
        if(time != 0)
            backgroundColor = isOutime() ? Color.GREEN : Color.RED;

        Drawable back = mediLayout.getBackground();
        if (back != null) {
            back.setColorFilter(new BlendModeColorFilter(backgroundColor, BlendMode.SRC_ATOP));
        }
        progressBar.setProgress((int) (getTimeSinceLast() / 1000), true);
    }

    private boolean isOutime() {
        return System.currentTimeMillis() > time + ecart;
    }
}
