package cbqcf.dim.meditime;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SpecialTime {
    public int Hour;
    public int Minute;

    public SpecialTime(int hour, int minute){
        this.Hour = hour;
        this.Minute = minute;
    }

    public void setTime(int hour, int minute){
        this.Hour = hour;
        this.Minute = minute;
    }

    public Timestamp getXDayTime(int Day){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, Day);
        cal.set(Calendar.HOUR_OF_DAY, Hour);
        cal.set(Calendar.MINUTE, Minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }

    @Override
    public String toString(){
        return String.format(Locale.getDefault(), "%02d:%02d", Hour, Minute);
    }
}
