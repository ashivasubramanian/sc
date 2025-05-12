package game_engine;

import common.models.TrainDirection;
import game_engine.Station;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import common.models.SignalAspect;

public class StationTest {
	
	@Test
	public void initializingStationWithLegalValuesShouldPass() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("no-of-tracks")).thenReturn("3");
		when(data.getAttribute("distance-from-home")).thenReturn("47");

		Station station = new Station(data);
		assertNotNull(station);
	}
	
	@Test
	public void stationShouldBeInitializedWithSTOPAspect() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("no-of-tracks")).thenReturn("3");
		when(data.getAttribute("distance-from-home")).thenReturn("47");

		Station station = new Station(data);
		assertArrayEquals(new SignalAspect[] {SignalAspect.STOP, SignalAspect.STOP},
				station.getAspects(), "Station aspects not set to STOP.");
	}
	
	@Test
	public void stationShouldBeInitializedWithTwoPoints() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("no-of-tracks")).thenReturn("3");
		when(data.getAttribute("distance-from-home")).thenReturn("47");
		
		Station station = new Station(data);
		assertEquals(2, station.getPoints().size());
		assertEquals(Track.TrackType.MAIN_TRACK, station.getPoints().get(0));
		assertEquals(Track.TrackType.MAIN_TRACK, station.getPoints().get(1));
	}
	
	@Test
	public void stationShouldBeInitializedWithThreeTracks() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("no-of-tracks")).thenReturn("3");
		when(data.getAttribute("distance-from-home")).thenReturn("47");
		
		Station station = new Station(data);
		assertEquals(3, station.getTracks().size());
		assertEquals(Track.TrackType.MAIN_TRACK, station.getTracks().get(0).getTrackType());
		assertEquals(Track.TrackType.LOOP_TRACK, station.getTracks().get(1).getTrackType());
		assertEquals(Track.TrackType.LOOP_TRACK, station.getTracks().get(2).getTrackType());
	}
	
	@Test
	public void changingAspectForASignalShouldPass() {
		Element data = mock(Element.class);
		when(data.getAttribute("name")).thenReturn("Calicut");
		when(data.getAttribute("no-of-tracks")).thenReturn("3");
		when(data.getAttribute("distance-from-home")).thenReturn("47");

		Station station = new Station(data);
		station.setAspect(TrainDirection.AWAY_FROM_HOME, SignalAspect.CAUTION);
		assertArrayEquals(new SignalAspect[] {SignalAspect.STOP, SignalAspect.CAUTION},
				station.getAspects(), "Second signal not set to Amber.");
		station.setAspect(TrainDirection.TOWARDS_HOME, SignalAspect.PROCEED);
		assertArrayEquals(new SignalAspect[] {SignalAspect.PROCEED, SignalAspect.CAUTION},
				station.getAspects(), "First signal not set to Green.");
	}

}
