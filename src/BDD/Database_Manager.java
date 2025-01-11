package BDD;

import Server.Vols;
import Server.Server;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.ArrayList;


public class Database_Manager {
    Connection conn;
    PreparedStatement sql;
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
        sql = conn.prepareStatement("SELECT Id_Client FROM Client where Nom=? AND Prenom=? AND Email=?");
            sql.setString(1, Lname);
            sql.setString(2, Fname);
            sql.setString(3, Email);
        Result = sql.executeQuery();
        
        if(!Result.isBeforeFirst()){
                // Adding not a Registered Client
            sql =conn.prepareStatement("INSERT INTO client(Nom,Prenom,Email) VALUES (?,?,?)");
                sql.setString(1, Lname);
                sql.setString(2, Fname);
                sql.setString(3, Email);
            int execute = sql.executeUpdate();
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
        sql = conn.prepareStatement("SELECT * FROM vol");
        Result = sql.executeQuery();
        
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
        
        sql = conn.prepareStatement("SELECT Num_Vol,DateDepart,DateArrive,empty_seats FROM vol WHERE Depart=? AND Arrive=? AND DateDepart>=?");
                sql.setString(1, origin);
                sql.setString(2, destination);
                sql.setString(3, DateD);
        Result = sql.executeQuery();
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
        
        sql = conn.prepareStatement("SELECT * FROM Client WHERE Id_client = ?");
            sql.setInt(1, Id_passager);
        Result = sql.executeQuery();
        Result.next();
        sql = conn.prepareStatement("INSERT INTO Passager (Id_Passager,Nom,Prenom,Email) VALUES (?,?,?,?)");
                sql.setInt(1, Result.getInt("Id_Client"));
                sql.setString(2, Result.getString("Nom"));
                sql.setString(3, Result.getString("Prenom"));
                sql.setString(4, Result.getString("Email"));
                
        int InsertedPassager = sql.executeUpdate();
                
        sql = conn.prepareStatement("INSERT INTO reservation (Num_Vol,Id_Passager,date_Reservation) VALUES (?,?,?)");
            sql.setString(1, Number_Plane);
            sql.setInt(2, Id_passager);
            sql.setString(3, Date);
        int InsertedRow = sql.executeUpdate();
        
        sql = conn.prepareStatement("UPDATE Vol Set empty_seats = empty_seats - 1 WHERE Num_Vol = ?");
            sql.setString(1, Number_Plane);
        int execute = sql.executeUpdate();
        
        return InsertedRow > 0 && execute > 0 && InsertedPassager > 0;
    }
    
    public boolean Have_Reservation(int Id_passager) throws SQLException{
        sql = conn.prepareStatement("SELECT Num_Vol FROM reservation WHERE Id_Passager =?");
            sql.setInt(1, Id_passager);
        Result = sql.executeQuery();
        if(!Result.isBeforeFirst()){
            sql = conn.prepareStatement("DELETE FROM Passager WHERE Id_Passager =?");
                sql.setInt(1, Id_passager);
            int execute = sql.executeUpdate();
            return execute > 0;
        }
            return true;
    }
    
    /*public int getIndex() throws SQLException{
        sql = conn.prepareStatement("SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = ? AND table_schema = ?");
            sql.setString(1,"historique");
            sql.setString(2,"airalgerie");
        Result = sql.executeQuery();
        Result.next();
        return Integer.parseInt(Result.getString(1));
    }*/
    public String SearchSender(int Id) throws SQLException {
        sql = conn.prepareStatement("SELECT Nom,Prenom FROM Client WHERE Id_Client=?");
            sql.setInt(1, Id);
        Result = sql.executeQuery();
        Result.next();
        return (Result.getString("Nom")+" "+Result.getString("Prenom"));
    }
    public ArrayList<ArrayList<String>> GetClientsList(int Id_Conncted) throws SQLException{
        
        ArrayList<ArrayList<String>> Clients = new ArrayList<>();
        
        sql = conn.prepareStatement("SELECT * FROM Client WHERE Id_Client!=?");
            sql.setInt(1, Id_Conncted);
        Result = sql.executeQuery();
        
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
        
        sql = conn.prepareStatement("SELECT Sender,Content,SentDate FROM Historique WHERE "
                + "(Sender=? AND Receiver =?) OR (Sender=? AND Receiver =?)");
                sql.setInt(1, Client_1);
                sql.setInt(2, Client_2);
                sql.setInt(3, Client_2);
                sql.setInt(4, Client_1);
        Result = sql.executeQuery();
        
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
        sql = conn.prepareStatement("INSERT INTO historique (Sender,Receiver,Content) VALUES (?,?,?)");
            sql.setInt(1, Source);
            sql.setInt(2, Destination);
            sql.setString(3, Content);
        int InsertedRow = sql.executeUpdate();
        return InsertedRow;
    }
}