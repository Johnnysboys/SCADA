/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PLCCommunication;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Dataprotocol
 * @author Steffen Skov
 */
public class Message implements IMessage 
{
    static byte serialNo = 0;
    private byte command;
    private byte direction;
    private byte currentSNo;
    private byte size; //length of data
    private Calendar timestamp;
    private byte [] data;
    protected byte [] answer = new byte[125];
        
    
    
    /**
     * Create a message
     * @param cmd to the PLC, see ICommands 
     * @param data data to PLC
     */
    public Message(byte cmd, byte [] data)
    {
        
        this(cmd);
        if (data != null)
        {
            if (data.length > 100)
                throw new ArrayIndexOutOfBoundsException("Data array too big");
            this.data = data;
            size = (byte)data.length;
        }
        
    }
    
    /**
     * Create message without data to the PLC, see ICommands
     * @param cmd to the PLC, see ICommands 
     */
    public Message(byte cmd)
    {
        command = cmd;
        data = null;
        size = 0; 
        direction = TOPLC;
        currentSNo = serialNo;
        timestamp = new GregorianCalendar();
    }
    
    /**
     * Pack the message
     * @return array of the message
     */
    public byte [] packMessage()
    {
        byte [] result = new byte[size + 10];
        timestamp = new GregorianCalendar();
        result[COMMAND] = getCommand();
        result[DIRECTION] = direction;
        result[SERIAL_NO] = currentSNo;
        if (serialNo == 127) 
            serialNo=0; //reset serial number
        else
            serialNo++;
        result[SIZE] = (byte) (size & 0x7f);
        //Timestamp
        result[YEAR] = (byte) (timestamp.get(Calendar.YEAR) - 2000);
        result[MONTH] = (byte) timestamp.get(Calendar.MONTH);
        result[DAY] = (byte) timestamp.get(Calendar.DAY_OF_MONTH);
        result[HOUR] = (byte) timestamp.get(Calendar.HOUR_OF_DAY);
        result[MINUTE] = (byte) timestamp.get(Calendar.MINUTE);
        result[SECOND] = (byte) timestamp.get(Calendar.SECOND);
        
        
        // Data  
        for (int i=0; i<size; i++)
            result[i+DATA_START] = data[i];
        System.out.print("packet:");
        for(int i = 0; i<(size+10);i++)
                System.out.print(result[i] + " ");
        System.out.println(";");
            
        return result;
    }
    
    /**
     * Get result raw bytes
     * @return complete PLC answer
     */
    public byte [] getResult()
    {
        return answer;   
    }
    
    /**
     * Get result data as raw bytes
     * @return data embedded in answer
     */
    public byte [] getResultData()
    {
        byte [] data;
        byte [] result = this.getResult();
        int dataSize = result[SIZE];

        data = new byte[dataSize];
        for (int i=0; i<dataSize; i++)
            data[i] = result[DATA_START + i];
        if (dataSize == 0)
            return null;
        else
            return data;
    }
    
    /**
     * Get result as a Message
     * @return answer as a Message
     */
    public Message getResultMessage()
    {
        Message ansMess = new Message(getResult()[0], getResultData());
        ansMess.direction = FROMPLC;
        ansMess.size = (byte) getResultData().length;
        return ansMess;
    }
    
    /**
     * Check Answer for correctnees
     * Checks: Communication direction, command and serial number in
     * the answer is correct
     * @return true if answer from PLC is the rigtht one
     */
    public boolean answerIsValid()
    {
        if (answer[DIRECTION] == FROMPLC)
            if (answer[COMMAND]== command+64)
                if (answer[SERIAL_NO] == currentSNo)
                    return true;
        // Check answer 
        return false;
    }
    

    /**
     * Get command 
     * @return the command
     */
    public byte getCommand() {
        return command;
    }

    /**
     * Set command
     * @param command the command to set
     */
    public void setCommand(byte command) {
        this.command = command;
    }

    /**
     * Get data
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set data to PLC
     * @param data the data to set
     */
    public void setData(byte[] data) 
    {
        if (data == null) 
        {
            size = 0;
            return;
        }
        this.data = data;
        size = (byte) data.length;
    }
    
    /**
     * Set no data to PLC
     */
    public void setData()
    {
        data = null;
        size = 0;
    }
    
    
    /**
     * Set data from a single int value
     * @param value 
     */
    public void setData(int value)
    {
        byte [] d = new byte[1];
        if (value < 128 && value > 0)
            d[0] = (byte) value;
        this.data = d;
        size = (byte) data.length;
    }
    
    /**
     * Message as string
     * @return sting representation of Message
     */
    public String toString()
    {
        StringBuilder data = new StringBuilder();
         for (byte item : this.data) data.append("," + item);
        return "cmd: " + command + "dic: " + this.direction + "size: " + size;
    }

    /*
    private Message(byte cmd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */
    

        
    
    
}
