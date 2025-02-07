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

        public async Task<HashSet<NearbyUser>> GetNearbyLocations(Point contextPoint)
        {
            var NearbyLocations = new List<NearbyUser>();

            var query = _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => locObj.Location.Distance(contextPoint) < 2000)
                .Select(locObj => new NearbyUser
                {
                    UserId = locObj.UserId,
                    openToMessages = locObj.openToMessages
                })
                .Take(20)
                .ToFeedIterator();

            while (query.HasMoreResults)
            {
                var response = await query.ReadNextAsync();
                NearbyLocations.AddRange(response);
            }

            return NearbyLocations.ToHashSet();
        }



    }
}
