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