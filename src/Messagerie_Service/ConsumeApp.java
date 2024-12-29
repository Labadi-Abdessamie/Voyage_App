package Messagerie_Service;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.util.ArrayList;
import org.apache.activemq.ActiveMQConnectionFactory;


public class ConsumeApp implements Runnable {
    
    private final int Direction;
    private ArrayList<MessageStr> Messages;
            
    public ConsumeApp(int Direction){
        this.Direction = Direction;
    }
    public ArrayList<MessageStr> ReceiveMessage(){
        Messages = new ArrayList<>();
        this.run();
        return Messages;
    }
    
    @Override
    public void run() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            
            try ( Connection connection = factory.createConnection() ) {

                connection.start();
                
                try ( Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE) ) {
                    
                    Destination queue = session.createQueue("UnivTiaret");

                    String selector = "Direction = '"+Direction+"'";

                    MessageConsumer consumer = session.createConsumer(queue,selector);                    

                    Message message = consumer.receive(1000);
                    
                    while(message!=null){
                        if (message instanceof TextMessage text) {
                            MessageStr Message = new MessageStr();
                            Message.setMessageSource(Integer.parseInt(text.getStringProperty("Source")));
                            Message.setMessageDirection(Direction);
                            Message.setContent(text.getText());
                            
                            Messages.add(Message);
                        }
                        message = consumer.receive(1000);
                    }
                }
            }
        }
        catch (JMSException ex) {
            System.out.println("Exception Occured");
        }
    }
}    
