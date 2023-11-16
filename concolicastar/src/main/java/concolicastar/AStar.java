package concolicastar;
import java.util.*;

public class AStar {

    private Object map;
    private Object start;
    private Object target;

    public Object PerformAStar(Object map, Object start, Object target){
        this.map = map;

        PriorityQueue<Object> closedList = new PriorityQueue<>();
        PriorityQueue<Object> openList = new PriorityQueue<>();

        start.f = start.g + start.calculateHeuristic(target);
        openList.add(start);

        while (!openList.isEmpty()){
            Object n = openList.peek();
            if (n == target){
                return n;
            }

            for(Edge edge : n.neighbours){
                Object m = edge.node;
                double totalWeight = n.g + edge.weight;

                if(!openList.contains(m) && !closedList.contains(m)){
                    m.parent = n;
                    m.g = totalWeight;
                    m.f = m.g + m.calculateHeuristic(target);
                    openList.add(m);
                } else {
                if(totalWeight < m.g){
                    m.parent = n;
                    m.g = totalWeight;
                    m.f = m.g + m.calculateHeuristic(target);

                    if(closedList.contains(m)){
                        closedList.remove(m);
                        openList.add(m);
                    }
                }
            }
        }
        openList.remove(n);
        closedList.add(n);
        }
        
        return null;
    }

    public double calculateHeuristic(Object target){
        return this.h;
    }

    public void addBranch(int weight, Object node){
        Edge newEdge = new Edge(weight, node);
        neighbors.add(newEdge);
    }

    public static class Edge {
        public int weight;
        public Object node;

        Edge(int weight, Object node){
            this.weight = weight;
            this.node = node;
        }
    }
    
}