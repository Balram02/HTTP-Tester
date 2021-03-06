package io.github.balram02.httptester;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static android.view.View.VISIBLE;

public class HomeActivity extends AppCompatActivity {

    private Spinner methodSpinner;
    private int methodType;
    private EditText editTextUrl;
    private RadioGroup radioGroup;
    private AppBarLayout appBarLayout;
    private AppBarLayout appBarLayoutContent;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static ResponseListener responsePretty, responseRaw, responsePreview;

    private static Animation fadeOut = new AlphaAnimation(0f, 1f);
    private static Animation fadeIn = new AlphaAnimation(1f, 0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayoutContent = (AppBarLayout) findViewById(R.id.app_bar_content);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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

        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appBarLayoutContent.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appBarLayoutContent.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeIn.setDuration(800);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appBarLayoutContent.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appBarLayoutContent.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fab.setOnClickListener(view -> {
            String link = editTextUrl.getText().toString();
            if (link != null && !link.isEmpty()) {
                if (Patterns.WEB_URL.matcher(link).matches()) {
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
                } else {
                    Toast.makeText(this, "Enter a valid URl", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(this, "Please enter URL", Toast.LENGTH_SHORT).show();
        });
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            if (i == 0) {
                fragment = new PrettyFragment();
            } else if (i == 1) {
                fragment = new RawFragment();
            } else {
                fragment = new PreviewFragment();
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            if (position == 0)
                title = "Pretty";
            else if (position == 1)
                title = "Raw";
            else
                title = "Preview";
            return title;
        }

        @Override
        public int getCount() {
            return 3;
        }
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
                responsePretty.setResponse(data);
                responseRaw.setResponse(data);
                responsePreview.setResponse(data);
                ((HomeActivity) activity).appBarLayout.setExpanded(false, true);
                progressDialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(activity, "error" + e, Toast.LENGTH_SHORT).show();
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
