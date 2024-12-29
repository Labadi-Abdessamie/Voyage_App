package Server;

import BDD.Database_Manager;
import Messagerie_Service.*;

import java.sql.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class Server {
    static ServerSocket server;
    static Socket client;    
    static Database_Manager DBM;
    static ClientStructure user;
    static BufferedReader in;
    static PrintWriter out;
    
    
    private static void StartServer() throws IOException, SQLException{
        server = new ServerSocket(1236);
        System.out.println("\tServer Waiting to Connexion");
        //MessageStr.Countable = DBM.getIndex();
            
        client = server.accept();
        System.out.println("  Connexion done with Client");
            
        user = new ClientStructure();
        
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);
    }
    private static void CloseServer() throws IOException{
        in.close();
        out.close();
        client.close();
        System.exit(0x1);
    }
    private static void DisplayFlights() throws SQLException{
        ArrayList<Vols> Vols = DBM.DisplayAllVols();
        if(Vols.isEmpty()){
            out.println("NO_Flights");
            return;
        }
        out.println("Flights_Availables");
        for (Vols vol : Vols) {
            out.println("ID = "+vol.Num_Vol+" | Depart = "+vol.Depart+" | Arrive = "+vol.Arrive+" | DateDepart = "+vol.DateDepart+" | DateArrive = "+vol.DateArrive+" | EmptySeats = "+vol.seats);
        }
        out.println(".");
    }
    private static ArrayList<Integer> GetVolsIDs(ArrayList<Vols> Vols){
        ArrayList <Integer> Num_Vols = new ArrayList<>();
        for (Vols vol : Vols) {
            Num_Vols.add(vol.Num_Vol);
        }
        return Num_Vols;
    }
    private static void ReceiveClientInfo() throws IOException, SQLException{
        if("Sending_Client_Info_Canceled".equals(in.readLine())){
                CloseServer();
        }else{
            user.setFirstName(in.readLine());
            user.setLastName(in.readLine());
            user.setEmail(in.readLine());
        }
    }
    private static void VerificationClientExistance() throws SQLException, IOException{
        user.setID(DBM.ClientVerification(user.getFname(),user.getLname(),user.getEmail()));
        if(user.getID()==0){
            System.err.println("ERROR IN CLIENT VERIFICATION OR ADDING");
            out.println("Verification_Client_Error");
        }else{
            out.println("Verification_Client_Done");
            System.out.println("Client ID = "+user.getID());
        }
        
    }
    private static ArrayList<String> ReceiveFlightInfo() throws IOException{   
        ArrayList<String> FlightInfo = new ArrayList<>();
        // The Reservation Step
        FlightInfo.add(in.readLine());
        FlightInfo.add(in.readLine());
        FlightInfo.add(in.readLine());
        
        System.out.println("Depart : "+FlightInfo.get(0));
        System.out.println("Arrive : "+FlightInfo.get(1));
        System.out.println("Date : "+FlightInfo.get(2));
        
        return FlightInfo;
    }
    private static void Search_Reservation(ArrayList<String> FlightInfo) throws IOException, SQLException{
        ArrayList<Vols> PossibleVols = new ArrayList<>();
        PossibleVols = DBM.VolVerification(FlightInfo.get(0),FlightInfo.get(1),FlightInfo.get(2));
                
        if(PossibleVols.isEmpty()){
            out.println("We are Sorry, There is no plane in your demand.");
            out.println("Vols_List_Not_Found");                        
        }else{
            out.println("The list of the Possible Planes for You : ");
            out.println("Vols_List_Found");
            ArrayList <Integer> Num_Vols = new ArrayList<>();
            
            for (Vols vol : PossibleVols) {
                out.println("ID = "+vol.Num_Vol+" DateDepart = "+vol.DateDepart+" DateArrive = "+vol.DateArrive+" EmptySeats = "+vol.seats);
                Num_Vols.add(vol.Num_Vol);
            }
            out.println(".");
            for(Integer e : Num_Vols){
                out.println(e);
            }
            out.println("Vols_Ended");
            Reserve(0);
        }
    }
    private static void Reserve(int Methode) throws IOException, SQLException{
        if(Methode==1){
            for(Integer e : GetVolsIDs(DBM.DisplayAllVols())){
                out.println(e);
            }
            out.println("Vols_Ended");
        }
        String Number_Plane = in.readLine();
        if(!Number_Plane.equals("Reservation_Canceled")){
            String Today = (LocalDate.now()).toString();    
            if(DBM.Make_Reservation(user.getID(),Number_Plane,Today)){
                out.println("Reservation_Done");
                System.out.println("Reservation Done : "+ user.getID() +" -> " + Number_Plane);
            }
        }
/*        else{
            if(DBM.Have_Reservation(user.getID())){
                System.out.println("Sync Done");
            }else{
                System.err.println("Sync Error : Passager with no Reservation in the DB");
            }
               // CloseServer();
        }*/
    }
    private static void Inbox() throws SQLException, IOException{
        ConsumeApp UserMessages = new ConsumeApp(user.getID());
        ArrayList<MessageStr> Messages = new ArrayList<>();
        Messages = UserMessages.ReceiveMessage();
        
        if(!Messages.isEmpty()){
            out.println("Messages");
            String Sender;
            for(MessageStr e : Messages){
                Sender = DBM.SearchSender(e.getMessageSource());
                out.println(Sender +" : "+e.getContent());
            }
            out.println("Messages_Ended");
        }
        else{
            out.println("NO_Messages");
        }
    }
    private static boolean SendClientsList() throws SQLException, IOException{
        ArrayList<ArrayList<String>> Clients = DBM.GetClientsList(user.getID());
        ArrayList<String> IDs = new ArrayList<>();
        
        if(!Clients.isEmpty()){
            out.println("Clients");
            for(ArrayList<String> e : Clients){
                IDs.add(e.get(0)); //Integer.valueOf(e.get(0)));
                out.println(e.get(0) + ")- "+ e.get(1)+ " "+ e.get(2));
            }
            out.println("Clients_Ended");
            for(String e : IDs){
                out.println(e);
            }
            out.println("IDs_Ended");
            return true;
        }else {
            out.println("NO_Clients");
            return false;
        }
    }
    private static void OpenConversation(int Receiver) throws SQLException{
        ArrayList<ArrayList<String>> Messages = DBM.GetHistory(user.getID(),Receiver);
      
        if(!Messages.isEmpty()){
            out.println("Messages");
            for(ArrayList<String> e : Messages){
                if(e.get(0).equals(String.valueOf(user.getID()))){
                    out.println(e.get(2) + " | You : "+ e.get(1));
                }else{
                    out.println(e.get(2) + " | "+ e.get(1));
                }
            }
            out.println("Messages_Ended");
        }
        else{
            out.println("NO_Messages");
        }
    }
    private static void SendMessage(int Destination) throws IOException, SQLException{   
        ProduceApp producer;
        
        String Message = in.readLine();
        while(!Message.equals("0")){
            producer = new ProduceApp(user.getID(), Destination, Message);
            producer.SendMessage();
            if(DBM.SaveMessage(user.getID(),Destination,Message)!=1){
                System.err.println("This Message Is Not Saved ");
                System.out.println("Message : From : "+ user.getID()+" | To : " +Destination+" | Content : " + Message);
            }
            Message = in.readLine();
        }
    }
    
    public static void main(String[] args) throws SQLException {
        
            DBM = new Database_Manager();
            
            DBM.Connection();
                        
            try{
            
                StartServer();
                while(true){
                    switch (in.readLine()){
                        case "Display_Flights" : {
                            DisplayFlights(); 
                            break;
                        }
                        case "Logged_Out" :{
                            user.ResetClient();
                            break;
                        }
                        case "Login" :{
                            ReceiveClientInfo();
                            VerificationClientExistance();
                            break;
                        }
                        case "ReserveByNumber" :{
                            DisplayFlights();
                            Reserve(1);
                            break;
                        }
                        case "ReserveByInfos" :{
                            Search_Reservation(ReceiveFlightInfo());
                            break;
                        }
                        case "Inbox" :{
                            Inbox();
                            String ConnectTo;
                            String BackToClientList;
                            while(true){
                                if(SendClientsList()){
                                    ConnectTo =in.readLine(); 
                                    if(!"Back".equals(ConnectTo)){
                                        OpenConversation(Integer.parseInt(ConnectTo));
                                        BackToClientList = in.readLine();
                                        if(!"Back".equals(BackToClientList)){
                                            SendMessage(Integer.parseInt(ConnectTo));
                                        }
                                    }else{
                                        break;
                                    }
                                }else{
                                    break;
                                }
                            }
                            break;
                        }
                        case "Quit" : {
                            CloseServer();
                        }    
                    }
                }
            }
            catch(IOException e){
                System.out.println("Error in Server"+e);
            }
    }
}
