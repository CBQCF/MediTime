package cbqcf.dim.meditime;

import android.app.Application;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


class MedicPanel extends LinearLayout {

    private long time ;
    private long ecart = 5000;
    private TextView nameView , timeView ;
    private ProgressBar progressBar ;
    private LinearLayout mediLayout ;

    public MedicPanel(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.medicpanel,this,true);
         nameView = findViewById(R.id.Name);
        timeView = findViewById(R.id.textTimer);
        progressBar = findViewById(R.id.progressBar);
        mediLayout = findViewById(R.id.MediLayout);
        //Setup progress bar
        progressBar.setMin(0);
        progressBar.setMax((int) ecart / 1000);
        resetTime();
        //Starting timer
        mHandler.postDelayed(update,1);

    }

    public MedicPanel(Context context ,  String name , int EcartH ){
        this(context);
        ecart = (long)(EcartH)  * 1000 * 60 *  60;
        nameView.setText(name);
        revalidateComponent();
    }

    public  String getName(){
        return nameView.getText().toString();
    }
    public long getEcart(){
        return  ecart ;
    }
    public void resetTime(){
        time = System.currentTimeMillis();
    }

    private void revalidateComponent(){
        progressBar.setMin(0);
        progressBar.setMax((int) ecart / 1000);

    }
    private Handler mHandler = new Handler();
    public Runnable update = new Runnable() {
        @Override
        public void run() {
            updateTime();
            updateLayout();
            mHandler.postDelayed(this, 1000);
        }
    };
    private long getTimeSinceLast(){
        return  System.currentTimeMillis() - time ;
    }
    private  String LongToHmsFormat(long t ){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(t) -  TimeUnit.MILLISECONDS.toHours(t);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MINUTES.toSeconds(minutes) -  TimeUnit.MILLISECONDS.toHours(t);
        long hours =  TimeUnit.MILLISECONDS.toHours(t);
        return String.format("%02d:%02d:%02d" , hours, minutes, seconds) ;
    }

    private  void updateTime(){
        timeView.setText(LongToHmsFormat(getTimeSinceLast()) );
    }
    private  void updateLayout(){

        Drawable back = mediLayout.getBackground();
        back.setColorFilter(new BlendModeColorFilter(( isOutime() ? 0x6FFF0000  : 0x6F00F0F0), BlendMode.SRC_ATOP));
        //ShapeDrawable shapeDrawable = (ShapeDrawable) back;
        //shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(),( isOutime() ? 0x6FFF0000  : 0x6F00F0F0)));



        progressBar.setProgress((int)getTimeSinceLast() / 1000 , true);
    }
    private boolean isOutime(){
        return time + ecart >= System.currentTimeMillis();
    }

}
