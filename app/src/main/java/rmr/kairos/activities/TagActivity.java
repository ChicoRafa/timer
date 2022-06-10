package rmr.kairos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import rmr.kairos.R;
import rmr.kairos.adapters.TagAdapter;
import rmr.kairos.model.Tag;

/**
 * Actividad que permite crear etiquetas para los pomodoros a realizar
 * @author Rafa M.
 * @version 1.0
 */
public class TagActivity extends AppCompatActivity {
    private ListView lvTag;
    private ImageView imBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        lvTag = findViewById(R.id.lvTags);
        imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToMain = new Intent(getApplicationContext(), rmr.kairos.activities.MainActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });
        newTagList(lvTag);
    }
    public TagAdapter newTagList(ListView lvTag){
        ArrayList<Tag> listaEtiquetas = new ArrayList<Tag>();
        //introducir los valores con un diaálogo o algo
        listaEtiquetas.add(new Tag("prueba", "#FFFFFF"));
        TagAdapter tagAdapter = new TagAdapter(this, R.layout.layout_tag, listaEtiquetas);
        lvTag.setAdapter(tagAdapter);
        return tagAdapter;
    }
}