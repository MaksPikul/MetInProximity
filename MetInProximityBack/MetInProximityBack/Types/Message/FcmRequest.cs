namespace MetInProximityBack.Types.Message
{
    public class FcmPayload
    {
        private FcmMessage message;

        public FcmPayload(string fcmToken, MessageResponse msgRes) {

            message = new FcmMessage
            {
                token = fcmToken,
                data = msgRes,  
                android = new FcmAndroid
                {
                    direct_boot_ok = true
                }
            };
        }

        private class FcmMessage
        {
            public string token { get; set; }
            public MessageResponse data { get; set; }  
            public FcmAndroid android { get; set; }
        }

        private class FcmAndroid
        {
            public bool direct_boot_ok { get; set; }
        }

    }

    
}
