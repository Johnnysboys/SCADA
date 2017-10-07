/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

/**
 *
 * @author Jakob
 */
public class Article {
    
    private int articleID;
    private String name;
    private int prefTemp;
    private int prefWat;
    
    public Article(int id, String name, int prefT, int prefW){
        this.articleID = id;
        this.name = name;
        this.prefTemp = prefT;
        this.prefWat = prefW;
    }
    
    @Override
    public String toString(){
        String display = this.name;
        return display;
    }
    
    
    

    public int getArticleID() {
        return articleID;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrefTemp() {
        return prefTemp;
    }

    public void setPrefTemp(int prefTemp) {
        this.prefTemp = prefTemp;
    }

    public int getPrefWat() {
        return prefWat;
    }

    public void setPrefWat(int prefWat) {
        this.prefWat = prefWat;
    }
    
    
    
    
}
