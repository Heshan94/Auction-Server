
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements Runnable {
    private static ServerSocket serverSocket;
    private Socket connectionSocket;

    public String name;
    public String stock;
    public String newBid;
    public static ServerDB db;


    private static JButton array0;
    private static JButton array1;
    private static JButton array2;
    private static JButton array3;
    private static JButton array4;
    private static JButton array5;
    private static JButton array6;
    private static JButton array7;
    private static TextField array8;

    private static SearchDB searchDB;
    private static JFrame frame;
    private static Timer timer;

    public Server(int socket) throws IOException {
        //  Create server database instant
        db = new ServerDB("stocks.csv");

        //Create new server socket to listen when client connect
        serverSocket = new ServerSocket(socket);

        //Create GUI to display stock prices
        frame = new JFrame("Stock price");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 240);
        frame.setLayout(new GridLayout(9, 1));

        array0 = new JButton(db.getentry("FB"));
        array1 = new JButton(db.getentry("VRTU"));
        array2 = new JButton(db.getentry("MSFT"));
        array3 = new JButton(db.getentry("GOOGL"));
        array4 = new JButton(db.getentry("YHOO"));
        array5 = new JButton(db.getentry("XLNX"));
        array6 = new JButton(db.getentry("TSLA"));
        array7 = new JButton(db.getentry("TXN"));
        //Create Text Field for search query updates
        array8 = new TextField();

        //Create Action Listener for update GUI stock prices
        updateFrame update = new updateFrame();

        //Create timer to update happen within 500msec
        timer = new Timer(500,update);

        //Create new instance of query database
        searchDB = new SearchDB();

        //Action listener for search box
        Search search = new Search(array8);

        array8.addActionListener(search);
        frame.add(array0);
        frame.add(array1);
        frame.add(array2);
        frame.add(array3);
        frame.add(array4);
        frame.add(array5);
        frame.add(array6);
        frame.add(array7);
        frame.add(array8);
        frame.setVisible(true);
        timer.start();
    }

    //Function for create new socket to accepted client
    public Server(Socket socket){

        this.connectionSocket = socket;

    }

    //Function for accept client and create thread for each client
    public void server_loop() throws IOException{

        while (true){

            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new Server(socket));
            worker.start();

        }
    }

    @Override
    public void run() {//run function for Thread
        try{
            //create reader & writer for read and write client side
            BufferedReader in = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(this.connectionSocket.getOutputStream()));

            //Read name and stock going to bid
            this.name = in.readLine();
            this.stock = in.readLine();

            //check whether stock is contain in database if it contain write the current value of stock in client side
            // if not in database display -1 on client side
            if(db.isintheDB(stock)){
                float value = db.getCurrentPrice(stock);
                out.printf(value+"\n" );
                out.flush();
                this.newBid = in.readLine();
                float bid = Float.parseFloat(newBid);
                if(bid > value) {//Update happen only when new Bid value is higher than old value
                    db.update(stock, newBid);
                    //Add query for Query database
                    if (searchDB.isintheQueryDB(stock)) {
                        searchDB.updatequeryDB(this.stock, this.name + " : " + this.newBid);
                    } else {
                        searchDB.addnewEntry(this.stock, this.name + " : " + this.newBid);
                    }
                }else {//If new Bid value is less than old value display -1 in client side
                    out.print(-1);
                    out.flush();
                }
            }else{
                out.print(-1);
                out.flush();
            }

        }catch (Exception e){
            System.out.println(e);
        }

        try{//close the client socket
            this.connectionSocket.close();
        }catch (IOException e){
            System.out.println(e);
        }
    }

    //Action Listener for update GUI
    public class updateFrame implements ActionListener{
        //Update all stocks contain in GUI
        @Override
        public void actionPerformed(ActionEvent e) {
            array0.setText(db.getentry("FB"));
            array1.setText(db.getentry("VRTU"));
            array2.setText(db.getentry("MSFT"));
            array3.setText(db.getentry("GOOGL"));
            array4.setText(db.getentry("YHOO"));
            array5.setText(db.getentry("XLNX"));
            array6.setText(db.getentry("TSLA"));
            array7.setText(db.getentry("TXN"));
        }
    }

    //Action Listener for Search text field
    public class Search implements ActionListener{

        private TextField search;
        private String searchText;

        public Search(TextField textField){
            search = textField;
        }

        //When search stock in the text field that search in the query database and print result in the text field
        @Override
        public void actionPerformed(ActionEvent e) {

            searchText = e.getActionCommand();

            if(searchDB.isintheQueryDB(searchText)) {
                search.setText(searchDB.display(searchText));
            }else if(db.isintheDB(searchText)){
                search.setText("No updates");
            }else {
                search.setText("Invalid stock key");
            }
        }
    }

}
