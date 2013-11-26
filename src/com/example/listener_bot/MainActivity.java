package com.example.listener_bot;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

public class MainActivity extends Activity {

    private TextView textView;
    private String message = "NO_MESSAGE";

    public static final String PREFS_NAME = "MyPrefsFile";
    private int count;
    private ListView mainListView;
    private Button buttonClear;
    private ArrayAdapter<String> listAdapter ;
    private final String IPAddress = "http://10.146.229.248";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //registerGMS(this);

        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.main);

        textView = (TextView) findViewById(R.id.mainText);

        Button buttonStart = (Button)findViewById(R.id.buttonClear);
        buttonStart.setOnClickListener(startListener3); // Register the onClick listener with the implementation abov

        Button buttonTwoHits = (Button)findViewById(R.id.buttonTwoHits);
        buttonTwoHits.setOnClickListener(startListener2); // Register the onClick listener with the implementation abov

        List<String> messages = new ArrayList<String>();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        int j = 0;
        String temp_message;
        while(true){
            j++;
            temp_message = settings.getString("message"+j, null);
            if(temp_message == null)
                break;
            else{
            messages.add(temp_message);
            }
        }

        populateList(messages);
        textView.setText("Wireless Networking App");
    }

    protected void onStop(){
        super.onStop();
    }

    public void writeToCache(String message){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        count =settings.getInt("number_of_entries",0);
        SharedPreferences.Editor editor = settings.edit();

        if(message!=null && message!="NO_MESSAGE" && message != "no messages")
            count = count + 1;
        editor.putInt("number_of_entries",count);
        editor.putString("message"+count,message);
        editor.commit();
    }

    public void populateList(List<String> dataList){
        mainListView = (ListView) findViewById( R.id.mainListView );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, dataList);
        mainListView.setAdapter( listAdapter );
    }

    private OnClickListener startListener = new OnClickListener() {
        public void onClick(View v) {

            String html = makeAGetRequest("http://10.146.69.203/index1.html");

            List<String> dataList = new ArrayList<String>();
            dataList.add(0, html);
            populateList(dataList);

        }
    };

    private OnClickListener startListener2 = new OnClickListener() {
        public void onClick(View v) {


            List<String> dataList = new ArrayList<String>();

            HashMap<Integer,String> nameHash = new HashMap<Integer, String>();

            nameHash.put(0,"bot.jpeg");
            nameHash.put(1,"f1.png");
            nameHash.put(2,"f2.png");
            nameHash.put(3,"f3.png");
            nameHash.put(4,"img1.jpg");

            nameHash.put(5,"bot1.jpeg");
            nameHash.put(6,"f4.png");
            nameHash.put(7,"f5.png");
            nameHash.put(8,"f6.png");
            nameHash.put(9,"img2.jpg");

            try {
                for(Map.Entry<Integer,String> entry:nameHash.entrySet()){
                    String statusCode  = makeAGetRequest(IPAddress + ":4567/fetchB/" + entry.getValue());
                    if(statusCode.contains("302"))
                        dataList.add(0, "Resource Not modified");
                    else if(statusCode.contains("200"))
                        dataList.add(0, "Resource retrieved from server");
                    else
                        dataList.add(0, statusCode);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            populateList(dataList);
        }
    };

    private OnClickListener startListener3 = new OnClickListener() {
        public void onClick(View v) {

            List <String> dataList = new ArrayList<String>();

            ArrayList<Integer> files = new ArrayList<Integer>();
            HashMap<Integer,String> nameHash = new HashMap<Integer, String>();
            HashMap<String, String> urlHash = new HashMap<String, String>();

            files.add(R.drawable.bot);
            files.add(R.drawable.f1);
            files.add(R.drawable.f2);
            files.add(R.drawable.f3);
            files.add(R.drawable.img1);

            files.add(R.drawable.bot1);
            files.add(R.drawable.f4);
            files.add(R.drawable.f5);
            files.add(R.drawable.f6);
            files.add(R.drawable.img2);

            nameHash.put(0,"bot.jpeg");
            nameHash.put(1,"f1.png");
            nameHash.put(2,"f2.png");
            nameHash.put(3,"f3.png");
            nameHash.put(4,"img1.jpg");

            nameHash.put(5,"bot1.jpeg");
            nameHash.put(6,"f4.png");
            nameHash.put(7,"f5.png");
            nameHash.put(8,"f6.png");
            nameHash.put(9,"img2.jpg");

            try {
                int count = 0;
            for(Integer filename : files){
                InputStream is = getResources().openRawResource(filename);
                String hsh = MD5Checksum.getMD5Checksum(is);
                dataList.add(0, hsh);
                //System.out.println("The md5 hash " + MD5Checksum.getMD5Checksum(is));
                urlHash.put("image:" + count, hsh);
                count++;

            }

                String resourceStatus = makeAGetRequestWithHash(IPAddress + ":4567/fetch", urlHash);
                dataList.add(0, resourceStatus);

                //check the resources that need to be fetched
                String[] statusValues = resourceStatus.split(",");

                for(int i=0;i<statusValues.length;i++){
                    if(statusValues[i].equals("1")){
                        //fetch the corresponding resource
                        String statusCode = makeAGetRequest(IPAddress + "/fetchB/" + nameHash.get(i));
                        if(statusCode.contains("200"))
                            dataList.add(0, "Request Successful");
                        else
                            dataList.add(0, "Request Failed");
                    }
                }



            }catch (Exception e){
                e.printStackTrace();
            }
            populateList(dataList);



        }
    };

    private String makeAGetRequest(String url){
        String html = "";
        Integer statusCode = 0;
        try{
        HttpClient client = HttpClientFactory.getThreadSafeClient();
        HttpGet request = new HttpGet(url);
        request.addHeader("User-Agent","awesome thing from android app");
        HttpResponse response = client.execute(request);


        InputStream in = response.getEntity().getContent();
        statusCode = response.getStatusLine().getStatusCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line = null;
        while((line = reader.readLine()) != null)
        {
            str.append(line);
        }
        in.close();
        html = str.toString();}
       catch(Exception e){

       }
        return statusCode.toString();
    }

    private String makeAGetRequestWithHash(String url, HashMap<String, String> urlHash){
        System.out.println("MAKING REQUEST");

        //String html = "";

        String resourceStatus = "";

        try{
            HttpClient client = HttpClientFactory.getThreadSafeClient();
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent","awesome thing from android app");
            String headerString = "";
            for(String imurl : urlHash.keySet()){
                headerString += imurl+" "+urlHash.get(imurl)+"::";
            }
            request.addHeader("check-resource",headerString);
            HttpResponse response = client.execute(request);



            Header[] hdrs = response.getHeaders("resource-status");

            for(Header h:hdrs){
                resourceStatus = h.getValue();
                break;
            }

            /*
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
            {
                str.append(line);
            }
            in.close();
            html = str.toString();
            //System.out.println("Response: " + html);
            */

        }
        catch(Exception e){

        }
        return resourceStatus;
    }

}