package com.example.jennifertran.cse110practice;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by emd_000 on 10/30/2015.
 */
public class JSONParser {
    static InputStream is = null;
    static JSONArray jsonObj;
    static String json ="";

    public JSONParser(){}

    public JSONArray makeHttpRequest(String url, String method,
                                      Map<String, String> params)
    {

     if(method.equals("POST"))
     {
         try {

             URL web = new URL(url);
             HttpsURLConnection c = (HttpsURLConnection) web.openConnection();
             c.setRequestMethod("POST");
             c.setDoInput(true);
             c.setDoOutput(true);
             c.setUseCaches(false);
             c.setRequestProperty("Content-Type", "application/json");
            // c.setRequestProperty("Host", "com.example.jennifertran.cse110practice");
             c.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
             //c.setRequestProperty("User-Agent",System.getProperty("http.agent"));
             c.setRequestProperty("Accept-Charset","UTF-8");
             c.setRequestProperty("Accept", "*/*");
             c.connect();

             JSONObject jsonParam = new JSONObject();
             String key;
             String value;
             OutputStreamWriter writer = new OutputStreamWriter(c.getOutputStream());

             for(Map.Entry<String, String> entry:params.entrySet()) {
                 jsonParam.put(entry.getKey(), entry.getValue());
             }
             //Make Post request
             writer.write(jsonParam.toString());
             writer.flush();

             //Handle http response
             InputStreamReader r = new InputStreamReader((InputStream) c.getContent());
             BufferedReader buff = new BufferedReader(r);
             StringBuilder str = new StringBuilder();
             String inputLine;
             while((inputLine = buff.readLine()) != null)
             {
                 str.append(inputLine + "\n");
             }
             json = str.toString();
             //Php returns a JSONArray, where each entry represents a JSONObject encoding of a row.
             try {
                 JSONArray response = new JSONArray(json);
                 if(response.length() == 0)
                     jsonObj = null;
                 else
                     jsonObj = new JSONArray(json);
                 writer.close();
                 r.close();
                 return jsonObj;
             }catch(Exception e){
                 e.printStackTrace();
                 return null;
             }

         }catch (UnsupportedEncodingException e) {
             e.printStackTrace();
             return null;
         } catch (IOException e) {
             e.printStackTrace();
             return null;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }

     } else
        return null;
    }
}