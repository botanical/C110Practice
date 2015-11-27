package com.example.jennifertran.cse110practice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emd_000 on 11/9/2015.
 *
 * Helper class designed to pass queries to remote server. Must be called
 * from an Async Task.
 */
public class RemoteDBHelper {

    private JSONParser jsonParser = new JSONParser();

    //CAN ONLY BE CALLED FROM AN ASYNC TASK
    public String queryRemote(String password, String query, String loginUrl){
        try{
            Map<String,String> params = new HashMap<>();
            //Auth is used by the server php file to determine whether it's being accessed
            //from an authorized source.
            params.put("auth", password);
            //Queries the database, in this case searching for the given username and its
            //associated info.
            params.put("query", query);
            JSONArray ar = jsonParser.makeHttpRequest(loginUrl, "POST", params);
            System.out.println("Params is " + params);
            if(ar == null)
                return "";

            //JSONObject json = ar.getJSONObject(0); //Get first row
            //if(json == null)
              //  return "";
            return ar.toString();

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /*In a table which has a dynamic number of columns, findIndexOfEntry finds the index of a given
     *entry where the index begins at the first of the dynamic columns.
     *Example: Table = (question, answer, option0, option1, option2, option3, userSelectedAnswer)
     * The function searches through the option entries looking for the given entry in a given row
     * So if for the given row option1=entry, the function returns 1;
     */

    public int findIndexOfEntry(String table, String entry, String uniqueColumnValue, String uniqueColumn,
                                String variableColumnPrefix,
                                String auth, String url)
    {
        String wTable = "`"+table+"`";
        String query = "SELECT * FROM "+wTable+" WHERE "+uniqueColumn+"='"+uniqueColumnValue+"'";
        String result = queryRemote(auth,query,url);
        try{
            JSONArray resultTable = new JSONArray(result);
            JSONObject row = resultTable.getJSONObject(0);
            for(int i = 0; i < row.length(); i++){
                String tmpEntry = row.getString((variableColumnPrefix+i));
                if(tmpEntry != null && tmpEntry.equals(entry))
                {
                    return i;
                };
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return -1; //if entry doesn't exist
    }
}
