package cbqcf.dim.meditime;

import android.util.Log;
import androidx.annotation.NonNull;
import java.util.Calendar;

public class Medication {

    public String id;
    private long lastTime;

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
    private Boolean hasBeenTaken;

    public Medication(String id, String name, String description, boolean adaptation, boolean hasBeenTaken, long delay, long lastTime){
        this.id = id;
        this.name = name;
        this.description = description;
        this.adaptation = adaptation;
        this.hasBeenTaken = hasBeenTaken;
        this.delay = delay;
        this.lastTime = lastTime;
    }

    public long getNextTime() {
        /*
         * Return the time to the next time for medication
         * TODO
         */
        return 0;
    }


    public void setId(String val){
        id = val;
    }
    public void setLastTime(long val){
        if(val <= System.currentTimeMillis()) {
            lastTime = val;
        }
        else {
            Log.w("DIM", "Medication.setLastTime : Last time is ahead of current time");
        }
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

    public void setHasBeenTaken(boolean val) {
        hasBeenTaken = val;
    }

    public void take(){
        /*
         * Called when the medicine has been taken
         */
        setLastTime(System.currentTimeMillis());
        setHasBeenTaken(true);
    }

    public long getDelay(){
        return delay;
    }

    public long getLastTime(){
        return lastTime;
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

    public Boolean getHasBeenTaken() {
        return hasBeenTaken;
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
