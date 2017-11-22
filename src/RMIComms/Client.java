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
import mes.IMESServer;
import mes.RMI_Constants;
import scada.ISCADAObserver;
import scada.Scada_Controller;

/**
 *
 * @author Jakob
 */
public class Client extends UnicastRemoteObject implements ISCADAObserver, Runnable {

    private String hostname;
    private IMESServer server;
    private Registry registry;
    private Scada_Controller scadCon;

    public Client(String ip) throws RemoteException {
        super();
        hostname = ip;
    }


    public void notifyHarvest(String orderNo) throws RemoteException {
        server.alertHarvest(orderNo);
    }

    public void notifyDiscard(String orderNo) throws RemoteException {
        server.alertDiscarded(orderNo);
    }
    
    public void notifyPlant(String orderNo) throws RemoteException {
        server.alertPlanted(orderNo);
    }
    
    public void setScadCon(Scada_Controller scad) {
        this.scadCon = scad;
    }

    @Override
    public void run() {
        try {
            registry = LocateRegistry.getRegistry(hostname, RMI_Constants.MES_PORT);
            // Get proxy to remote object from the registry server
            server = (IMESServer) registry.lookup(RMI_Constants.MES_OBJECTNAME);
            server.addObserver(this);
            scadCon.setError("Succesfully Connected.");
        } catch (Exception e) {
            scadCon.setError("Error connecting to MES");
        }
    }
    
    @Override
    public int getCapacity() throws RemoteException {
        return scadCon.getCurrentCapacity();
    }

    @Override
    public void postOrder(OrderINFO oinfo) throws RemoteException {
        scadCon.addOrder(oinfo);
    }
}
