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
            LocationFactory.CreateLocObj( "1_near&priv", 0.00002889, -0.00060548, true, true ), 
            LocationFactory.CreateLocObj( "2_near&priv", 0.00216696, 0.00000003, true, true ),
            LocationFactory.CreateLocObj( "3_near&priv", -0.00138628, 0.00225841, true, true ), 

            // Not available to private but nearby
            LocationFactory.CreateLocObj( "4_near", -0.00040432,0.00050540, true, false ), 
            LocationFactory.CreateLocObj( "5_near", 0.00028890, 0.00060548, true, false ),

            // Not nearby
            LocationFactory.CreateLocObj( "6", 0.00121100, -0.00000000, true, true ),
            LocationFactory.CreateLocObj( "7", -0.00080864, -0.00101080, true, true ),
            LocationFactory.CreateLocObj( "8", 0.00000000, 0.00121100, true, true )
        };

    }
}
