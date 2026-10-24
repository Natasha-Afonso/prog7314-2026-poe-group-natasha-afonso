using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models;


public class Profile
{

 // The Firebase uid, taken from the token
    [Key]                                                    
    public string UserId { get; set; } = string.Empty;      

    public string UserName { get; set; } = string.Empty;  
// e.g. "CURIOUS"
    public string Personality { get; set; } = string.Empty;  
    public DateOnly? Birthday { get; set; }
// e.g. "ORGANISED"                
    public string Aspiration { get; set; } = string.Empty;  

    // avatar choices (option names; the app turns them into drawables)
    public string BodyRef { get; set; } = "base_body_f";          
    public string HairRef { get; set; } = "hair_1";              
    public string ExtensionRef { get; set; } = "extention_hair";  
    public string EyeColor { get; set; } = "#240C0C";            
    public string MouthRef { get; set; } = "grin_mouth";        
    public string LipstickRef { get; set; } = "none";  
    public DateTime CreatedAt { get; set; }                      
}