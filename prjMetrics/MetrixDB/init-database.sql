 USE master;
GO
-- Create the MetrixDB database only if it does not already exist
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'MetrixDB')
BEGIN
    CREATE DATABASE MetrixDB;
END
GO
-- Switch to the Metrix database
USE MetrixDB;
GO
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

CREATE TABLE Journal (
    JournalId INT PRIMARY KEY IDENTITY(1,1),
    UserId NVARCHAR(128) NOT NULL,
    Mood NVARCHAR(MAX) NOT NULL,
    Text NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETUTCDATE(),
    UpdatedAt DATETIME DEFAULT GETUTCDATE()
);

GO