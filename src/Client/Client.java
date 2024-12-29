package Client;

import Server.ClientStructure;
        
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Client {
    static Socket socket;
    static PrintWriter out;
    static BufferedReader in;
    static Scanner scanInt;
    static Scanner scanString;
    static ClientStructure Client;
    static boolean Logged_In = false;
    static int Service; 
    
    private static void StartApp() throws IOException{
        socket = new Socket("localhost",1236);
        out = new PrintWriter(socket.getOutputStream(),true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        scanInt = new Scanner(System.in);
        scanString = new Scanner(System.in);
        
        Client = new ClientStructure();
            
        System.out.println("\tWelcome To The AirAlgerie Reservation Site.");
        System.out.println("Please enter your information to continue to the reservation step.\n");    
    }
    private static void CloseApp() throws IOException{
            in.close();
            out.close();
            socket.close();
            System.exit(0);
    }
    private static int ChooseService(){
        System.out.println("Enter Name of Service : ");
        System.out.println("1- Display All Flights : ");
        if(Logged_In){
            System.out.println("2- Sign Out : ");
        }else{
            System.out.println("2- Login : ");
        }
        System.out.println("3- Reserve By PlaneNumber : ");
        System.out.println("4- Reserve By Form : ");
        System.out.println("5- Inbox : ");
        System.out.println("6- Quit : ");
        Service = scanInt.nextInt();
        
        if(Service == 3 && !Logged_In){
            System.out.println("You are not Logged In, Redirected to Login");
            return 2;
        }
        if(Service == 4 && !Logged_In){
            System.out.println("You are not Logged In, Redirected to Login");
            return 2;
        }
        if(Service == 5 && !Logged_In){
            System.out.println("You are not Logged In, Redirected to Login");
            return 2;
        }
        return Service;
    }
    private static void ReceiveFlightsInfo() throws IOException{
        String Vol;
        if("NO_Flights".equals(in.readLine())){
            System.out.println("No Flights Available !");
        }
        else{
            while(true){
                Vol = in.readLine();
                if(!Vol.equals(".")) System.out.println("  "+Vol); 
                else break;
            }
        }
    }
    private static void SendClientInfo() throws IOException{
        String msg;
            do{
                System.out.println(" Your First Name (Prenom) : ");
                Client.setFirstName(scanString.nextLine());

                System.out.println(" Your Last Name (Nom) : ");
                Client.setLastName(scanString.nextLine());

                System.out.println(" Your E-mail : ");
                Client.setEmail(scanString.nextLine());

                // Validating the Entered Information
                System.out.println(" Sure about the Information ?(Y/N  (0 = Annuler Completement)) ");
                msg = scanString.nextLine();
                
            }while(!msg.equals("y") && !msg.equals("Y") && !msg.equals("0"));
            if(msg.equals("0")) msg = "Sending_Client_Info_Canceled";
            out.println(msg);
            if(msg.equals("Sending_Client_Info_Canceled")){
                CloseApp();
            }else{
                out.println(Client.getFname());
                out.println(Client.getLname());
                out.println(Client.getEmail());
                Logged_In = true;
            }
    }
    private static void SendFlightInfo(){
        ArrayList<String> FlightInfo = new ArrayList<>();
                
        System.out.println("\n\n We are in the reservation step.");
                
        System.out.println(" From where you wanna flight : ");
        FlightInfo.add(scanString.nextLine());
        System.out.println(" Where you wanna go : ");
        FlightInfo.add(scanString.nextLine());        
                
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = null;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.print(" When you wanna the flight (yyyy-MM-dd) : ");
                String input = scanString.nextLine();
                date = LocalDate.parse(input, formatter);
                valid = true; 
            }
            catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }   
        }
        FlightInfo.add(formatter.format(date));
        out.println(FlightInfo.get(0));
        out.println(FlightInfo.get(1));
        out.println(FlightInfo.get(2));                
    }
    private static void ReserveByID() throws IOException, ClassNotFoundException{
        int Choice;
        String Vol;
        ArrayList <String> Planes = new ArrayList<>();
            Vol = in.readLine();
            while(!Vol.equals("Vols_Ended")){
                Planes.add(Vol);
                Vol = in.readLine();
            }
        do{
            System.out.println("Enter the Number of the flight you wanna go with (0 For Cancelling) :");
            Choice = scanInt.nextInt();
        }while(!Planes.contains(String.valueOf(Choice)) && Choice !=0);
        if(Choice == 0) {
            out.println("Reservation_Canceled");
            //CloseApp();
        }else{
            out.println(Choice);
            if(in.readLine().equals("Reservation_Done")){
                System.out.println("Reservation Done");
            }else{
                System.err.println("Error Making the Reservation");
        }
        }
        
    }
    private static void ReserveByForm() throws IOException, ClassNotFoundException{
        int Choice;
               
        System.out.println("  "+in.readLine());
        
        if("Vols_List_Found".equals(in.readLine())){
            // Listing the Possible Vols
            String Vol;
            while(true){
                Vol = in.readLine();
                if(!Vol.equals(".")) System.out.println("  "+Vol); 
                else break;
            }
            
            ArrayList <String> Planes = new ArrayList<>();
            Vol = in.readLine();
            while(!Vol.equals("Vols_Ended")){
                Planes.add(Vol);
                Vol = in.readLine();
            }
            
            do{
                System.out.println("Enter the Number of the flight you wanna go with (0 For Cancelling) :");
                Choice = scanInt.nextInt();
            }while(!Planes.contains(String.valueOf(Choice)) && Choice !=0);
            if(Choice == 0) {
                out.println("Reservation_Canceled");
                return;
                //CloseApp();
            }else{
                out.println(Choice);
            }
            if(in.readLine().equals("Reservation_Done")){
                System.out.println("Reservation Done");
            }else{
                System.err.println("Error Making the Reservation");
            }
        }
    }
    private static void Inbox() throws IOException, ClassNotFoundException{
        if(!"NO_Messages".equals(in.readLine())){
            System.out.println("Received Messages :");
            String Message = in.readLine();
            while(!Message.equals("Messages_Ended")){
                System.out.println(Message);
                Message = in.readLine();
            }
        }
        else{
            System.out.println("Your Inbox is Empty.\n");
        }
        
    }
    private static void ReveiveClientsList() throws IOException, ClassNotFoundException{
        
        if(!"NO_Clients".equals(in.readLine())){
            System.out.println("Select Client : ");
            String Message = in.readLine();
            while(!Message.equals("Clients_Ended")){
                System.out.println(Message);
                Message = in.readLine();
            }
            ArrayList <String> Clients = new ArrayList<>();
            Message = in.readLine();
            while(!Message.equals("IDs_Ended")){
                Clients.add(Message);
                Message = in.readLine();
            }
            
            int Destination;
            do{
                System.out.println("Enter the ID of the Client (0 For Back) :");
                Destination = scanInt.nextInt();
            }while(!Clients.contains(String.valueOf(Destination)) && Destination !=0);
            if(Destination == 0) {
                out.println("Back");
                //CloseApp();
            }else{
                out.println(Destination);
                OpenConversation();
                SendMessage();
            }
        }else{
            System.out.println("You are the Only, You can talk to the administration");
        }
    }
    private static void OpenConversation() throws IOException{
        if(!"NO_Messages".equals(in.readLine())){
            System.out.println("Your Conversation :\n");
            String Message = in.readLine();
            while(!Message.equals("Messages_Ended")){
                System.out.println(Message);
                Message = in.readLine();
            }
        }else{
            System.out.println("Say Hi! \n");
        }
    }
    private static void SendMessage() throws IOException, ClassNotFoundException{
        int Choice;
        do{
            System.out.println("1)- Send Message.");
            System.out.println("2)- Back.");
            Choice = scanInt.nextInt();
        }while(Choice !=1 && Choice !=2);
        if(Choice==2){
            out.println("Back");
        }else{
            out.println("Send");
            String Message;
            do{
                System.out.println("Enter your Message (0 Quit): ");
                Message = scanString.nextLine();
                out.println(Message);
            }while(!"0".equals(Message));
        }
        ReveiveClientsList();
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        try{
                boolean Verified = false;
                StartApp();
               
                while(true){
                    switch (ChooseService()) {
                        case 1 :
                            out.println("Display_Flights");
                            ReceiveFlightsInfo();
                            break;
                        case 2 :
                            if(Logged_In){
                                out.println("Logged_Out");
                                System.out.println("User : "+Client.getLname()+" "+Client.getFname()+" Disconnected\n");
                                Logged_In = false;
                                Client.ResetClient();
                            }else{
                                out.println("Login");
                                SendClientInfo();
                                if("Verification_Client_Done".equals(in.readLine())){
                                    Verified = true;
                                }
                            }
                            break;
                        case 3 :{
                            if(Verified){
                                out.println("ReserveByNumber");
                                ReceiveFlightsInfo();
                                ReserveByID();
                            }else{
                                System.err.println("Server Down");
                            }
                            break;
                        }
                        case 4 :{
                            if(Verified){
                                out.println("ReserveByInfos");
                                SendFlightInfo();
                                ReserveByForm();                            
                            }else{
                                System.err.println("Server Down");
                            }
                            break;
                        }
                        case 5 : {
                            if(Verified){
                                out.println("Inbox");
                                Inbox();
                                ReveiveClientsList();
                            }else{
                                System.err.println("Server Down");
                            }
                            break;
                        }
                        case 6 :
                            out.println("Quit");
                            CloseApp();
                    }
                }
        }
        catch(IOException e){
            System.out.println("Error in Server"+e);
        }
    }
}
