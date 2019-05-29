package tematpi2;

 

import java.net.*;

import java.io.*;

import java.util.Scanner;
import java.util.TimerTask;
import javax.swing.JButton;

import javax.swing.JOptionPane; 
import java.util.Timer;

 

public class Server extends Thread {           

    private Scanner scanTCP;

    private PrintStream printTCP;

    private Socket socketTCP;

    //private static Orar orar = new Orar();
    
 

    public Server(Socket conexiuneTCP) throws IOException {
        

        this.socketTCP = conexiuneTCP;        // Obtinere socket

        this.scanTCP = new Scanner(socketTCP.getInputStream());

        this.printTCP = new PrintStream(socketTCP.getOutputStream());

    }
    
    public Server(){
        
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
        Server srv = new Server();
        if(!srv.hasTimerStarted()){
            System.out.println("start timer...");
            srv.start();
        }
        ServerSocket serverTCP = new ServerSocket(portTCP); // Creare socket server
        
            
        while (true) {
            System.out.println("server accept...");
            Socket conexiune = serverTCP.accept();
            DataOutputStream dOut = new DataOutputStream(conexiune.getOutputStream());
            dOut.writeByte(1);
            dOut.writeLong(srv.secondsPassed);
            dOut.flush();
            
            Server server = new Server(conexiune);
            System.out.println("server start...");
            server.start();

        }

    }

}