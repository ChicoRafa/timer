package rmr.kairos.dialogs;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashMap;

import rmr.kairos.R;
import rmr.kairos.activities.MainActivity;
import rmr.kairos.adapters.TagMainAdapter;
import rmr.kairos.database.KairosDB;
import rmr.kairos.model.Tag;

public class TagDialog extends AlertDialog {
    private RecyclerView rvTagMain;
    private KairosDB db;
    private MainActivity ma;
    private TextView tvTagMain;
    protected TagDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
         this.db = new KairosDB(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.tvTagMain = findViewById(R.id.tvTagMain);
        this.rvTagMain = findViewById(R.id.rvTagDialog);
        TagMainAdapter tagMainAdapter = new TagMainAdapter(db.selectTags(), new TagMainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Tag tag) {
                String[] colorsCode =   ma.getResources().getStringArray(R.array.tagSpinnerArray);
                String[] colorsName = ma.getResources().getStringArray(R.array.tagSpinnerColor);
                LinkedHashMap<String, String> mapaColores = new LinkedHashMap<String, String>();
                for (int i = 0; i < colorsName.length; i++) {
                    mapaColores.put(colorsName[i], colorsCode[i]);
                }
                tvTagMain.setTextColor(Color.parseColor(mapaColores.get(tag.getTagColor())));
                tvTagMain.setText(tag.getTagName());
                tvTagMain.setVisibility(View.VISIBLE);
            }
        });
        rvTagMain.setAdapter(tagMainAdapter);
    }


}