package cbqcf.dim.meditime;

import java.util.HashMap;

public class MedicationManager {
    private static final MedicationManager ourInstance = new MedicationManager();
    private HashMap<String, Medication> medications = new HashMap<>();

    public static MedicationManager getInstance() {
        return ourInstance;
    }

    private MedicationManager() {
    }

    public void addMedication(Medication medication) {
        medications.put(String.valueOf(medication.getId()), medication);
    }

    public Medication getMedication(String id) {
        return medications.get(id);
    }

    public void updateMedication(Medication medication) {
        medications.put(String.valueOf(medication.getId()), medication);
    }
}
