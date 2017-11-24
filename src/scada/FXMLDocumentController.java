package scada;

import dto.OrderINFO;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Jakob
 */
public class FXMLDocumentController implements Initializable {

    private Scada_Controller scadCon;

    private ObservableList<IDeployable> data;
    private ObservableList<OrderINFO> orderData;

    private Stage eStage = new Stage();
    private Label eLabel = new Label("Errorprinting");

    @FXML
    private Button loaderButton;
    @FXML
    private TableView<IDeployable> displayTable;
    @FXML
    private Button plantButton;
    @FXML
    private Button startButton;
    @FXML
    private ComboBox<Integer> numberDropDown;
    @FXML
    private Button harvestButton;
    @FXML
    private Button discardButton;
    @FXML
    private Pane rmiLogonPane;
    @FXML
    private TextField serverField;
    @FXML
    private Button mesConnectButton;
    @FXML
    private TableView<OrderINFO> orderView;
    @FXML
    private Label orderLabel;
    @FXML
    private ListView<String> harvestDelayed;
    @FXML
    private ListView<String> discardDelayed;
    @FXML
    private ListView<String> plantDelayed;
    @FXML
    private Label errorLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Button notifyButton;

    /**
     * EventHandler for the Plant-button.
     *
     * @param event - Fired when the user clicks the Plant-button.
     * @throws RemoteException
     */
    @FXML
    private void handlePlantButton(ActionEvent event) throws RemoteException {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        Article art = Products.getArticle(scadCon.getCurrentOrder().getArticleNumber());
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(25);
        Label lbl = new Label("Planting in deployable " + id + "\n - Are you Sure?");
        dialogVbox.getChildren().add(lbl);

        Button btn1 = new Button();
        btn1.setText("Yes");
        btn1.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    scadCon.plant(id - 1, art);
                    dialog.close();
                } catch (RemoteException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        Button btn2 = new Button();
        btn2.setText("No");
        btn2.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                plantButton.setDisable(false);
                dialog.close();
            }
        }
        );
        dialogVbox.getChildren().addAll(btn1, btn2);
        Scene dialogScene = new Scene(dialogVbox, 200, 150);
        dialogVbox.setMargin(btn1, new Insets(0, 0, 0, 10));
        dialogVbox.setMargin(btn2, new Insets(0, 0, 0, 10));
        dialogVbox.setMargin(lbl, new Insets(0, 0, 0, 10));
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * EventHandler for the Harvest-button.
     *
     * @param event - Fired when the user clicks the Harvest-button.
     * @throws RemoteException
     */
    @FXML
    private void handleHarvestButton(ActionEvent event) throws RemoteException {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(25);
        Label lbl = new Label("Harvesting from deployable " + id + "\n - Are you Sure?");
        dialogVbox.getChildren().add(lbl);

        Button btn1 = new Button();
        btn1.setText("Yes");
        btn1.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    scadCon.harvest(id - 1);
                    dialog.close();
                } catch (RemoteException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        Button btn2 = new Button();
        btn2.setText("No");
        btn2.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        }
        );
        dialogVbox.getChildren().addAll(btn1, btn2);
        Scene dialogScene = new Scene(dialogVbox, 200, 150);
        dialogVbox.setMargin(btn1, new Insets(0, 0, 0, 10));
        dialogVbox.setMargin(btn2, new Insets(0, 0, 0, 10));
        dialogVbox.setMargin(lbl, new Insets(0, 0, 0, 10));
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * EventHandler for the Discard-button.
     *
     * @param event - Fired when the user clicks the Discard-button.
     * @throws RemoteException
     */
    @FXML
    private void handleDiscardButton(ActionEvent event) throws RemoteException {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(25);
        Label lbl = new Label("Discarding from deployable " + id + "\n - Are you Sure?");
        dialogVbox.getChildren().add(lbl);

        Button btn1 = new Button();
        btn1.setText("Yes");
        btn1.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    scadCon.discard(id - 1);
                    dialog.close();
                } catch (RemoteException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        Button btn2 = new Button();
        btn2.setText("No");
        btn2.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        }
        );
        dialogVbox.getChildren().addAll(btn1, btn2);
        Scene dialogScene = new Scene(dialogVbox, 200, 150);
        dialogVbox.setMargin(btn1, new Insets(0, 0, 0, 10));
        dialogVbox.setMargin(btn2, new Insets(0, 0, 0, 10));
        dialogVbox.setMargin(lbl, new Insets(0, 0, 0, 10));
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initiateOrderView();

        loaderButton.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                VBox dialogVbox = new VBox(25);
                Label lbl = new Label("Add Greenhouse");
                dialogVbox.getChildren().add(lbl);
                TextField txtF = new TextField("Enter Port-number");
                txtF.setMaxWidth(200);
                dialogVbox.getChildren().add(txtF);
                TextField txtIP = new TextField("89.239.200.114");
                txtIP.setMaxWidth(200);
                dialogVbox.getChildren().add(txtIP);

                Button btn = new Button();
                btn.setText("Add");
                btn.setOnAction(
                        new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String port = txtF.getText();
                        String ip = txtIP.getText();
                        try {
                            int iPort = Integer.parseInt(port);

                            scadCon.addPortIp(iPort, ip);
                            lbl.setText("Port " + iPort + " added at " + ip);
                            startButton.setDisable(false);
                            int size = scadCon.getDeployList().size();
                            numberDropDown.getItems().clear();
                            for (int i = 0; i < size; i++) {
                                numberDropDown.getItems().add(i + 1);
                            }
                            scadCon.increaseCurrentCapacity();
                        } catch (NumberFormatException e) {
                            txtF.setText("Integers Only");
                        }
                    }
                });
                dialogVbox.setMargin(txtF, new Insets(0, 0, 0, 10));
                dialogVbox.setMargin(txtIP, new Insets(0, 0, 0, 10));
                dialogVbox.setMargin(lbl, new Insets(0, 0, 0, 10));
                dialogVbox.setMargin(btn, new Insets(0, 0, 0, 10));
                dialogVbox.getChildren().add(btn);
                Scene dialogScene = new Scene(dialogVbox, 300, 250);
                dialog.setScene(dialogScene);
                dialog.show();
            }
        });

        mesConnectButton.setOnAction(event -> {
            String ip = serverField.getText();
            errorLabel.setText("");
            if (ip.length() > 0) {
                try {
                    scadCon.createConnector(ip);
                } catch (RemoteException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }

                mesConnectButton.setText("Connecting...");
                mesConnectButton.setDisable(true);

                Task task = new Task<Void>() {
                    @Override
                    public Void call() {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                task.setOnSucceeded(taskFinishEvent -> {
                    errorLabel.setText(scadCon.getError());
                    mesConnectButton.setText("Connected");
                    mesConnectButton.setDisable(false);
                    this.updateOrderView(scadCon.getOrders());
                });
                new Thread(task).start();
            };
        });

    }

    /**
     * Initiates the columns and factories for the IDeployable TableView with
     * columns corresponding to the implementing IDeployable class.
     */
    public void initiateOverview() {

        ArrayList<String> columnNames = scadCon.getDeployList().get(0).getColumnNames();
        ArrayList<String> columnAttri = scadCon.getDeployList().get(0).getColumnAttributes();

        for (int i = 0; i < columnNames.size(); i++) {
            TableColumn numberCol = new TableColumn(columnNames.get(i));
            numberCol.setCellValueFactory(new PropertyValueFactory<IDeployable, String>(columnAttri.get(i)));
            numberCol.setMinWidth(200);
            displayTable.getColumns().add(numberCol);
        }

        data = FXCollections.observableArrayList(scadCon.getDeployList());
        displayTable.setItems(data);
    }

    /**
     * EventHandler for selecting the active order for other parts of the
     * program.
     */
    @FXML
    private void handleRowSelect() {
        OrderINFO order = orderView.getSelectionModel().getSelectedItem();
        if (order == null) {
            orderLabel.setText("//");
            scadCon.setCurrentOrder(null);
            plantButton.setDisable(true);
            typeLabel.setText("No order selected.");
        } else {
            orderLabel.setText(order.getOrderID());
            scadCon.setCurrentOrder(order);
            plantButton.setDisable(false);
            typeLabel.setText(Products.getArticle(scadCon.getCurrentOrder().getArticleNumber()).getName());
        }
    }

    /**
     * EventHandler for selecting deployables from the tableview.
     */
    @FXML
    private void handleDeploySelect() {
        IDeployable ree = displayTable.getSelectionModel().getSelectedItem();
        numberDropDown.getSelectionModel().select(Integer.parseInt(ree.getDeployId()) - 1);
    }

    /**
     * Initiates the columns and factories for the order TableView.
     */
    public void initiateOrderView() {
        TableColumn orderNoCol = new TableColumn("Order Number");
        orderNoCol.setCellValueFactory(new PropertyValueFactory<OrderINFO, String>("orderID"));

        TableColumn artCol = new TableColumn("Article");
        artCol.setCellValueFactory(new PropertyValueFactory<OrderINFO, String>("articleNumber"));

        TableColumn quantCol = new TableColumn("Quantity");
        quantCol.setCellValueFactory(new PropertyValueFactory<OrderINFO, String>("quantity"));

        orderView.getColumns().addAll(orderNoCol, artCol, quantCol);
    }

    /**
     * Saves the Scada_Controller so the GUI can call methods on the controller.
     *
     * @param scad - The Scada_Controller to associate with the GUI-controller.
     */
    public void setScadaCon(Scada_Controller scad) {
        this.scadCon = scad;
    }

    /**
     * Updates the IDeployable TableView with an up-to-date list of deployables.
     *
     * @param list - The list of deployables to show in the GUI.
     */
    public void updateTable(ArrayList<IDeployable> list) {
        data = FXCollections.observableArrayList(list);
        displayTable.setItems(data);
        displayTable.refresh();
    }

    /**
     * Updates the order TableView with a new list of orders.
     *
     * @param list - The list of orders to display on the GUI.
     */
    public void updateOrderView(ArrayList<OrderINFO> list) {
        orderData = FXCollections.observableArrayList(list);
        orderView.setItems(orderData);
        orderView.refresh();
        if (scadCon.getCurrentOrder() == null) {
            plantButton.setDisable(true);
            orderLabel.setText("//");
            typeLabel.setText("No order selected.");
        } else {
            plantButton.setDisable(false);
            typeLabel.setText(Products.getArticle(scadCon.getCurrentOrder().getArticleNumber()).getName());
        }
    }

    /**
     * Updates the ListView with delayed harvest notifications.
     *
     * @param list - The list to display.
     */
    public void updatePlantDelay(ArrayList<String> list) {
        plantDelayed.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Updates the ListView with delayed harvest notifications.
     *
     * @param list - The list to display.
     */
    public void updateHarvestDelay(ArrayList<String> list) {
        harvestDelayed.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Updates the ListView with delayed discard notifications.
     *
     * @param list - The list to display.
     */
    public void updateDiscardDelay(ArrayList<String> list) {
        discardDelayed.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Notifies the MES-server of all delayed notifications.
     *
     * @param event
     * @throws RemoteException
     */
    @FXML
    private void handleNotifyButton(ActionEvent event) throws RemoteException {
        scadCon.notifyDelayed();
    }

    /**
     * One-time method handling the start-button. Loads the correct types from
     * the implemented IDeployable and instantiates the combo-boxes with the
     * correct values. Should only be fired once per start-up.
     *
     * @param event
     */
    @FXML
    private void handleStartButton(ActionEvent event) {
        int size = scadCon.getDeployList().size();
        numberDropDown.getItems().clear();
        for (int i = 0; i < size; i++) {
            numberDropDown.getItems().add(i + 1);
        }
        numberDropDown.getSelectionModel().select(0);
        this.initiateOverview();
        startButton.setVisible(false);
    }

}
