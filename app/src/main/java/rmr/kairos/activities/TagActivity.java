package rmr.kairos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import rmr.kairos.R;
import rmr.kairos.adapters.TagAdapter;
import rmr.kairos.model.Tag;

/**
 * Actividad que permite crear etiquetas para los pomodoros a realizar
 *
 * @author Rafa M.
 * @version 1.0
 */
public class TagActivity extends AppCompatActivity {
    private ListView lvTag;
    private ImageView imBack;
    private ImageView imNewTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        lvTag = findViewById(R.id.lvTags);
        imBack = findViewById(R.id.imBack);
        imNewTag = findViewById(R.id.imNewTag);
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
        newTagList(lvTag);
    }

    public TagAdapter newTagList(ListView lvTag) {
        ArrayList<Tag> listaEtiquetas = new ArrayList<Tag>();
        //introducir los valores con un diaálogo o algo
        listaEtiquetas.add(new Tag("prueba", "#FFFFFF"));
        TagAdapter tagAdapter = new TagAdapter(this, R.layout.layout_tag, listaEtiquetas);
        lvTag.setAdapter(tagAdapter);
        return tagAdapter;
    }

    public void showTagDialog() {
        AlertDialog.Builder tagBuilder = new AlertDialog.Builder(TagActivity.this);
        View tagView = getLayoutInflater().inflate(R.layout.dialog_tag, null);
        TextView tvDialogTad = tagView.findViewById(R.id.tvDialogTag);
        EditText etTagName = tagView.findViewById(R.id.etTagName);
        RadioGroup rgColores = tagView.findViewById(R.id.rgColores);
        ArrayList<RadioButton> buttonList = new ArrayList<RadioButton>();
        for (int i = 0; i < rgColores.getChildCount(); i++) {
            View bt = rgColores.getChildAt(i);
            if (bt instanceof RadioButton){
                buttonList.add((RadioButton)bt);
            }
        }
        String[] colors = getResources().getStringArray(R.array.tagSpinnerArray);
        for (int i = 0; i< buttonList.size(); i++) {
            buttonList.get(i).setTextColor(Color.parseColor(colors[i]));
        }
        tagBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        tagBuilder.setCancelable(true);
        tagBuilder.setView(tagView);
        AlertDialog dialog = tagBuilder.create();
        dialog.show();
    }
}