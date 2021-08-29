package sample;

import solver.EdmondsKarpSolver;
import solver.FordFulkersonSolver;
import entity.Network;
import solver.NetworkFlowSolver;
import com.jfoenix.controls.*;
import entity.Arrow;
import entity.Edge;
import entity.Node;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.controlsfx.control.HiddenSidesPane;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static entity.Node.copyList;

public class MenuController implements Initializable, ChangeListener {
    static final long INF = Long.MAX_VALUE / 2;


    @FXML
    private HiddenSidesPane hiddenPane;
    @FXML
    private JFXButton canvasBackButton, clearButton, resetButton, playPauseButton;
    @FXML
    private JFXToggleButton addNodeButton, addEdgeButton, edmonsButton, fordButton;
    @FXML
    private ToggleGroup algoToggleGroup;
    @FXML
    private Pane viewer;
    @FXML
    private Group canvasGroup;
    @FXML
    private Line edgeLine;
    @FXML
    private Label sourceText = new Label("Source"), weight;
    @FXML
    private Pane border;
    @FXML
    private Arrow arrow;
    @FXML
    private JFXNodesList nodeList;
    @FXML
    private JFXSlider slider = new JFXSlider();
    @FXML
    private ImageView playPauseImage, openHidden;
    @FXML
    private Button stepButton;
    @FXML
    private Label detailsLabel;

    boolean menuBool = false;
    ContextMenu globalMenu;

    int nNode = 0, time = 500;
    NodeFX selectedNode = null;
    List<Node> listNode = new ArrayList<>();
    List<NodeFX> circles = new ArrayList<>();
    List<Edge> mstEdges = new ArrayList<>(), realEdges = new ArrayList<>();
    List<Shape> edges = new ArrayList<>();
    boolean addNode = true, addEdge = false, calculate = false,
            calculated = false, playing = false, paused = false, pinned = false;
    List<Label> distances = new ArrayList<Label>(), visitTime = new ArrayList<>(), lowTime = new ArrayList<Label>();

    public SequentialTransition st;

    public AnchorPane hiddenRoot = new AnchorPane();

    public static TextArea textFlow = new TextArea();
    public ScrollPane textContainer = new ScrollPane();
    private int count_ford = -1;
    private int count_edmons = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("In intit");
        hiddenPane.setContent(canvasGroup);
//        anchorRoot.setManaged(false);

        viewer.prefHeightProperty().bind(border.heightProperty());
        viewer.prefWidthProperty().bind(border.widthProperty());
        AddNodeHandle(null);
        addEdgeButton.setDisable(true);
        addNodeButton.setDisable(true);

        //Set back button action
        canvasBackButton.setOnAction(e -> {
            try {
                ResetHandle(null);
                Parent root = FXMLLoader.load(getClass().getResource("Panel1FXML.fxml"));

                Scene scene = new Scene(root);
                sample.Main.primaryStage.setScene(scene);
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        //Setup Slider
        slider = new JFXSlider(10, 1000, 500);
        slider.setPrefWidth(150);
        slider.setPrefHeight(80);
        slider.setSnapToTicks(true);
        slider.setMinorTickCount(100);
        slider.setIndicatorPosition(JFXSlider.IndicatorPosition.RIGHT);
        slider.setBlendMode(BlendMode.MULTIPLY);
        slider.setCursor(Cursor.CLOSED_HAND);
        nodeList.addAnimatedNode(slider);
        nodeList.setSpacing(50D);
        nodeList.setRotate(270D);
        slider.toFront();
        nodeList.toFront();
        slider.valueProperty().addListener(this);

        hiddenRoot.setPrefWidth(220);
        hiddenRoot.setPrefHeight(581);

        hiddenRoot.setCursor(Cursor.DEFAULT);

        //Set Label "Detail"
        Label detailLabel = new Label("Detail");
        detailLabel.setPrefSize(hiddenRoot.getPrefWidth() - 20, 38);
        detailLabel.setAlignment(Pos.CENTER);
        detailLabel.setFont(new Font("Roboto", 20));
        detailLabel.setPadding(new Insets(7, 40, 3, -10));
        detailLabel.setStyle("-fx-background-color: #dcdde1;");
        detailLabel.setLayoutX(35);

        //Set TextFlow pane properties
        textFlow.setPrefSize(hiddenRoot.getPrefWidth(), hiddenRoot.getPrefHeight() - 2);
//        textFlow.prefHeightProperty().bind(hiddenRoot.heightProperty());
        textFlow.setStyle("-fx-background-color: #dfe6e9;");
        textFlow.setLayoutY(39);
        textContainer.setLayoutY(textFlow.getLayoutY());
        textFlow.setPadding(new Insets(5, 0, 0, 5));
        textFlow.setEditable(false);
        textContainer.setContent(textFlow);

        //Set Pin/Unpin Button
        JFXButton pinUnpin = new JFXButton();
        pinUnpin.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        ImageView imgPin = new ImageView(new Image(getClass().getResourceAsStream("/icon/pinned.png")));
        imgPin.setFitHeight(20);
        imgPin.setFitWidth(20);
        ImageView imgUnpin = new ImageView(new Image(getClass().getResourceAsStream("/icon/unpinned.png")));
        imgUnpin.setFitHeight(20);
        imgUnpin.setFitWidth(20);
        pinUnpin.setGraphic(imgPin);

        pinUnpin.setPrefSize(20, 39);
        pinUnpin.setButtonType(JFXButton.ButtonType.FLAT);
        pinUnpin.setStyle("-fx-background-color: #dcdde1;");
        pinUnpin.setOnMouseClicked(e -> {
            if (pinned) {
                pinUnpin.setGraphic(imgPin);
                hiddenPane.setPinnedSide(null);
                pinned = false;
            } else {
                pinUnpin.setGraphic(imgUnpin);
                hiddenPane.setPinnedSide(Side.RIGHT);
                pinned = true;
            }
        });

        //Add Label and TextFlow to hiddenPane
//        hiddenRoot.getChildren().addAll(pinUnpin, detailLabel, textContainer);
//        hiddenPane.setRight(hiddenRoot);
//        hiddenRoot.setOnMouseEntered(e -> {
//            hiddenPane.setPinnedSide(Side.RIGHT);
//            openHidden.setVisible(false);
//            e.consume();
//        });
//        hiddenRoot.setOnMouseExited(e -> {
//            if (!pinned) {
//                hiddenPane.setPinnedSide(null);
//                openHidden.setVisible(true);
//            }
//            e.consume();
//        });
//        hiddenPane.setTriggerDistance(60);

    }

    /**
     * Change Listener for change in speed slider values.
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        int temp = (int) slider.getValue();

        if (temp > 500) {
            int diff = temp - 500;
            temp = 500;
            temp -= diff;
            temp += 10;
        } else if (temp < 500) {
            int diff = 500 - temp;
            temp = 500;
            temp += diff;
            temp -= 10;
        }
        time = temp;
        System.out.println(time);
    }

    /**
     * Handles events for mouse clicks on the canvas. Adds a new node on the
     * drawing canvas where mouse is clicked.
     *
     * @param ev
     */
    @FXML
    public void handle(MouseEvent ev) {
        if (addNode) {
            if (nNode == 1) {
                addNodeButton.setDisable(false);
            }
            if (nNode == 2) {
                addEdgeButton.setDisable(false);
                AddNodeHandle(null);
            }

            if (!ev.getSource().equals(canvasGroup)) {
                if (ev.getEventType() == MouseEvent.MOUSE_RELEASED && ev.getButton() == MouseButton.PRIMARY) {
                    if (menuBool == true) {
                        System.out.println("here" + ev.getEventType());
                        menuBool = false;
                        return;
                    }
                    nNode++;
                    NodeFX circle = new NodeFX(ev.getX(), ev.getY(), 1.2, String.valueOf(nNode));
                    canvasGroup.getChildren().add(circle);

                    circle.setOnMousePressed(mouseHandler);
                    circle.setOnMouseReleased(mouseHandler);
                    circle.setOnMouseDragged(mouseHandler);
                    circle.setOnMouseExited(mouseHandler);
                    circle.setOnMouseEntered(mouseHandler);

                    ScaleTransition tr = new ScaleTransition(Duration.millis(100), circle);
                    tr.setByX(10f);
                    tr.setByY(10f);
                    tr.setInterpolator(Interpolator.EASE_OUT);
                    tr.play();

                }
            }
        }
    }

    /**
     * Checks if an edge already exists between two nodes before adding a new
     * edge.
     *
     * @param u = selected node
     * @param v = second selected node
     * @return True if edge already exists. Else false.
     */
    boolean edgeExists(NodeFX u, NodeFX v) {
        for (Edge e : realEdges) {
            if (e.getSource() == u.node && e.getTarget() == v.node) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an edge between two selected nodes. Handles events for mouse clicks
     * on a node.
     */
    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            NodeFX circle = (NodeFX) mouseEvent.getSource();
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (!circle.isSelected) {
                    if (selectedNode != null) {
                        if (addEdge && !edgeExists(selectedNode, circle)) {
                            weight = new Label();
                            System.out.println("Adding Edge");
                            //Adds the edge between two selected nodes
                            arrow = new Arrow(selectedNode.point.x, selectedNode.point.y, circle.point.x, circle.point.y);
                            canvasGroup.getChildren().add(arrow);
                            arrow.setId("arrow");

                            //Adds weight between two selected nodes
                            weight.setLayoutX(((selectedNode.point.x) + (circle.point.x)) / 2);
                            weight.setLayoutY(((selectedNode.point.y) + (circle.point.y)) / 2);

                            TextInputDialog dialog = new TextInputDialog("0");
                            dialog.setTitle(null);
                            dialog.setHeaderText("Enter Weight of the Edge :");
                            dialog.setContentText(null);

                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                weight.setText(result.get());
                            } else {
                                weight.setText("0");
                            }
                                canvasGroup.getChildren().add(weight);
                            Shape line_arrow = null;
                            Edge temp = null;
                                temp = new Edge(selectedNode.node, circle.node, Long.valueOf(weight.getText()), arrow, weight);
                                selectedNode.node.getAdjacents().add(temp);
                                edges.add(arrow);
                                line_arrow = arrow;
                                realEdges.add(temp);

                            RightClickMenu rt = new RightClickMenu(temp);
                            ContextMenu menu = rt.getMenu();
                                rt.changeId.setText("Change Weight");
                            final Shape la = line_arrow;
                            line_arrow.setOnContextMenuRequested(e -> {
                                System.out.println("In Edge Menu :" + menuBool);

                                if (menuBool == true) {
                                    globalMenu.hide();
                                    menuBool = false;
                                }
                                if (addEdge || addNode) {
                                    globalMenu = menu;
                                    menu.show(la, e.getScreenX(), e.getScreenY());
                                    menuBool = true;
                                }
                            });
                            menu.setOnAction(e -> {
                                menuBool = false;
                            });
                        }
                        if (addNode || (calculate && !calculated) || addEdge) {
                            selectedNode.isSelected = false;
                            FillTransition ft1 = new FillTransition(Duration.millis(300), selectedNode, Color.RED, Color.BLACK);
                            ft1.play();
                        }
                        selectedNode = null;
                        return;
                    }

                    FillTransition ft = new FillTransition(Duration.millis(300), circle, Color.BLACK, Color.RED);
                    ft.play();
                    circle.isSelected = true;
                    selectedNode = circle;
                } else {
                    circle.isSelected = false;
                    FillTransition ft1 = new FillTransition(Duration.millis(300), circle, Color.RED, Color.BLACK);
                    ft1.play();
                    selectedNode = null;
                }

            }
        }

    };

    /**
     * Get a random node to start Articulation Point.
     *
     * @return A node from the current node list.
     */
    private Node getRandomStart() {
        return circles.get(0).node;
    }

    /**
     * Event handler for the Play/Pause button.
     *
     * @param event
     */
    @FXML
    public void PlayPauseHandle(ActionEvent event) {
        System.out.println("IN PLAYPAUSE");
        System.out.println(playing + " " + paused);

        try{
            if (playing && st != null && st.getStatus() == Animation.Status.RUNNING) {
                Image image = new Image(getClass().getResourceAsStream("/play_arrow_black_48x48.png"));
                playPauseImage.setImage(image);
                System.out.println("Pausing");
                st.pause();
                paused = true;
                playing = false;
                return;
            } else if (paused && st != null) {
            } else if (paused && st != null) {
                Image image = new Image(getClass().getResourceAsStream("/pause_black_48x48.png"));
                playPauseImage.setImage(image);
                if(st.getStatus() == Animation.Status.PAUSED)
                    st.play();
                else if(st.getStatus() == Animation.Status.STOPPED)
                    st.playFromStart();
                playing = true;
                paused = false;
                return;
            }
        } catch(Exception e){
            System.out.println("Error while play/pause: " + e);
            ClearHandle(null);
        }
    }

    /**
     * Event handler for the Reset button. Clears all the lists and empties the
     * canvas.
     *
     * @param event
     */
    @FXML
    public void ResetHandle(ActionEvent event) {
        nNode = 0;
        canvasGroup.getChildren().clear();
        canvasGroup.getChildren().addAll(viewer);
        selectedNode = null;
        circles = new ArrayList<NodeFX>();
        distances = new ArrayList<Label>();
        visitTime = new ArrayList<Label>();
        lowTime = new ArrayList<Label>();
        addNode = true;
        addEdge = false;
        calculate = false;
        calculated = false;
        addNodeButton.setSelected(true);
        addEdgeButton.setSelected(false);
        addEdgeButton.setDisable(true);
        addNodeButton.setDisable(false);
        edmonsButton.setSelected(false);
        fordButton.setSelected(false);

//        algo = new Algorithm();
        Image image = new Image(getClass().getResourceAsStream("/icon/pause_black_48x48.png"));
        hiddenPane.setPinnedSide(null);
        detailsLabel.setText("");
        listNode.clear();
        realEdges.clear();
        count_edmons = -1;
        count_ford = -1;

        playing = false;
        paused = false;
    }

    /**
     * Event handler for the Clear button. Re-initiates the distance and node
     * values and labels.
     *
     * @param event
     */
    @FXML
    public void ClearHandle(ActionEvent event) {
        if(st != null && st.getStatus() != Animation.Status.STOPPED)
            st.stop();
        if(st != null) st.getChildren().clear();
        menuBool = false;
        selectedNode = null;
        calculated = false;
        System.out.println("IN CLEAR:" + circles.size());
        for (NodeFX n : circles) {
            n.isSelected = false;
            n.node.setVisited(0);
            FillTransition ft1 = new FillTransition(Duration.millis(300), n);
            ft1.setToValue(Color.BLACK);
            ft1.play();
        }
        for (Shape x : edges) {
                FillTransition ftEdge = new FillTransition(Duration.millis(time), x);
                ftEdge.setToValue(Color.BLACK);
                ftEdge.play();
        }
        canvasGroup.getChildren().remove(sourceText);
        for (Label x : distances) {
            x.setText("Distance : INFINITY");
            canvasGroup.getChildren().remove(x);
        }
        for (Label x : visitTime) {
            x.setText("Visit : 0");
            canvasGroup.getChildren().remove(x);
        }
        for (Label x : lowTime) {
            x.setText("Low Value : NULL");
            canvasGroup.getChildren().remove(x);
        }
        textFlow.clear();

        Image image = new Image(getClass().getResourceAsStream("/icon/pause_black_48x48.png"));

        distances = new ArrayList<>();
        visitTime = new ArrayList<>();
        lowTime = new ArrayList<>();
        addNodeButton.setDisable(false);
        addEdgeButton.setDisable(false);
        AddNodeHandle(null);
        playing = false;
        paused = false;
    }

    /**
     * Event handler for the Add Edge button.
     *
     * @param event
     */
    @FXML
    public void AddEdgeHandle(ActionEvent event) {
        addNode = false;
        addEdge = true;
        calculate = false;
        addNodeButton.setSelected(false);
        addEdgeButton.setSelected(true);
    }
    @FXML
    public void AddNodeHandle(ActionEvent event) {
        addNode = true;
        addEdge = false;
        calculate = false;
        addNodeButton.setSelected(true);
        addEdgeButton.setSelected(false);
        selectedNode = null;
    }

    /**
     * Changes the current Name/ID of a node.
     *
     * @param source Node reference of selected node
     */
    public void changeID(NodeFX source) {
        System.out.println("Before-------");
        for (NodeFX u : circles) {
            System.out.println(u.node.getName() + " - ");
            for (Edge v : u.node.getAdjacents()) {
                System.out.println(v.getSource().getName() + " " + v.getTarget().getName());
            }
        }
        selectedNode = null;

        TextInputDialog dialog = new TextInputDialog(Integer.toString(nNode));
        dialog.setTitle(null);
        dialog.setHeaderText("Enter Node ID :");
        dialog.setContentText(null);

        String res = null;
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            res = result.get();
        }

        circles.get(circles.indexOf(source)).id.setText(res);
        circles.get(circles.indexOf(source)).node.setName(res);

        System.out.println("AFTER----------");
        for (NodeFX u : circles) {
            System.out.println(u.node.getName() + " - ");
            for (Edge v : u.node.getAdjacents()) {
                System.out.println(v.getSource().getName() + " " + v.getTarget().getName());
            }
        }
    }

    /**
     * Deletes the currently selected node.
     *
     * @param sourceFX
     */
    public void deleteNode(NodeFX sourceFX) {
        selectedNode = null;
        System.out.println("Before-------");
        for (NodeFX u : circles) {
            System.out.println(u.node.getName() + " - ");
            for (Edge v : u.node.getAdjacents()) {
                System.out.println(v.getSource().getName() + " " + v.getTarget().getName());
            }
        }

        Node source = sourceFX.node;
        circles.remove(sourceFX);
        listNode.remove(source);

        List<Edge> tempEdges = new ArrayList<>();
        List<Node> tempNodes = new ArrayList<>();
        for (Edge e : source.getAdjacents()) {
            e.weightLabel.setText("");
            Node u = e.getTarget();
            for (Edge x : u.getAdjacents()) {
                if (x.getTarget() == source) {
                    x.setTarget(null);
                    tempNodes.add(u);
                    tempEdges.add(x);
                }
            }
            edges.remove(e.getLine());
            canvasGroup.getChildren().remove(e.getLine());
            mstEdges.remove(e);
        }
        for (Node q : tempNodes) {
            q.getAdjacents().removeAll(tempEdges);
        }
        List<Edge> tempEdges2 = new ArrayList<>();
        List<Shape> tempArrows = new ArrayList<>();
        List<Node> tempNodes2 = new ArrayList<>();
        for (NodeFX z : circles) {
            for (Edge s : z.node.getAdjacents()) {
                if (s.getTarget() == source) {
                    tempEdges2.add(s);
                    tempArrows.add(s.line);
                    tempNodes2.add(z.node);
                    canvasGroup.getChildren().remove(s.line);
                }
            }
        }
        for (Node z : tempNodes2) {
            z.getAdjacents().removeAll(tempEdges2);
        }
        realEdges.removeAll(tempEdges);
        realEdges.removeAll(tempEdges2);
        canvasGroup.getChildren().remove(sourceFX.id);
        canvasGroup.getChildren().remove(sourceFX);

        System.out.println("AFTER----------");
        for (NodeFX u : circles) {
            System.out.println(u.node.getName() + " - ");
            for (Edge v : u.node.getAdjacents()) {
                System.out.println(v.getSource().getName() + " " + v.getTarget().getName());
            }
        }
    }

    /**
     * Deletes the currently selected Edge.
     *
     * @param sourceEdge
     */
    public void deleteEdge(Edge sourceEdge) {
        System.out.println("Before-------");
        for (NodeFX u : circles) {
            for (Edge v : u.node.getAdjacents()) {
                System.out.println(v.getSource().getName() + " " + v.getTarget().getName());
            }
        }

        System.out.println(sourceEdge.getSource().getName() + " -- " + sourceEdge.getTarget().getName());
        List<Edge> ls1 = new ArrayList<>();
        List<Shape> lshape2 = new ArrayList<>();
        for (Edge e : sourceEdge.getSource().getAdjacents()) {
            if (e.getTarget() == sourceEdge.getTarget()) {
                ls1.add(e);
                lshape2.add(e.line);
            }
        }
        for (Edge e : sourceEdge.getTarget().getAdjacents()) {
            if (e.getTarget() == sourceEdge.getSource()) {
                ls1.add(e);
                lshape2.add(e.line);
            }
        }
        System.out.println("sdsdsd  " + ls1.size());
        sourceEdge.getSource().getAdjacents().removeAll(ls1);
        sourceEdge.getTarget().getAdjacents().removeAll(ls1);
        realEdges.removeAll(ls1);

        edges.removeAll(lshape2);
        canvasGroup.getChildren().removeAll(lshape2);
        sourceEdge.weightLabel.setText("");

        System.out.println("AFTER----------");
        for (NodeFX p : circles) {
            for (Edge q : p.node.getAdjacents()) {
                System.out.println(q.getSource().getName() + " " + q.getTarget().getName());
            }
        }
    }

    /**
     * Change weight of the currently selected edge. Disabled for unweighted
     * graphs.
     *
     * @param sourceEdge
     */
    public void changeWeight(Edge sourceEdge) {
        System.out.println("Before-------");
        for (NodeFX u : circles) {
            System.out.println(u.node.getName() + " - ");
            for (Edge v : u.node.getAdjacents()) {
                System.out.println(v.getSource().getName() + " " + v.getTarget().getName() + " weight: " + v.getCapacity());
            }
        }

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(null);
        dialog.setHeaderText("Enter Weight of the Edge :");
        dialog.setContentText(null);

        String res = null;
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            res = result.get();
        }

        for (Edge e : sourceEdge.getSource().getAdjacents()) {
            if (e.getTarget() == sourceEdge.getTarget()) {
                e.setCapacity(Long.valueOf(res));
                e.weightLabel.setText(res);
            }
        }
        for (Edge e : sourceEdge.getTarget().getAdjacents()) {
            if (e.getTarget() == sourceEdge.getSource()) {
                e.setCapacity(Long.valueOf(res));
            }
        }
        for (Edge e : mstEdges) {
            if (e.getSource() == sourceEdge.getSource() && e.getTarget() == sourceEdge.getTarget()) {
                e.setCapacity(Long.valueOf(res));
            }
        }

        System.out.println("AFTER----------");
        for (NodeFX p : circles) {
            System.out.println(p.node.getName() + " - ");
            for (Edge q : p.node.getAdjacents()) {
                System.out.println(q.getSource().getName() + " " + q.getTarget().getName() + " weigh: " + q.getCapacity());
            }
        }
    }

    /**
     * Shape class for the nodes.
     */
    public class NodeFX extends Circle {

        Node node;
        Point point;
        Label id;
        boolean isSelected = false;

        public NodeFX(double x, double y, double rad, String name) {
            super(x, y, rad);
            node = new Node(name, this);
            point = new Point((int) x, (int) y);
            id = new Label(name);
            canvasGroup.getChildren().add(id);
            id.setLayoutX(x - 18);
            id.setLayoutY(y - 18);
            this.setOpacity(0.5);
            this.setBlendMode(BlendMode.MULTIPLY);
            this.setId("node");

            RightClickMenu rt = new RightClickMenu(this);
            ContextMenu menu = rt.getMenu();
            globalMenu = menu;
            this.setOnContextMenuRequested(e -> {
                if (addEdge || addNode) {
                    menu.show(this, e.getScreenX(), e.getScreenY());
                    menuBool = true;
                }
            });
            menu.setOnAction(e -> {
                menuBool = false;
            });

            circles.add(this);
            System.out.println("ADDing: " + circles.size());
            for (int i = 0; i < circles.size(); i++) {
                if (listNode.isEmpty() || listNode.contains(circles.get(i).node) == false) {
                    listNode.add(circles.get(i).node);
                }
            }
            for (int i = 0; i < listNode.size(); i++) {
                System.out.println(listNode.get(i).getName());
            }
        }
    }

    /*
     * Algorithm Declarations ------------------------------------------
     */

    public void fordStepByStep() {
        for(int j = 0; j < realEdges.size(); j++) {
            if(realEdges.get(j).line.getFill() == Color.RED)
            {
                realEdges.get(j).line.setFill(Color.BLUE);
            }
        }
        count_ford++;
        Network network = new Network(copyList(listNode), listNode.get(0).copy(), listNode.get(listNode.size() - 1).copy());
        for (int i = 0; i < realEdges.size(); i++) {
            network.addEdge(realEdges.get(i).getSource().copy(), realEdges.get(i).getTarget().copy(), realEdges.get(i).copy().getCapacity());
        }
        NetworkFlowSolver solver = new FordFulkersonSolver(network);
        long f = -1;
        for(int i = 0; i <= count_ford; i++)
        {
            solver.getResult().clear();
            f = ((FordFulkersonSolver) solver).dfs(network.getS(),INF);
            if(f != 0) {
                ((FordFulkersonSolver) solver).visitedToken++;
                network.setMaxFlow(f);
            }
        }
        for (int i = solver.getResult().size() - 1; i >= 0; i--) {
             detailsLabel.setText(detailsLabel.getText() + '\n' + solver.getResult().get(i).toString());
             for(int j = 0; j < realEdges.size(); j++)
             {
                 if(solver.getResult().get(i).equals(realEdges.get(j)))
                 {
                    realEdges.get(j).line.setFill(Color.RED);
                 }
             }
        }
        if (f == 0) {
            detailsLabel.setText(detailsLabel.getText() + '\n' + "ALGORITHM HAS FINISHED !!!");
        } else {
            detailsLabel.setText(detailsLabel.getText() + '\n' + "FLOW : " + network.getMaxFlow() + '\n');
        }
    }

    public void edStepByStep() {
        for(int j = 0;j<realEdges.size();j++)
        {
            if(realEdges.get(j).line.getFill() == Color.RED)
            {
                realEdges.get(j).line.setFill(Color.BLUE);
            }
        }
        count_edmons++;
//        for (int i = 0; i < realEdges.size(); i++) {
//            System.out.println(realEdges.get(i).toString());
//        }
        Network network = new Network(listNode, listNode.get(0), listNode.get(listNode.size() - 1));
        for (int i = 0; i < realEdges.size(); i++) {
            network.addEdge(realEdges.get(i).getSource(), realEdges.get(i).getTarget(), realEdges.get(i).getCapacity());
        }
        EdmondsKarpSolver solver = new EdmondsKarpSolver(network);
        long flow = -1;
        for(int i =0;i<=count_edmons;i++)
        {
            if(flow != 0) {
                solver.getResult().clear();
                solver.visitedToken++;
                flow = solver.bfs();
                network.setMaxFlow(flow);
            }
        }
        for (int i = solver.getResult().size() - 1; i >= 0 ; i--) {
            detailsLabel.setText(detailsLabel.getText() + '\n' + solver.getResult().get(i).toString());
            for(int j = 0;j<realEdges.size();j++)
            {
                if(solver.getResult().get(i).equals(realEdges.get(j)))
                {
                    realEdges.get(j).line.setFill(Color.RED);
                }
            }
        }
        for(int i =0;i<listNode.size();i++)
        {
            listNode.get(i).setVisited(0);
        }
        solver.visitedToken = 0;

        if (flow == 0) {
            detailsLabel.setText(detailsLabel.getText() + '\n' + "ALGORITHM HAS FINISHED !!!");
        } else {
            detailsLabel.setText(detailsLabel.getText() + '\n' + "FLOW : " + network.getMaxFlow() + '\n');
        }
    }

    @FXML
    public void fordFulkersonAlgo() {
        edmonsButton.setSelected(false);
        detailsLabel.setText("");
        for(int j = 0; j < realEdges.size(); j++) {
            realEdges.get(j).line.setFill(Color.BLACK);
        }
        count_edmons = -1;
    }

    @FXML
    public void edmonsKarpAlgo() {
        fordButton.setSelected(false);
        detailsLabel.setText("");
        for(int j = 0; j < realEdges.size(); j++) {
            realEdges.get(j).line.setFill(Color.BLACK);
        }
        count_ford = -1;
    }

    @FXML
    public void stepByStep() {
        if (edmonsButton.isSelected()) {
            edStepByStep();
        } else {
            fordStepByStep();
        }
    }
}