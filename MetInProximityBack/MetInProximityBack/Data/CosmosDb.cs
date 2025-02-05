using Microsoft.Azure.Cosmos;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Types.Location;

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


        public async Task AddLocation (LocationObject locObj)
        {

            await _container.UpsertItemAsync(locObj, new PartitionKey(locObj.Geohash));

        }




    }
}
