namespace MetInProximityBack.Models
{
    public class Event
    {
        public string Id { get; set; } = Guid.NewGuid().ToString();
        public string Title { get; set; } = string.Empty;
        public string Desc { get; set; } = string.Empty;

        // May or May not add Picture uploading for events
        //public string Picture { get; set; } = string.Empty; 
        public DateTime createdAt { get; set; } = DateTime.Now;
        public DateTime updatedAt { get; set; }
        public List<AppUser_Event> AppUserEvents { get; set; } = new List<AppUser_Event>();
    }
}
