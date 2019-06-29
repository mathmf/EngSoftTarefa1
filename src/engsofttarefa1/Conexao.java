/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engsofttarefa1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;





/**
 *
 * @author mathe_000
 */
public class Conexao implements Runnable {
    protected Socket clientSocket = null;
    protected String serverText   = null;

    public Conexao(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
             
            String line = null;
            String firstWord;
             
            line = br.readLine() ;
            
            String arr[] = line.split(" ", 2);
            firstWord = arr[0];
            String Resto = arr[1];
            arr = Resto.split(" ", 2);
            String filePath = "pages";
            
            switch(firstWord){
                case "GET":
                    filePath = filePath.concat(arr[0]);
                    break;
                case "HEAD":
                    
                    break;
                case "POST":
                    
                    break;
                case "PUT":
                    
                    break;
            }                
                    
            System.out.println(filePath);           
            
            long time = System.currentTimeMillis();
            if("/".equals(filePath) || "/favicon.ico".equals(filePath)){
                output.write(("HTTP/1.1 200 OK\n\n Conexao: " +
            this.serverText + " - " +
            time +
            "").getBytes());
            }
            else{
                File tempFile = new File(filePath);
                boolean exists = tempFile.exists();
                
                if(exists){
                    System.out.println("oi");
                    
                    output.write(("HTTP/1.1 200 OK\n\n").getBytes());
                    try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                output.write(line.getBytes());
            }   

            bufferedReader.close();         
                }
                catch(FileNotFoundException ex) {
                    System.out.println(
                        "Unable to open file '" + 
                        filePath + "'");                
                }
                catch(IOException ex) {
                    System.out.println(
                        "Error reading file '" 
                        + filePath + "'");                  
                    // Or we could just do this: 
                    // ex.printStackTrace();
                }                 
                }
            }
            
            
            
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    
    
    
}
