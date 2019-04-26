import java.io.*;
import java.net.*;
import java.util.PriorityQueue;
import java.util.Queue;

public class Monitor
{
	public static Queue<String> item_q = new PriorityQueue<String>();
	static int capacity = 10;
	 
	public static void printQueue(){
		System.out.println("Queue elements:");
		
		for(String s : item_q) { 
			  System.out.print(s.toString()+" , "); 
			}
		System.out.println("\nEnd of Queue Elements");
	}
	
	 
    public static void produce(String value) throws InterruptedException// This function is called by the producer
    {
        
                // producer thread waits while list
                // is full
                while (item_q.size()==capacity)
                    Thread.sleep(5000);;

                System.out.println("Producer has produced:"
                                              + value);

                // to insert the jobs in the list
                //synchronized(item_q){
                	 item_q.add(value);
                	 printQueue();
                	 System.out.println("Lock with producer");
                	 Thread.sleep(5000);
                //}
               
                // notifies the consumer thread that
                // now it can start consuming
               // notify();

                // makes the working of program easier
                // to  understand
                Thread.sleep(5000);
                
           
    }
       
    public static String consume() throws InterruptedException// this function is called by the consumer
    {
           
                // consumer thread waits while list
                // is empty
                while (item_q.size()==0)
                	Thread.sleep(5000);;

                //to retrieve the first job in the list
                String value=null;
               // synchronized(item_q){
                	value = item_q.poll();
                	System.out.println("Lock with consumer");
                	Thread.sleep(5000);
                //}
                System.out.println("Consumer consumed-"
                                                + value);
                printQueue();
                // and sleep
                Thread.sleep(5000);
                
                return value;
          
    }
    
    
    public static void main(String args[])throws IOException, InterruptedException
    {
    	ServerSocket socket = new ServerSocket(7000);
        BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
        Socket ss1=socket.accept();//accept producer
      
        Thread producer = new Thread(new Runnable()// Create producer thread
        {
        	PrintStream out = new PrintStream(ss1.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(ss1.getInputStream())); 
            @Override
            public void run()
            {
            	while(true){
            		String item=null;
					try {
						item = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
            		try {
						produce(item);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}  		
            		out.println("PRODUCE");
            	}
            }
        });
 
        producer.start();
        
        Socket ss2=socket.accept();//Accept consumer

        Thread consumer = new Thread(new Runnable()// Create consumer thread
        {
        	PrintStream out = new PrintStream(ss2.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(ss2.getInputStream()));
            
            @Override
            public void run()
            {
            	while(true){
            		try {
						in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
            		String item=null;
					try {
						item = consume();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            		out.println(item);
            	}
            }
        });
        
        consumer.start();
        
        
    }
}