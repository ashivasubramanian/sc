package game_engine;

import common.models.SignalAspect;
import common.models.TrainDirection;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StationTest {
	
	@Test
	public void initializingStationWithLegalValuesShouldPass() {
		Station station = new Station("CAL", "Calicut", 3, 47);
		assertNotNull(station);
	}
	
	@Test
	public void stationShouldBeInitializedWithSTOPAspect() {
		Station station = new Station("CAL", "Calicut", 3, 47);
		assertArrayEquals(new SignalAspect[] {SignalAspect.STOP, SignalAspect.STOP},
				station.getAspects(), "Station aspects not set to STOP.");
	}
	
	@Test
	public void stationShouldBeInitializedWithTwoPoints() {
		Station station = new Station("CAL", "Calicut", 3, 47);
		assertEquals(2, station.getPoints().size());
		assertEquals(Track.TrackType.MAIN_TRACK, station.getPoints().get(0));
		assertEquals(Track.TrackType.MAIN_TRACK, station.getPoints().get(1));
	}
	
	@Test
	public void stationShouldBeInitializedWithThreeTracks() {
		Station station = new Station("CAL", "Calicut", 3, 47);
		assertEquals(3, station.getTracks().size());
		assertEquals(Track.TrackType.MAIN_TRACK, station.getTracks().get(0).getTrackType());
		assertEquals(Track.TrackType.LOOP_TRACK, station.getTracks().get(1).getTrackType());
		assertEquals(Track.TrackType.LOOP_TRACK, station.getTracks().get(2).getTrackType());
	}
	
	@Test
	public void changingAspectForASignalShouldPass() {
		Station station = new Station("CAL", "Calicut", 3,47);
		station.setAspect(TrainDirection.AWAY_FROM_HOME, SignalAspect.CAUTION);
		assertArrayEquals(new SignalAspect[] {SignalAspect.STOP, SignalAspect.CAUTION},
				station.getAspects(), "Second signal not set to Amber.");
		station.setAspect(TrainDirection.TOWARDS_HOME, SignalAspect.PROCEED);
		assertArrayEquals(new SignalAspect[] {SignalAspect.PROCEED, SignalAspect.CAUTION},
				station.getAspects(), "First signal not set to Green.");
	}

	@Test
	public void shouldNotifyTowardsHomeTrainForTowardsHomeAspectChange() {
		Station station = new Station("CAL", "Calicut", 3,47);
		Train towardsHomeTrain = mock(Train.class);
		Train awayFromHomeTrain = mock(Train.class);
		when(towardsHomeTrain.getDirection()).thenReturn(TrainDirection.TOWARDS_HOME);
		when(awayFromHomeTrain.getDirection()).thenReturn(TrainDirection.AWAY_FROM_HOME);

		station.addObserverForSignal(towardsHomeTrain, towardsHomeTrain.getDirection());
		station.setAspect(towardsHomeTrain.getDirection(), SignalAspect.CAUTION);

		ArgumentCaptor<PropertyChangeEvent> event = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(towardsHomeTrain).propertyChange(event.capture());
		verify(awayFromHomeTrain, never()).propertyChange(any());
		assertEquals(SignalAspect.CAUTION, event.getValue().getNewValue());
	}

	@Test
	public void shouldNotifyAwayFromHomeTrainForAwayFromHomeAspectChange() {
		Station station = new Station("CAL", "Calicut", 3,47);
		Train towardsHomeTrain = mock(Train.class);
		Train awayFromHomeTrain = mock(Train.class);
		when(towardsHomeTrain.getDirection()).thenReturn(TrainDirection.TOWARDS_HOME);
		when(awayFromHomeTrain.getDirection()).thenReturn(TrainDirection.AWAY_FROM_HOME);

		station.addObserverForSignal(awayFromHomeTrain, awayFromHomeTrain.getDirection());
		station.setAspect(awayFromHomeTrain.getDirection(), SignalAspect.PROCEED);

		ArgumentCaptor<PropertyChangeEvent> event = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(towardsHomeTrain, never()).propertyChange(any());
		verify(awayFromHomeTrain).propertyChange(event.capture());
		assertEquals(SignalAspect.PROCEED, event.getValue().getNewValue());
	}

	@Test
	public void shouldNotNotifyTrainsThatHaveDeregisteredAsObserver() {
		Station station = new Station("CAL", "Calicut", 3,47);
		Train awayFromHomeTrain = mock(Train.class);
		when(awayFromHomeTrain.getDirection()).thenReturn(TrainDirection.AWAY_FROM_HOME);

		station.addObserverForSignal(awayFromHomeTrain, awayFromHomeTrain.getDirection());
		station.setAspect(awayFromHomeTrain.getDirection(), SignalAspect.PROCEED);
		verify(awayFromHomeTrain).propertyChange(any());
		Mockito.clearInvocations(awayFromHomeTrain);
		station.removeObserverForSignal(awayFromHomeTrain, awayFromHomeTrain.getDirection());
		station.setAspect(awayFromHomeTrain.getDirection(), SignalAspect.CAUTION);
		verify(awayFromHomeTrain, never()).propertyChange(any());
	}
}
