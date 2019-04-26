import java.io.*;
import java.net.*;

public class Consumer
{
    public static void main(String args[])throws IOException, InterruptedException
    {
        Socket socket=new Socket("127.0.0.1",7000);
        BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
        
        //Streams
        PrintStream out = new PrintStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        while(true){
        	System.out.println("Would you like to Consume?");
        	String console_inp=scan.readLine();
        	
        	if(console_inp.equalsIgnoreCase("Yes")){
        		out.println("Consume!");
        		
        		String item=in.readLine();
            	
        		System.out.println("Consumer has consumed: "  + item);
        	}
        	 
        	
        }
        
    }
}