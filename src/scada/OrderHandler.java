package scada;

import dto.OrderINFO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jakob
 */
public class OrderHandler {
    
    private List<OrderINFO> orderArray = Collections.synchronizedList(new ArrayList<OrderINFO>());
    
    private OrderINFO currentOrder = null;
    
    public OrderHandler() {
        
    }
    
    /**
     * Adds an order to the OrderHandler
     * @param order - The order to add.
     */
    public void addOrder(OrderINFO order) {
        orderArray.add(order);
    }
    
    /**
     * Fetches the currently active order.
     * @return OrderINFO
     */
    public OrderINFO getCurrentOrder() {
        return currentOrder;
    }
    
    /**
     * Sets the current order.
     * @param currentOrder 
     */
    public void setCurrentOrder(OrderINFO currentOrder) {
        this.currentOrder = currentOrder;
    }
    
    /**
     * Decreases the quantity of the current order.
     */
    public void decreaseCurrentOrder() {
        currentOrder.setQuantity(currentOrder.getQuantity() - 1);
        if(currentOrder.getQuantity()<=0){
            orderArray.remove(currentOrder);
            currentOrder = null;
        }
    }
    
    /**
     * Returns a list of all orders currently in the OrderHandler.
     * @return List
     */
    public List<OrderINFO> getOrderArray() {
        return orderArray;
    }
    
}
