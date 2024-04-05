package org.opentripplanner.ext.flex.trip;

import java.util.List;
import org.opentripplanner.model.StopTime;
import org.opentripplanner.transit.model.framework.FeedScopedId;

public class ScheduledDeviatedTripBuilder
  extends FlexTripBuilder<ScheduledDeviatedTrip, ScheduledDeviatedTripBuilder> {

  private List<StopTime> stopTimes;
  private FlexDurationModifier durationModifier = FlexDurationModifier.NONE;

  ScheduledDeviatedTripBuilder(FeedScopedId id) {
    super(id);
  }

  public ScheduledDeviatedTripBuilder(ScheduledDeviatedTrip original) {
    super(original);
    throw new IllegalStateException("Updating is not supported for ScheduledDeviatedTrip");
    // TODO: implement copying
    // this.stopTimes = original.stopTimes;
  }

  public ScheduledDeviatedTripBuilder withStopTimes(List<StopTime> stopTimes) {
    this.stopTimes = stopTimes;
    return this;
  }

  public List<StopTime> stopTimes() {
    return stopTimes;
  }

  public FlexDurationModifier durationModifier() {
    return durationModifier;
  }

  public ScheduledDeviatedTripBuilder withDurationModifier(FlexDurationModifier modifier) {
    this.durationModifier = modifier;
    return this;
  }

  @Override
  ScheduledDeviatedTripBuilder instance() {
    return this;
  }

  @Override
  protected ScheduledDeviatedTrip buildFromValues() {
    return new ScheduledDeviatedTrip(this);
  }
}
