package cbqcf.dim.meditime;

import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class Medication {

    public int id;
    private String name;
    private String description;

    // Delay
    private Boolean isFixedDelay;
    private long delay;
    private ArrayList<SpecialTime> specialTimes;

    // Mode
    private Boolean isWeaningMode;

    // Constructors
    public Medication(int id, String name, String description, boolean weaningMode, long delay, boolean FixedDelay, ArrayList<SpecialTime> specialTimes){
        this.id = id;
        this.name = name;
        this.description = description;
        this.isWeaningMode = weaningMode;
        this.delay = delay;
        this.isFixedDelay = FixedDelay;
        this.specialTimes = specialTimes;
    }

    public Medication(int id, String name, String description, boolean weaningMode, long delay, boolean FixedDelay){
        this.id = id;
        this.name = name;
        this.description = description;
        this.isWeaningMode = weaningMode;
        this.delay = delay;
        this.isFixedDelay = FixedDelay;
        this.specialTimes = new ArrayList<>();
    }
    // End Constructors

    public void Take(){
        MedicationDatasource.getInstance(null).TakeMedication(this);
    }

    public Timestamp getNextTime(){
        Timestamp lastTaken = getLastTaken();
        if (isFixedDelay)
            return new Timestamp(lastTaken.getTime() + delay);
        else {
            long getLastAimed = getLastAimedDate().getTime();
            if(getLastAimed == 0)
                return getStartOfTheDay();

            return getClosest(getLastAimed);


        }
    }

    private Timestamp getClosest(Long time){
        int day = 0;
        long closest = Long.MAX_VALUE;
        SpecialTime closestTime = null;
        while (closestTime==null) {
            for (SpecialTime specialTime : specialTimes) {
                long diff = Math.abs(specialTime.getXDayTime(day).getTime() - time);
                if (diff < closest && specialTime.getXDayTime(day).getTime() > time) {
                    closest = diff;
                    closestTime = specialTime;
                }
            }
            day++;
        }
        return new Timestamp(closestTime.getXDayTime(day-1).getTime());
    }

    private Timestamp getStartOfTheDay(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }
    public Timestamp getLastTaken(){
        return MedicationDatasource.getInstance(null).getLastTaken(this);
    }

    private Timestamp getLastAimedDate(){
        return MedicationDatasource.getInstance(null).getLastAimedDate(this);
    }

    public void setDelay(long val){
        if(val >= 0)
            delay = val;
        else
            Log.e("Medication", "Delay must be positive");
    }

    public void setName(String val){
        name = val;
    }

    public void setDescription(String val){
        description = val;
    }

    public void setWeaningMode(boolean val) {
        isWeaningMode = val;
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

    public Boolean getWeaningMode() {
        return isWeaningMode;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    public void loadSpecialTimes(){
        specialTimes = MedicationDatasource.getInstance(null).getTimestamps(this);
    }
    public void addSpecialTime(SpecialTime specialTime){
        specialTimes.add(specialTime);
    }

    public ArrayList<SpecialTime> getSpecialTimes(){
        return specialTimes;
    }

    public void removeTimestamp(SpecialTime specialTime){
        specialTimes.remove(specialTime);
    }

    public boolean isFixedDelay() {
        return isFixedDelay;
    }

    public void setFixedDelay(boolean b) {
        isFixedDelay = b;
    }

    public void setId(int id) {
        this.id = id;
    }
}
