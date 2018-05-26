package noracee.ankidictionary.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import noracee.ankidictionary.R;

/**
 * Provides HTTP calls
 */

public class HttpManager implements Response.ErrorListener {
    private static final int ERROR_REQUEST = R.string.error_request;

    private RequestQueue queue;
    private StatusManager statusManager;

    public HttpManager(Context context, StatusManager statusManager) {
        this.statusManager = statusManager;

        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        statusManager.error(ERROR_REQUEST);
        error.printStackTrace();
    }

    /**
     * Performs a HTTP GET request with given URL and response listener
     * @param url url to perform request
     * @param listener response listener to trigger when response is received
     */
    public void get(String url, Response.Listener<String> listener) {
        StringRequest request = new StringRequest(Request.Method.GET, url, listener,this);
        queue.add(request);
    }
}
