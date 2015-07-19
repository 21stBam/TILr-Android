package com.abawaji.tilr;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private RelativeLayout relativeLayout;
    private Button showTILButton;
    private TextView tilLabel;
    private ColorWheel mColorWheel = new ColorWheel();
    private TIL mTIL;
    private int count = 1;
    private JSONObject innerDataObject;
    private static String TAG = "MAIN_ACTIVITY";
    private String mURL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Declare our View variables and assign the the Views from the layout file
        tilLabel = (TextView) findViewById(R.id.tilTextView);
        showTILButton = (Button) findViewById(R.id.showTILButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);


        String redditUrl = "http://www.reddit.com/r/todayilearned/.json";
        getJSONData(redditUrl);
        showTILButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(count < 25) {
                        updateScreen(mTIL.getTilArray().getJSONObject(count).getJSONObject("data"));
                        count++;
                    } else {
                        count = 0;
                        String redditUrl = "http://www.reddit.com/r/todayilearned/.json?count=25&after="+mTIL.getAfter();
                        Log.d(TAG, redditUrl);
                        getJSONData(redditUrl);
                    }
                } catch (JSONException e) {
                    alertUserAboutError();
                }
            }
        });

        tilLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mURL != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mURL));
                    startActivity(intent);
                    Log.d(TAG, "tilLabel is clicking");
                }
            }
        });

    }

    private void getJSONData(String redditUrl) {
        if (isNetworkAvailable()) {
            Log.d(TAG, "BeginningIfStatement");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(redditUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            mTIL = getTILDetails(jsonData);
                            JSONObject childrenObject = mTIL.getTilArray().getJSONObject(count);
                            innerDataObject = childrenObject.getJSONObject("data");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG,"runOnUiThreadWorking");
                                    try {
                                        updateScreen(innerDataObject);
                                        count++;
                                    }
                                    catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        alertUserAboutError();
                    } catch (JSONException e) {
                        alertUserAboutError();
                    }
                }
            });
        }
    }

    private void updateScreen(JSONObject object) throws JSONException {
        int color = mColorWheel.getColor();
        relativeLayout.setBackgroundColor(color);
        showTILButton.setTextColor(color);
        mURL = object.getString("url");
        try {
            tilLabel.setText(object.getString("title"));
        } catch (JSONException e) {
            alertUserAboutError();
        }

    }


    private TIL getTILDetails(String jsonData) throws JSONException{

        JSONObject redditObject = new JSONObject(jsonData);
        JSONArray childrenArray = redditObject.getJSONObject("data").getJSONArray("children");
        TIL innerTil = new TIL();
        innerTil.setAfter(redditObject.getJSONObject("data").getString("after"));
        innerTil.setTilArray(childrenArray);

        return innerTil;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvialable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvialable = true;
        }
        return isAvialable;
    }

    private void alertUserAboutError(){

        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

}
