package org.opentripplanner.routing.algorithm.filterchain.filters.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentripplanner.model.plan.Itinerary.toStr;
import static org.opentripplanner.model.plan.PlanTestConstants.A;
import static org.opentripplanner.model.plan.PlanTestConstants.B;
import static org.opentripplanner.model.plan.TestItineraryBuilder.newItinerary;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opentripplanner.model.plan.Itinerary;
import org.opentripplanner.model.plan.Leg;
import org.opentripplanner.transit.model.timetable.RealTimeState;
import org.opentripplanner.utils.collection.ListSection;

public class NumItinerariesFilterTest {

  private static final Itinerary I1 = newItinerary(A, 6).walk(1, B).build();
  private static final Itinerary I2 = newItinerary(A).bicycle(6, 8, B).build();
  private static final Itinerary I3 = newItinerary(A).bus(21, 7, 9, B).build();

  @Test
  public void name() {
    NumItinerariesFilter subject = new NumItinerariesFilter(3, ListSection.TAIL);
    assertEquals("number-of-itineraries-filter", subject.name());
  }

  @Test
  public void testCropHead() {
    NumItinerariesFilter subject = new NumItinerariesFilter(1, ListSection.HEAD);
    List<Itinerary> itineraries = List.of(I1, I2, I3);
    var result = subject.removeMatchesForTest(itineraries);
    assertEquals(toStr(List.of(I3)), toStr(result));
  }

  @Test
  public void testCropTailAndSubscribe() {
    var subject = new NumItinerariesFilter(2, ListSection.TAIL);
    var itineraries = List.of(I1, I2, I3);

    var processedList = subject.removeMatchesForTest(itineraries);

    assertEquals(
      I3.startTime().toInstant().toString(),
      subject.getNumItinerariesFilterResult().earliestRemovedDeparture().toString()
    );

    assertEquals(
      I3.startTime().toInstant().toString(),
      subject.getNumItinerariesFilterResult().latestRemovedDeparture().toString()
    );

    assertEquals(I2.keyAsString(), subject.getNumItinerariesFilterResult().pageCut().keyAsString());

    assertEquals(toStr(List.of(I1, I2)), toStr(processedList));
  }

  @Test
  public void testCropHeadAndSubscribe() {
    var subject = new NumItinerariesFilter(1, ListSection.HEAD);
    var itineraries = List.of(I1, I2, I3);

    var processedList = subject.removeMatchesForTest(itineraries);

    assertEquals(
      I2.startTime().toInstant().toString(),
      subject.getNumItinerariesFilterResult().earliestRemovedDeparture().toString()
    );

    assertEquals(
      I2.startTime().toInstant().toString(),
      subject.getNumItinerariesFilterResult().latestRemovedDeparture().toString()
    );

    assertEquals(I3.keyAsString(), subject.getNumItinerariesFilterResult().pageCut().keyAsString());

    assertEquals(toStr(List.of(I3)), toStr(processedList));
  }

  @Test
  public void testMinNonCancelledResultsExpandsWindowBeyondMaxLimit() {
    // [C, C, NC, NC] with maxLimit=2, minNonCancelledResults=2
    // The 2nd non-cancelled is at index 3 → effectiveLimit = max(2, 4) = 4 = size
    // Nothing should be removed.
    var c1 = cancelledItinerary();
    var c2 = cancelledItinerary();
    var subject = new NumItinerariesFilter(2, ListSection.TAIL, 2);
    var itineraries = List.of(c1, c2, I1, I2);

    var result = subject.removeMatchesForTest(itineraries);

    assertEquals(4, result.size());
    assertTrue(result.containsAll(List.of(c1, c2, I1, I2)));
  }

  @Test
  public void testMinNonCancelledResultsDoesNotExpandWhenAlreadySatisfied() {
    // [NC, NC, NC] with maxLimit=2, minNonCancelledResults=2
    // 2nd non-cancelled at index 1 → effectiveLimit = max(2, 2) = 2, no expansion
    var subject = new NumItinerariesFilter(2, ListSection.TAIL, 2);
    var itineraries = List.of(I1, I2, I3);

    var result = subject.removeMatchesForTest(itineraries);

    assertEquals(toStr(List.of(I1, I2)), toStr(result));
  }

  @Test
  public void testMinNonCancelledResultsGracefulDegradationWhenAllCancelled() {
    // [C, C, C] with maxLimit=2, minNonCancelledResults=2
    // No non-cancelled found → effectiveLimit = size = 3, nothing removed
    var c1 = cancelledItinerary();
    var c2 = cancelledItinerary();
    var c3 = cancelledItinerary();
    var subject = new NumItinerariesFilter(2, ListSection.TAIL, 2);
    var itineraries = List.of(c1, c2, c3);

    var result = subject.removeMatchesForTest(itineraries);

    assertEquals(3, result.size());
    assertTrue(result.containsAll(List.of(c1, c2, c3)));
  }

  private static Itinerary cancelledItinerary() {
    Leg leg = mock(Leg.class);
    when(leg.realTimeState()).thenReturn(RealTimeState.CANCELED);

    Itinerary itinerary = mock(Itinerary.class);
    when(itinerary.legs()).thenReturn(List.of(leg));
    return itinerary;
  }
}
