import java.util.ArrayList;
import java.util.Collections;

/**
 * A an ordered list of Points defined as Point(lat, lon, time) for the purpose
 * of storing a traveled path on the globe.
 *
 * @author brian
 *
 */
public class Trip {

	private ArrayList<Point> points;

	/**
	 * Initializes the Trip with an empty set of points.
	 */
	public Trip() {
		points = new ArrayList<>();
	}

	/**
	 * @return The ArrayList of Points
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 * Adds a point to the end of the trip.
	 *
	 * @param p
	 *            Point to be added
	 */
	public void addPoint(Point p) {
		points.add(p);
	}

	/**
	 * The time at the start of the trip in seconds.
	 *
	 * @return Time at the start of the trip.
	 */
	public double minTime() {
		return points.get(0).getTime();
	}

	/**
	 * The time at the end of the trip in seconds.
	 *
	 * @return Time at the end of the trip.
	 */
	public double maxTime() {
		return points.get(points.size() - 1).getTime();
	}

	/**
	 * Offsets all points in the trip in time by a value 'timeOffset'
	 *
	 * @param timeOffset
	 *            Time value by which to shift the trip.
	 */
	public void offsetTime(double timeOffset) {
		for (Point p : points) {
			p.offsetTime(timeOffset);
		}
	}

	/**
	 * Scales all point times in the trip by scaleFactor.
	 *
	 * @param timeOffset
	 *            Time value by which to shift the trip.
	 */
	public void scaleTime(double scaleFactor) {
		for (Point p : points) {
			p.scaleTime(scaleFactor);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (Point p : this.points) {
			sb.append(p.toString() + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Will find the lat-lon location on a trip at a given time and returns it
	 * all as a Point object.
	 *
	 * If the time is less than the starting time of the trip, it returns the
	 * first Point of the trip. If the time is greater than the ending time of
	 * the trip, it returns the final Point of the trip. Otherwise it will
	 * return an interpolated value along the trip path.
	 *
	 * This method implements a binary search on the trip. It is assumed that
	 * the trip times are monotonic and increasing. It will find an interpolated
	 * value in O(log(N)) time, where N is the number of points in the trip.
	 *
	 * @param time
	 *            The time
	 * @return A Point object defined by Point(lat, lon, time).
	 */
	public Point atTime(double time) {
		if (time <= this.minTime()) {
			// before you leave on the trip, you stationary at the beginning
			// location.
			return points.get(0);
		} else if (time >= this.maxTime()) {
			// after you arrive, you are assumed to be at the end forever.
			return points.get(points.size() - 1);
		} else {
			// define a dummy point with the request time to serve as the
			// comparison object.
			Point searchPoint = new Point(Double.NaN, Double.NaN, time);
			int insPoint = Collections.binarySearch(points, searchPoint, null);
			// Quoting JavaDocs: the index of the search key, if it is contained
			// in the list; otherwise, (-(insertion point) - 1). The insertion
			// point is defined as the point at which the key would be inserted
			// into the list: the index of the first element greater than the
			// key, or list.size() if all elements in the list are less than the
			// specified key. Note that this guarantees that the return value
			// will be >= 0 if and only if the key is found.
			if (insPoint >= 0) {
				// if you found a point with the request time, return it.
				return points.get(insPoint);
			} else {
				int upperIndex = -(insPoint + 1);
				int lowerIndex = upperIndex - 1;
				Point upperPoint = points.get(upperIndex);
				Point lowerPoint = points.get(lowerIndex);

				double upperTime = upperPoint.getTime();
				double lowerTime = lowerPoint.getTime();

				double frac = (time - lowerTime) / (upperTime - lowerTime);
				double newLat = (1 - frac) * lowerPoint.getLat()
						+ (frac) * upperPoint.getLat();
				double newLon = (1 - frac) * lowerPoint.getLon()
						+ (frac) * upperPoint.getLon();
				return new Point(newLat, newLon, time);
			}
		}

	}
}
