/*
 * The MIT License
 *
 * Copyright 2020 Emre Demir.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.redelsoft.gisconverter;

import java.io.File;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.redelsoft.gisconverter.core.ConvertProcess;
import net.redelsoft.gisconverter.ext.ExtensionsUtil;
import net.redelsoft.gisconverter.ext.FileExtension;
import org.apache.commons.io.FilenameUtils;

/**
 * GIS Converter GUI
 *
 * @author Emre Demir
 */
public class GISConverterGUI extends Application {

    private File selectedFile = null;
    private FileExtension selectedExtension = null;

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ExtensionsUtil extUtil = new ExtensionsUtil();

        stage.setTitle(GUISettings.APP_TITLE);
        ComboBox<FileExtension> extensions = new ComboBox<>();
        extensions.setDisable(true);

        Button convertButton = new Button("Convert");
        convertButton.setDisable(checkConvertButtonDisable());

        /*Fx Title Part*/
        Label titleText = new Label(GUISettings.APP_TITLE);
        titleText.setFont(Font.font(GUISettings.FONT_NAME, FontWeight.BOLD, FontPosture.REGULAR, 25));
        titleText.prefHeight(200d);
        HBox titleBox = new HBox(titleText);
        titleBox.setAlignment(Pos.CENTER);
        /*Fx Title Part Finish*/

 /*Fx Select File Part*/
        Text selectFileText = new Text("Select File: ");
        selectFileText.setFont(Font.font(GUISettings.FONT_NAME, FontWeight.NORMAL, FontPosture.REGULAR, 12));

        TextField filePathText = new TextField();
        filePathText.setDisable(true);
        filePathText.setPrefColumnCount(32);

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Shape File", extUtil.getExtensionsFileChooser())
        );

        Button button = new Button("...");
        button.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                filePathText.setText(selectedFile.getAbsolutePath());
                extensions.setDisable(false);
                extensions.getItems().clear();
                extensions.getItems().addAll(extUtil.getExtensions(FilenameUtils.getExtension(selectedFile.getAbsolutePath())));
            } else {
                filePathText.setText("");
                extensions.getItems().clear();
                extensions.setDisable(true);
            }
            convertButton.setDisable(checkConvertButtonDisable());
        });

        HBox fileSelectionBox = new HBox();
        fileSelectionBox.getChildren().add(filePathText);
        fileSelectionBox.getChildren().add(button);
        fileSelectionBox.setAlignment(Pos.CENTER_LEFT);
        /*Fx Select File Part Finish*/

 /*Fx Select Output Format Part*/
        Label outpuFormatText = new Label("Output Format: ");
        outpuFormatText.setFont(Font.font(GUISettings.FONT_NAME, FontWeight.NORMAL, FontPosture.REGULAR, 12));

        extensions.setPromptText("Select");
        extensions.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends FileExtension> ov, final FileExtension oldValue, final FileExtension newValue) -> {
                    selectedExtension = newValue;
                    convertButton.setDisable(checkConvertButtonDisable());
                });

        HBox outputFormatSelectBox = new HBox();
        outputFormatSelectBox.setAlignment(Pos.CENTER_LEFT);
        outputFormatSelectBox.getChildren().add(extensions);
        /*Fx Select Output Format Part Finish*/

        /*Fx Convert Button Part*/
        convertButton.setOnAction(e -> {
            convertButton.setDisable(true);
            try {
                ConvertProcess p = new ConvertProcess(selectedFile, selectedExtension);
                p.execute();
                showAlert(GUISettings.ALERT_SUCCESS_HEADER, GUISettings.ALERT_SUCCESS_MESSAGE, false);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(GUISettings.ALERT_ERROR_HEADER, GUISettings.ALERT_ERROR_MESSAGE, true);
            } finally {
                convertButton.setDisable(false);
            }
        });
        /*Fx Convert Button Part Finish*/

        GridPane root = new GridPane();
        root.setHgap(0);
        root.setVgap(10);

        root.addRow(0, new Label(""), titleBox);
        root.addRow(1, selectFileText, fileSelectionBox);
        root.addRow(2, outpuFormatText, outputFormatSelectBox);
        root.addRow(3, new Label(""), convertButton);
        root.setMinSize(550, 250);
        root.setMaxSize(550, 250);
        root.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: gray;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(GUISettings.APP_TITLE);
        stage.show();
    }

    public boolean checkConvertButtonDisable() {
        return !(selectedFile != null && selectedExtension != null);
    }

    public void showAlert(String title, String message, boolean error) {
        Alert.AlertType alertType = null;
        if (error) {
            alertType = Alert.AlertType.ERROR;
        } else {
            alertType = Alert.AlertType.INFORMATION;
        }

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
