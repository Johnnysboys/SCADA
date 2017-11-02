/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

import dto.OrderINFO;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    @FXML
    private Button loaderButton;
    @FXML
    private TableView<IDeployable> displayTable;
    @FXML
    private ComboBox<Article> typeDropdown;
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
    
    /**
     * EventHandler for the ServerButton. Used to connect to MES-Server.
     * @param event - Fired when the user clicks the Server Button.
     * @throws RemoteException 
     */
    @FXML
    private void handleServerButton(ActionEvent event) throws RemoteException {
        String ip = serverField.getText();
        scadCon.createConnector(ip);
    }

    @FXML
    private void handlePlantButton(ActionEvent event) throws RemoteException {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        Article art = typeDropdown.getSelectionModel().getSelectedItem();
        scadCon.plant(id - 1, art);
    }

    @FXML
    private void handleHarvestButton(ActionEvent event) throws RemoteException {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        scadCon.harvest(id - 1);
    }

    @FXML
    private void handleDiscardButton(ActionEvent event) throws RemoteException {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        scadCon.discard(id - 1);
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
                            scadCon.setMaxCapacity(scadCon.getMaxCapacity()+1);
                            scadCon.setCurrentCapacity(scadCon.getCurrentCapacity()+1);

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
        } else {
            orderLabel.setText(order.getOrderID());
            scadCon.setCurrentOrder(order);
        }
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
    }
    
    /**
     * Updates the ListView with delayed harvest notifications.
     * @param list 
     */
    public void updateHarvestDelay(ArrayList<String> list){
        harvestDelayed.setItems(FXCollections.observableArrayList(list));
    }
    
    /**
     * Updates the ListView with delayed discard notifications.
     * @param list 
     */
    public void updateDiscardDelay(ArrayList<String> list) {
        discardDelayed.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * One-time medthod handling the start-button. Loads the correct types from
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

        typeDropdown.getItems().clear();
        typeDropdown.getItems().addAll(scadCon.getDeployList().get(0).getArticles());
        typeDropdown.getSelectionModel().select(0);
        this.initiateOverview();
        startButton.setVisible(false);
    }

}
