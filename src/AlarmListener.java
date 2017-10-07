
import java.util.BitSet;
import java.util.EventListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Not used 
 * Preparation for asynchronous approuch
 * @author Steffen Skov
 */
public interface AlarmListener extends EventListener
{
    void alarmRise(BitSet s);
    
}
