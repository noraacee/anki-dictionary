package noracee.ankidictionary.util;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import noracee.ankidictionary.R;

/**
 * Manages interaction with the status message bar
 */

public class StatusManager {
    // commands to the handler
    private static final int CMD_OPEN_TIMED = 0;
    private static final int CMD_OPEN = 1;
    private static final int CMD_CLOSE = 2;

    // duration to keep the status open
    private static final int DURATION_OPEN = 5000;

    private Handler handler;
    private ResourceManager resourceManager;

    private ImageView statusButton;
    private TextView statusView;
    private TextView titleView;

    public StatusManager(ResourceManager resourceManager, TextView titleView, ImageView statusButton,
                         TextView statusView) {
        this.resourceManager = resourceManager;
        this.titleView = titleView;
        this.statusButton = statusButton;
        this.statusView = statusView;

        handlerInit();
    }

    /**
     * Sends the command to close the status
     */
    public void closeStatus() {
        handler.sendEmptyMessage(CMD_CLOSE);
    }

    /**
     * Opens status as an error status
     * @param errorId id of the error message to show
     */
    public void error(int errorId) {
        error(errorId, null, null);
    }

    /**
     * Opens status as an error status
     * @param errorId id of the error message to show
     * @param before String, Integer, etc. to be appended before error message
     * @param after String, Integer, etc. to be appended after error message
     */
    public void error(int errorId, Object before, Object after) {
        if (before == null && after == null) {
            statusView.setText(errorId);
        } else {
            StringBuilder sb = new StringBuilder();
            if (before != null)
                sb.append(before);

            sb.append(resourceManager.getString(errorId));

            if (after != null)
                sb.append(after);

            statusView.setText(sb.toString());
        }

        statusView.setBackgroundColor(resourceManager.getColor(R.color.error));
        statusView.setTextColor(Color.WHITE);
        handler.sendEmptyMessage(CMD_OPEN_TIMED);
    }

    /**
     * Hides the status button without removing its spot in the layout
     */
    public void hideStatusButton() {
        statusButton.setVisibility(View.INVISIBLE);
    }

    public void setStatusButtonListener(View.OnClickListener listener) {
        statusButton.setOnClickListener(listener);
    }

    /**
     * Shows the status button
     */
    public void showStatusButton() {
        statusButton.setVisibility(View.VISIBLE);
    }

    /**
     * Opens status bar as a message
     * @param statusId id of the status to show
     */
    public void status(int statusId) {
        status(statusId, null, null);
    }

    /**
     * Opens status bar as a message
     * @param statusId id of the status to show
     * @param before String, Integer, etc. to be appended before status
     * @param after String, Integer, etc. to be appended after status
     */
    public void status(int statusId, Object before, Object after) {
        if (before == null && after == null) {
            statusView.setText(statusId);
        } else {
            StringBuilder sb = new StringBuilder();
            if (before != null)
                sb.append(before);

            sb.append(resourceManager.getString(statusId));

            if (after != null)
                sb.append(after);

            statusView.setText(sb.toString());
        }

        statusView.setBackgroundColor(resourceManager.getColor(R.color.loading));
        statusView.setTextColor(Color.WHITE);
        handler.sendEmptyMessage(CMD_OPEN);
    }

    /**
     * Sets the title of the status bar
     * @param titleId id of the title to show
     */
    public void title(int titleId) {
        titleView.setText(titleId);
    }

    /**
     * Sets the title of the status bar
     * @param titleId id of the title to show
     * @param before String, Integer, etc. to be appended before the title
     * @param after String, Integer, etc. to be appended after the title
     */
    public void title(int titleId, Object before, Object after) {
        StringBuilder sb = new StringBuilder();
        if (before != null)
            sb.append(before);

        sb.append(resourceManager.getString(titleId));

        if (after != null)
            sb.append(after);

        titleView.setText(sb.toString());
    }

    /**
     * Initializes the handler that controls closing the status bar
     */
    private void handlerInit() {
        // attaches handler to the main looper to make UI changes in main thread
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CMD_OPEN_TIMED:
                        handler.sendEmptyMessageDelayed(CMD_CLOSE, DURATION_OPEN);
                    case CMD_OPEN:
                        statusView.setVisibility(View.VISIBLE);
                        break;
                    case CMD_CLOSE:
                        statusView.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }
}
