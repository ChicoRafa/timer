package rmr.kairos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import rmr.kairos.R;
import rmr.kairos.model.Tag;

/**
 * Clase adaptador de las etiquetas
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.UsuarioViewHolder> {
    private Context activity;
    ArrayList<Tag> listaEtiquetas;
    private ItemClickListener mItemListener;
    public TagAdapter(ArrayList<Tag> listaEtiquetas, ItemClickListener mItemListener) {
       this.listaEtiquetas = listaEtiquetas;
       this.mItemListener = mItemListener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tag, null,
                false);
        return  new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        holder.tvTagTitle.setText("Nombre: "+listaEtiquetas.get(position).getTagName());
        holder.tvTagColor.setText("Color: "+listaEtiquetas.get(position).getTagColor());
        //holder.tvTagColor.setTextColor(listaEtiquetas.get(position).getTagColorCode());
        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(listaEtiquetas.get(position));
        });
    }


    @Override
    public int getItemCount() {
        return listaEtiquetas.size();
    }

    public interface  ItemClickListener{
        void onItemClick(Tag tag);
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagTitle;
        TextView tvTagColor;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagTitle = itemView.findViewById(R.id.tvTagTitle);
            tvTagColor = itemView.findViewById(R.id.tvTagColor);
        }
    }
}