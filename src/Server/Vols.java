package Server;

public class Vols {
    int Num_Vol;
    String Depart;
    String Arrive;
    String DateDepart;
    String DateArrive;
    int seats;
    
    public Vols(int id,String Depart,String Arrive,String DateD,String DateA,int seats){
    this.Num_Vol=id;
    this.Depart = Depart;
    this.Arrive = Arrive;
    this.DateDepart=DateD;
    this.DateArrive=DateA;
    this.seats=seats;
    }
}
