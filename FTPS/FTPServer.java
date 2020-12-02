
package FTPS;

/**
 * This program is a very simple network file server.The 
 * server has a list of available text files.
 * The Client can:
 * 1: see the list of files,
 * 2: download any text files available in the server,
 * 3: and also can send some text files,
 */

 
import java.net.*;
import java.io.*;

public class FTPServer
{
    public static void main(String args[]) throws Exception
    {
        ServerSocket soc=new ServerSocket(3000);
        System.out.println("FTP Server Started on Port Number 3000");
        while(true)
        {
            System.out.println("Waiting for Connection ...");
            new transferfile(soc.accept());  
        }  
    }
}

class transferfile extends Thread
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    
    transferfile(Socket soc)
    {
        try
        {
            ClientSoc=soc;                        
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();   
        }
        catch(Exception ex)
        {
        }           
    }

    void SendIndex() throws Exception 
    {
            // Create a file object with the location of server 
            File f = new File("E:/Computer Networks/JavaSocketProgramming/FTPS"); 

            FileFilter filter = new FileFilter()
            { 
                public boolean accept(File f) 
                { 
                    return f.getName().endsWith("txt"); 
                } 
            }; 
  
            // Get all the names of the text files in the server 
            File[] files = f.listFiles(filter); 
  
            // Display the names of the files 
            for (int i = 0; i < files.length; i++)
            { 
                dout.writeUTF(files[i].getName());  
               
            }
            dout.writeUTF("over");
            return;
     }

    void SendFile() throws Exception
    {        
        String filename=din.readUTF();
        File f=new File(filename);
        if(!f.exists())
        {
            dout.writeUTF("File Not Found");
            return;
        }
        else
        {
            dout.writeUTF("READY");
            FileInputStream fin=new FileInputStream(f);
            int ch;
            do
            {
                ch=fin.read();
                dout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);    
            fin.close();    
            dout.writeUTF("File Receive Successfully");                            
        } 
    }
    
    void ReceiveFile() throws Exception
    {
        String filename=din.readUTF();
        if(filename.compareTo("File not found")==0)
        {
            return;
        }
        File f=new File(filename);
        String option;
        
        if(f.exists())
        {
            dout.writeUTF("File Already Exists");
            option=din.readUTF();
        }
        else
        {
            dout.writeUTF("SendFile");
            option="Y";
        }
            
            if(option.compareTo("Y")==0)
            {
                FileOutputStream fout=new FileOutputStream(f);
                int ch;
                String temp;
                do
                {
                    temp=din.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);                    
                    }
                }while(ch!=-1);
                fout.close();
                dout.writeUTF("File Send Successfully");
            }
            else
            {
                return;
            }      
    }

    public void run()
    {
        while(true)
        {
            try
            {
                System.out.println("Waiting for Command ...");
                String Command=din.readUTF();
                if(Command.compareTo("GET")==0)
                {
                    System.out.println("\tGET Command Received ...");
                    SendFile();
                    continue;
                }

                else if(Command.compareTo("LIST")==0)
                {
                    System.out.println("\tLIST Command Receiced ...");                
                    SendIndex();
                    continue;
                }

                else if(Command.compareTo("SEND")==0)
                {
                    System.out.println("\tSEND Command Receiced ...");                
                    ReceiveFile();
                    continue;
                }
                else if(Command.compareTo("DISCONNECT")==0)
                {
                    System.out.println("\tDisconnect Command Received ...");
                    System.exit(1);
                }
                
                }
                catch(Exception ex)
                {
            }
            
        }
    }  
}