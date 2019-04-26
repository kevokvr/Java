import java.io.*;
import java.net.*;

public class Producer
{
    public static void main(String args[])throws IOException, InterruptedException
    {
        Socket socket=new Socket("127.0.0.1",7000);
        BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
        
        //Input Output Streams
        PrintStream out = new PrintStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       
        while(true){
        	System.out.println("Want to produce?");
        	String console_inp = scan.readLine();
        	
        	if(console_inp.equalsIgnoreCase("Yes")){
        		String item=scan.readLine();
        		
        		out.println(item);
        		
        		in.readLine();
        		System.out.println("Producer produced - "  + item);
        		
        	}
        	 
        	
        }
        
    }
}


