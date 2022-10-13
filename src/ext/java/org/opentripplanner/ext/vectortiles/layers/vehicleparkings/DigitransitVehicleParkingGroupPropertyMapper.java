package org.opentripplanner.ext.vectortiles.layers.vehicleparkings;

import java.util.Collection;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opentripplanner.common.model.T2;
import org.opentripplanner.ext.vectortiles.PropertyMapper;

public class DigitransitVehicleParkingGroupPropertyMapper
  extends PropertyMapper<VehicleParkingAndGroup> {

  public static DigitransitVehicleParkingGroupPropertyMapper create() {
    return new DigitransitVehicleParkingGroupPropertyMapper();
  }

  @Override
  protected Collection<T2<String, Object>> map(VehicleParkingAndGroup parkingAndGroup) {
    var group = parkingAndGroup.vehicleParkingGroup();
    String parking = JSONArray.toJSONString(
      parkingAndGroup
        .vehicleParking()
        .stream()
        .map(vehicleParkingPlace -> {
          JSONObject parkingObject = new JSONObject();
          parkingObject.put("carPlaces", vehicleParkingPlace.hasCarPlaces());
          parkingObject.put("bicyclePlaces", vehicleParkingPlace.hasBicyclePlaces());
          parkingObject.put("id", vehicleParkingPlace.getId().toString());
          // TODO translate name
          parkingObject.put("name", vehicleParkingPlace.getName().toString());
          return parkingObject;
        })
        .toList()
    );
    // TODO translate name
    return List.of(
        new T2<>("id", group.id().toString()),
        new T2<>("name", group.name().toString()),
        new T2<>("vehicleParking", parking)
    );
  }
}
