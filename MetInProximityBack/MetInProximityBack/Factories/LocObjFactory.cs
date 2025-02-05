using Geohash;

namespace MetInProximityBack.Types.Location
{
    public static class LocObjFactory
    {

        public static LocationObject CreateLocObj(
            string userId,
            double longitude,
            double latitude
        )
        {
            var geohasher = new Geohasher();

            var location = new LocationObject
            {
                UserId = userId,
                Location = new GeoJsonPoint(longitude, latitude),
                Geohash = geohasher.Encode(latitude, longitude)
            };

            return location;
        }

    }
}
