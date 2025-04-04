namespace MetInProximityBack.Types.NearbyUser
{
    public class NearbyUserWithConnId : NearbyUser
    {
        public string? connId { get; set; } = string.Empty;

        public NearbyUserWithConnId(NearbyUser user, string connId)
        {
            UserId = user.UserId;
            this.connId = connId;
            openToMessages = user.openToMessages;
        }
    }
}
