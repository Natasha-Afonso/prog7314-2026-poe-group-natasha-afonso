using System;
using System.Globalization;

namespace MetrixAPI.Helpers
{
    // Custom exception class for throwing application specific exceptions (e.g. for validation) 
    // that can be caught and handled within the application
    public class AppException : Exception
    {
        public AppException() : base() { }

        public AppException(string message) : base(message) { }

        public AppException(string message, params object[] args)
            : base(String.Format(CultureInfo.CurrentCulture, message, args))
        {
        }
    }
}
/*References
 
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Helpers/AppException.cs > [Accessed 17 September 2026]
 
 */