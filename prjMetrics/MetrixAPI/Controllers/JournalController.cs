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
    //(Emeris School of Computer Science, 2025)
    [Authorize]
    [ApiController]
    [Route("api/client")]
    public class JournalController : ControllerBase
    {
        private string? CurrentUserId => User.FindFirst("user_id")?.Value; //gets current user with correct JWT
        private readonly ApplicationDbContext _context;

        public JournalController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<JournalResponse>>> Index()
        {
            var uid = await _context.Client.FirstOrDefaultAsync(l => l.Username == username);
            if (uid is null)
            {
                return Unauthorized();
            }

            //(StackOverflow, 2023) 
            //display the entries of the currently logged in user only
            //used inner join LINQ to join user and journal table data
            var entries = await _context.Journal.Where(j => j.UserId == uid)
                         .OrderByDescending(j => j.CreatedAt)
                         .Select(j => new JournalResponse)
                          {    //(Ahmed, 2018)
                               JournalId = j.JournalId,
                               Text = j.Text,
                               Mood = j.Mood,
                               CreatedAt = j.CreatedAt,
                               UpdatedAt = j.UpdatedAt

                          }).ToListAsync();


            return Ok(entries); //returns entries user already created
        }


        //(IIE Emeris School of Computer Science, 2025)
        [HttpPost]
        public async Task<IActionResult<JournalResponse>> Create(JournalRequest dto)
        {
            var uid = CurrentUserId; //gets current user ID
            if (uid is null)
            {
                return Unauthorized();
            }

            //creates a new journal entry
            var entry = new Journal
            {
                Mood = dto.Mood,
                UserId = uid
                Text = dto.Text,
                CreatedAt = DateTime.UtcNow
            };

            _context.Journal.Add(entry);
            await _context.SaveChanges(); //save changes to DB

            //returns a new updated response for JSON
            var response = new JournalResponse
            {
                Mood = entry.Mood,
                JournalId = entry.JournalId,
                Text = entry.Text,
                CreatedAt = entry.CreatedAt,
                UpdatedAt = entry.UpdatedAt
            };

            return CreatedAtAction(nameof(GetById, new {id = entry.JournalId}, response))
        }

        //method to get a specific entrys' ID when user wants it
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var uid = CurrentUserId; //gets current authorized user
            if (uid == null)
            {
                return Unauthorized();
            }

            //delete the entry created by user
            var entry = await _context.Journal.FirstOrDefaultAsync(j => j.JournalId == id && j.UserId == uid);
            if (entry is null)
            {
                return NotFound();
            }

            //returns a new updated response for JSON
            return Ok(new JournalResponse
            {
                Mood = entry.Mood,
                JournalId = entry.JournalId,
                Text = entry.Text,
                CreatedAt = entry.CreatedAt,
                UpdatedAt = entry.UpdatedAt
            });
        }

        //(Troelsen and Japikse, 2022)
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var uid = CurrentUserId;
            if (uid == null)
            {
                return Unauthorized();
            }

            //delete the entry created by user
            var entry = await _context.Journal.FirstOrDefaultAsync(j => j.JournalId == id && j.UserId == uid);
            if (entry is null)
            {
                return NotFound();
            }

            //deleted from database
            _context.Journal.Remove(entry);
            await _context.SaveChangesAsync();
            //return Ok();
            return NoContent();
        }

        [HttpPut("{id:int}")]
        public async Task<IActionResult> UpdateEntry(int id, JournalRequest dto)
        {
            var uid = CurrentUserId;
            if (client == null)
            {
                return Unauthorized();
            }

            //gets the entry
            var entry = await _context.Journal.FirstOrDefaultAsync(j => j.JournalId == id && j.UserId == uid);
            if (entry is null)
            {
                return NotFound();
            }

            //only the text, mood and time can be updated
            entry.Text = dto.Text;
            entry.Mood = dto.Mood;
            entry.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChanges(); //save changes to DB

            //returns a new updated response for JSON
            return Ok(new JournalResponse
            {
                Mood = entry.Mood,
                JournalId = entry.JournalId,
                Text = entry.Text,
                CreatedAt = entry.CreatedAt,
                UpdatedAt = entry.UpdatedAt
            });
        }

    }
}

/*References
 * 
 * Ahmed, F. 2018. How to join operations with database using LINQ. 2018. (Version 8)[Source Code]. Available at: https://www.c-sharpcorner.com/article/how-to-use-join-operations-with-database-using-linq/ [Accessed 20 September 2026]
 * IIE Emeris School of Computer Science. 2025. Getting strated with MVC, Entity Framework and SQL Azure database for CLDV6211 Part 3. (Version 8.0-SNAPSHOT) [Source code]. Available at: https://youtu.be/qkQ3YiKN5nk?si=9Zr8iT7DN8yE6x6C [Accessed 20 September 2026].
 * StackOverflow. 2023. How can I asynchronously transform one IEnumerable to another, just like LINQ's Select(), but using await on every transformed item? (Version 10)[source code]. Available at: <https://stackoverflow.com/questions/50884086/how-can-i-asynchronously-transform-one-ienumerable-to-another-just-like-linqs> [Accessed 20 September 2026]
 * Troelsen, A. and Japikse, P. 2022. Pro C# 10 with .NET 6 Foundational Principles and Practices in Programming. 11th ed. New York: Apress.
 */
