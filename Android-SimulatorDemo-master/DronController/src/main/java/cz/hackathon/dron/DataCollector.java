package cz.hackathon.dron;

import cz.hackathon.dron.model.Box;
import cz.hackathon.dron.model.FlyMoment;

/**
 * Allows to persist data from fly and boxes scanning.
 */
public interface DataCollector {

	void save(FlyMoment flyMoment);

	void save(Box box);

}
