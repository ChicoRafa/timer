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


public class TagMainAdapter extends RecyclerView.Adapter<TagMainAdapter.TagViewHolder> {
    private Context activity;
    ArrayList<Tag> listaEtiquetas;
    private ItemClickListener mItemListener;

    public TagMainAdapter(ArrayList<Tag> listaEtiquetas, ItemClickListener mItemListener) {
        this.listaEtiquetas = listaEtiquetas;
        this.mItemListener = mItemListener;
    }

    @NonNull
    @Override
    public TagMainAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tag_main, null,
                false);
        return new TagViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TagMainAdapter.TagViewHolder holder, int position) {
        holder.tvTagTitle.setText("Nombre: " + listaEtiquetas.get(position).getTagName());
        holder.tvTagColor.setText("Color: " + listaEtiquetas.get(position).getTagColor());
        //holder.tvTagColor.setTextColor(listaEtiquetas.get(position).getTagColorCode());
        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(listaEtiquetas.get(position));
        });
    }


    @Override
    public int getItemCount() {
        return listaEtiquetas.size();
    }

    public interface ItemClickListener {
        void onItemClick(Tag tag);
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagTitle;
        TextView tvTagColor;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagTitle = itemView.findViewById(R.id.tvTagTitle);
            tvTagColor = itemView.findViewById(R.id.tvTagColor);
        }
    }
}