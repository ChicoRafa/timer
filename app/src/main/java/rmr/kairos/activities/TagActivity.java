package rmr.kairos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rmr.kairos.R;
import rmr.kairos.adapters.TagAdapter;
import rmr.kairos.database.KairosDB;
import rmr.kairos.model.Tag;

/**
 * Actividad que permite crear etiquetas para los pomodoros a realizar
 *
 * @author Rafa M.
 * @version 1.0
 */
public class TagActivity extends AppCompatActivity {
    private RecyclerView lvTag;
    private ImageView imBack;
    private ImageView imNewTag;
    private KairosDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        lvTag = findViewById(R.id.lvTags);
        imBack = findViewById(R.id.imBack);
        imNewTag = findViewById(R.id.imNewTag);
        ArrayList<Tag> listaEtiquetas = new ArrayList<>();
        lvTag.setLayoutManager(new LinearLayoutManager(this));
        db = new KairosDB(TagActivity.this);
        TagAdapter tagAdapter = new TagAdapter(db.selectTags(), new TagAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Tag tag) {
                Toast.makeText(TagActivity.this, "la etiqueta "+tag.getTagName()+" ha sido pulsada"
                        , Toast.LENGTH_SHORT).show();
                editTag(tag.getTagName());
            }
        });
        lvTag.setAdapter(tagAdapter);


        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToMain = new Intent(getApplicationContext(), rmr.kairos.activities.MainActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });
        imNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagDialog();
            }
        });

    }

    public void showTagDialog() {
        AlertDialog.Builder tagBuilder = new AlertDialog.Builder(TagActivity.this);
        View tagView = getLayoutInflater().inflate(R.layout.dialog_tag, null);
        TextView tvDialogTag = tagView.findViewById(R.id.tvDialogTag);
        EditText etTagName = tagView.findViewById(R.id.etTagName);
        RadioGroup rgColores = tagView.findViewById(R.id.rgColores);
        ArrayList<RadioButton> buttonList = new ArrayList<RadioButton>();
        for (int i = 0; i < rgColores.getChildCount(); i++) {
            View bt = rgColores.getChildAt(i);
            if (bt instanceof RadioButton) {
                buttonList.add((RadioButton) bt);
            }
        }
        String[] colors = getResources().getStringArray(R.array.tagSpinnerArray);
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).setTextColor(Color.parseColor(colors[i]));
        }
        tagBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int radioId = rgColores.getCheckedRadioButtonId();
                RadioButton checkedButton = tagView.findViewById(radioId);
                int color = checkedButton.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                if (!etTagName.equals("")) {
                    long id = db.insertTag(etTagName.getText().toString(),checkedButton.getText().toString());
                    if (id > 0){
                        Toast.makeText(getApplicationContext(), R.string.strTagCreateT , Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                    else Toast.makeText(getApplicationContext(), R.string.strTagCreateF , Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagBuilder.setCancelable(true);
        tagBuilder.setView(tagView);
        AlertDialog dialog = tagBuilder.create();
        dialog.show();
        //return list;
    }
    public void editTag(String tagName){
        AlertDialog.Builder tagBuilder = new AlertDialog.Builder(TagActivity.this);
        View tagView = getLayoutInflater().inflate(R.layout.dialog_tag, null);
        TextView tvDialogTag = tagView.findViewById(R.id.tvDialogTag);
        EditText etTagName = tagView.findViewById(R.id.etTagName);
        etTagName.setText(tagName);
        Tag editTag = db.selectSingleTag(etTagName.getText().toString());
        RadioGroup rgColores = tagView.findViewById(R.id.rgColores);
        ArrayList<RadioButton> buttonList = new ArrayList<RadioButton>();
        for (int i = 0; i < rgColores.getChildCount(); i++) {
            View bt = rgColores.getChildAt(i);
            if (bt instanceof RadioButton) {
                buttonList.add((RadioButton) bt);
                if (((RadioButton) bt).getText().toString().equals(editTag.getTagColor().toString())){
                    ((RadioButton) bt).setChecked(true);
                }
            }
        }
        String[] colors = getResources().getStringArray(R.array.tagSpinnerArray);
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).setTextColor(Color.parseColor(colors[i]));
        }
        tagBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int radioId = rgColores.getCheckedRadioButtonId();
                RadioButton checkedButton = tagView.findViewById(radioId);
                int color = checkedButton.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                if (!etTagName.equals("")) {
                    boolean correcto = db.updateTag(editTag.getId(), etTagName.getText().toString(),checkedButton.getText().toString());
                    if (correcto){
                        Toast.makeText(getApplicationContext(), R.string.strTagCreateT , Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                    else Toast.makeText(getApplicationContext(), R.string.strTagCreateF , Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagBuilder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean correcto = db.deleteTag(editTag.getId());
                if (correcto){
                    Toast.makeText(getApplicationContext(), R.string.strDeleteTagT  , Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
                else Toast.makeText(getApplicationContext(), R.string.strDeleteTagF  , Toast.LENGTH_SHORT).show();
            }
        });
        tagBuilder.setCancelable(true);
        tagBuilder.setView(tagView);
        AlertDialog dialog = tagBuilder.create();
        dialog.show();
    }
}