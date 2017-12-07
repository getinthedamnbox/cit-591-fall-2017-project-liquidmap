import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LiquidMap {

	private Settings settings;
	private HashMap<String, DataSource> dataSources = new HashMap<>();
	ArrayList<Trip> trips = new ArrayList<>();

	public LiquidMap() {
		readSettings();
		importDataSources();
		getTrips();
		Animator animator = new Animator();

		PointWorld pointWorldUpperLeft = settings.outputVars.pointUpperLeft;
		PointWorld pointWorldLowerRight = settings.outputVars.pointLowerRight;
		Integer outputWidth = settings.outputVars.imageWidth;

		Converter converter = new Converter(pointWorldUpperLeft,
				pointWorldLowerRight, outputWidth);
		animator.animateTrips(converter.getConvertedListTrips(trips),
				"animation", outputWidth, "#000000", 1, 200, "#AAFF88",
				"#FFFFFF", "#FFFFFF", 0.05);
	}

	private void readSettings() {
		SettingsFileReader sfr = new SettingsFileReader();
		settings = sfr.getSettings();
	}

	private void importDataSources() {
		for (int i = 0; i < settings.rasterDataDescs.size(); i++) {
			String name = settings.rasterDataDescs.get(i).name;
			String mapFileName = settings.rasterDataDescs.get(i).mapFileName;
			PointWorld pt1 = settings.rasterDataDescs.get(i).point1;
			PointWorld pt2 = settings.rasterDataDescs.get(i).point2;
			Pixel px1 = settings.rasterDataDescs.get(i).pixel1;
			Pixel px2 = settings.rasterDataDescs.get(i).pixel2;
			RasterDataSource rds = new RasterDataSource(mapFileName, pt1, px1,
					pt2, px2);
			dataSources.put(name, rds);
		}

		for (int i = 0; i < settings.vectorDataDescs.size(); i++) {
			String name = settings.vectorDataDescs.get(i).name;
			String vecFileName = settings.vectorDataDescs.get(i).vecFileName;
			VectorDataSource vds = new VectorDataSource(vecFileName);
			dataSources.put(name, vds);
		}
	}

	private void getTrips() {
		Random rdn = new Random();
		GHInterface ghi = new GHInterface(settings.cityMapFile);
		DataSource sourceBeg = dataSources.get(settings.routingVars.routeBeg);
		DataSource sourceEnd = dataSources.get(settings.routingVars.routeEnd);
		int goodTripCount = 0;
		while (goodTripCount < settings.routingVars.routeCount) {
			PointWorld ptBeg = sourceBeg.getRandPoint();
			PointWorld ptEnd = sourceEnd.getRandPoint();
			System.out.println(ptBeg);
			System.out.println(ptEnd);
			Trip trip = ghi.getTrip(ptBeg, ptEnd);
			if (trip.maxTime() < settings.routingVars.routeMaxTime) {
				double tVar = settings.routingVars.timeStartVariance;
				double tOff = tVar * rdn.nextDouble();
				double sVar = settings.routingVars.speedVariance;
				double sOff = tVar * rdn.nextDouble();
				double sMult = sOff + 1 - sVar / 2;
				trip.scaleTime(sMult);
				trip.offsetTime(tOff);
				trips.add(trip);
			}
		}
	}

	public static void main(String[] args) {
		LiquidMap lm = new LiquidMap();
	}
}
