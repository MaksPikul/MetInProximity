using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    [Route("api/user")]
    [ApiController]
    public class UserActionController : Controller
    {
        [HttpGet]
        public IActionResult GetAvailableForPrivateUserMsg()
        {
            return View();
        }

        
        public IActionResult UpdateUserAvailableForPrivateMsg()
        {
            return View();
        }
    }

}
