package com.patech.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.java.rssfeed.model.feed.Feed;
import com.patech.feedreader.FeedReaderApplication;
import com.patech.feedreader.MainActivity;
import com.patech.feedreader.R;

import static com.patech.utils.AppConstants.FEED_DATA;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class FeedDialog extends DialogFragment {

    private View dialogView;

    // while editing the feed, the feed will be set by callee and displayed
    private Feed feed = null;
    public interface FeedDialogInterface {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    private MainActivity mHandler;

    public static FeedDialog newInstance() {
        return new FeedDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mHandler = (MainActivity) activity;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_feed, null);

        EditText urlText = (EditText) dialogView.findViewById(R.id.url);
        urlText.setLongClickable(true);
        urlText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((FeedReaderApplication)activity.getApplication()).copyToClipboard((TextView) view, FEED_DATA);
                return true;
            }
        });
    }


    public void setFeedToDisplay(Feed feed) {
        this.feed = feed;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        if (this.feed != null) {
            EditText nameText = (EditText) dialogView.findViewById(R.id.name);
            EditText summaryText = (EditText) dialogView.findViewById(R.id.filterText);
            EditText urlText = (EditText) dialogView.findViewById(R.id.url);

            nameText.setText(this.feed.getName());
            summaryText.setText(this.feed.getDescription());
            urlText.setText(this.feed.getLink());
            urlText.setEnabled(false);
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mHandler.onDialogPositiveClick(FeedDialog.this);

                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mHandler.onDialogNegativeClick(FeedDialog.this);
                    }
                });
        return builder.create();
    }
}
