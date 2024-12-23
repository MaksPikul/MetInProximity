using MetInProximityBack.Enums;

namespace MetInProximityBack.Models
{
    public class AppUser_Event
    {
        public string AppUserId { get; set; }
        public string EventId { get; set; }
        public AppUser AppUser { get; set; }
        public Event Event { get; set; }
        public EventRole Role { get; set; }
    }
}
