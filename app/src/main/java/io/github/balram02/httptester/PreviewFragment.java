package io.github.balram02.httptester;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PreviewFragment extends Fragment implements ResponseListener {


    private WebView webView;

    public PreviewFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        HomeActivity.responsePreview = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_preview, container, false);

        webView = (WebView) v.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return false;
            }
        });
        return v;
    }

    @Override
    public void setResponse(String response) {
        webView.loadData(response, "", "UTF-8");
    }
}
