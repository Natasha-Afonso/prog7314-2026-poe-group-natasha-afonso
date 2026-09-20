using MetrixAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using System.IO;
using System.Net.Http;
using MetrixAPI.Data;
using System.ComponentModel.Design.Serialization;

namespace MetrixAPI.Controllers
{
    //(Emeris School of Computer Science, 2025.)
    [ApiController]
    [Route("api/client")]
    public class JournalController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        //private readonly IConfiguration _configuration;

        public JournalController(ApplicationDbContext context)
        {
            _context = context;
            //_configuration = configuration;
        }

        [HttpGet("contracts")]
        public async Task<ActionResult<IEnumerable<ClientContract>>> Index([FromQuery] string username)
        {
            var client = await _context.Client.FirstOrDefaultAsync(l => l.Username == username);
            if (client == null)
            {
                return NotFound();
            }

            //(StackOverflow, 2019) 
            //display the contracts of the currently logged in client only
            //used inner join LINQ to join contract and client table data to be displayed to the coordinator
            var clientsContracts = await (from contract in _context.Contract
                                          join clients in _context.Client //(Code with Praveen, 2020)
                                          on contract.clientId equals clients.clientId
                                          where contract.clientId == client.clientId
                                          select new ClientContract
                                          {     //(Ahmed, 2018)
                                              contractId = contract.contractId,
                                              contractName = contract.contractName,
                                              startDate = contract.startDate,
                                              endDate = contract.endDate,
                                              documents = contract.documents,
                                              status = contract.status,
                                              serviceLevel = contract.serviceLevel,
                                              clientId = clients.clientId,
                                              clientName = clients.clientName,
                                              contactNo = clients.contactNo,
                                              emailAddress = clients.emailAddress,
                                              region = clients.region,
                                              cost = contract.cost
                                          }).ToListAsync();


            return Ok(clientsContracts); //return contracts in the table
        }

        [HttpGet("addcontract")]
        public async Task<IActionResult> AddContract([FromQuery] string username)
        {
            var client = await _context.Client.FirstOrDefaultAsync(l => l.Username == username);
            if (client == null)
            {
                return NotFound();
            }

            var contract = await _context.Contract.FirstOrDefaultAsync(l => l.clientId == client.clientId);

            var info = new ClientContract
            {
                clientId = client.clientId,
                clientName = client.clientName,
                region = client.region,
                //ShippingType = client.GetShippingType(),
                cost = contract?.cost ?? 0,
                contactNo = client.contactNo,
                emailAddress = client.emailAddress,
                serviceLevel = contract?.serviceLevel ?? string.Empty,
                contractName = contract?.contractName ?? string.Empty
            };


            return Ok(info);
        }

        //(IIE Emeris School of Computer Science, 2025)
        [HttpPost("addcontract")]
        // action method for handling post requests - file uploads
        public async Task<IActionResult> AddContract(IFormFile file, [FromForm] string username)
        {
            var clientUsername = HttpContext.Session.GetString("Username");

            //pull information about client from the database registered by the HR
            var client = await _context.Client.FirstOrDefaultAsync(l => l.Username == username);
            var contract = await _context.Contract.FirstOrDefaultAsync(c => c.clientId == client.clientId);

            if (client == null)
            {
                return BadRequest("Client information not found");
            }
            if (contract == null)
            {
                return NotFound();
            }


            await _context.SaveChangesAsync();
            return Ok();
        }

        [HttpGet("requests")]
        public async Task<IActionResult> AllRequests([FromQuery] string username)
        {

            //fetch data specific to logged in client
            var client = await _context.Client.FirstOrDefaultAsync(l => l.Username == username);
            if (client == null)
            {
                return NotFound();
            }

            //(StackOverflow, 2019) 
            //display the servie requests of the currently logged in client only
            var clientsRequests = await _context.ServiceRequest.Where(i => i.clientId == client.clientId).ToListAsync();

            return Ok(clientsRequests); //return requests in the table
        }

        //(Troelsen and Japikse, 2022)
        [HttpDelete("clients/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var client = await _context.Client.FindAsync(id);
            if (client == null)
            {
                return NotFound();
            }

            //delete the account registered to client
            var account = await _context.Account.FirstOrDefaultAsync(l => l.Username == client.Username);
            if (account != null)
            {
                _context.Account.Remove(account);
            }

            //delete clients contracts
            var contracts = await _context.Contract.Where(l => l.clientId == client.clientId).ToListAsync();
            if (contracts != null)
            {
                _context.Contract.RemoveRange(contracts);
            }

            //deleted from database
            _context.Client.Remove(client);
            await _context.SaveChangesAsync();
            return Ok();
        }

        [HttpPut("Update-employee/{id:guid}")]
        public IActionResult UpdateEmployee(Guid id, UpdateEmployeeDto dto)
        {
            var employee = dbContext.employees.Find(id);

            if (employee is null)
            {
                return NotFound();
            }

            employee.Name = dto.Name;
            employee.Email = dto.Email;
            employee.Salary = dto.Salary;
            employee.Phone = dto.Phone;
            //dbContext.employees.Update(id)

            dbContext.employees.Update(employee);
            dbContext.SaveChanges();

            return Ok(employee);
        }

    }
}

/*References
 * 
 * IIE Emeris School of Computer Science. 2025. SQL Database Setup: Choosing the right price plan & configuration (CLDV6211 Guide-Part 1). (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/THHFg1ovMFE?si=dyIrGkDbR31AuK2R [Accessed 2 April 2025].
 * IIE Emeris School of Computer Science. 2025. Creating SQL tables in Azure & Connecting to SSMS CLDV6211 Guide - Part 2. (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/DZnj-dCXVs4?si=94PYj0UOEr1JQyeZ [Accessed 2 April 2025].
 * IIE Emeris School of Computer Science. 2025. Getting started with MVC, Entity Framework and SQL Azure database for CLDV6211 Part 1. (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/FaAVkUH2qSw?si=CqwmA25LQt2baZ0G [Accessed 19 October 2025].
 * IIE Emeris School of Computer Science. 2025. Getting strated with MVC, Entity Framework and SQL Azure database for CLDV6211 Part 2. (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/eXGo2-nZnzk?si=I_eVpZtrhTMZiSNE [Accessed 19 October 2025].
 * IIE Emeris School of Computer Science. 2025. Getting strated with MVC, Entity Framework and SQL Azure database for CLDV6211 Part 3. (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/qkQ3YiKN5nk?si=9Zr8iT7DN8yE6x6C [Accessed 19 October 2025].
 * IIE Emeris School of Computer Science. 2025. Getting strated with MVC, Entity Framework and SQL Azure database for CLDV6211 Part 4. (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/FQgpg3cSphs?si=ygVsNST8k0CmF558 [Accessed 19 October 2025].
 * StackOverflow. 2014. Validate uploaded file extension. (Version 8)[Source Code]. Available at: https://stackoverflow.com/questions/25807209/validate-uploaded-file-extension [Accessed 18 October 2025]
 * Troelsen, A. and Japikse, P. 2022. Pro C# 10 with .NET 6 Foundational Principles and Practices in Programming. 11th ed. New York: Apress.
 */

/*
References for Part 3

* Chandra, S. 2026. JWT Authentication in ASP.NET Core: A complete step-bystey guide. (Version 10.0) [Source Code]. Available at: <https://medium.com/@Rajdip27/jwt-authentication-in-asp-net-core-a-complete-step-by-step-guide-94c955d907da> [Accessed 7 June 2026]
* CodeWithGopi. 2025. Create and Consume Web API in ASP.NET Core MVC | Full CRUD Operatins & API Consumption. (Version 10.0) [Source Code]. Available at:<https://youtu.be/knTcwvFWOQM?si=b3q3ksLg6HNnBjDg> [Accessed 4 June 2026]
* Emad, C. 2024. ASP.NET Core Web API with JWT (JSON Web Token) authentication. (Version 10.0) [Source Code]. Available at: <https://github.com/carolemad965/ASP.NET-Core-Web-API-with-JWT-Authentication> [Accessed 7 June 2026]
* Emeris School of Computer Science. 2025. PROG7311 Docker part 1 - Getting started. (Version 10.0) [Source Code]. Available at: <https://youtu.be/ldfC-rTzHPY?si=znsmPz-ZSPaY-4Ii> [Accessed 5 June 2026]
* Emeris School of Computer Science. 2025. PROG7311 Docker part 2- connecting between the two!. (Version 10.0) [Source Code]. Available at: <https://youtu.be/_0EhC3WauAc?si=nEazkCpBTV1YpHBp> [Accessed 5 June 2026]
* Emeris School of Computer Science. 2025. PROG7311 Docker part 3- databases in docker. (Version 10.0) [Source Code]. Available at: <https://youtu.be/6tYEbngWY-E?si=10rSEEpfSNL5Kg8k> [Accessed 5 June 2026]
* Emeris School of Computer Science. 2025. PROG7311 Docker part 4- connecting to database from the API.(Version 10.0) [Source Code]. Available at: <https://youtu.be/_PwpPMoeqzw?si=Q0ELlPZdN42dFU9q> [Accessed 5 June 2026]
* Emeris School of Computer Science. 2025. PROG7311 Docker part 5- connecting them all together- DB -API -MVC. (Version 10.0) [Source Code]. Available at: <https://youtu.be/YlIajRMdLPk?si=D75XZKO-L6nQhR-3> [Accessed 5 June 2026]
* Microsoft Learn. 2026. Parameter Binding in Minimal API apps. (Version 10)[source code]. Available at: <https://learn.microsoft.com/en-us/aspnet/core/fundamentals/minimal-apis/parameter-binding?view=aspnetcore-10.0> [Accessed 5 June 2026]
* Microsoft Learn. 2023. Use IHttpClientFactory to implement resilient HTTP requests. (Version 10)[source code]. Available at: <https://learn.microsoft.com/en-us/dotnet/architecture/microservices/implement-resilient-applications/use-httpclientfactory-to-implement-resilient-http-requests> [Accessed 5 June 2026]
* StackOverflow. 2025. How does IHttpClientFactory.CreateClient(customClient) creates the async definition of an HttpClient?. (Version 10)[source code]. Available at: <https://stackoverflow.com/questions/73308651/how-does-ihttpclientfactory-createclientcustomclient-creates-the-async-definit> [Accessed 5 June 2026]
* StackOverflow. 2023. How can I asynchronously transform one IEnumerable to another, just like LINQ's Select(), but using await on every transformed item? (Version 10)[source code]. Available at: <https://stackoverflow.com/questions/50884086/how-can-i-asynchronously-transform-one-ienumerable-to-another-just-like-linqs> [Accessed 5 June 2026]
* Verma, S. 2024. Model Binding in ASP.NET Core. (Version 10)[source code]. Available at: <https://www.c-sharpcorner.com/article/model-binding-in-asp-net-core2/> [Accessed 5 June 2026]
*/