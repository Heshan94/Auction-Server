

import java.util.HashMap;


public class SearchDB {
    //Hash map for track queries
    HashMap<String,String> query = new HashMap<>();

    //Function for add new stock Query to Hashmap
    public synchronized void addnewEntry(String key,String value){
        query.put(key,value);
    }

    //Function for check stock name contain in the Hashmap
    public synchronized boolean isintheQueryDB(String key){
        return query.containsKey(key);
    }

    //Function for update new query that stock contain in Query Hash map
    public synchronized void updatequeryDB(String key,String value){

        String oldValue = query.get(key);
        query.replace(key,query.get(key),oldValue+","+value);
    }

    //Function for display query history
    public synchronized String display(String key){
        return query.get(key);

    }
}
