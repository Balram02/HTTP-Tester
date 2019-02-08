package io.github.balram02.httptester;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrettyFragment extends Fragment implements ResponseListener {

    private TextView response_textView;

    public PrettyFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        HomeActivity.responsePretty = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pretty, container, false);

        response_textView = (TextView) v.findViewById(R.id.response_textView);

        return v;
    }

    @Override
    public void setResponse(String response) {
        try {
            response_textView.setText(new JSONObject(response).toString(4));
        } catch (Exception e1) {
            try {
                response_textView.setText(new JSONArray(response).toString(4));
            } catch (Exception e2) {
                response_textView.setText(response);
            }
        }
    }
}
