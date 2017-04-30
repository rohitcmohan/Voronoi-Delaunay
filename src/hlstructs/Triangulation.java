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
	
	public Set<Triangle> neighbours(Triangle tr) {
		return triGraph.neighbours(tr);
	}
	
	/*Fiand triangle around vertex*/
	public List<Triangle> adjTriangles(Point v, Triangle tr) {
		if (!tr.contains(v))
            throw new IllegalArgumentException("Vertex not in triangle");
		List<Triangle> list = new ArrayList<Triangle>();
		Triangle begin = tr;
		Point startP = tr.getVertex(v);
		while(true) {
			list.add(tr);
			Triangle prev = tr;
			tr = this.triOpp(startP, tr);
			startP = prev.getVertex(v, startP);
			if(tr == begin)
				break;
		}
		return list;
	}
	
	public Triangle find(Point pt) {
		Triangle tr = last;
		if(!this.contains(tr))
			tr = null;
		
		Set<Triangle> visited = new HashSet<Triangle>();
		while(tr != null) {
			if(!visited.contains(tr))
				break;			//find loop
			visited.add(tr);
			Point next = pt.isOutside(tr.toArray(new Point[0]));
			if(next == null)
				return tr;
			tr = this.triOpp(next, tr);
		}
		
		//Check all triangles
		for(Triangle tri: this) {
			if(pt.isOutside(tri.toArray(new Point[0])) == null)
				return tri;
		}
		return null;
	}
	
	/*Find the hole created by new site point*/
	public Set<Triangle> getHole(Point v, Triangle tr) {
		Set<Triangle> holeSet = new HashSet<Triangle>();
		Queue<Triangle> toCheck = new LinkedList<Triangle>();
		Set<Triangle> visited = new HashSet<Triangle>();
		
		toCheck.add(tr);
		visited.add(tr);
		while(!toCheck.isEmpty()) {
			tr = toCheck.poll();
			if(v.checkCircumcircle(tr.toArray(new Point[0])) == 1)
				continue;
			holeSet.add(tr);
			for(Triangle nbr: triGraph.neighbours(tr)) {
				if(visited.contains(nbr))
					continue;
				visited.add(nbr);
				toCheck.add(nbr);
			}
		}
		return holeSet;
	}
	
	public Triangle update(Point v, Set<Triangle> hole) {
		Set<Set<Point>> holeBoundary = new HashSet<Set<Point>>();
		Set<Triangle> holeTri = new HashSet<Triangle>();
		
		//Find boundary and adj triangles
		for(Triangle tri: hole) {
			holeTri.addAll(neighbours(tri));
			for(Point vertex: tri) {
				Set<Point> facet = tri.facetOpp(vertex);
				if(holeBoundary.contains(facet))
					holeBoundary.remove(facet);
				else
					holeBoundary.add(facet);
			}
		}
		holeTri.removeAll(hole);
		
		//Remove hole triangles from triangulation
		for(Triangle tri: hole)
			triGraph.remove(tri);
		
		Set<Triangle> newTri = new HashSet<Triangle>();
		for(Set<Point> vertices: holeBoundary) {
			vertices.add(v);
			Triangle tri = new Triangle(vertices);
			triGraph.add(tri);
			newTri.add(tri);
		}
		
		holeTri.addAll(newTri);
		for(Triangle tri: newTri) {
			for(Triangle other: holeTri) {
				if(tri.isNeighbour(other))
					triGraph.add(tri, other);
			}
		}
		
		return newTri.iterator().next();
	}
	
	/*Add new site to delaunay triangulation*/
	public void delaunayAdd(Point v) {
		Triangle tr = find(v);
		if(tr == null)
			throw new IllegalArgumentException("No triangle");
		if(tr.contains(v))
			return;
		
		Set<Triangle> hole = getHole(v, tr);
		last = update(v, hole);
	}
	
}
