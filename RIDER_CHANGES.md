# Fixes

Fix issue where cancelled trips don't update their trip times
file: application/src/main/java/org/opentripplanner/updater/trip/gtfs/TripTimesUpdater.java
commit: 1a247c3ca47d21c777d435b8f2652ed0fa46afd5

Allow pedestrians on cycleways.
This was needed so that you could walk around station Geldrop.
file: application/src/main/java/org/opentripplanner/osm/tagmapping/OsmTagMapper.java
commit: 191643f7e61346f2dc7ae77f237aa920a6160ef7

Make sure realtime added trips have a route and agency assigned, without needing a routeId.
file: application/src/main/java/org/opentripplanner/updater/trip/gtfs/RouteFactory.java
commit: 0c74a5832132e1bcd70a1a4d4d179f40e9aca81f

Allow nesting of stations
files:
- application/src/main/java/org/opentripplanner/transit/model/site/Station.java
- application/src/main/java/org/opentripplanner/transit/model/site/StationBuilder.java
- application/src/main/java/org/opentripplanner/gtfs/mapping/GTFSToTransitDataImportMapper.java
commit: 4db83a0a9b775a2b042ac4eed84edbc101fd0f2d

Added links between stops and boarding areas
commit: 13b901474b90e2063120af82b4adc8fd4329fae1

turned on logging of incremental realtime updates
commit: c846b3999eba0cbf0907314308719aaed3473402