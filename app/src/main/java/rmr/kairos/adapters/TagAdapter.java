package rmr.kairos.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import rmr.kairos.R;
import rmr.kairos.model.Tag;

/**
 * Clase adaptador de las etiquetas
 * @author Rafa M.
 */
public class TagAdapter extends ArrayAdapter<Tag> {
    private Context activity;
    public TagAdapter(@NonNull Context context, int resource, @NonNull List<Tag> objects) {
        super(context, resource, objects);
        this.activity = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = View.inflate(this.activity, R.layout.layout_tag, null);
        }
        TextView tvTagTitle = convertView.findViewById(R.id.tvTagTitle);
        TextView tvTagColor = convertView.findViewById(R.id.tvTagColor);
        tvTagTitle.setText("Nombre: "+getItem(position).getTagName());
        tvTagColor.setText("Color: "+getItem(position).getTagColor());

        return convertView;
    }

}