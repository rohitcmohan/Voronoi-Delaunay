package hlstructs;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ds.Point;
import ds.Triangle;
import hlstructs.GraphDS;

public class Triangulation extends AbstractSet<Triangle> {
	
	private Triangle last;
	private GraphDS<Triangle> triGraph;
	
	public Triangulation(Triangle tr) {
		triGraph = new GraphDS<Triangle>();
		triGraph.add(tr);
		last = tr;
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return triGraph.nodeSet().iterator();
	}
	
	@Override
	public int size() {
		return triGraph.nodeSet().size();
	}
	
	public boolean contains(Object tr) {
		return triGraph.nodeSet().contains(tr);
	}
	
	/*Triangle opposite vertex*/
	public Triangle triOpp(Point v, Triangle tr) {
		if(!tr.contains(v))
			throw new IllegalArgumentException("Vertex not in triangle");
		for(Triangle nbr: triGraph.neighbours(tr))
			if(!nbr.contains(v))
				return nbr;
		return null;
	}
	
}
