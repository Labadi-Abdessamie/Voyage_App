package Server;

public class ClientStructure {
    private int ID;
    private String FirstName = null;
    private String LastName = null;
    private String Email = null;
    
    private boolean first = true;
            
    public ClientStructure(){    }
    public void setID(int id){
        if(first){
            ID = id;
            first = false;
        }
    }
    public void setFirstName(String Fname){
        FirstName = Fname;
    }
    public void setLastName(String Lname){
        LastName = Lname;
    }
    public void setEmail(String email){
        Email = email;
    }
    public int getID(){
        return ID;
    }
    public String getFname(){
        return FirstName;
    }
    public String getLname(){
        return LastName;
    }
    public String getEmail(){
        return Email;
    }         
    public void ResetClient(){
        setID(0);
        setFirstName(null);
        setLastName(null);
        setEmail(null);
        first = true;
    }
}
