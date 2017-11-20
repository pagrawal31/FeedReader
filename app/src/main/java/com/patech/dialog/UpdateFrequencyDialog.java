package com.patech.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.patech.feedreader.MainActivity;
import com.patech.feedreader.R;

/**
 * Created by pagrawal on 18-11-2017.
 */

public class UpdateFrequencyDialog extends DialogFragment {

    private MainActivity mHandler;
    private View dialogView;
    private long frequencyInMillis = 0;

    public interface UpdateFrequencyInterface {
        public void onFrequencyUpdatePositiveClick(DialogFragment fragment);
        public void onFrequencyUpdateNegativeClick(DialogFragment fragment);
    }

    public static UpdateFrequencyDialog newInstance() {
        return new UpdateFrequencyDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setFrequencyInMillis(long frequencyInMillis) {
        this.frequencyInMillis = frequencyInMillis;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandler = (MainActivity) context;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_frequency, null);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (this.frequencyInMillis != 0) {
            EditText nameText = (EditText) dialogView.findViewById(R.id.freqValue);
            nameText.setText("" + this.frequencyInMillis);
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mHandler.onFrequencyUpdatePositiveClick(UpdateFrequencyDialog.this);
//                        Toast.makeText(getActivity(), "OK Pressed", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mHandler.onFrequencyUpdateNegativeClick(UpdateFrequencyDialog.this);
                    }
                });
        return builder.create();
    }
}
