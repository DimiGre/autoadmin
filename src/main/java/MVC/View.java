package MVC;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import admin.Downloader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static MVC.App.way;

public class View {

    private MVC.Model model;

    public void setModel(MVC.Model _model) {
        model = _model;
    }

    private VBox contMakersList;

    @FXML
    public Button addGroup;

    @FXML
    public VBox GroupList;

    @FXML
    public VBox GroupControl;

    public View(){}


    public void addBtn(Button btn, Pane pane){
        pane.getChildren().add(btn);
    }

    public String getGroupURLDialog(){
        TextInputDialog tid = new TextInputDialog();
        tid.setHeaderText("Укажите ссылку на группу");
        tid.showAndWait();
        return tid.getEditor().getText();
    }

    public void addBtnGroup(ActionEvent event){
        String GroupURL = getGroupURLDialog();
        Button btn = new Button();
        btn.setOnAction(onClick);
        addBtn(btn, GroupList);

        Downloader.PhotoInfo.Response response = null;
        try {
            response = Downloader.getID(GroupURL);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        btn.setId(response.id);
        try {
            btn.setText(new String(response.name.getBytes("windows-1251"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (Files.exists(Path.of(way + "\\data.txt")) == false) {
            try {
                Files.createFile(Path.of(way + "\\data.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(way + "\\data.txt", true);
            fileWriter.write(response.id + " " + response.name + '\n');
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    EventHandler<ActionEvent> onClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Button btn = (Button)event.getSource();
            GroupControl.getChildren().clear();

            HBox name = new HBox(new Label("ID выбранной группы: "), new Label(btn.getText()));
            GroupControl.getChildren().addAll(name);
            GroupControl.getChildren().add(new Label("Список контент-мейкеров:"));
            Button addContMaker = new Button("Добавть контент-мейкера");
            addContMaker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Downloader.PhotoInfo.Response response = Downloader.getID(getGroupURLDialog());
                    model.addContentMaker(response);
                }
            });
            contMakersList = new VBox();
            contMakersList.getChildren().add(addContMaker);
            addContMaker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Downloader.PhotoInfo.Response response = Downloader.getID(getGroupURLDialog());
                    model.addContentMaker(response);

                    try {
                        System.out.println(response.name.getBytes());
                        addContentMaker(response.id, new String(response.name.getBytes("windows-1251"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
            GroupControl.getChildren().add(contMakersList);
            TextField textField = new TextField("30");
            Button setTime = new Button("Принять");
            setTime.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    model.setTimeBetweenPosts(Integer.parseInt(textField.getText()) * 60000);
                }
            });
            GroupControl.getChildren().add(new HBox(new Label("Перерыв между постами(мин): "), textField, setTime));
            model.startThread(Integer.parseInt(btn.getId()));
        }
    };

    public void addContentMaker(String ID, String name){
        System.out.println(name + " added");
        Label label = new Label(name);
        label.setId(ID);
        if(contMakersList != null) {
            Button button = new Button("Удалить");
            button.setId(ID);
            HBox hBox = new HBox(new HBox(label, button));
            contMakersList.getChildren().removeAll(hBox);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    model.removeContMaker(ID);
                    contMakersList.getChildren().removeAll(hBox);
                }
            });
            contMakersList.getChildren().add(hBox);
        }
    }

    protected void makeModel() {
        App.setModel(this);
        model = App.getModel();
    }
}
