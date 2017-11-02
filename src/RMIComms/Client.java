/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMIComms;

import dto.OrderINFO;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import scada.Scada_Controller;

/**
 *
 * @author Jakob
 */
public class Client extends UnicastRemoteObject implements IClient, Runnable {

    private String hostname;
    private IServer server;
    private Registry registry;
    private Scada_Controller scadCon;

    public Client(String ip) throws RemoteException {
        super();
        hostname = ip;
    }

    @Override
    public String getSpecificMessage() throws RemoteException {
        return "My name is, HAH, MY NAME IS";
    }

    public void notifyHarvest(String orderNo) throws RemoteException {
        server.alertHarvest(orderNo);
    }

    public void notifyDiscard(String orderNo) throws RemoteException {
        server.alertDiscard(orderNo);
    }
    
    public void notifyPlant(String orderNo) throws RemoteException {
        server.alertPlant(orderNo);
    }
    
    public void setScadCon(Scada_Controller scad) {
        this.scadCon = scad;
    }

    @Override
    public void run() {
        //IServer server;
        //Registry registry;
        try {
            // Locate remote registry server
            registry = LocateRegistry.getRegistry(hostname, RMICONFIG.PORT);
            // Get proxy to remote object from the registry server
            server = (IServer) registry.lookup(RMICONFIG.OBJECT);
            server.addObserver(this);
            //server.pingReady();
        } catch (RemoteException | NotBoundException e) {
            throw new Error("Error when communicating: " + e);
        }
//        Scanner input = new Scanner(System.in);
//        while (true) {
//            System.out.println("enter somthing when server is ready");
//            System.out.println("loop");
//            input.nextLine();
//            try {
//                server.pingReady();
//                this.notifyHarvest(hostname);
//            } catch (RemoteException ex) {
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }

    @Override
    public void sendOrder(OrderINFO order) throws RemoteException {
        scadCon.addOrder(order);
    }
}
