package FTPS;

import java.io.*; 
  
public class Solution { 
    public static void main(String args[]) 
    { 
  
        // try-catch block to handle exceptions 
        try { 
  
            // Create a file object 
            File f = new File("E:/Computer Networks/JavaSocketProgramming/FTPS"); 
  
            // Create a FileFilter 
            FileFilter filter = new FileFilter() { 
  
                public boolean accept(File f) 
                { 
                    return f.getName().endsWith("txt"); 
                } 
            }; 
  
            // Get all the names of the files present 
            // in the given directory 
            // which are text files 
            File[] files = f.listFiles(filter); 
  
            System.out.println("Files are:"); 
  
            // Display the names of the files 
            for (int i = 0; i < files.length; i++) { 
                System.out.println(files[i].getName()); 
            } 
        } 
        catch (Exception e) { 
            System.err.println(e.getMessage()); 
        } 
    } 
} 