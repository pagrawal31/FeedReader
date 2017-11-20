package com.patech.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.patech.feedreader.MainActivity;
import com.patech.feedreader.R;

/**
 * Created by pagrawal on 31-10-2017.
 */

public class AddFilterDialog extends DialogFragment {

    public interface AddFilterInterface {
        public void onDialogFilterPositiveClick(DialogFragment dialog);
        public void onDialogFilterNegativeClick(DialogFragment dialog);
    }

    private MainActivity mHandler;

    public static AddFilterDialog newInstance() {
        return new AddFilterDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandler = (MainActivity) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_add_filter, null))
                // Add action buttons
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...

                        mHandler.onDialogFilterPositiveClick(AddFilterDialog.this);
//                        Toast.makeText(getActivity(), "OK Pressed", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        LoginDialogFragment.this.getDialog().cancel();
//                        Toast.makeText(getActivity(), "Cancel Pressed", Toast.LENGTH_SHORT).show();
                        mHandler.onDialogFilterNegativeClick(AddFilterDialog.this);
                    }
                });
        return builder.create();
    }

}
