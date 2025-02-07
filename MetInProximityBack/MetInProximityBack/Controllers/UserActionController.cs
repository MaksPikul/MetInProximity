using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    public class UserActionController : Controller
    {
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
