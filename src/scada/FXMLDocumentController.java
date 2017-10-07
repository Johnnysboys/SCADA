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

    private ObservableList<Greenhouse> data;

    @FXML
    private Button loaderButton;
    @FXML
    private TableView<Greenhouse> displayTable;
    @FXML
    private Button testButton;
    @FXML
    private ComboBox<?> comboTest;
    @FXML
    private TextField idTextField;
    @FXML
    private ComboBox<Article> typeDropdown;
    @FXML
    private Button plantButton;

    @FXML
    private void handleTestButton(ActionEvent event) {
        scadCon.updateAll();

        this.initiateGreenOverview();
    }

    @FXML
    private void handlePlantButton(ActionEvent event) {
        String ree = idTextField.getText();
        try {
            int id = Integer.parseInt(idTextField.getText());
            Article art = typeDropdown.getSelectionModel().getSelectedItem();
            System.out.println(art.toString());
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            System.out.println("Enter an integer for ID.");

        }

    }

    public void setDropdown() {
        Article Salad = new Article(1, "Salad", 23, 500);
        Article Cress = new Article(2, "Cress", 20, 500);
        Article Potato = new Article(3, "Potato", 17, 400);
        typeDropdown.getItems().add(Salad);
        typeDropdown.getItems().add(Cress);
        typeDropdown.getItems().add(Potato);
        typeDropdown.getSelectionModel().select(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setDropdown();

        TableColumn numberCol = new TableColumn("Greenhouse Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<Greenhouse, String>("ghNumber"));
        numberCol.setMinWidth(200);

        TableColumn tempCol = new TableColumn("Temperature");
        tempCol.setCellValueFactory(new PropertyValueFactory<Greenhouse, String>("ghTemp"));
        tempCol.setMinWidth(200);

        TableColumn waterCol = new TableColumn("Water Level");
        waterCol.setCellValueFactory(new PropertyValueFactory<Greenhouse, String>("ghWater"));
        waterCol.setMinWidth(200);

        TableColumn setTempCol = this.createTempCol();
        setTempCol.setMinWidth(200);

        TableColumn idleCol = new TableColumn("Status");
        idleCol.setCellValueFactory(new PropertyValueFactory<Greenhouse, String>("idle"));
        idleCol.setMinWidth(200);

        displayTable.getColumns().addAll(numberCol, tempCol, setTempCol, waterCol, idleCol);
        displayTable.setEditable(true);

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
                TextField txtIP = new TextField("Enter IP-Address");
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

                            if (ip.equals("Enter IP-Address")) {
                                scadCon.addGreenPort(iPort);
                                lbl.setText("Port " + iPort + " added...");
                            } else {
                                scadCon.addGreenPortIp(iPort, ip);
                                lbl.setText("Port " + iPort + " added at " + ip);
                            }

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

    private TableColumn<Greenhouse, String> createTempCol() {
        TableColumn setPointTempCol = new TableColumn("Temperature Setpoint");
        setPointTempCol.setCellValueFactory(new PropertyValueFactory<Greenhouse, String>("ghSetTemp"));
        setPointTempCol.setCellFactory(TextFieldTableCell.forTableColumn());
        //Sætter OnCommit EventHandler, der tager den indtastede værdi og bruger den som ny SetPoint for det gældende Greenhouse
        setPointTempCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Greenhouse, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Greenhouse, String> t) {

                try {
                    int newPoint = Integer.parseInt(t.getNewValue());
                    Greenhouse gh = t.getRowValue();
                    String changedGH = gh.getGhNumber();

                    scadCon.changeSetPoint(changedGH, newPoint);

                } catch (NumberFormatException e) {
                    System.out.println("New value is not an integer.");
                }

            }
        });
        return setPointTempCol;
    }

    public void initiateGreenOverview() {
        data = FXCollections.observableArrayList(scadCon.getGreenList());
        displayTable.setItems(data);
    }

    public void setScadaCon(Scada_Controller scad) {
        this.scadCon = scad;
    }

    public void updateTable(ArrayList<Greenhouse> list) {
        data = FXCollections.observableArrayList(list);
        displayTable.setItems(data);
        displayTable.refresh();
    }

}
