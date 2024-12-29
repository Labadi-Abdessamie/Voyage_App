
package Messagerie_Service;


import java.io.Serializable;


public class MessageStr  implements Serializable
{       
        //public static int Countable = 1;
        //private final int Id_Message;
        private int MessageSource;
	private int MessageDirection;
	private String Content;

        /*public MessageStr(){
            Id_Message = Countable++;
        }
        public int getId_Message(){
            return Id_Message;
        }*/
        
	public int getMessageSource(){
            return MessageSource;
	}

	public void setMessageSource(int messageFrom){
            this.MessageSource = messageFrom;
	}
        
        public int getMessageDirection(){
            return MessageDirection;
	}

	public void setMessageDirection(int messageTo){
            this.MessageDirection = messageTo;
	}

	public String getContent()
	{
            return Content;
	}

	public void setContent(String content)
	{
            this.Content = content;
	}
        
	@Override
	public String toString()
	{
            return "Message [MessageSource=" + MessageSource + ", MessageDirection=" + MessageDirection + ", Content=" + Content + "]";
	}

}
