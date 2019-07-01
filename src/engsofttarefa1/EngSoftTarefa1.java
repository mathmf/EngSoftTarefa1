/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engsofttarefa1;

/**
 *
 * @author mathe_000
 */
public class EngSoftTarefa1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Servidor server = new Servidor(9000);
        new Thread(server).start();

        try {
            Thread.sleep(40 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Parando Servidor");
        server.stop();
        
    }
    
}
