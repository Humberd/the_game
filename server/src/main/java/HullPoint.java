import java.util.Objects;

public class HullPoint implements Comparable<HullPoint> {

    public final double x;
    public final double y;


    public HullPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public String toString() {
        return String.format("Point(%g, %g)", x, y);
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof HullPoint))
            return false;
        else {
            HullPoint other = (HullPoint) obj;
            return x == other.x && y == other.y;
        }
    }


    public int hashCode() {
        return Objects.hash(x, y);
    }


    public int compareTo(HullPoint other) {
        if (x != other.x)
            return Double.compare(x, other.x);
        else
            return Double.compare(y, other.y);
    }

}
