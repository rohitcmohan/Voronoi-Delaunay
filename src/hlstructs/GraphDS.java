package hlstructs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ds.ArraySet;

public class GraphDS<N> {
	
	private Map<N, Set<N>> nbrs = new HashMap<N, Set<N>>();
	private Set<N> nodeSet = Collections.unmodifiableSet(nbrs.keySet());
	
	//Add node
	public void add(N node) {
		if(nbrs.containsKey(node))
			return;
		nbrs.put(node, new ArraySet<N>());
	}
	
	//Add edge
	public void add(N nodeA, N nodeB) throws NullPointerException {
		nbrs.get(nodeA).add(nodeB);
		nbrs.get(nodeB).add(nodeA);
	}
	
	//remove node from to and from edge lists
	public void remove(N node) {
		if(!nbrs.containsKey(node))
			return;
		for(N nbr: nbrs.get(node))
			nbrs.get(nbr).remove(node);
		nbrs.get(node).clear();
		nbrs.remove(node);
	}
	
	//remove edge
	public void remove (N nodeA, N nodeB) throws NullPointerException {
		nbrs.get(nodeA).remove(nodeB);
		nbrs.get(nodeB).remove(nodeA);
	}
	
	public Set<N> neighbours(N node) throws NullPointerException {
		return Collections.unmodifiableSet(nbrs.get(node));
	}
	
	public Set<N> nodeSet() {
		return nodeSet;
	}
	
}
