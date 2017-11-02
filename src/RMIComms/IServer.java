/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMIComms;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Jakob
 */
public interface IServer extends Remote {

    void addObserver(IClient client) throws RemoteException;

    void pingReady() throws RemoteException;
    
    void alertHarvest(String orderNo) throws RemoteException;
    
    void alertDiscard(String orderNo) throws RemoteException;
    
    void alertPlant(String orderNo) throws RemoteException;
}
