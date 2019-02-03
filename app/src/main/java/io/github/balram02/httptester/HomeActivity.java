package io.github.balram02.httptester;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private Spinner methodSpinner;
    private int methodType;
    private TextView responseContent;
    private EditText editTextUrl;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        responseContent = (TextView) findViewById(R.id.response_content);
        editTextUrl = (EditText) findViewById(R.id.edit_text_url);

        radioGroup = (RadioGroup) findViewById(R.id.radio_grp);

        methodSpinner = (Spinner) findViewById(R.id.request_method);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.spinner_item, getResources().getStringArray(R.array.methods));
        methodSpinner.setAdapter(arrayAdapter);

        methodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                methodType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                methodType = 0;
            }
        });

        fab.setOnClickListener(view -> {
            String link = editTextUrl.getText().toString();
            if (link != null && !link.isEmpty()) {

                if (!link.startsWith("http://") || !link.startsWith("https://")) {
                    int id = radioGroup.getCheckedRadioButtonId();
                    if (id == R.id.radio_http) {
                        link = "http://" + link;
                    } else {
                        link = "https://" + link;
                    }
                }
                RequestServer requestServer = new RequestServer(this, link, null, null);
                requestServer.execute();
            } else
                Toast.makeText(this, "enter url", Toast.LENGTH_SHORT).show();
        });
    }


    private static class RequestServer extends AsyncTask<Void, Void, String> {

        private ProgressDialog progressDialog;
        private WeakReference<HomeActivity> weakReference;
        private String link;
        private HashMap<String, String> headersMap;
        private HashMap<String, String> bodyMap;

        RequestServer(HomeActivity activity, String link, HashMap<String, String> headersMap, HashMap<String, String> bodyMap) {
            this.weakReference = new WeakReference<>(activity);
            this.link = link;
            this.headersMap = headersMap;
            this.bodyMap = bodyMap;
        }

        @Override
        protected void onPreExecute() {
            Activity activity = weakReference.get();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String data) {
            Activity activity = weakReference.get();
            try {
                if (data.startsWith("{") || data.startsWith("["))
                    ((HomeActivity) activity).responseContent.setText(new JSONObject(data).toString(5));
                else
                    ((HomeActivity) activity).responseContent.setText(data);
                progressDialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Activity activity = weakReference.get();
                UrlRequestHandler requestHandler = new UrlRequestHandler(link);
                requestHandler.setRequest(((HomeActivity) activity).methodType);
                requestHandler.setHeaders(headersMap);
                requestHandler.setBody(bodyMap);
                return requestHandler.getServerResponse();
            } catch (Exception e) {
                Log.d("TESTER-TAG", "exception" + e);
                return "NULL";
            }
        }
    }
}
