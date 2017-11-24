package scada;

import GreenhouseAPI.Greenhouse;
import PLCCommunication.PLCConnection;
import PLCCommunication.UDPConnection;
import RMIComms.Client;
import dto.OrderINFO;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Jakob
 */
public class DeployHandler {

    private AtomicInteger capacity = new AtomicInteger();

    private List<IDeployable> deployArray = Collections.synchronizedList(new ArrayList<IDeployable>());
    private List<String> harvestArray = Collections.synchronizedList(new ArrayList<String>());
    private List<String> discardArray = Collections.synchronizedList(new ArrayList<String>());
    private List<String> plantArray = Collections.synchronizedList(new ArrayList<String>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DeployHandler() {
        Runnable updater = () -> {
            this.update();
        };
        // Sets how often to cycle through deployables and update them. Second integer determines cycle length.
        scheduler.scheduleWithFixedDelay(updater, 5, 5, TimeUnit.SECONDS);
    }

    public AtomicInteger getCapacity() {
        return capacity;
    }

    public List<String> getHarvestArray() {
        return harvestArray;
    }

    public List<String> getDiscardArray() {
        return discardArray;
    }

    public List<String> getPlantArray() {
        return plantArray;
    }

    /**
     * Iterates through all connected deployables and updates their information.
     */
    public void update() {
        Date date = new Date();
        System.out.println("Updated at: " + date.toString());
        for (IDeployable deploy : deployArray) {
            new Thread(() -> {
                deploy.update(date);
            }).start();
        }
    }

    /**
     * Adds a deployable to the DeployHandler
     *
     * @param port - The port of the newly added deployable.
     * @param ip - The IP-address of the newly added deployable.
     * @return - An integer containing the number of deployables currently
     * added.
     */
    public int addDeployable(int port, String ip) {
        PLCConnection con = new UDPConnection(port, ip);
        IDeployable deploy = new Greenhouse(con);
        deployArray.add(deploy);
        deploy.setDeployNumber(deployArray.size());
        capacity.getAndIncrement();
        return deployArray.size();
    }

    /**
     * Returns the list of deployables currently connected to the SCADA.
     *
     * @return List of IDeployable-objects.
     */
    public List<IDeployable> getDeployList() {
        return this.deployArray;
    }

    /**
     * Registers the planting of an article in a specific greenhouse.
     *
     * @param id - ID of the deployable to plant in.
     * @param order
     * @param rmiClient
     * @return
     * @throws java.rmi.RemoteException
     */
    public OrderINFO plant(int id, OrderINFO order, Client rmiClient) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        Article art = Products.getArticle(order.getArticleNumber());

        if (order == null) {
            System.out.println("No order selected as active.");
        } else {
            if (!deploy.getStatus()) {
                String orderNo = order.getOrderID();
                try {
                    capacity.getAndDecrement();
                    deploy.deployArticle(art, order.getOrderID());

                    new Thread(() -> {
                        try {
                            rmiClient.notifyPlant(orderNo);
                        } catch (RemoteException ex) {
                            System.out.println("No MES-Server connected - Plant added to queue.");
                            plantArray.add(orderNo);
                        }
                    }).start();
                } catch (NullPointerException e) {
                    System.out.println("No MES-Server connected - Plant added to queue.");
                    plantArray.add(orderNo);
                }
            } else {
                System.out.println("Tried planting a non-idle deployable.");
            }
        }
        return order;
    }

    /**
     * Registers a harvest from a deployable.
     *
     * @param id - ID of the deployable that is harvested from.
     * @param rmiClient
     * @throws java.rmi.RemoteException
     */
    public void harvest(int id, Client rmiClient) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStatus()) {
            String orderNo = deploy.emptyArticle();
            try {
                new Thread(() -> {
                    try {
                        rmiClient.notifyHarvest(orderNo);
                    } catch (RemoteException ex) {
                        System.out.println("No MES-Server connected - Harvest added to queue.");
                        harvestArray.add(orderNo);
                    }
                }).start();
                capacity.getAndIncrement();

            } catch (NullPointerException e) {
                System.out.println("No MES-Server connected - Harvest added to queue.");
                harvestArray.add(orderNo);
            }
        } else {
            System.out.println("Tried harvesting an idle deployable.");
        }
    }

    /**
     * Registers a discard from a deployable.
     *
     * @param id - ID of the deployable that is discarded from.
     * @param rmiClient
     * @throws java.rmi.RemoteException
     */
    public void discard(int id, Client rmiClient) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStatus()) {
            String orderNo = deploy.emptyArticle();
            try {
                new Thread(() -> {
                    try {
                        rmiClient.notifyDiscard(orderNo);
                    } catch (RemoteException ex) {
                        System.out.println("No MES-Server connected - Discard added to queue.");
                        discardArray.add(orderNo);
                    }
                }).start();
                capacity.getAndIncrement();
            } catch (NullPointerException e) {
                System.out.println("No MES-Server connected - Discard added to queue.");
                discardArray.add(orderNo);
            }
        } else {
            System.out.println("Tried discarding an idle deployable.");
        }
    }

    /**
     * Notifies the MES-server of all delayed notifications, delayed while no
     * connection was established.
     *
     * @param rmiClient - The to send notifications through.
     * @throws RemoteException
     */
    public void notifyDelayed(Client rmiClient) throws RemoteException {
        for (String e : plantArray) {
            rmiClient.notifyPlant(e);
            plantArray.remove(e);
        }
        for (String e : harvestArray) {
            rmiClient.notifyHarvest(e);
            harvestArray.remove(e);
        }
        for (String e : discardArray) {
            rmiClient.notifyDiscard(e);
            discardArray.remove(e);
        }
    }

}
