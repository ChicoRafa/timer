package rmr.kairos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //newTagList(lvTag);
        listaEtiquetas();
    }

    public TagAdapter newTagList(ListView lvTag, ArrayList<String> listaDatos) {
        String[] colorsCode = getResources().getStringArray(R.array.tagSpinnerArray);
        String[] colorsName = getResources().getStringArray(R.array.tagSpinnerColor);
        LinkedHashMap<String, String> mapaColores = new LinkedHashMap<String, String>();
        for (int i = 0; i < colorsName.length; i++) {
            mapaColores.put(colorsCode[i], colorsName[i]);
        }
        ArrayList<Tag> listaEtiquetas = new ArrayList<Tag>();

        //introducir los valores con un diálogo o algo
        for (int i = 1; i < listaDatos.size(); i++) {
            String colorEtiqueta = mapaColores.get(listaDatos.get(i+1));
            listaEtiquetas.add(new Tag(listaDatos.get(i), colorEtiqueta));
            i++;
        }
        TagAdapter tagAdapter = new TagAdapter(this, R.layout.layout_tag, listaEtiquetas);
        lvTag.setAdapter(tagAdapter);

        return tagAdapter;
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
            Log.d("RMRTAG", "run: " + colors[i]);
        }

        tagBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int radioId = rgColores.getCheckedRadioButtonId();
                RadioButton checkedButton = tagView.findViewById(radioId);
                int color = checkedButton.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                //Log.d("XXXTAGXXX", "onClick: "+etTagName.getText() + " y " + hexColor);
                if (!etTagName.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];
                            field[0] = "nombre_tag";
                            field[1] = "color_tag";
                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = etTagName.getText().toString();
                            data[1] = hexColor;
                            PutData putData = new PutData("http://192.168.56.1/login/LogIn-SignUp-master/LogIn-SignUp-master/setTag.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Tag Creation Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                        //llamada al método add del listView
                                    } else
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                    //End ProgressBar (Set visibility to GONE)
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                } else
                    Toast.makeText(getApplicationContext(), R.string.strAllFields, Toast.LENGTH_SHORT).show();
                listaEtiquetas();
            }
        });
        tagBuilder.setCancelable(true);
        tagBuilder.setView(tagView);
        AlertDialog dialog = tagBuilder.create();
        dialog.show();
    }

    public void listaEtiquetas() {
        ArrayList<String> list = new ArrayList<>();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                FetchData fetchData = new FetchData("http://192.168.56.1/login/LogIn-SignUp-master/LogIn-SignUp-master/getTag.php");
                if (fetchData.startFetch()) {
                    if (fetchData.onComplete()) {
                        Pattern pName = Pattern.compile("nombre_tag");
                        Matcher m = pName.matcher(fetchData.getData().replaceAll("  ", ""));
                        int n = 0;
                        String cleanerData = fetchData.getData().replaceAll("  ", "");
                        cleanerData = cleanerData.replaceAll("Array", "");
                        cleanerData = cleanerData.replaceAll("=> ", "");
                        cleanerData = cleanerData.replaceAll("\\[[0-9]", "")
                                .replaceAll("\\[", " ")
                                .replaceAll("]", "")
                                .replaceAll("\\(", "").replaceAll("\\)", "")
                                .replaceAll("nombre_tag", "")
                                .replaceAll("color_tag", "");
                        while (m.find()) {
                            n++;
                        }
                        for (int i = 0; i <= n * 2; i++) {
                            String tagNames[] = cleanerData.split("  ");
                            list.add(tagNames[i]);
                        }
                        newTagList(lvTag, list);
                    }
                }
                //End Write and Read data with URL
            }
        });
        //return list;
    }
}