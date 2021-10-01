import java.util.Objects;

public class Node {
    private final Point pos;
    private double f_val;
    private double g_val;
    private double h_val;
    private Node prevNode;
    private int rank;


    public Node(Point pos, double g_val, double h_val, Node previousNode, int rank) {
        this.pos = pos;
        this.g_val = g_val;
        this.h_val = h_val;
        f_val = this.g_val + this.h_val;
        prevNode = previousNode;
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public double getF_val() {
        return f_val;
    }

    public double getG_val() {
        return g_val;
    }

    public double getH_val() {
        return h_val;
    }

    public Point getPos() {
        return pos;
    }

    public void setF_val(int f_val) {
        this.f_val = f_val;
    }

    public void setG_val(int g_val) {
        this.g_val = g_val;
        f_val = g_val + h_val;
    }

    public void setH_val(int h_val) {
        this.h_val = h_val;
        f_val = g_val + h_val;
    }
    public Node getPrevNode() {
        return prevNode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!getClass().equals(obj.getClass()))
            return false;

        Node n = (Node) obj;
        return Objects.equals(pos, n.pos);
    }
}
