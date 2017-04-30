package ds;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.Point;

/**
 * A Triangle is an immutable Set of exactly three Pnts.
 */
public class Triangle extends ArraySet<Point>{
	
	private int id;
	private Point ccenter;
	
	private static int idGen = 0;
	
	public Triangle(Point... vertices) {
		this(Arrays.asList(vertices));
	}
	
	public Triangle(Collection<? extends Point> coll) {
		super(coll);
		id = idGen++;
	}
	
	public int getId() {
		return id;
	}
	
	public Point getVertex(Point... badV) {
		Collection<Point> bad = Arrays.asList(badV);
		for(Point p: this)
			if(!bad.contains(p))
				return p;
		throw new NoSuchElementException("Only bad vertices found");
	}
	
	public boolean isNeighbour(Triangle triangle) {
		int c = 0;
		for(Point v: this) 
			if(!triangle.contains(v))
				c++;
		return c == 1;
	}
	
	public ArraySet<Point> facetOpp(Point vertex) {
		ArraySet<Point> facet = new ArraySet<Point>(this);
		if(facet.remove(vertex))
			return facet;
		throw new IllegalArgumentException("Vertex not in triangle");
	}
	
	public Point getCircumcenter() {
		if(ccenter == null)
			ccenter = new Point().circumcenter(this.toArray(new Point[0]));
		return ccenter;
	}
	
	@Override
    public boolean add (Point vertex) {
        throw new UnsupportedOperationException();
    }
	
	@Override
	public Iterator<Point> iterator () {
	    return new Iterator<Point>() {
	        private Iterator<Point> it = Triangle.super.iterator();
	        public boolean hasNext() {
	        	return it.hasNext();
	        }
	        public Point next() {
	        	return it.next();
	        }
	        public void remove() {
	        	throw new UnsupportedOperationException();
	        }
	    };
	}
	
	/* The following two methods ensure that all triangles are different. */
	
	@Override
	public int hashCode () {
	    return (int)(id^(id>>>32));
	}
	
	@Override
	public boolean equals (Object o) {
	    return (this == o);
	}
}
