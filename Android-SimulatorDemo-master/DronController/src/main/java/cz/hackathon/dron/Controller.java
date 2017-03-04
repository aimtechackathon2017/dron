package cz.hackathon.dron;

public class Controller {

	private final DronDriver driver;

	private final DataCollector collector;

	private final SensorsAnalyzer analyzer;

	public Controller(DronDriver driver, DataCollector collector, SensorsAnalyzer analyzer) {
		super();
		this.driver = driver;
		this.collector = collector;
		this.analyzer = analyzer;
	}

}
