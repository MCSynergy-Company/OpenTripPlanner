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