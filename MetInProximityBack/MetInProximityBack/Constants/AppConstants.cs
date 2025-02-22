namespace MetInProximityBack.Constants
{
    public static class AppConstants
    {
        public static string ConnIdCacheKey(string userId)
        {
            return $"CHAT/USER:{userId}";
        }

        public static string COSMO_LOC_DB = "MetinCosmo";
        public static string COSMO_LOC_CON = "Locations";




    }
}
