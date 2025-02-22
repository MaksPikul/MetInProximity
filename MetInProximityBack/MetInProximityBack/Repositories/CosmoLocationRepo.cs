using Microsoft.Azure.Cosmos;
using MetInProximityBack.Constants;
using Microsoft.Azure.Cosmos.Spatial;
using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Linq;
using Microsoft.EntityFrameworkCore;
using System.ClientModel.Primitives;
using System.Threading;


namespace MetInProximityBack.Repositories
{
    public class CosmoLocationRepo
    {

        private Container _container;

        public CosmoLocationRepo(CosmosClient cosmosClient)
        {
            _container = cosmosClient.GetContainer(
                AppConstants.COSMO_LOC_DB,
                AppConstants.COSMO_LOC_CON
            );
        }

        // TESTED MANUALLY WORKS
        public async Task<ItemResponse<LocationObject>> AddOrUpdateLocation(LocationObject locObj)
        {
            ItemResponse<LocationObject> response = await _container.UpsertItemAsync(
                locObj, 
                new PartitionKey(locObj.UserId)
            );

            return response;
        }

        // Mostly used for testing icl
        public async Task<ItemResponse<LocationObject>> RemoveLocation(LocationObject locObj)
        {
            ItemResponse<LocationObject> response = await _container.DeleteItemAsync<LocationObject>(
                locObj.Id, 
                new PartitionKey(locObj.UserId)
            );

            return response;
        }

        public async Task<List<NearbyUser>> GetNearbyLocations(Point contextPoint, string RequestingUserId)
        {
            var NearbyLocations = new List<NearbyUser>();

            var query = _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => locObj.Location.Distance(contextPoint) < 2000 && locObj.UserId != RequestingUserId)
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

        public async Task<LocationObject?> GetLocationByUserId(string userId)
        {
            LocationObject? locObj = await _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => userId == locObj.UserId)
                .OrderByDescending(locObj => locObj.Timestamp)
                .FirstOrDefaultAsync();

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
