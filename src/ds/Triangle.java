package ds;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

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
}
