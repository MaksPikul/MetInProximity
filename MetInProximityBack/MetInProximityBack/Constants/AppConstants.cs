using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos;
using System.Collections;

namespace MetInProximityBack.Constants
{
    public static class AppConstants
    {
        public static string ConnIdCacheKey(string userId)
        {
            return $"CHAT/USER:{userId}";
        }

        public static string COSMO_LOC_DB = "MetinCosmos";
        public static string COSMO_LOC_CON = "Locations";
        public static string COSMO_PART_KEY = "/UserId";



        // Phone user 
        // long = -122.08395287867832
        // lat = 37.42342342342342

        // 300 m radius, emulator puts location point on google HQ in California
        // This link should show you a radius around phone location (in dev mode)
        // https://www.freemaptools.com/radius-around-point.htm?lat=37.421778&lng=-122.084670&r=0
        public static ArrayList CosmoDbDummyData = new ArrayList
        {
            // Nearby and open to private
            LocationFactory.CreateLocObj( "1_near&priv", -122.084009, 37.425636, true, true ),
            LocationFactory.CreateLocObj( "2_near&priv", -122.084695, 37.421751, true, true ),
            LocationFactory.CreateLocObj( "3_near&priv", -122.081757, 37.422279, true, true ),

            // Not available to private but nearby
            LocationFactory.CreateLocObj( "4_near", -122.086282, 37.423335, true, false ),
            LocationFactory.CreateLocObj( "5_near", -122.081414, 37.423114, true, false ),

            // Not nearby
            LocationFactory.CreateLocObj( "6", -122.088023, 37.423702, true, true ),
            LocationFactory.CreateLocObj( "7", -122.079617, 37.421929, true, true ),
            LocationFactory.CreateLocObj( "8", -122.080218, 37.426565, true, true )
        };

    }
}
