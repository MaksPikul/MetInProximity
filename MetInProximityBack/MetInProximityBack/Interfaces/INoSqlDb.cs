﻿using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Spatial;

namespace MetInProximityBack.Interfaces
{
    public interface INoSqlDb
    {
        Task AddLocation(LocationObject locObj);

        Task<HashSet<NearbyUser>> GetNearbyLocations(Point contextPoint);


    }
}
