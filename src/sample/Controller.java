package sample;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    AnchorPane pane = new AnchorPane();
    @FXML
    Button draw = new Button();
    @FXML
    TextArea output = new TextArea();
    //Actuall Programming
    private static double fieldWidth = 8.23;
    private static double fieldHight = 8.23;

    private ArrayList<WayPoint> wayPoints;
    private double xOffset;
    private double yOffset;

    private static double cK = 0.50;

    private double paneX;
    private double paneY;

    private double ratioX;
    private double ratioY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wayPoints = new ArrayList<WayPoint>();
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                canvasMouseClicked(event.getX(), event.getY());
            }
        });
        pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                canvasMouseReleased(event.getX(), event.getY());
            }
        });

        paneX = pane.getPrefWidth();
        paneY = pane.getPrefHeight();

        ratioX = fieldWidth/paneX;
        ratioY = fieldHight/paneY;
    }

    public void canvasMouseClicked(double x, double y){
        wayPoints.add(newPoint(x,y,0));
        drawPoint(x, y);
    }
    public void canvasMouseReleased(double x, double y){
        double theta = getTheta(x, y);
        if (theta==0) theta=Math.PI/2;
        wayPoints.get(wayPoints.size()-1).theta = theta;
        drawTheta();
    }

    public double getTheta(double x, double y){
        double x_hat = wayPoints.get(wayPoints.size()-1).x;
        double y_hat = wayPoints.get(wayPoints.size()-1).y;

        x = x-xOffset;
        y = -(y-yOffset);

        double theta = Math.atan2(y - y_hat, x-x_hat);

        return theta;
    }

    private void drawTheta(){
        int lineLength=10;

        Line line = new Line();
        line.setStartX(wayPoints.get(wayPoints.size()-1).x + xOffset);
        line.setStartY(yOffset - wayPoints.get((wayPoints.size()-1)).y );
        line.setEndX((wayPoints.get(wayPoints.size()-1).x + xOffset) + (lineLength*Math.cos(-wayPoints.get(wayPoints.size()-1).theta)));
        line.setEndY((yOffset - wayPoints.get(wayPoints.size()-1).y) + (lineLength*Math.sin(-wayPoints.get(wayPoints.size()-1).theta)));

        line.setStroke(Color.RED);
        line.setStrokeWidth(4);

        pane.getChildren().add(line);
    }

    private WayPoint newPoint(double x, double y,double theta){
        if(wayPoints.size() == 0) {

            xOffset = x;
            yOffset = y;

            return new WayPoint(0, 0, 0);
        }
        else {
            return new WayPoint(x-xOffset, -(y-yOffset), theta);
        }
    }

    public void drawPoint(double x, double y){

        Circle point = new Circle(0,0,6);
        point.setCenterX(x);
        point.setCenterY(y);

        pane.getChildren().add(point);
    }

    public void drawSpline(){
        System.out.println(wayPoints);
        String out = "X: " + wayPoints.get(0).x * ratioX + "Y: " + wayPoints.get(0).y * ratioY + "Theta: " + wayPoints.get(0).theta + Math.PI/2 + "\n";

        output.setText(out);


        for (int i=1; i<wayPoints.size(); i++){
            out = "X: " + wayPoints.get(i).x * ratioX + "Y: " + wayPoints.get(i).y * ratioY + "Theta: " + wayPoints.get(i).theta +  Math.PI/2 + "\n";
            output.setText(output.getText() + out);
            CubicCurve curve = createSpline(wayPoints.get(i-1).x + xOffset, wayPoints.get(i-1).y + (paneY-yOffset), wayPoints.get(i-1).theta,
                    wayPoints.get(i).x + xOffset, wayPoints.get(i).y + (paneY-yOffset), wayPoints.get(i).theta);
            pane.getChildren().add(curve);
        }

    }

    private CubicCurve createSpline(double x0, double y0, double theta0, double x1, double y1, double theta1){
        theta1+=Math.PI;

        System.out.println(x0 + " " + y0);

        CubicCurve curve = new CubicCurve();
        curve.setStartX(x0);
        curve.setStartY(getYCordinate(y0));

        double[] controlPoint0 = getControlPoint(x0, y0, x1, y1, theta0);
        curve.setControlX1(controlPoint0[0]);
        curve.setControlY1(getYCordinate(controlPoint0[1]));

        double[] controlPoint1 = getControlPoint(x1, y1, x0, y0, theta1);
        curve.setControlX2(controlPoint1[0]);
        curve.setControlY2(getYCordinate(controlPoint1[1]));

        curve.setEndX(x1);
        curve.setEndY(getYCordinate(y1));

        curve.setStroke(Color.FORESTGREEN);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(null);
        return curve;

    }

    private double[] getControlPoint(double x0, double y0, double x1, double y1, double theta){
        double length = cK*Math.sqrt(Math.pow(x1-x0, 2)+Math.pow(y1-y0,2));

        int controlY = (int) (y0 + (length*Math.sin(theta)));
        int controlX = (int) (x0 + (length*Math.cos(theta)));

        return new double[]{controlX, controlY};
    }

    private double getYCordinate(double y){
        return paneY-y;
    }

    public void clear(){
        pane.getChildren().remove(1,pane.getChildren().size());
        wayPoints = new ArrayList<WayPoint>();
        output.setText("");

    }

}
