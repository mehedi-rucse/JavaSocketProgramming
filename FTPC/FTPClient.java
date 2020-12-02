package FTPC;

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

class FTPClient
{
    public static void main(String args[]) throws Exception
    {
        Socket soc=new Socket("127.0.0.1",3000);
        transferfileClient t=new transferfileClient(soc);
        t.displayMenu(); 
    }
}
class transferfileClient
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;
    transferfileClient(Socket soc)
    {
        try
        {
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            br=new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex)
        {
        }        
    }

    void GetIndex() throws Exception
    {       
        System.out.println("File list from server:");
        while (true)
        {
            String line = din.readUTF();
            if (line.equals("over")){
                break;
            }  
            System.out.println("  " + line);
        }
    }
    
    void SendFile() throws Exception
    {         
        String filename;
        System.out.print("Enter File Name :");
        filename=br.readLine();
            
        File f=new File(filename);
        if(!f.exists())
        {
            System.out.println("File not Exists...");
            dout.writeUTF("File not found");
            return;
        }
        
        dout.writeUTF(filename);
        
        String msgFromServer=din.readUTF();
        if(msgFromServer.compareTo("File Already Exists")==0)
        {
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
            Option=br.readLine();            
            if(Option=="Y")    
            {
                dout.writeUTF("Y");
            }
            else
            {
                dout.writeUTF("N");
                return;
            }
        }
        
        System.out.println("Sending File ...");
        FileInputStream fin=new FileInputStream(f);
        int ch;
        do
        {
            ch=fin.read();
            dout.writeUTF(String.valueOf(ch));
        }
        while(ch!=-1);
        fin.close();
        System.out.println(din.readUTF());   
    }
    
    void ReceiveFile() throws Exception
    {
        String fileName;
        System.out.print("Enter File Name :");
        fileName=br.readLine();
        dout.writeUTF(fileName);
        String msgFromServer=din.readUTF();
        
        if(msgFromServer.compareTo("File Not Found")==0)
        {
            System.out.println("File not found on Server ...");
            return;
        }
        else if(msgFromServer.compareTo("READY")==0)
        {
            System.out.println("Receiving File ...");
            File f=new File(fileName);
            if(f.exists())
            {
                String Option;
                System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
                Option=br.readLine();            
                if(Option=="N")    
                {
                    dout.flush();
                    return;    
                }                
            }
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
            System.out.println(din.readUTF());    
        } 
    }

    public void displayMenu() throws Exception
    {
        while(true)
        {    
            System.out.println("\n  [ MENU ]");

            System.out.println("1. File List");
            System.out.println("2. Send File");
            System.out.println("3. Receive File");
            System.out.println("4. Exit");
            System.out.print("\nEnter Choice :");
            int choice;
            choice=Integer.parseInt(br.readLine());
            if(choice == 1)
            {
                dout.writeUTF("LIST");
                GetIndex();
            }

            else if(choice == 2)
            { 
                dout.writeUTF("SEND");
                SendFile();
            }

            else if(choice==3)
            {
                dout.writeUTF("GET");
                ReceiveFile();
            }
            
            else
            {
                dout.writeUTF("DISCONNECT");
                System.exit(1);
            }
        }
    }
}