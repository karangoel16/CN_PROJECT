import java.io.*;
import java.net.*;
//all the files have been included 
public class Client {
	private static Socket req;
	private static ObjectOutputStream out;//for writing to the socket
	private static ObjectInputStream in;//for reading to the socket
	private static String message_input;//for sending the message to the server
	private static String message_output;//for receiving message from the server
	private static int port=8000;//this is for port number
	public static String client_name;
	private static String add="localhost";
	private static String location;
	private static FileInputStream fis = null;
	private static FileOutputStream fos =null;
	private static BufferedOutputStream bos=null;
    private static BufferedInputStream bis = null;
	public Client()
	{
	}
	//this class is used to write to the client
	static private void sendmessage(String msg)
	{
		try
		{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	//function overloading is used to send byte array or string 
	static private void sendmessage(byte[] mybytearray)
	{
		try
		{
			out.writeObject(mybytearray);
			out.flush();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	//a thread has been created to listen to the server 
	static Thread Handler_input=new Thread()
	{
		public void run()
		{
			try
			{
				boolean run=true;
				while(run)
				{
					message_input=(String)in.readObject();
					//this is done to remove the commands from the screen to appear
					if(!message_input.equals("FILE") && !message_input.startsWith("CORRECT"))
						System.out.println(message_input);
					//if the input from the server comes to be file then we will go into this loop to get file data from the server
					if(message_input.equals("FILE"))
					{
						message_input=(String)in.readObject();
						File myFile = new File (location+message_input);//this will create new file
						myFile.getParentFile().mkdirs();//creates a path if it doesn't exist
						if(!myFile.exists())
						{
							myFile.createNewFile();
						}
						message_input=(String)in.readObject();
						byte [] mybytearray  = new byte [Integer.parseInt(message_input)];
						fos = new FileOutputStream(myFile);
						bos=new BufferedOutputStream(fos);
						mybytearray=(byte[])in.readObject();
						bos.write(mybytearray, 0, mybytearray.length);
						bos.close();
						fos.close();
						System.out.println(myFile.getName()+" has been created");
						
					}
					if(message_input=="EXIT")
					{
						run=false;
					}
				}
			}
			catch(java.net.SocketException ioe)
			{
				System.out.println("Server closed");
				System.exit(0);
			}
			catch(IOException | ClassNotFoundException ioe)
			{
				ioe.printStackTrace();
			}
		}
	};
	static Thread Handler_output=new Thread()
	{	//to suppress warning from the compiler on using deprecated APIs
		@SuppressWarnings("deprecation")
		public void run()
		{
			try
			{
				message_input="";
				BufferedReader buff=new BufferedReader(new InputStreamReader(System.in));
				boolean run=true;
				while(run)
				{
					//here menu has been created
					System.out.println("Enter a message");
					System.out.println("MYUSER");
					System.out.println("ACTIVEUSER");
					System.out.println("BROADCAST");
					System.out.println("UNICAST");
					System.out.println("BLOCKCAST");
					System.out.println("SENDTO");
					System.out.println("FILE BROADCAST");
					System.out.println("EXIT");
					message_output = buff.readLine();//to read a message
					sendmessage(message_output); // to send it to the server
					if(message_output.toUpperCase().equals("MYUSER"))
					{
						//do nothing output would come on the other thread
					}
					if(message_output.toUpperCase().equals("BROADCAST"))
					{
						//version 2.0 to add the message to be sent in broadcast
						System.out.println("Enter the message you want to broadcast");
						message_output=buff.readLine();
						sendmessage(message_output); //calls the sendmessage class
					}
					//this part is used to send message to a particular client
					if(message_output.toUpperCase().equals("UNICAST"))
					{
						System.out.println("Enter the client number whom you want to send message");
						while(message_output.toUpperCase().equals("UNICAST"))
						{
							message_output=buff.readLine();
							sendmessage(message_output);
						}
						Thread.sleep(1000);
						//if the server notifies that the client number is correct
						if(!message_input.toUpperCase().startsWith("INCORRECT"))
						{
							System.out.println("Enter the message");
							message_output=buff.readLine();
							sendmessage(message_output);
						}
					}
					if(message_output.toUpperCase().equals("BLOCKCAST"))
					{
						System.out.println("Enter the number of the client you do not want to send message");
					    while(message_output.toUpperCase().equals("BLOCKCAST"))
						{
							message_output=buff.readLine();
							sendmessage(message_output);		
						}
					    Thread.sleep(1000);
					    if(!message_input.toUpperCase().startsWith("INCORRECT"))
					    {
					    	System.out.println("Enter the message");
					    	message_output=buff.readLine();
					    	sendmessage(message_output);
					    }
					}
					if(message_output.toUpperCase().startsWith("SENDTO"))
					{
						System.out.println("Give me client's number");
						message_input="";//message_input has been cleared
					    while(message_output.toUpperCase().equals("SENDTO"))
						{
							message_output=buff.readLine();
							sendmessage(message_output);
								
						}
					    Thread.sleep(1000);
					    if(!message_input.toUpperCase().startsWith("INCORRECT"))
					    {
					    	System.out.println("Send the address of the file which you want to send");
							while(true)
							{
								message_output=buff.readLine();
								File myFile = new File (message_output); // creating an object of the mentioned file
								//to check if the file exists or not
								if(myFile.exists())
								{
									sendmessage(myFile.getName());//this will send the name of the file to the different client
									sendmessage(Integer.toString((int)myFile.length()));//this will send the length of the file
									byte [] mybytearray  = new byte [(int)myFile.length()];
									fis = new FileInputStream(myFile);
									bis = new BufferedInputStream(fis);
									bis.read(mybytearray,0,mybytearray.length);
									sendmessage(mybytearray);
									//to tell the computer that we are ready to receive the data
									bis.close();
									fis.close();
									break;
								}
								else
								{
									System.out.println("Enter the correct file path to be sent");
								}
							}
					    }
					}
					if(message_output.toUpperCase().startsWith("FILE BROADCAST"))
					{
						//this is used to broadcast file to the entire network
						System.out.println("Send the address of the file which you want to send");
						message_output=buff.readLine();
						File myFile = new File (message_output);
						//to check if the file exists or not
						if(myFile.exists())
						{
							sendmessage(myFile.getName());
							sendmessage(Integer.toString((int)myFile.length()));
							byte [] mybytearray  = new byte [(int)myFile.length()];
							fis = new FileInputStream(myFile);
							bis = new BufferedInputStream(fis);
							bis.read(mybytearray,0,mybytearray.length);
							sendmessage(mybytearray);
							bis.close();
							fis.close();
						}
						else
						{
							System.out.println("Enter the correct file name");
						}
					}
					if(message_output.toUpperCase().startsWith("EXIT"))		
					{
						sendmessage(message_output);
						Handler_input.stop();
						run=false;
					}
				}
			}
			catch(IOException | InterruptedException ioe)
			{
				ioe.printStackTrace();
			} 
			finally
			{
				try
				{
					in.close();
					out.close();
					req.close();
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
	};
	//this is where main is called
	public static void main(String[] args) throws Exception
	{
		BufferedReader buff=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the location where you wanna store the file e.g.(C:\\localhost\\):");
		location=buff.readLine();
		//enter the location of the file
		try{
			req=new Socket(add,port);
			out=new ObjectOutputStream(req.getOutputStream());
			out.flush();
			in=new ObjectInputStream(req.getInputStream());
			Handler_input.start();
			Handler_output.start();
		}
		//this is where we will check if the 
		catch(java.net.ConnectException ioe)
		{
			System.out.println("Server not found");
			System.exit(1);
		}
	}
}
