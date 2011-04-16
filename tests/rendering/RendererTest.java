package rendering;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Vector;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;

import calculations.Station;

public class RendererTest {
	
	@Test
	public void shouldSetStationAspects() {
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
		ReflectionTestUtils.setField(renderer, "objStationNames", stationNames);
		ReflectionTestUtils.setField(renderer, "objStations", stations);
		
		String[] aspect = new String[] {"Green", "Green"};
		renderer.setAspect(stationName, aspect);
		
		assertArrayEquals(new Integer[] {3, 3}, station.getAspects());
	}

}
