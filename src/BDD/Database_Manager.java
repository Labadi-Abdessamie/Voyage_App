package BDD;

import Server.Vols;
import Server.Server;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.ArrayList;


public class Database_Manager {
    Connection conn;
    Statement statement;
    String sql;
    ResultSet Result;
    
    
    public void Connection(){
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/airalgerie","root","");
        System.out.println("\tSystem connected To DataBase");
        
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Problem with connection To Database");
            System.exit(-1);
        }
    }
    
    public int ClientVerification(String Fname,String Lname,String Email) throws SQLException{
        statement = conn.createStatement();
        sql = "select Id_Client FROM Client where Nom='"+Lname+"' and Prenom='"+Fname+"' and Email='"+Email+"'";
        Result = statement.executeQuery(sql);
        if(!Result.isBeforeFirst()){
                // Adding not a Registered Client
                sql = "insert into client(Nom,Prenom,Email) VALUES ('" +Lname+ "','" +Fname+ "','" + Email +"')";
            int execute = statement.executeUpdate(sql);
            if(execute > 0){
                System.out.println("New Client Added Successfully");
                return ClientVerification(Fname, Lname, Email);
            }
            return 0;
        }else{
                // Returning the Id_Client
            Result.next();
            return Integer.parseInt(Result.getString(1));
        }
    }
    
    public ArrayList<Vols> DisplayAllVols() throws SQLException{
        ArrayList <Vols> Vols = new ArrayList<>();
        statement = conn.createStatement();
        sql = "SELECT * FROM vol";
        Result = statement.executeQuery(sql);
        
        if(!Result.isBeforeFirst()){
            return Vols;
        }
        else{
            while(Result.next()){
                if(Result.getInt("empty_seats")!=0){
                    int Num_vol= Result.getInt("Num_Vol");
                    int seats= Result.getInt("empty_seats");
                    String Depart = Result.getString("Depart");
                    String Arrive = Result.getString("Arrive");
                    String DateDepart= Result.getString("DateDepart");
                    String DateA= Result.getString("DateArrive");
                    Vols.add(new Vols(Num_vol,Depart,Arrive,DateDepart,DateA,seats));
                }    
            }
            return Vols;
        }
    }
    
    public ArrayList<Vols> VolVerification(String origin,String destination,String DateD) throws SQLException{
        ArrayList <Vols> Vols = new ArrayList<>();
        statement = conn.createStatement();
        sql = "SELECT Num_Vol,DateDepart,DateArrive,empty_seats FROM vol WHERE Depart='"+origin+
                "' and Arrive='"+destination+"' and DateDepart>='"+DateD+"'";
        Result = statement.executeQuery(sql);
        if(!Result.isBeforeFirst()){
            return Vols;
        }
        else{
            while(Result.next()){
                if(Result.getInt("empty_seats")==0){
                    System.out.println("The plane Number "+Result.getInt("Num_Vol")+"is full");                    
                }
                else{
                    int Num_vol= Result.getInt("Num_Vol");
                    int seats= Result.getInt("empty_seats");
                    String DateDepart= Result.getString("DateDepart");
                    String DateA= Result.getString("DateArrive");
                    Vols.add(new Vols(Num_vol,origin,destination,DateDepart,DateA,seats));
                }
            }
            return Vols;
        }
    }
    
    public boolean Make_Reservation(int Id_passager,String Number_Plane,String Date) throws SQLException{
        statement = conn.createStatement();
    
        sql = "SELECT * FROM Client WHERE Id_client = '"+Id_passager+"'";
        Result = statement.executeQuery(sql);
        Result.next();
        sql = "INSERT INTO Passager (Id_Passager,Nom,Prenom,Email) VALUES ('"
                + Result.getInt("Id_Client")+"','"+Result.getString("Nom")+"','"+Result.getString("Prenom")+"','"
                + Result.getString("Email")+"')";
        
        int InsertedPassager = statement.executeUpdate(sql);
                
        sql = "INSERT INTO reservation (Num_Vol,Id_Passager,date_Reservation) VALUES ('"+Number_Plane+"','"+Id_passager+"','"+Date+"')";
        int InsertedRow = statement.executeUpdate(sql);
        
        sql = "UPDATE Vol Set empty_seats = empty_seats - 1 Where Num_Vol = "+Number_Plane;
        int execute = statement.executeUpdate(sql);
        
        return InsertedRow > 0 && execute > 0 && InsertedPassager > 0;
    }
    
    public boolean Have_Reservation(int Id_passager) throws SQLException{
        statement = conn.createStatement();
        sql = "Select Num_Vol FROM reservation WHERE Id_Passager ='"+Id_passager+"'";
        Result = statement.executeQuery(sql);
        if(!Result.isBeforeFirst()){
             sql = "DELETE FROM Passager WHERE Id_Passager ='"+Id_passager+"'";
            int execute = statement.executeUpdate(sql);
            return execute > 0;
        }
            return true;
    }
    
    /*public int getIndex() throws SQLException{
        statement = conn.createStatement();
        sql = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = 'historique' AND table_schema = 'airalgerie'";
        Result = statement.executeQuery(sql);
        Result.next();
        return Integer.parseInt(Result.getString(1));
    }*/
    public String SearchSender(int Id) throws SQLException {
        statement = conn.createStatement();
        sql = "SELECT Nom,Prenom FROM Client WHERE Id_Client='"+Id+"'";
        Result = statement.executeQuery(sql);
        Result.next();
        return (Result.getString("Nom")+" "+Result.getString("Prenom"));
    }
    public ArrayList<ArrayList<String>> GetClientsList(int Id_Conncted) throws SQLException{
        
        ArrayList<ArrayList<String>> Clients = new ArrayList<>();
        
        statement = conn.createStatement();
        sql = "SELECT * FROM Client WHERE Id_Client!='"+Id_Conncted+"'";
        Result = statement.executeQuery(sql);
        
        if(!Result.isBeforeFirst()){
            return Clients;
        }
        else{
            while(Result.next()){
                ArrayList<String> Client = new ArrayList<>();
                    Client.add(String.valueOf(Result.getInt("Id_Client")));
                    Client.add(Result.getString("Nom"));
                    Client.add(Result.getString("Prenom"));
                    Clients.add(Client);
            }
            return Clients;
        }        
    }
    public ArrayList<ArrayList<String>> GetHistory(int Client_1,int Client_2) throws SQLException{
        ArrayList<ArrayList<String>> OldMessages = new ArrayList<>();
        
        statement = conn.createStatement();
        sql = "SELECT Sender,Content,SentDate FROM Historique WHERE (Sender='"
                + Client_1 +"' and Receiver ='"+Client_2+"')"
                + " OR (Sender='"+Client_2+"' and Receiver ='" +Client_1+"')";
        Result = statement.executeQuery(sql);
        
        if(!Result.isBeforeFirst()){
            return OldMessages;
        }
        else{
            while(Result.next()){
                ArrayList<String> Message = new ArrayList<>();
                    Message.add(Result.getString("Sender"));
                    Message.add(Result.getString("Content"));
                    Message.add(Result.getTimestamp("SentDate").toString());
                    OldMessages.add(Message);
            }
            return OldMessages;
        }
    }
    public int SaveMessage(int Source,int Destination,String Content) throws SQLException{
        statement = conn.createStatement();
        sql = "INSERT INTO historique (Sender,Receiver,Content) VALUES ('"+Source+"','"+Destination+"','"+Content+"')";
        int InsertedRow = statement.executeUpdate(sql);
        return InsertedRow;
    }
}