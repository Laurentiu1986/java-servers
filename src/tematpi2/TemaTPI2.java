/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tematpi2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author amzar
 */
public class TemaTPI2 extends Thread{

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //connectToServer();
        
        TemaTPI2 client = new TemaTPI2();
        //client.start();
    }
    
    long uptime;
    int server;
    int secondsPassed = 0;
    //boolean hasStarted = false;
    Timer timer = new Timer();
    
    TimerTask task = new TimerTask() {
        Server srv = new Server();
        @Override
        public void run() {
            System.out.println("hasStarted: "+this.srv.hasTimerStarted());
            if(this.srv.hasTimerStarted()) {
                long threadId = Thread.currentThread().getId();   
                //hasStarted = true;
                secondsPassed++;
                System.out.println("Uptime: " + secondsPassed + " thread:: "+threadId);
            } else {
                System.out.println("Server 1 is not running!");
            }
            
            
            
        }
    };
    
    public void start() {
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    //private JFrame frame;

//     public void setUptime(String uptime) {
//         System.out.println("setUptime: "+uptime);
//         this.uptime = uptime;
//     }       
    public void setUptime(int server, long uptimeS) {
        System.out.println("setUptime: "+uptimeS);
        this.uptime = uptimeS;
        this.server = server;
    }

    public long getUptime() {
        return this.uptime;
    }
    public int getServerCount() {
        return this.server;
    }
     

    public TemaTPI2() throws IOException {     

        
        JFrame frame = new JFrame("Client TCP");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container containerCurent = frame.getContentPane();

        containerCurent.setLayout(new BorderLayout());

 

        JPanel pane = new JPanel(new GridLayout(0, 2));

        final JLabel eticheta = new JLabel("Optiuni:");
        JButton monitorButton = new JButton("Monitorizeaza server");
        JButton configButton = new JButton("Configureaza server");

        
        monitorButton.addActionListener(new ActionListener() {

            
            
            private Scanner scanTCP;

            private PrintStream printTCP;

            private Socket socketTCP;

            private int portTCP;

            private InetAddress adresaIP;
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                this.portTCP = Integer.parseInt(JOptionPane.showInputDialog("Client: introduceti numarul de port al serverului"));
                try {
                    this.adresaIP = InetAddress.getByName(JOptionPane.showInputDialog("Client: introduceti adresa serverului"));
                } catch (UnknownHostException ex) {
                    Logger.getLogger(TemaTPI2.class.getName()).log(Level.SEVERE, null, ex);
                }
                long upt = 0;
                try {
                    this.socketTCP = new Socket(adresaIP, portTCP);
                    boolean done = false;
                    DataInputStream dIn = new DataInputStream(this.socketTCP.getInputStream());
                    //
                    //while(!done) {
                        byte messageType = dIn.readByte();
                        
                        switch(messageType)
                        {
                        case 1: // Type A
                            while(dIn.available()>0) {
                                upt = dIn.readLong();
                                System.out.println("Server 1: "+upt);
                            }
                            setUptime(1, upt);
                            break;
                        case 2: // Type A
                            while(dIn.available()>0) {
                                upt = dIn.readLong();
                                System.out.println("Server 2: "+upt);
                            }
                            setUptime(2, upt);
                            break;
                        case 3: // Type A
                            while(dIn.available()>0) {
                                upt = dIn.readLong();
                                System.out.println("Server 3: "+upt);
                            }
                            setUptime(3, upt);
                            break;
                        default:
                          done = true;
                        }
                      //}
                    
                } catch (IOException ex) {
                    //Logger.getLogger(TemaTPI2.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(containerCurent,
                    "Acest server nu este deschis!",
                    "Warning!",
                    JOptionPane.WARNING_MESSAGE);
                }

                try {
                    this.scanTCP = new Scanner(socketTCP.getInputStream());
                } catch (IOException ex) {
                    Logger.getLogger(TemaTPI2.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    this.printTCP = new PrintStream(socketTCP.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(TemaTPI2.class.getName()).log(Level.SEVERE, null, ex);
                }
                openMonitorWindow(adresaIP, portTCP);
            }

            
            
          
        });
        
        
        //pane.add(eticheta);
        
        //containerCurent.add(pane, BorderLayout.WEST);
        containerCurent.add(monitorButton, BorderLayout.PAGE_START);
        containerCurent.add(configButton, BorderLayout.AFTER_LAST_LINE);
 


        final JTextArea outGrafic = new JTextArea(8, 40); // Zona non-editabila

        JScrollPane scrollPane = new JScrollPane(outGrafic,

                     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,

                     JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        outGrafic.setEditable(false);

        containerCurent.add(outGrafic, BorderLayout.CENTER);

        frame.pack(); frame.setVisible(true);


    }
    
    public void openMonitorWindow(InetAddress adresaIP, int portTCP) {
        
        System.out.println("openMonitor...");
        JFrame frame = new JFrame("Monitor");
        frame.setSize(500, 300);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container containerCurentMonitor = frame.getContentPane();
        containerCurentMonitor.setLayout(new BorderLayout());
        
        final JTextArea outGrafic = new JTextArea(8, 40); // Zona non-editabila
        JScrollPane scrollPane = new JScrollPane(outGrafic,
                     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                     JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        outGrafic.setEditable(false);
        containerCurentMonitor.add(outGrafic, BorderLayout.CENTER);
        JLabel label = new JLabel("Uptime Server "+getServerCount()+": "+getUptime());
        frame.add(label);
        frame.pack(); frame.setVisible(true);
        
        //System.out.println("secondsPassed from Server 1: "+srv.secondPassedServer1());       
        
    }

}
