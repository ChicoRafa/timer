package rmr.kairos.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rmr.kairos.R;
import rmr.kairos.activities.LoginActivity;

/**
 * Fragmento que crea el menú de estilo BottomSheet en nuestra pantalla principal
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class PreferenceFragment extends BottomSheetDialogFragment {
    private BottomSheetListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_preference, container, false);
        TextView opLogin = v.findViewById(R.id.opLogin);
        TextView opAjustes = v.findViewById(R.id.opAjustes);
        TextView opEstadistica = v.findViewById(R.id.opEstadistica);
        TextView opEtiqueta = v.findViewById(R.id.opEtiqueta);
        opAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBottomSheetClicked(1);
                dismiss();
            }
        });
        opEstadistica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBottomSheetClicked(2);
                dismiss();
            }
        });
        opEtiqueta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBottomSheetClicked(3);
                dismiss();
            }
        });
        opLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.onBottomSheetClicked(4);
               dismiss();
            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void onBottomSheetClicked(int op);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "Debe implementar la interfaz BottomSheetListener");
        }
    }
}