package MVC;

import MVC.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static MVC.App.way;

public class Controller extends MVC.View {

    private MVC.Model model;

    public Controller(){}

    public void setModel(MVC.Model model) {
        this.model = model;
    }

    @Override
    @FXML
    public void addBtnGroup(ActionEvent event){
        super.addBtnGroup(event);
    }

    @FXML
    public void initialize(){
        model = App.getModel();
        if (Files.exists(Path.of(way + "\\data.txt"))){
            try {
                File file = new File(way + "\\data.txt");
                Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
                while (sc.hasNext()) {
                    String id = sc.next();
                    String name = sc.nextLine();
                    Button btn = new Button(name);
                    btn.setId(id);
                    btn.setOnAction(onClick);
                    GroupList.getChildren().add(btn);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            super.makeModel();
        }
    }

}
