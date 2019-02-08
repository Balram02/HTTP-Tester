package io.github.balram02.httptester;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RawFragment extends Fragment implements ResponseListener {

    private TextView response_textView;

    public RawFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        HomeActivity.responseRaw = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_raw, container, false);

        response_textView = (TextView) v.findViewById(R.id.response_textView);

        return v;
    }

    @Override
    public void setResponse(String response) {
        response_textView.setText(response);
    }
}
