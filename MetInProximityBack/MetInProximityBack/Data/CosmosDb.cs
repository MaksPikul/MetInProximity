using Microsoft.Azure.Cosmos;
using Microsoft.Azure.Cosmos.Spatial;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Linq;


namespace MetInProximityBack.Data
{
    public class CosmosDb : INoSqlDb
    {

        private readonly Container _container;

        public CosmosDb(
            CosmosClient cosmosClient, 
            string databaseName, 
            string containerName
        ) {
            _container = cosmosClient.GetContainer(databaseName, containerName);
        }

        public async Task AddLocation(LocationObject locObj)
        {
            await _container.UpsertItemAsync(locObj, new PartitionKey(locObj.Geohash));
        }

        public async Task<List<LocationObject>> GetNearbyLocations(Point contextPoint)
        {
            var NearbyLocations = new List<LocationObject>();

            var query = _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => locObj.Location.Distance(contextPoint) < 2000)
                .Take(20)
                .ToFeedIterator();

            while (query.HasMoreResults)
            {
                var response = await query.ReadNextAsync();
                NearbyLocations.AddRange(response);
            }

            return NearbyLocations.ToList();
        }



    }
}
