package org.opentripplanner.routing.algorithm.filterchain.filters.system;

import java.util.List;
import org.opentripplanner.model.plan.Itinerary;
import org.opentripplanner.routing.algorithm.filterchain.framework.spi.RemoveItineraryFlagger;
import org.opentripplanner.transit.model.timetable.RealTimeState;
import org.opentripplanner.utils.collection.ListSection;

/**
 * Flag all itineraries after the provided limit. This flags the itineraries at the end of the list
 * for removal, so the list should be sorted on the desired key before this filter is applied.
 * <p>
 * When {@code minNonCancelledResults > 0}, the effective limit is expanded until at least that
 * many non-cancelled itineraries are included, ensuring actionable results are always present
 * alongside cancelled trips during service disruptions.
 * <p>
 * This filter reports information about the removed itineraries in the results variable.
 */
public class NumItinerariesFilter implements RemoveItineraryFlagger {

  public static final String TAG = "number-of-itineraries-filter";

  private final int maxLimit;
  private final ListSection cropSection;
  private final int minNonCancelledResults;
  private NumItinerariesFilterResult numItinerariesFilterResult = null;

  public NumItinerariesFilter(int maxLimit, ListSection cropSection) {
    this(maxLimit, cropSection, 0);
  }

  public NumItinerariesFilter(int maxLimit, ListSection cropSection, int minNonCancelledResults) {
    this.maxLimit = maxLimit;
    this.cropSection = cropSection;
    this.minNonCancelledResults = minNonCancelledResults;
  }

  @Override
  public String name() {
    return TAG;
  }

  @Override
  public List<Itinerary> flagForRemoval(List<Itinerary> itineraries) {
    int effectiveLimit = computeEffectiveLimit(itineraries);

    if (itineraries.size() <= effectiveLimit) {
      return List.of();
    }

    List<Itinerary> itinerariesToKeep;
    List<Itinerary> itinerariesToRemove;

    if (cropSection == ListSection.HEAD) {
      int removeCount = itineraries.size() - effectiveLimit;

      itinerariesToRemove = itineraries.subList(0, removeCount);
      itinerariesToKeep = itineraries.subList(removeCount, itineraries.size());
    } else {
      itinerariesToRemove = itineraries.subList(effectiveLimit, itineraries.size());
      itinerariesToKeep = itineraries.subList(0, effectiveLimit);
    }

    // This result is used for paging. It is collected by an aggregator.
    numItinerariesFilterResult = new NumItinerariesFilterResult(
      itinerariesToKeep,
      itinerariesToRemove,
      cropSection
    );

    return itinerariesToRemove;
  }

  public NumItinerariesFilterResult getNumItinerariesFilterResult() {
    return numItinerariesFilterResult;
  }

  private int computeEffectiveLimit(List<Itinerary> itineraries) {
    if (minNonCancelledResults <= 0) {
      return maxLimit;
    }

    if (cropSection == ListSection.HEAD) {
      return computeEffectiveLimitFromTail(itineraries);
    }

    return computeEffectiveLimitFromHead(itineraries);
  }

  /**
   * For TAIL cropping (forward pagination): scan from the front until minNonCancelledResults
   * non-cancelled itineraries are found.
   */
  private int computeEffectiveLimitFromHead(List<Itinerary> itineraries) {
    int nonCancelledCount = 0;
    for (int i = 0; i < itineraries.size(); i++) {
      if (!isCancelled(itineraries.get(i))) {
        nonCancelledCount++;
        if (nonCancelledCount >= minNonCancelledResults) {
          return Math.max(maxLimit, i + 1);
        }
      }
    }
    // Fewer than minNonCancelledResults found — include all available
    return itineraries.size();
  }

  /**
   * For HEAD cropping (reverse pagination): scan from the back until minNonCancelledResults
   * non-cancelled itineraries are found.
   */
  private int computeEffectiveLimitFromTail(List<Itinerary> itineraries) {
    int nonCancelledCount = 0;
    for (int i = itineraries.size() - 1; i >= 0; i--) {
      if (!isCancelled(itineraries.get(i))) {
        nonCancelledCount++;
        if (nonCancelledCount >= minNonCancelledResults) {
          int keepCount = itineraries.size() - i;
          return Math.max(maxLimit, keepCount);
        }
      }
    }
    // Fewer than minNonCancelledResults found — include all available
    return itineraries.size();
  }

  private static boolean isCancelled(Itinerary itinerary) {
    return itinerary
      .legs()
      .stream()
      .anyMatch(leg -> leg.realTimeState() == RealTimeState.CANCELED);
  }
}
