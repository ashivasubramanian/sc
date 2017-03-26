package rendering;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Vector;

import org.junit.Test;
import org.w3c.dom.Element;

import calculations.Station;
import calculations.Train;

//TODO: Test methods shouldLoadStations() and shouldLoadTrains() only test that some data was loaded, not whether the correct data was loaded.
//This is a consequence of the DataAccess class being final - Mockito does not mock final classes. Once these data are being read from a
//db, this will have to be fixed.
public class RendererTest {

	@Test
	public void shouldSetStationAspects() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Vector<String> stationNames = new Vector<String>();
		String stationName = "station1";
		stationNames.add(stationName);
		Vector<Station> stations = new Vector<Station>();
		Element mockStationElement = mock(Element.class);
		when(mockStationElement.getAttribute("name")).thenReturn(stationName);
		when(mockStationElement.getAttribute("nooftracks")).thenReturn("3");
		when(mockStationElement.getAttribute("distancefromhome")).thenReturn("30");
		Station station = new Station(mockStationElement);
		stations.add(station);
		
		Renderer renderer = Renderer.getInstance();
		renderer.getClass().getDeclaredField("objStationNames").set(renderer, stationNames);
		renderer.getClass().getDeclaredField("objStations").set(renderer, stations);
		
		String[] aspect = new String[] {"Green", "Green"};
		renderer.setAspect(stationName, aspect);
		
		assertArrayEquals(new Integer[] {3, 3}, station.getAspects());
	}
	
	@Test
	public void shouldReturnTheSameInstanceEverytime() {
		Renderer renderer = Renderer.getInstance();
		Renderer renderer2 = Renderer.getInstance();
		assertEquals(renderer, renderer2);
	}
	
	@Test
	public void shouldLoadStations() throws Exception {
		Renderer renderer = Renderer.getInstance();
		renderer.getClass().getDeclaredField("objStations").set(renderer, new Vector<Station>()); //to clear the singleton data from the previous tests.
		
		Vector<Station> objStations = (Vector<Station>) renderer.getClass().getDeclaredField("objStations").get(renderer);
		assertTrue(objStations.isEmpty());
		Method populateStationsMethod = renderer.getClass().getDeclaredMethod("populateStations");
		populateStationsMethod.setAccessible(true);
		populateStationsMethod.invoke(renderer);
		objStations = (Vector<Station>) renderer.getClass().getDeclaredField("objStations").get(renderer);
		assertFalse(objStations.isEmpty());
	}

	//TODO: Separate Renderer.populateTrains() into three methods - one to load daily trains, another to load day-specific trains,
	//and another to filter out those trains that do not run in the next one hour. Write tests for each method.
	@Test
	public void shouldLoadTrains() throws Exception {
		Renderer renderer = Renderer.getInstance();
		renderer.getClass().getDeclaredField("objTrains").set(renderer, new Vector<Station>()); //to clear the singleton data from the previous tests.
		
		Vector<Train> objTrains = (Vector<Train>) renderer.getClass().getDeclaredField("objTrains").get(renderer);
		assertTrue(objTrains.isEmpty());
		Method populateTrainsMethod = renderer.getClass().getDeclaredMethod("populateTrains");
		populateTrainsMethod.setAccessible(true);
		populateTrainsMethod.invoke(renderer);
		objTrains = (Vector<Train>) renderer.getClass().getDeclaredField("objTrains").get(renderer);
		assertFalse(objTrains.isEmpty());
	}
}
