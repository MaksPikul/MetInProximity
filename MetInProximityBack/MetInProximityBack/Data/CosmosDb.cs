using Microsoft.Azure.Cosmos;

namespace MetInProximityBack.Data
{
    public class CosmosDb
    {

        private readonly Container _container;

        public CosmosDb(
            CosmosClient cosmosClient, 
            string databaseName, 
            string containerName
        ) {
            _container = cosmosClient.GetContainer(databaseName, containerName);
        }





    }
}
