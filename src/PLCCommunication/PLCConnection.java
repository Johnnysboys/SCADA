/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PLCCommunication;

/**
 * Methods to PLC communication
 * @author Steffen Skov
 */
abstract public class PLCConnection 
{
    protected Message mess;
    abstract public boolean send();
    
    /**
     * Add message
     * @param m the message
     */
    public void addMessage(Message m)
    {
        mess = m;
    }
}
