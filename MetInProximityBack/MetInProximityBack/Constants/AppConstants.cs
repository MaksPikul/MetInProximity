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

    }
}
