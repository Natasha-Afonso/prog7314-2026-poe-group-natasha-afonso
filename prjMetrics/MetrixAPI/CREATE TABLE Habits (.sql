CREATE TABLE Habits (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Label NVARCHAR(100) NOT NULL,
    IconKey NVARCHAR(50) NOT NULL DEFAULT 'default',
    ColorHex NVARCHAR(9) NOT NULL DEFAULT '#29B6F6',
    GoldCoins INT NOT NULL DEFAULT 5,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE()
);

CREATE TABLE HabitLogs (
    Id INT PRIMARY KEY IDENTITY(1,1),
    HabitId INT NOT NULL,
    LogDate DATE NOT NULL,
    Progress FLOAT NOT NULL DEFAULT 0,
    CONSTRAINT FK_HabitLogs_Habit FOREIGN KEY (HabitId)
        REFERENCES Habits(Id) ON DELETE CASCADE,
    CONSTRAINT UQ_HabitLogs_Habit_Date UNIQUE (HabitId, LogDate)
);

CREATE TABLE ClothingItems
(
    Id INT PRIMARY KEY IDENTITY(1,1),

    UserId NVARCHAR(128) NOT NULL,

    Name NVARCHAR(100) NOT NULL,

    Category NVARCHAR(50) NOT NULL,

    ImageUrl NVARCHAR(1000) NULL,

    IsWishlist BIT NOT NULL DEFAULT 0,

    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE()
);


CREATE TABLE Journal (
    JournalId INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Mood NVARCHAR(MAX) NOT NULL,
    Text NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE(),
    UpdatedAt DATETIME DEFAULT GETUTCDATE()
);



CREATE TABLE Profiles (
    -- Firebase uid (one profile per user)
    UserId       NVARCHAR(128) PRIMARY KEY,               
    UserName  NVARCHAR(50)  NOT NULL,        
    -- enum name from the app, e.g. "CURIOUS"         
    Personality  NVARCHAR(50)  NOT NULL DEFAULT '',       
    Birthday     DATE          NULL,         
     -- enum name from the app, e.g. "ORGANISED"           
    Aspiration   NVARCHAR(50)  NOT NULL DEFAULT '',      
    -- avatar choices: names of options, the app maps them to drawables
    BodyRef      NVARCHAR(50)  NOT NULL DEFAULT 'base_body_f',
    HairRef      NVARCHAR(50)  NOT NULL DEFAULT 'hair_1',
    ExtensionRef NVARCHAR(50)  NOT NULL DEFAULT 'extention_hair',
    EyeColor     NVARCHAR(9)   NOT NULL DEFAULT '#240C0C', -- #RRGGBB, iris colour
    MouthRef     NVARCHAR(50)  NOT NULL DEFAULT 'grin_mouth',
    LipstickRef  NVARCHAR(50)  NOT NULL DEFAULT 'lipstick6_grin',
    CreatedAt    DATETIME      NOT NULL DEFAULT GETUTCDATE()
);

CREATE TABLE Outfits (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Name NVARCHAR(200) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE()
);

CREATE TABLE OutfitItems (
    Id INT PRIMARY KEY IDENTITY(1,1),
    OutfitId INT NOT NULL,
    ClothingItemId INT NOT NULL,
    CONSTRAINT FK_OutfitItems_Outfit FOREIGN KEY (OutfitId)
        REFERENCES Outfits(Id) ON DELETE CASCADE,
    CONSTRAINT FK_OutfitItems_ClothingItem FOREIGN KEY (ClothingItemId)
        REFERENCES ClothingItems(Id) ON DELETE CASCADE
);

CREATE TABLE OutfitSchedules (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    OutfitId INT NOT NULL,
    [Date] DATE NOT NULL,
    CONSTRAINT FK_OutfitSchedules_Outfit FOREIGN KEY (OutfitId)
        REFERENCES Outfits(Id) ON DELETE CASCADE,
    CONSTRAINT UQ_OutfitSchedules_User_Date UNIQUE (UserId, [Date])
);

CREATE TABLE OutfitWearLogs (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    OutfitId INT NOT NULL,
    [Date] DATE NOT NULL,
    AcceptedSuggestion BIT NOT NULL,
    CONSTRAINT FK_OutfitWearLogs_Outfit FOREIGN KEY (OutfitId)
        REFERENCES Outfits(Id) ON DELETE CASCADE
);

CREATE TABLE SelfCareQuizResults (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Score INT NOT NULL,
    Reflection NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE()
);

CREATE TABLE UserSettings (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Language NVARCHAR(100) NOT NULL DEFAULT 'English',
    Theme NVARCHAR(100) NOT NULL DEFAULT 'System',
    PushNotifications BIT NOT NULL DEFAULT 1,
    FontSize NVARCHAR(50) NOT NULL DEFAULT 'Medium',
    HighContrast BIT NOT NULL DEFAULT 0,
    UpdatedAt DATETIME NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT UQ_UserSettings_UserId UNIQUE (UserId)
);

CREATE TABLE BugReports (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    Description NVARCHAR(4000) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE()
);

CREATE TABLE AppRatings (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Rating INT NOT NULL,
    Comment NVARCHAR(1000) NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE()
);


GO