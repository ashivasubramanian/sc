package calculations;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.w3c.dom.Element;

public class StationTest {
	
	@Test
	public void initializingStationWithLegalValuesShouldPass() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("nooftracks")).thenReturn("3");
		when(data.getAttribute("distancefromhome")).thenReturn("47");

		Station station = new Station(data);
		assertNotNull(station);
	}
	
	@Test
	public void stationShouldBeInitializedWithSTOPAspect() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("nooftracks")).thenReturn("3");
		when(data.getAttribute("distancefromhome")).thenReturn("47");

		Station station = new Station(data);
		assertArrayEquals("Station aspects not set to STOP.",
				new Integer[] {station.STOP, station.STOP},
				station.getAspects());
	}
	
	@Test
	public void stationShouldBeInitializedWithTwoPoints() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("nooftracks")).thenReturn("3");
		when(data.getAttribute("distancefromhome")).thenReturn("47");
		
		Station station = new Station(data);
		assertEquals(2, station.points.size());
		assertEquals(station.MAIN_TRACK, station.points.get(0));
		assertEquals(station.MAIN_TRACK, station.points.get(1));
	}
	
	@Test
	public void stationShouldBeInitializedWithThreeTracks() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("nooftracks")).thenReturn("3");
		when(data.getAttribute("distancefromhome")).thenReturn("47");
		
		Station station = new Station(data);
		assertEquals(3, station.tracks.size());
		assertEquals(station.MAIN_TRACK, station.tracks.get(0));
		assertEquals(station.LOOP1_TRACK, station.tracks.get(1));
		assertEquals(station.LOOP2_TRACK, station.tracks.get(2));
	}
	
	@Test
	public void changingAspectForASignalShouldPass() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("nooftracks")).thenReturn("3");
		when(data.getAttribute("distancefromhome")).thenReturn("47");

		Station station = new Station(data);
		station.setAspect(1, "Amber");
		assertArrayEquals("Second signal not set to Amber.",
				new Integer[] {station.STOP, station.CAUTION},
				station.getAspects());
		station.setAspect(0, "Green");
		assertArrayEquals("First signal not set to Green.",
				new Integer[] {station.PROCEED, station.CAUTION},
				station.getAspects());
	}

}
