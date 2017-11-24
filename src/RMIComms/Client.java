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
    private int currentCapacity = 0;

    /**
     * Client constructor
     *
     * @param ip - The IP of the MES-server.
     * @param capa - The current capacity of this SCADA-unit.
     * @throws RemoteException
     */
    public Client(String ip, int capa) throws RemoteException {
        super();
        hostname = ip;
        currentCapacity = capa;
    }

    /**
     * Increases the observable capacity by one.
     */
    public void increaseCapacity() {
        this.currentCapacity++;
    }

    /**
     * Decreases the observable capacity by one.
     */
    public void decreaseCapactiy() {
        this.currentCapacity--;
    }

    /**
     * Redundant method
     */
    public void connect() {
        try {
            registry = LocateRegistry.getRegistry(hostname, RMI_Constants.MES_PORT);
            // Get proxy to remote object from the registry server
            server = (IMESServer) registry.lookup(RMI_Constants.MES_OBJECTNAME);
            server.addObserver(this);
            scadCon.setError("Succesfully Connected.");
        } catch (Exception e) {
            e.printStackTrace();
            scadCon.setError("Error connecting to MES");
        }
    }

    /**
     * Notifies the MES-server of a harvest.
     *
     * @param orderNo - String containing the order number connected to the
     * harvest.
     * @throws RemoteException
     */
    public void notifyHarvest(String orderNo) throws RemoteException {
        server.alertHarvest(orderNo);
    }

    /**
     * Notifies the MES-server of a discard.
     *
     * @param orderNo - String containing the order number connected to the
     * discard.
     * @throws RemoteException
     */
    public void notifyDiscard(String orderNo) throws RemoteException {
        server.alertDiscarded(orderNo);
    }

    /**
     * Notifies the MES-server of a plant.
     *
     * @param orderNo - String containing the order number connected to the
     * plant.
     * @throws RemoteException
     */
    public void notifyPlant(String orderNo) throws RemoteException {
        server.alertPlanted(orderNo);
    }

    /**
     * Saves a reference to the Scada_Controller
     *
     * @param scad - The Scada_Controller to save.
     */
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
            e.printStackTrace();
            scadCon.setError("Error connecting to MES");
        }
    }

    @Override
    public int getCapacity() throws RemoteException {
        return currentCapacity;
    }

    @Override
    public void postOrder(OrderINFO oinfo) throws RemoteException {
        scadCon.addOrder(oinfo);
    }
}
