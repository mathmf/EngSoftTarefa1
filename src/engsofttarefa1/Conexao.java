/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engsofttarefa1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;





/**
 *
 * @author mathe_000
 */
public class Conexao implements Runnable {
    protected Socket clientSocket = null;
    protected String serverText   = null;
    private String senhaAdmin = "admin:projeto";

    public Conexao(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            
            final String CRLF = "\r\n"; 
            String line = null;
            String header = null;
            StringTokenizer senha = null;
            boolean Autenticado = false;
            boolean Restrito = false;
             
            line = br.readLine() ;
            
            String log = line;
            String StatusLog = null;
            
            while ((header = br.readLine()).length() != 0) {
                //Obtendo linhas do cabecalho para log
                //pega a linha que possui a senha
                if(header.contains("Authorization: Basic")){
                senha = new
                StringTokenizer(header);
                //Pula "Authorization: Basic"
                senha.nextToken();
                senha.nextToken();
                //Pega senha na base64
                String pass = new String(Base64.getDecoder().decode(senha.nextToken()));
                //Decodifica senha na base64
                if(pass.equals(this.senhaAdmin)){
                Autenticado = true;
                }
                }
                System.out.println (header);
            }

            
            StringTokenizer requisicao = new StringTokenizer(line);
            //pula método mostrado na requisicao (GET, POST)
            String metodo = requisicao.nextToken();
            String filePath = requisicao.nextToken();
            //Acrescente um "." de modo que a requisição do arquivo
            //esteja dentro do dirtório atual
            filePath = "pages" + filePath;
            //Abre o arquivo requisitado
            FileInputStream fis = null;
            //Verifica existencia do arquivo
            boolean exists = true;
            String Status = null;
            String ContentType = null;
            String pagHtml = null;
             
            
            switch(metodo){
                case "GET":
                    try{
                        fis = new FileInputStream(filePath);
                        }
                        catch(FileNotFoundException e){
                        exists = false;
                        }
                        if (filePath.contains("RESTRITO")||
                        filePath.contains("restrito")){
                        Restrito = true;
                        }
                        if((Restrito) && (Autenticado) == false){
                        Status = "HTTP/1.0 401 Unauthorized" + CRLF;
                        StatusLog = "401";
                        ContentType = "WWW-Authenticate: Basic realm=\"RESTRITO\"" + CRLF;
                        pagHtml = "<HTML><HEAD><TITLE> Acesso Nao Autorizado " +
                        "</TITLE></HEAD>" +
                        "<BODY> Acesso Nao Autorizado </BODY></HTML>";
                        exists = false;
                        }
                        else{
                        if(exists){
                        Status = "HTTP/1.0 200 OK" + CRLF;
                        StatusLog = "200";
                        ContentType = "Content-type: " +
                        TypeChecker(filePath)+
                        CRLF;
                        }
                        else{
                        Status = "HTTP/1.0 404 Not found" +
                        CRLF;
                        StatusLog = "404";
                        ContentType = "Content-type: " + TypeChecker(filePath)+
                        CRLF;
                        pagHtml = "<HTML><HEAD><TITLE> Arquivo Nao Encontrado" +
                        "</TITLE></HEAD>" +
                        "<BODY> Arquivo Nao Encontrado </BODY></HTML>";
                        }
                        }
                        
                        output.write(Status.getBytes());
                        
                        output.write(ContentType.getBytes());
                        
                        output.write(CRLF.getBytes());
                        
                        
                        break;
                case "HEAD":
                    
                    break;
                case "POST":
                    //Obtem o corpo do pacote POST
                    char[] buffer = new char[2048];
                    String corpo = "";
                    String corpoPost = "";
                    while(br.ready())
                    {
                    int i;
                    if((i = br.read(buffer)) > 0)
                    corpo = corpo + (new String(buffer,0,i) +
                    "\n");
                    corpoPost = corpoPost + (new String(buffer,0,i) +
                    "<BR>");
                    System.out.println(corpo);
                    }
                    corpo = "CORPO ENVIADO PELO POST: " + corpo;
                    log = log + corpo;
                    pagHtml = "<HTML><HEAD><TITLE> MENSAGEM POST </TITLE></HEAD>" +
                    "<BODY> MENSAGEM ENVIADA PELO POST: </BR>" + corpoPost
                    +
                    "</BODY></HTML>";
                    exists = false;

                    break;
                case "PUT":
                    
                    break;
            }
            
            if(exists){
            sendBytes(fis, output);
            fis.close();
            }
            else{
            output.write(pagHtml.getBytes());
            }
            Log(output, log, clientSocket,StatusLog);
                    
            System.out.println(filePath);           
            
            
            
            output.close();
            input.close();
            
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String TypeChecker(String filePath) {
        if(filePath.endsWith(".htm")||
        filePath.endsWith(".html")||
        filePath.endsWith(".txt")) return "text/html";
        if(filePath.endsWith(".gif")) return "image/gif";
        if(filePath.endsWith(".jpeg")) return "image/jpeg";
        //caso a extensão do arquivo seja desconhecida
        return "application/octet-stream";
    }

    private void sendBytes(FileInputStream fis, OutputStream output) throws Exception {

        byte[] buffer = new byte[1024];
        int bytes = 0;

        while((bytes = fis.read(buffer)) != -1){
        output.write(buffer, 0, bytes);
}
}

    private void Log(DataOutputStream output, String log, Socket clientSocket, String StatusLog) {
            try{
            //Data de requisicao
            Date date = new Date(System.currentTimeMillis());
            String dataRequisicao = date.toString();
            String pulaLinha =
            System.getProperty("line.separator");
            FileWriter fw = new FileWriter("arquivo_de_log.txt",
            true);
            fw.write("------------------------------------------------------" +
            pulaLinha);
            fw.write(clientSocket.getLocalPort() + " " +"unknown" + " - [" + dataRequisicao + "]"
            + pulaLinha);
            fw.write("\""+ log + "\" " + StatusLog +" " + output.size() + pulaLinha);
            fw.write("------------------------------------------------------" +
            pulaLinha);
            fw.write(pulaLinha);
            fw.close();
            }
            catch(IOException io){
            System.out.println(io.getMessage());
            }
}

    
    
    
}
