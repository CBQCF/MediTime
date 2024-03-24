package cbqcf.dim.meditime;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


class MedicPanel extends LinearLayout {

    private long time ;
    private long ecart;
    private TextView nameView , timeView ;
    private ProgressBar progressBar ;

    public MedicPanel(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.medicpanel,this,true);
         nameView = findViewById(R.id.Name);
        timeView = findViewById(R.id.textTimer);
        progressBar = findViewById(R.id.progressBar);
        resetTime();
        mHandler.postDelayed(mRunnable,1);

    }

    public void resetTime(){
        time = System.currentTimeMillis();
    }


    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Mettez ici le code pour mettre à jour votre vue
            // Par exemple, mise à jour d'un TextView avec l'heure actuelle
            long durationInMillis = System.currentTimeMillis() - time; // Votre durée en millisecondes
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) - TimeUnit.MINUTES.toSeconds(minutes);

            timeView.setText( String.format("%02d:%02d", minutes, seconds));

            // Planifiez à nouveau le Runnable pour s'exécuter après une seconde
            mHandler.postDelayed(this, 1000);
        }
    };
}
