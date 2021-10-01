import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;

class AStarPathingStrategy implements PathingStrategy {

    private static double euclidDist(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * Program has additionnal functionality to break ties when dequeing from
     * priority queue.
     */
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        int runs = 0;
        List<Point> path = new LinkedList<Point>();
        Node startNode = new Node(start, 0.0, euclidDist(start, end), null, runs);
        // comparator checks for lowest f_value, then breaks ties with lowest g_val,
        // finally break ties with whichever node was added first.
        Comparator<Node> comp = Comparator.comparing(Node::getF_val);
        // comp = comp.thenComparing((n1, n2) -> (int) (n1.getG_val() - n2.getG_val()));
        // comp = comp.thenComparing(Node::getRank);
        HashedPriorityQueue<Point, Node> openList = new HashedPriorityQueue<>(comp, n -> n.getPos());
        openList.putAdd(startNode);
        List<Node> neighList;
        Map<Point, Node> closedMap = new HashMap<Point, Node>();
        Node endNode = null;

        while (!openList.isEmpty()) {
            int newRuns = runs;
            Node curNode = openList.remove();

            if (withinReach.test(curNode.getPos(), end)) {
                endNode = new Node(end, 1.0 + curNode.getG_val(), 0.0, curNode, runs);
                break;
            }
            neighList = potentialNeighbors.apply(curNode.getPos()).filter(canPassThrough)
                    .filter(pos -> !closedMap.containsKey(pos))
                    .map(pos -> new Node(pos, curNode.getG_val() + 1, euclidDist(pos, end), curNode, newRuns))
                    .collect(Collectors.toList());

            for (Node neigh : neighList) {
                if (openList.containsKey(neigh.getPos())) {
                    if (openList.get(neigh.getPos()).getG_val() > neigh.getG_val()) {
                        openList.remove(neigh.getPos());
                        openList.putAdd(neigh);
                    }
                } else {
                    openList.putAdd(neigh);
                }
            }
            closedMap.put(curNode.getPos(), curNode);

            runs++;
        }
        if (endNode != null) {
            Node loopNode = endNode;
            while (loopNode.getPrevNode() != startNode) {
                path.add(loopNode.getPrevNode().getPos());
                loopNode = loopNode.getPrevNode();
            }
        }
        Collections.reverse(path);
        // System.out.println(path.size());
        return path;
    }

    // public List<Point> computePath(Point start, Point end, Predicate<Point>
    // canPassThrough,
    // BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>>
    // potentialNeighbors) {
    // List<Point> path = new LinkedList<Point>();
    // Node startNode = new Node(start, 0.0, euclidDist(start, end), null);
    // HashedPriorityQueue<Point, Node> openList = new
    // HashedPriorityQueue<>(Comparator.comparing(Node::getF_val),
    // n -> n.getPos());
    // openList.putAdd(startNode);
    // List<Node> neighList;
    // Map<Point, Node> closedMap = new HashMap<Point, Node>();
    // Node endNode = null;

    // while (!openList.isEmpty()) {
    // Node curNode = openList.remove();
    // neighList = potentialNeighbors.apply(curNode.getPos()).filter(canPassThrough)
    // .filter(pos -> !closedMap.containsKey(pos))
    // .map(pos -> new Node(pos, curNode.getG_val() + 1.0, euclidDist(pos, end),
    // curNode))
    // .collect(Collectors.toList());

    // for (Node neigh : neighList) {
    // if (openList.containsKey(neigh.getPos())) {
    // if (openList.get(neigh.getPos()).getG_val() > neigh.getG_val()) {
    // openList.remove(neigh.getPos());
    // openList.putAdd(neigh);
    // }
    // } else {
    // openList.putAdd(neigh);
    // }
    // }
    // closedMap.put(curNode.getPos(), curNode);

    // if (withinReach.test(curNode.getPos(), end)) {
    // endNode = new Node(end, 1 + curNode.getG_val(), 0, curNode);
    // break;
    // }
    // }
    // if (endNode != null) {
    // Node loopNode = endNode;
    // while (loopNode.getPrevNode() != startNode) {
    // path.add(loopNode.getPrevNode().getPos());
    // loopNode = loopNode.getPrevNode();
    // }
    // }
    // Collections.reverse(path);
    // return path;
    // }
}
