﻿using Geohash;
using Microsoft.Azure.Cosmos.Spatial;

namespace MetInProximityBack.Types.Location
{
    public static class LocationFactory
    {

        public static LocationObject CreateLocObj(
            string userId,
            double longitude,
            double latitude,
            bool openToMessages,
            bool openToPrivate = false
        )
        {
            var geohasher = new Geohasher();

            var location = new LocationObject
            {
                Id = userId,
                UserId = userId,
                Location = CreatePoint(longitude, latitude),
                Geohash = geohasher.Encode(latitude, longitude),
                openToMessages = openToMessages,
                openToPrivate = openToPrivate
            };

            return location;
        }

        public static LocResObj CreateLocResObj(
            double lon ,
            double lat ,
            string mapImage
        )
        {
            return new LocResObj
            {
                lon = lon,
                lat = lat,
                mapImage = mapImage
            };
        }

        public static Point CreatePoint(
            double longitude,
            double latitude
        )
        {
            return new Point(longitude, latitude);
        }


    }
}
