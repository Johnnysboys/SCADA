/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PLCCommunication;

import static PLCCommunication.Message.serialNo;

/**
 * Protocol structure
 * @author Steffen Skov
 */
public interface IMessage 
{
    // direction defination
    final byte TOPLC = 0;
    final byte FROMPLC = 1;

    //Protocol
    final int COMMAND = 0;
    final int DIRECTION = 1;
    final int SERIAL_NO = 2;
    final int SIZE =3;  // Size of data 
    // Timestamp to sec. precision
    final int YEAR = 4; 
    final int MONTH = 5;
    final int DAY = 6;
    final int HOUR = 7;
    final int MINUTE = 8;
    final int SECOND = 9;
    // Data
    final int DATA_START = 10;
    final int MAX_DATA = 100 + DATA_START;
    
    
}
