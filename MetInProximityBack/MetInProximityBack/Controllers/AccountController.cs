using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    [Route("api/account")]
    [ApiController]
    public class AccountController : Controller
    {
        [HttpPost("googleLogin")]
        public IActionResult googleOAuthLogin()
        {
            return View();
        }

        [HttpPost("microsoftLogin")]
        public IActionResult microsoftOAuthLogin()
        {
            return View();
        }

        [HttpPost("logout")]
        public IActionResult logout()
        {
            return View();
        }

        [HttpPost("ping")]
        public IActionResult ping()
        {
            return View();
        }
    }
}
