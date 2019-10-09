
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class ServerDB {
    //Create new data structure to store stock name and price
    HashMap<String,String> stocks = new HashMap<String,String>();

    public ServerDB(String csvFile){
        try {//Read the stock csv file that contain initial values of stocks & put that into data structure

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(",");
                stocks.put(item[0], item[1] + "," + item[2]);
            }

        }catch (IOException e){
            System.out.println(e);
        }

    }

    //Function for check whether stock search is contain or not
    public synchronized boolean isintheDB(String key){
        return stocks.containsKey(key);
    }

    //Function for update stock contain in data structure
    public synchronized void update(String key,String value){

        String[] oldValue = stocks.get(key).split(",");
        stocks.replace(key,stocks.get(key),oldValue[0]+","+value);

    }

    //Function for get current value of the given stock
    public synchronized float getCurrentPrice(String key){

        String[] value = stocks.get(key).split(",");
        return Float.parseFloat(value[1]);

    }

    //Function for get stock details to print in GUI
    public synchronized String getentry(String key){

        String[] value = stocks.get(key).split(",");
        return value[0]+" : "+value[1];

    }

}
