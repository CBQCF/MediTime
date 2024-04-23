package cbqcf.dim.meditime;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchResult extends LinearLayout {
    private LinearLayout layout;
    private TextView nameView, descView;
    private Medication result;

    public SearchResult(Context context, Medication result) {
        super(context);
        this.result = result;
        initializeUI(context);
        layout = findViewById(R.id.ResLayout);
        layout.setOnClickListener(v -> onClick());
    }

    private void initializeUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.searchresult, this, true);
        nameView = findViewById(R.id.res_name);
        descView = findViewById(R.id.res_description);

        nameView.setText(result.getName());
        descView.setText(result.getDescription());
    }

    public void onClick() {
        Intent intent = new Intent(getContext(), EditMedicationActivity.class);
        intent.putExtra("MEDICATION_NAME", result.getName());
        intent.putExtra("MEDICATION_DESCRIPTION", result.getDescription());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
