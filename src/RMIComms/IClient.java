/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMIComms;

import dto.OrderINFO;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Jakob
 */
public interface IClient extends Remote {

    String getSpecificMessage() throws RemoteException;

    void sendOrder(OrderINFO order) throws RemoteException;
}
