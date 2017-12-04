/**
 * Represents a position in latitude-longitude-time space.
 * Latitude and longitude are measured in degrees (e.g., on a globe).
 * Time is measured in seconds.
 * @author Brian Edwards, Matt Surka
 */
public class PointWorld implements Point {
	private double lat;
	private double lon;
	private double time;
	private double probWeight = 1.0f;

	/**
	 * Constructor. Sets the latitude, longitude, and time of the Point.
	 * @param lat The latitude in degrees.
	 * @param lon The longitude in degrees.
	 * @param time The time in seconds.
	 */
	public PointWorld(double lat, double lon, double time) {
		this.lat = lat;
		this.lon = lon;
		this.time = time;
	}
	
	/**
	 * Constructor. Sets the latitude and longitude of the Point.
	 * @param lat The latitude in degrees.
	 * @param lon The longitude in degrees.
	 */
	public PointWorld(double lat, double lon) {
		this(lat, lon, 0.);
	}

	/**
	 * Sets the time of the Point.
	 * @param time The time in seconds.
	 */
	public void setTime(double time) {
		this.time = time;
	}
	
	/**
	 * Sets the probability weight
	 * @param weight the probability weight between 0 and 1
	 */
	public void setProbWeight(double weight) {
		this.probWeight = weight;
	}

	/**
	 * Gets the latitude of the Point.
	 * @return The latitude in degrees.
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * Gets the longitude of the Point.
	 * @return The longitude in degrees.
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * Gets the time of the Point.
	 * @return The time of the Point in seconds.
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * gets the probability weight of the Point
	 * @return probability weight between 0 and 1
	 */
	public double getWeight() {
		return probWeight;
	}

	/**
	 * Overrides toString().
	 * Returns a description of the Point as a String.
	 * @return A description of the Point as a String.
	 */
	@Override
	public String toString() {
		return String.format("(lat (deg): %f  lon (deg): %f  time (s): %f)", getLat(), getLon(), getTime());
	}

	/**
	 * Determines if this Point is equal to a provided Point.
	 * Equality is defined as having all attributes equal or very close.
	 * Having equality defined on Points makes jUnit testing much easier.
	 * @param pointOther The Point to compare to.
	 * @return True if the Points are equal; false otherwise.
	 */
	public boolean equals(Point pointOther) {
		double closeDistance = 3;
		double closeTime = 1E-3;
		
		double dblDistance = this.distanceTo(pointOther);
		double dblTimeDifference = Math.abs(getTime() - pointOther.getTime());
		
		boolean isCloseInSpace = dblDistance < closeDistance;
		boolean isCloseInTime = dblTimeDifference < closeTime;
		return isCloseInTime && isCloseInSpace;
	}

	/**
	 * Computes the distance between this Point and a provided Point.
	 * @param pointOther The Point to compare to.
	 * @return The distance between the Points in degrees.
	 */
	public double distanceTo(Point pointOther) {
		double dblDistance = 0;
		double dblDistanceX = 0;
		double dblDistanceY = 0;
		
		double EARTH_RAD = 6371000; // meters
		double meanLat = (this.getLat() + pointOther.getLat()) / 2;
		double sf = Math.sin(meanLat * Math.PI / 180.);
		double mPerDegLat = 2 * Math.PI * EARTH_RAD / 360;
		double mPerDegLon = mPerDegLat * sf;
		
		dblDistanceX = (this.getLat() - pointOther.getLat()) * mPerDegLat;
		dblDistanceY = (this.getLon() - pointOther.getLon()) * mPerDegLon;		
		dblDistance = Math.sqrt(Math.pow(dblDistanceX, 2) + Math.pow(dblDistanceY, 2));
		
		return dblDistance;
	}
}