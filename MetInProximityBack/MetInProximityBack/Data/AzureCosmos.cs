using Microsoft.Azure.Cosmos;
using Microsoft.Azure.Cosmos.Spatial;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Linq;
using Microsoft.EntityFrameworkCore;


namespace MetInProximityBack.Data
{
    public class AzureCosmos
    {

        private Container _container;
        
        public AzureCosmos (CosmosClient cosmosClient, string databaseName, string containerName)
        {
           
            Container con = cosmosClient.GetContainer(databaseName, containerName);
            _container = con;
        }

        // TESTED MANUALLY WORKS
        public async Task AddLocation(LocationObject locObj)
        {
            try
            {
                ItemResponse<LocationObject> res = await _container.UpsertItemAsync(locObj, new PartitionKey(locObj.UserId));
                Console.WriteLine($"Upsert Successful! Item: {res.Resource}");
            }
            catch (CosmosException ex)
            {
                Console.WriteLine($"CosmosException: {ex.StatusCode} - {ex.Message}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Unexpected error: {ex.Message} - {ex.InnerException}");
            }
        }

        public async Task<List<NearbyUser>> GetNearbyLocations(Point contextPoint, string RequestingUserId)
        {
            var NearbyLocations = new List<NearbyUser>();

            var query = _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => (locObj.Location.Distance(contextPoint) < 2000) && locObj.UserId != RequestingUserId )
                .Select(locObj => new NearbyUser
                {
                    UserId = locObj.UserId,
                    openToMessages = locObj.openToMessages,
                    openToPrivate = locObj.openToPrivate,
                })
                .Take(20)
                .ToFeedIterator();

            while (query.HasMoreResults)
            {
                var response = await query.ReadNextAsync();
                NearbyLocations.AddRange(response);
            }
            

            return NearbyLocations.ToList();
        }

        public async Task<LocationObject> GetLocationObjectByUserId( string userId)
        {
            LocationObject locObj =  await _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => userId == locObj.UserId)
                .OrderByDescending(locObj => locObj.Timestamp)
                .FirstAsync();

            return locObj;
        }



    }
}

// find or create database and container

/*
    Container container = cosmosClient.GetContainer(databaseName, containerName);
    if (container != null)
    { 
        return container;
    }
           
    DatabaseResponse dbRes = await cosmosClient.CreateDatabaseIfNotExistsAsync(
        id: databaseName,
        throughput: 400
    );

    ContainerResponse conRes = await dbRes.Database.CreateContainerIfNotExistsAsync(
    id: containerName,
    partitionKeyPath: "/UserId"
    );
*/
