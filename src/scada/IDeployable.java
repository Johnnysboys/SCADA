/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Jakob
 */
public interface IDeployable {

    /**
     * The deployable unit does its update routine.
     * @param d - The date Object for when the update happens.
     */
    public void update(Date d);

    /**
     * Tells the deployable object what number it is, usually told so by the
     * controller.
     *
     * @param i - The number of the deployable object.
     */
    public void setDeployNumber(int i);

    /**
     * Returns the idle status of the deployable. True is occupied and false is idle. 
     * @return - A boolean value reflecting the occupation-status of the deployable.
     */
    public boolean getStatus();
    
    /**
     * Specific to the Greenhouse-class. Has to be in the interface because
     * ChangeSetPoint in Scada_Controller uses it, without knowing the
     * Greenhouse-class.
     *
     * @param kelvin - The temperature in Kelvin that is to be the new setpoint.
     * @return Returns a boolean, true if the message was a success and false if
     * not.
     */
    public boolean SetTemperature(int kelvin);

    /**
     * Returns an ArrayList of all possible articles of a given type of
     * deployable.
     *
     * @return ArrayList<Article>
     */
    //public ArrayList<Article> getArticles();
    
    /**
     * Returns the specific article of the deployable. 
     * @return 
     */
    public Article getCurrentArt();
    
    /**
     * Empties the deployable, and sets the status to idle. 
     * @return String containing the ordernumber the article was related to.
     */
    public String emptyArticle();
    
    /**
     * Deploys a certain article in the given deployable with a given order number. 
     * @param art - The article to deploy.
     * @param orderNo - The order number to associate with the deployed article. 
     */
    public void deployArticle(Article art, String orderNo);

    /**
     * Returns an ArrayList of strings containing the displayed column names for
     * the given type of deployable.
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> getColumnNames();

    /**
     * Returns an ArrayList of strings containing the attribute names of the
     * column attributes.
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> getColumnAttributes();
    
    /**
     * Returns the ID of the deployable.
     * @return - String containing the ID.
     */
    public String getDeployId();

}
