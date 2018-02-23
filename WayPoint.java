package sample;

public class WayPoint {
    public double x;
    public double y;
    public double theta;

    public WayPoint(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public WayPoint() {
    }

    @Override
    public String toString() {

        return String.format("X: %f Y: %f Theta: %f\n", x , y, theta);
    }
}
