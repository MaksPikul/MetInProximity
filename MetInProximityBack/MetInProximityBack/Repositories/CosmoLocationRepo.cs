using Microsoft.Azure.Cosmos;
using MetInProximityBack.Constants;
using Microsoft.Azure.Cosmos.Spatial;
using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Linq;
using Microsoft.EntityFrameworkCore;
using System.ClientModel.Primitives;
using System.Threading;
using NetTopologySuite.Index.HPRtree;


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
            try
            {
                ItemResponse<LocationObject> response = await _container.DeleteItemAsync<LocationObject>(
                    locObj.Id,
                    new PartitionKey(locObj.UserId)
                );

                return response;
            }
            catch (CosmosException ex)
            {
                if (ex.StatusCode == System.Net.HttpStatusCode.NotFound) {
                    return null;
                }
                throw new Exception(ex.Message);
            }
        }

        public async Task<List<NearbyUser>> GetNearbyLocations(Point contextPoint, string RequestingUserId)
        {
            var NearbyLocations = new List<NearbyUser>();

            var query = _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => locObj.Location.Distance(contextPoint) < 300 && locObj.UserId != RequestingUserId)
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

        // This shoudl get latest
        // Didnt give any lint errors
        // FOR ASYNC OPS, use this setup (while loop)
        public async Task<LocationObject?> GetLocationByUserId(string userId)
        {
            var query = _container.GetItemLinqQueryable<LocationObject>()
                .Where(locObj => userId == locObj.UserId)
                .OrderByDescending(locObj => locObj.Timestamp)
                .ToFeedIterator();

            while (query.HasMoreResults)
            {
                var response = await query.ReadNextAsync();
                return response.FirstOrDefault(); // Get the first item from the response
            }

            return null;
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
