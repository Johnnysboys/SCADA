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
public final class Products {

    private Products() {

    }

    public static Article getArticle(String art) {
        Article article = null;
        if (art.equals("2014203")) {
            article = new Article(art, "Salad", 23, 500);
        } else if (art.equals("2014201")) {
            article = new Article(art, "Cress", 20, 500);
        } else if (art.equals("2014101")) {
            article = new Article(art, "Potato", 17, 500);
        } else if (art.equals("2014001")) {
            article = new Article(art, "Tulip", 19, 500);
        } else {
            System.out.println("No matching article was found.");
        }

        return article;
    }
}
