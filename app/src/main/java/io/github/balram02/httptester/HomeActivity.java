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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private Spinner methodSpinner;
    private int methodType;
    private TextView responseContent;
    private EditText editTextUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        methodSpinner = (Spinner) findViewById(R.id.request_method);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.spinner_item, getResources().getStringArray(R.array.methods));
        methodSpinner.setAdapter(arrayAdapter);

        responseContent = (TextView) findViewById(R.id.response_content);
        editTextUrl = (EditText) findViewById(R.id.edit_text_url);

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
                ((HomeActivity) activity).responseContent.setText(data);
                progressDialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
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








/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
