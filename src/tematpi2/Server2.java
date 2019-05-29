package tematpi2;

 

import java.net.*;

import java.io.*;

import java.util.Scanner;
import java.util.TimerTask;
import javax.swing.JButton;

import javax.swing.JOptionPane; 
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

 

public class Server2 extends Thread {           

    private Scanner scanTCP;

    private PrintStream printTCP;

    private Socket socketTCP;

    //private static Orar orar = new Orar();
    
 

    public Server2(Socket conexiuneTCP) throws IOException {
        

        this.socketTCP = conexiuneTCP;        // Obtinere socket

        this.scanTCP = new Scanner(socketTCP.getInputStream());

        this.printTCP = new PrintStream(socketTCP.getOutputStream());

    }
    
    public Server2(){
        
    }

    int secondsPassed = 0;
    boolean hasStarted = false;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            
            long threadId = Thread.currentThread().getId();   
            hasStarted = true;
            secondsPassed++;
            System.out.println("Uptime: " + secondsPassed + " thread:: "+threadId);
            
        }
    };
    
    public boolean hasTimerStarted() {
        return this.hasStarted;
    }
    
    public long secondPassedServer1() {
        return this.secondsPassed;
    }
    
    public void start() {
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }
    

    public static void main(String[] args) throws IOException {

        int portTCP = Integer.parseInt(JOptionPane.showInputDialog(

                         "Server: introduceti numarul de port al serverului"));
        //JButton btn = new JButton("Adaugati alt server?");
        Server2 srv = new Server2();
        if(!srv.hasTimerStarted()){
            System.out.println("start timer...");
            srv.start();
        }
        ServerSocket serverTCP = new ServerSocket(portTCP); // Creare socket server
        
            
        while (true) {
            Timer timer2 = new Timer();
            System.out.println("server accept...");
            Socket conexiune = serverTCP.accept();
            TimerTask task2 = new TimerTask() {

                @Override
                public void run() {
                    DataOutputStream dOut = null;
                    try {
                        dOut = new DataOutputStream(conexiune.getOutputStream());
                        try {
                            dOut.writeByte(1);
                        } catch (IOException ex) {
                            Logger.getLogger(Server2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            dOut.writeLong(srv.secondsPassed);
                        } catch (IOException ex) {
                            Logger.getLogger(Server2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            dOut.flush();
                        } catch (IOException ex) {
                            Logger.getLogger(Server2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    } catch (IOException ex) {
                        Logger.getLogger(Server2.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            dOut.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Server2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            timer2.scheduleAtFixedRate(task2, 100, 500);
            
            Server2 server2 = new Server2(conexiune);
            System.out.println("server start...");
            server2.start();

        }

    }

}