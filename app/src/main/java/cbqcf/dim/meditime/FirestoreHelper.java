package cbqcf.dim.meditime;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreHelper {

    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchMedicationById(String medicationId, final FirestoreCallback callback) {
        DocumentReference docRef = db.collection("medications").document(medicationId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String description = document.getString("description");
                        Boolean adaptation = document.getBoolean("adaptation");
                        callback.onSuccess(medicationId, name, description, adaptation);
                    } else {
                        callback.onError("Medication with ID " + medicationId + " not found.");
                    }
                } else {
                    callback.onError("Failed to fetch medication data: " + task.getException().getMessage());
                }
            }
        });
    }

    public void fetchMedicationById(int medicationId, final FirestoreCallback callback) {
        fetchMedicationById(Integer.toString(medicationId), callback);
    }

    public void fetchMedicationByName(String name, final FirestoreCallback callback) {
        CollectionReference medicationsRef = db.collection("medications");
        Query query = medicationsRef.whereEqualTo("name", name);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String description = document.getString("description");
                        Boolean adaptation = document.getBoolean("adaptation");
                        String id = document.getId();
                        callback.onSuccess(id, name, description, adaptation);
                        // Assuming there is only one medication with the provided name,
                        // you can break the loop after finding the first match.
                        return;
                    }
                    callback.onError("Medication with name " + name + " not found.");
                } else {
                    callback.onError("Failed to fetch medication data: " + task.getException().getMessage());
                }
            }
        });
    }

    public class FirestoreCallback {
        Medication onSuccess(String id, String name, String description, Boolean adaptation){
            return new Medication(id, name, description, adaptation, false, 0, 0);
        }
        void onError(String errorMessage){
            Log.w("DIM", errorMessage);
        }
    }
}