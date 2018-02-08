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
import android.widget.ImageButton;
import android.widget.TextView;

import com.java.rssfeed.model.feed.Feed;
import com.patech.feedreader.FeedReaderApplication;
import com.patech.feedreader.MainActivity;
import com.patech.feedreader.R;
import com.patech.utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.patech.utils.AppConstants.FEED_DATA;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class FeedDialog extends DialogFragment {

    private View dialogView;
    @BindView(R.id.name) EditText nameText;
    @BindView(R.id.filterText) EditText summaryText;
    @BindView(R.id.url) EditText urlText;
    @BindView(R.id.copy_to_clipboard_btn) ImageButton copyToClipBoardBtn;

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
        ButterKnife.bind(this, dialogView);
    }

    public void setFeedToDisplay(Feed feed) {
        this.feed = feed;
    }

    @OnClick(R.id.copy_to_clipboard_btn)
    void copyToClipBoardBtnClick(View view) {
        if (this.feed != null) {
            String link = this.feed.getLink();
            ((FeedReaderApplication) getActivity().getApplication()).copyToClipboard(link, AppConstants.URL_LINK);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        if (this.feed != null) {
            copyToClipBoardBtn.setVisibility(View.VISIBLE);

            nameText.setText(this.feed.getTitle());
            summaryText.setText(this.feed.getDescription());
            urlText.setText(this.feed.getLink());
            urlText.setEnabled(false);
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialog, int id) {}
                    })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });
        }
        else {

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
        }
        return builder.create();
    }
}
