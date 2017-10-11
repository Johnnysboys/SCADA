/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

import GreenhouseAPI.Greenhouse;
import java.net.URL;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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

    @FXML
    private Button loaderButton;
    @FXML
    private TableView<IDeployable> displayTable;
    private TextField idTextField;
    @FXML
    private ComboBox<Article> typeDropdown;
    @FXML
    private Button plantButton;
    @FXML
    private Button startButton;
    @FXML
    private ComboBox<Integer> numberDropDown;
    @FXML
    private Button setpointButton;
    @FXML
    private TextField setpointField;
    @FXML
    private Button harvestButton;
    @FXML
    private Button discardButton;
    @FXML
    private ComboBox<?> comboTest;

    @FXML
    private void handlePlantButton(ActionEvent event) {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        Article art = typeDropdown.getSelectionModel().getSelectedItem();
        scadCon.plant(id-1, art);
        System.out.println("Planting");

    }

    @FXML
    private void handleHarvestButton(ActionEvent event) {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        scadCon.harvest(id - 1);
        System.out.println("Harvesting");
    }

    @FXML
    private void handleDiscardButton(ActionEvent event) {
        int id = numberDropDown.getSelectionModel().getSelectedItem();
        scadCon.discard(id - 1);
        System.out.println("Discarding");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
                TextField txtIP = new TextField("192.168.0.10");
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
                            startButton.setVisible(true);

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
     * Initiates the tableview with columns corresponding to the type of the
     * IDeployable implementing class.
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

    public void setScadaCon(Scada_Controller scad) {
        this.scadCon = scad;
    }

    public void updateTable(ArrayList<IDeployable> list) {
        data = FXCollections.observableArrayList(list);
        displayTable.setItems(data);
        displayTable.refresh();
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        int size = scadCon.getDeployList().size();
        numberDropDown.getItems().clear();
        for (int i = 0; i < size; i++) {
            numberDropDown.getItems().add(i + 1);
        }
        typeDropdown.getItems().clear();
        typeDropdown.getItems().addAll(scadCon.getDeployList().get(0).getArticles());
        this.initiateOverview();
    }

}
