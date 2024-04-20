package cbqcf.dim.meditime;

import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.util.Calendar;

public class Medication {

    public String id;

    /*
     * Special delay codes
     * 0b1000 : Morning
     * 0b0100 : Noon
     * 0b0010 : Diner
     * 0b0001 : Night
     *
     * Other delay must be above 0b1111
     */
    private long delay;
    private String name;
    private String description;
    private Boolean adaptation; // Check if the medication time should be touched by the optimisation algorithm

    public Medication(String id, String name, String description, boolean adaptation, long delay){
        this.id = id;
        this.name = name;
        this.description = description;
        this.adaptation = adaptation;
        this.delay = delay;
    }

    public long getNextTime(Timestamp lastTaken){
        if (delay <= 0b1111){
            // Special delay
            // Get the start of the day
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startOfDay = cal.getTimeInMillis();

            long nextTime = Long.MAX_VALUE;

            if((delay & 0b1000) == 0b1000) {
                long morningTime = startOfDay + 8 * 3600 * 1000;
                if (morningTime > lastTaken.getTime() && morningTime < nextTime) {
                    nextTime = morningTime;
                }
            }
            if ((delay & 0b0100) == 0b0100) {
                long noonTime = startOfDay + 12 * 3600 * 1000;
                if (noonTime > lastTaken.getTime() && noonTime < nextTime) {
                    nextTime = noonTime;
                }
            }
            if ((delay & 0b0010) == 0b0010) {
                long dinnerTime = startOfDay + 19 * 3600 * 1000;
                if (dinnerTime > lastTaken.getTime() && dinnerTime < nextTime) {
                    nextTime = dinnerTime;
                }
            }
            if ((delay & 0b0001) == 0b0001) {
                long nightTime = startOfDay + 22 * 3600 * 1000;
                if (nightTime > lastTaken.getTime() && nightTime < nextTime) {
                    nextTime = nightTime;
                }
            }

            if (nextTime == Long.MAX_VALUE) {
                Log.w("Medication", "No next time found for medication " + name + " with delay " + delay + " and last taken at " + lastTaken.toString());
            }

            return nextTime;
        }
        else {
            // Delay in milliseconds
            return lastTaken.getTime() + delay;
        }
    }


    public void setId(String val){
        id = val;
    }

    public void setDelay(long val){
        delay = val;
    }

    public void setName(String val){
        name = val;
    }

    public void setDescription(String val){
        description = val;
    }

    public void setAdaptation(boolean val) {
        adaptation = val;
    }

    public long getDelay(){
        return delay;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Boolean getAdaptation() {
        return adaptation;
    }

    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
