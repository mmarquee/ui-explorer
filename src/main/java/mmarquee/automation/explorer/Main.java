package mmarquee.automation.explorer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import mmarquee.automation.AutomationTreeWalker;
import mmarquee.automation.UIAutomation;

import java.io.File;

public class Main extends Application {

    // Originally from http://www.java2s.com/Tutorials/Java/JavaFX/0660__JavaFX_Tree_View.htm

    private TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = (File) getValue();
                    isLeaf = f.isFile();
                }
                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(
                    TreeItem<File> TreeItem) {
                File f = TreeItem.getValue();
                if (f == null) {
                    return FXCollections.emptyObservableList();
                }
                if (f.isFile()) {
                    return FXCollections.emptyObservableList();
                }
                File[] files = f.listFiles();
                if (files != null) {
                    ObservableList<TreeItem<File>> children = FXCollections
                            .observableArrayList();
                    for (File childFile : files) {
                        children.add(createNode(childFile));
                    }
                    return children;
                }
                return FXCollections.emptyObservableList();
            }
        };
    }

    private UIAutomation automation = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Get the top most level
        automation = UIAutomation.getInstance();

        AutomationTreeWalker walker = automation.getControlViewWalker();

        // Create UI
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, 600, 400);

        Button btnRefresh = new Button("Refresh");
        CheckBox chkAutoRefresh = new CheckBox("Auto Refresh");

        ToolBar toolBar1 = new ToolBar();
        toolBar1.getItems().addAll(
                new Separator(),
                btnRefresh,
                new Separator(),
                chkAutoRefresh,
                new Separator()
        );

        VBox vbox = new VBox();

        TreeItem<File> root = createNode(new File("c:/"));
        TreeView treeView = new TreeView<File>(root);

        vbox.getChildren().add(treeView);
        ((BorderPane) scene.getRoot()).getChildren().add(vbox);

        pane.setTop(toolBar1);
        pane.setLeft(treeView);

        primaryStage.setScene(scene);
        primaryStage.setTitle("UI Automation Explorer");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
