package ds;

public class Point {

	private double[] coordinates;
	
	/*Creates new point*/
	public Point(double... coords) {
		coordinates = new double[coords.length];
        System.arraycopy(coords, 0, coordinates, 0, coords.length);
	}
	
	public boolean equals(Object s) {
		if(!(s instanceof Point)) return false;
		
		Point p = (Point) s;
		if (this.coordinates.length != p.coordinates.length) return false;
		for (int i = 0; i < this.coordinates.length; i++)
			if(this.coordinates[i] != p.coordinates[i]) return false;
		
		return true;
	}
	
	public double coord (int i) {
        return this.coordinates[i];
    }
	
	public int dimension () {
        return coordinates.length;
    }
	
	public int dimCheck (Point p) {
        int len = this.coordinates.length;
        if (len != p.coordinates.length)
            throw new IllegalArgumentException("Dimension mismatch");
        return len;
    }
	
	public double magnitude() {
		return Math.sqrt(this.dot(this));
	}
	
	public double angle(Point p) {
		return Math.acos(this.dot(p) / (this.magnitude() * p.magnitude()));
	}
	
	/*To increase dimensionality, mainly for bisector and cross*/
	public Point extend (double... coords) {
        double[] result = new double[coordinates.length + coords.length];
        System.arraycopy(coordinates, 0, result, 0, coordinates.length);
        System.arraycopy(coords, 0, result, coordinates.length, coords.length);
        return new Point(result);
    }
	
	public double dot(Point p) {
		int len = dimCheck(p);
		double sum = 0;
        for (int i = 0; i < len; i++)
            sum += this.coordinates[i] * p.coordinates[i];
        return sum;
	}
	
	public Point subtract(Point p) {
		int len = dimCheck(p);
		double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] - p.coordinates[i];
        return new Point(coords);
	}
	
	public Point add(Point p) {
		int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] + p.coordinates[i];
        return new Point(coords);
	}
	
	/*Bisector represented as a line in 3D by extending 2D point*/
	public Point bisector(Point p) {
		Point diff = this.subtract(p);
		Point sum = this.add(p);
		double dot = diff.dot(sum);
		return diff.extend(-dot / 2);
	}
	
	public  double determinant (Point[] matrix) {
        boolean[] columns = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++) columns[i] = true;
        return determinant(matrix, 0, columns);
    }

    private  double determinant(Point[] matrix, int row, boolean[] columns){
        if (row == matrix.length) return 1;
        double sum = 0;
        int sign = 1;
        for (int col = 0; col < columns.length; col++) {
            if (!columns[col]) continue;
            columns[col] = false;
            sum += sign * matrix[row].coordinates[col] *
                   determinant(matrix, row+1, columns);
            columns[col] = true;
            sign = -sign;
        }
        return sum;
    }
	
	/*cross of 2D vector is vectors in z direction*/
	public  Point cross(Point[] matrix) {
		int len = matrix.length + 1;
        boolean[] columns = new boolean[len];
        for (int i = 0; i < len; i++) columns[i] = true;
        double[] res = new double[len];
        int sign = 1;
        
        for (int i = 0; i < len; i++) {
            columns[i] = false;
            res[i] = sign * determinant(matrix, 0, columns);
            columns[i] = true;
            sign = -sign;
        }
        return new Point(res);
	}
	
	public  double content(Point[] simplex) {
		Point[] matrix = new Point[simplex.length];
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = simplex[i].extend(1);
        int fact = 1;
        for (int i = 1; i < matrix.length; i++) fact = fact*i;
        return determinant(matrix) / fact;
    }
	
	public int[] relation(Point[] simplex) {
		int dim = simplex.length - 1;
		Point[] matrix = new Point[dim+1];
		
		double[] coords = new double[dim+2];
		for(int i = 0; i<coords.length; i++) coords[i] =  1;
		matrix[0] = new Point(coords);
		
		for(int i = 0; i<dim; i++) {
			coords[0] = this.coordinates[i];
			for(int j = 0; j<simplex.length; j++)
				coords[j+1] = simplex[j].coordinates[i];
			matrix[i+1] = new Point(coords);
		}
		
		Point vector = cross(matrix);
		double content = vector.coordinates[0];
		int[] res = new int[dim+1];
		for(int i = 0; i<res.length; i++) {
			double value = vector.coordinates[i+1];
			if(Math.abs(value) <= 1.0e-6 * Math.abs(content))
				res[i] = 0;
            else if(value < 0)
            	res[i] = -1;
            else
            	res[i] = 1;
		}
		if(content < 0) {
			for(int i = 0; i<res.length; i++)
				res[i] = -res[i];
		}
		if(content == 0) {
			for(int i = 0; i<res.length; i++)
				res[i] = Math.abs(res[i]);
		}
		
		return res;
	}
	
	/*Check if point outside*/
	public Point isOutside(Point[] simplex) {
		int[] res = this.relation(simplex);
		for(int i = 0; i<res.length; i++) {
			if(res[i] > 0)
				return simplex[i];
		}
		return null;
	}
	
	public Point isOn(Point[] simplex) {
		int[] res = this.relation(simplex);
		Point witness = null;
		for(int i = 0; i<res.length; i++) {
			if(res[i] == 0)
				witness = simplex[i];
			else if(res[i] > 0)
				return null;
		}
		return witness;
	}
	
	public boolean isInside (Point[] simplex) {
		int[] res = this.relation(simplex);
		for (int r: res)
			if (r >= 0)
				return false;
		return true;
	}
	
	//-1, 0, or +1 for inside, on, or outside of circumcircle
	public int checkCircumcircle (Point[] simplex) {
		Point[] matrix = new Point[simplex.length + 1];
		for (int i = 0; i < simplex.length; i++)
		    matrix[i] = simplex[i].extend(1, simplex[i].dot(simplex[i]));
		matrix[simplex.length] = this.extend(1, this.dot(this));
		double d = determinant(matrix);
		int result = (d < 0)? -1 : ((d > 0)? +1 : 0);
		if (content(simplex) < 0)
			result = - result;
		return result;
    }
	
	public  Point circumcenter (Point[] simplex) {
		int dim = simplex[0].dimension();
		Point[] matrix = new Point[dim];
		for (int i = 0; i < dim; i++)
		    matrix[i] = simplex[i].bisector(simplex[i+1]);
		Point hCenter = cross(matrix);      // Center in homogeneous coordinates
		
		double last = hCenter.coordinates[dim];
		double[] result = new double[dim];
		for (int i = 0; i < dim; i++)
			result[i] = hCenter.coordinates[i] / last;
		return new Point(result);
    }

}
