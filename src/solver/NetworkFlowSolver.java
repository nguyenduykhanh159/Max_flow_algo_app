package solver;

import entity.Edge;
import entity.Network;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkFlowSolver {
    protected Network network;
    protected List<Edge> result = new ArrayList<>();

    public NetworkFlowSolver(Network network) {
        super();
        this.network = network;
    }

    public List<Edge> getResult() {
        return result;
    }

    public List<Edge>[] getEachWay() {

        int count = 0;
        int size = result.size();
        ArrayList<String> indexs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (result.get(i).getTarget().equals(network.getT())) {
                indexs.add(String.valueOf(i));
                count++;
            }
        }
        List<Edge>[] eachWay = new List[count];
        for (int i = 0; i < count; i++) {
            eachWay[i] = new ArrayList<>();
        }

        for (int i = 0; i < count; i++) {
            int index1 = Integer.parseInt(indexs.get(i));
            if (i == count - 1) {
                for (int j = index1; j < result.size(); j++) {
                    eachWay[i].add(result.get(j));
                }
                break;
            }
            int index2 = Integer.parseInt(indexs.get(i+1));
            for (int j = index1; j < index2; j++) {
                eachWay[i].add(result.get(j));
            }
        }
        return eachWay;

    }
    // Method to implement which solves the network flow problem.
    public abstract void solve();
}
