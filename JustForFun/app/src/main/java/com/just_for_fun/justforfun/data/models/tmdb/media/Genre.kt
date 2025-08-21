package com.just_for_fun.justforfun.data.models.tmdb.award.media

enum class Genre(val displayName: String) {
    // Action and Subgenres
    ACTION("Action"),
    ACTION_DISASTER("Action - Disaster"),
    ACTION_HEROIC_BLOODSHED("Action - Heroic Bloodshed"),
    ACTION_MARTIAL_ARTS("Action - Martial Arts"),
    ACTION_SPY("Action - Spy"),
    ACTION_SUPERHERO("Action - Superhero"),
    ACTION_WAR("Action - War"),

    // Adventure and Subgenres
    ADVENTURE("Adventure"),
    ADVENTURE_PIRATE("Adventure - Pirate"),
    ADVENTURE_SWASHBUCKLER("Adventure - Swashbuckler"),
    ADVENTURE_SAMURAI("Adventure - Samurai"),

    // Animation
    ANIMATION("Animation"),
    ANIMATION_CGI("Animation - CGI"),
    ANIMATION_CUTOUT("Animation - Cutout"),
    ANIMATION_STOP_MOTION("Animation - Stop Motion"),
    ANIMATION_TRADITIONAL("Animation - Traditional"),

    // Biography
    BIOGRAPHY("Biography"),

    // Comedy and Subgenres
    COMEDY("Comedy"),
    COMEDY_BLACK("Comedy - Black"),
    COMEDY_PARODY("Comedy - Parody"),
    COMEDY_ROMANTIC("Comedy - Romantic"),
    COMEDY_SLAPSTICK("Comedy - Slapstick"),
    COMEDY_SATIRE("Comedy - Satire"),
    COMEDY_SCREWBALL("Comedy - Screwball"),

    // Crime and Subgenres
    CRIME("Crime"),
    CRIME_GANGSTER("Crime - Gangster"),
    CRIME_POLICE_PROCEDURAL("Crime - Police Procedural"),
    CRIME_HEIST("Crime - Heist"),
    CRIME_NOIR("Crime - Film Noir"),
    CRIME_NEO_NOIR("Crime - Neo-noir"),

    // Documentary
    DOCUMENTARY("Documentary"),
    DOCUMENTARY_NATURE("Documentary - Nature"),
    DOCUMENTARY_SOCIAL("Documentary - Social"),
    DOCUMENTARY_BIOGRAPHICAL("Documentary - Biographical"),

    // Drama and Subgenres
    DRAMA("Drama"),
    DRAMA_CRIME("Drama - Crime"),
    DRAMA_HISTORICAL("Drama - Historical"),
    DRAMA_MEDICAL("Drama - Medical"),
    DRAMA_POLITICAL("Drama - Political"),
    DRAMA_ROMANTIC("Drama - Romantic"),
    DRAMA_SOCIAL("Drama - Social"),
    DRAMA_TRAGEDY("Drama - Tragedy"),
    DRAMA_LEGAL("Drama - Legal"),

    // Family
    FAMILY("Family"),

    // Fantasy and Subgenres
    FANTASY("Fantasy"),
    FANTASY_DARK("Fantasy - Dark"),
    FANTASY_HIGH("Fantasy - High"),
    FANTASY_URBAN("Fantasy - Urban"),

    // History
    HISTORY("History"),

    // Horror and Subgenres
    HORROR("Horror"),
    HORROR_BODY("Horror - Body Horror"),
    HORROR_COMEDY("Horror - Comedy"),
    HORROR_GOTHIC("Horror - Gothic"),
    HORROR_PSYCHOLOGICAL("Horror - Psychological"),
    HORROR_SLASHER("Horror - Slasher"),
    HORROR_SUPERNATURAL("Horror - Supernatural"),
    HORROR_ZOMBIE("Horror - Zombie"),
    HORROR_FOUND_FOOTAGE("Horror - Found Footage"),

    // Music & Musical
    MUSIC("Music"),
    MUSICAL("Musical"),

    // Mystery
    MYSTERY("Mystery"),
    MYSTERY_WHO_DUNNIT("Mystery - Whodunnit"),

    // Romance
    ROMANCE("Romance"),
    ROMANCE_HISTORICAL("Romance - Historical"),
    ROMANCE_PARANORMAL("Romance - Paranormal"),
    ROMANCE_TRAGEDY("Romance - Tragedy"),

    // Science Fiction and Subgenres
    SCI_FI("Sci-Fi"),
    SCI_FI_APOCALYPTIC("Sci-Fi - Apocalyptic"),
    SCI_FI_CYBERPUNK("Sci-Fi - Cyberpunk"),
    SCI_FI_DYSTOPIAN("Sci-Fi - Dystopian"),
    SCI_FI_SPACE_OPERA("Sci-Fi - Space Opera"),
    SCI_FI_TIME_TRAVEL("Sci-Fi - Time Travel"),

    // Sport
    SPORT("Sport"),

    // Thriller and Subgenres
    THRILLER("Thriller"),
    THRILLER_CONSPIRACY("Thriller - Conspiracy"),
    THRILLER_LEGAL("Thriller - Legal"),
    THRILLER_PSYCHOLOGICAL("Thriller - Psychological"),
    THRILLER_TECHNO("Thriller - Techno"),
    THRILLER_POLITICAL("Thriller - Political"),
    THRILLER_CRIME("Thriller - Crime"),

    // War
    WAR("War"),

    // Western
    WESTERN("Western"),
    WESTERN_SPAGHETTI("Western - Spaghetti Western"),
    WESTERN_REVISIONIST("Western - Revisionist Western");

    override fun toString(): String {
        return displayName
    }
}
