package org.opentripplanner.street.model.edge;

import org.opentripplanner.core.model.id.FeedScopedId;
import org.opentripplanner.street.model.vertex.StreetVertex;
import org.opentripplanner.street.model.vertex.TransitBoardingAreaVertex;

/**
 * This represents the connection between a street vertex and a transit vertex belonging the street
 * network.
 */
public class StreetTransitBoardingLink extends StreetTransitEntityLink<TransitBoardingAreaVertex> {

  private final boolean isEntrance;

  private StreetTransitBoardingLink(StreetVertex fromv, TransitBoardingAreaVertex tov) {
    super(fromv, tov, tov.getWheelchairAccessibility());
    isEntrance = true;
  }

  private StreetTransitBoardingLink(TransitBoardingAreaVertex fromv, StreetVertex tov) {
    super(fromv, tov, fromv.getWheelchairAccessibility());
    isEntrance = false;
  }

  public static StreetTransitBoardingLink createStreetTransitBoardingLink(
    StreetVertex fromv,
    TransitBoardingAreaVertex tov
  ) {
    return connectToGraph(new StreetTransitBoardingLink(fromv, tov));
  }

  public static StreetTransitBoardingLink createStreetTransitBoardingLink(
    TransitBoardingAreaVertex fromv,
    StreetVertex tov
  ) {
    return connectToGraph(new StreetTransitBoardingLink(fromv, tov));
  }

  public boolean isEntrance() {
    return isEntrance;
  }

  public boolean isExit() {
    return !isEntrance;
  }

  /**
   * Get the id of the entrance that this edge links to.
   */
  public FeedScopedId entrance() {
    if (getToVertex() instanceof TransitBoardingAreaVertex tev) {
      return tev.getId();
    } else if (getFromVertex() instanceof TransitBoardingAreaVertex tev) {
      return tev.getId();
    }
    throw new IllegalStateException("%s doesn't link to an entrance.".formatted(this));
  }

  protected int getStreetToStopTime() {
    return 0;
  }
}
