package Messagerie_Service;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ProduceApp implements Runnable {
    private final int Source;
    private final int Direction;
    private final String Content;
    
    public ProduceApp(int Source,int Direction,String Content){
        this.Source = Source;
        this.Direction = Direction;
        this.Content = Content;
    }
    public void SendMessage(){
        this.run();
        
        /*MessageStr message = new MessageStr();
        message.setMessageSource(Source);
        message.setMessageDirection(Direction);
        message.setContent(Content);
        System.out.println("Producer Sent: " + message.toString());
        */
    }
    
    @Override
    public void run() {
        try {  
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            try ( Connection connection = factory.createConnection() ) {
                
                connection.start();
                
                try ( Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE) ) {
                    
                    Destination queue = session.createQueue("UnivTiaret");
                    
                    MessageProducer producer = session.createProducer(queue);
                    
                    TextMessage Message = session.createTextMessage(Content);
                    Message.setStringProperty("Source", String.valueOf(Source)); 
                    Message.setStringProperty("Direction", String.valueOf(Direction));
                    
                    producer.send(Message);
                }
            }
        }
        catch (JMSException ex) {
            System.out.println("Exception Occured");
        }
    }
}
