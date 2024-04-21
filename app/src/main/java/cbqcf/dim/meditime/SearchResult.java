package cbqcf.dim.meditime;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchResult extends LinearLayout {
    private TextView nameView, descView;
    private Medication result;

    public SearchResult(Context context, Medication result) {
        super(context);
        this.result = result;
        initializeUI(context);
    }

    private void initializeUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.searchresult, this, true);
        nameView = findViewById(R.id.res_name);
        descView = findViewById(R.id.res_description);

        nameView.setText(result.getName());
        descView.setText(result.getDescription());
    }
}
