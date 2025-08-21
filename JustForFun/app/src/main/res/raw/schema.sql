enum Genre {
  ACTION
  ACTION_DISASTER
  ACTION_HEROIC_BLOODSHED
  ACTION_MARTIAL_ARTS
  ACTION_SPY
  ACTION_SUPERHERO
  ACTION_WAR
  ADVENTURE
  ADVENTURE_PIRATE
  ADVENTURE_SWASHBUCKLER
  ADVENTURE_SAMURAI
  ANIMATION
  ANIMATION_CGI
  ANIMATION_CUTOUT
  ANIMATION_STOP_MOTION
  ANIMATION_TRADITIONAL
  BIOGRAPHY
  COMEDY
  COMEDY_BLACK
  COMEDY_PARODY
  COMEDY_ROMANTIC
  COMEDY_SLAPSTICK
  COMEDY_SATIRE
  COMEDY_SCREWBALL
  CRIME
  CRIME_GANGSTER
  CRIME_POLICE_PROCEDURAL
  CRIME_HEIST
  CRIME_NOIR
  CRIME_NEO_NOIR
  DOCUMENTARY
  DOCUMENTARY_NATURE
  DOCUMENTARY_SOCIAL
  DOCUMENTARY_BIOGRAPHICAL
  DRAMA
  DRAMA_CRIME
  DRAMA_HISTORICAL
  DRAMA_MEDICAL
  DRAMA_POLITICAL
  DRAMA_ROMANTIC
  DRAMA_SOCIAL
  DRAMA_TRAGEDY
  DRAMA_LEGAL
  FAMILY
  FANTASY
  FANTASY_DARK
  FANTASY_HIGH
  FANTASY_URBAN
  HISTORY
  HORROR
  HORROR_BODY
  HORROR_COMEDY
  HORROR_GOTHIC
  HORROR_PSYCHOLOGICAL
  HORROR_SLASHER
  HORROR_SUPERNATURAL
  HORROR_ZOMBIE
  HORROR_FOUND_FOOTAGE
  MUSIC
  MUSICAL
  MYSTERY
  MYSTERY_WHO_DUNNIT
  ROMANCE
  ROMANCE_HISTORICAL
  ROMANCE_PARANORMAL
  ROMANCE_TRAGEDY
  SCI_FI
  SCI_FI_APOCALYPTIC
  SCI_FI_CYBERPUNK
  SCI_FI_DYSTOPIAN
  SCI_FI_SPACE_OPERA
  SCI_FI_TIME_TRAVEL
  SPORT
  THRILLER
  THRILLER_CONSPIRACY
  THRILLER_LEGAL
  THRILLER_PSYCHOLOGICAL
  THRILLER_TECHNO
  THRILLER_POLITICAL
  THRILLER_CRIME
  WAR
  WESTERN
  WESTERN_SPAGHETTI
  WESTERN_REVISIONIST
}

enum MediaType {
  MOVIE
  TV_SHOW
}

enum ParentType {
  REVIEW
  REPLY
}

enum AwardPrestige {
  LOW
  MEDIUM
  HIGH
  LEGENDARY
}

enum CompanyType {
  PRODUCTION
  DISTRIBUTION
  STUDIO
  NETWORK
  STREAMING
  INDEPENDENT
}

enum Gender {
  MALE
  FEMALE
  NON_BINARY
  OTHER
  PREFER_NOT_TO_SAY
}

enum PersonRole {
  ACTOR
  ACTRESS
  DIRECTOR
  PRODUCER
  WRITER
  SCREENWRITER
  CINEMATOGRAPHER
  EDITOR
  COMPOSER
  SOUND_DESIGNER
  PRODUCTION_DESIGNER
  COSTUME_DESIGNER
  MAKEUP_ARTIST
  VISUAL_EFFECTS_SUPERVISOR
  STUNT_COORDINATOR
  CASTING_DIRECTOR
  EXECUTIVE_PRODUCER
  CO_PRODUCER
  ASSOCIATE_PRODUCER
  NARRATOR
  VOICE_ACTOR
  CHOREOGRAPHER
  SET_DECORATOR
  SCRIPT_SUPERVISOR
  GAFFER
  CAMERA_OPERATOR
}

enum ReviewType {
  USER
  CRITIC
  VERIFIED_BUYER
  EARLY_ACCESS
  FESTIVAL
}

Table User {
  id varchar [pk]
  name varchar
  email varchar
  username varchar
  bio varchar
  profilePicture varchar
  joinDate bigint
  location varchar
  website varchar
  isPrivate boolean
  isVerified boolean
  lastActivateDate bigint
  totalReviews int
  totalRatings int
  averageRating float
}

Table UserPreferences {
  userId varchar [pk, ref: - User.id]
  notificationsEnable boolean
  emailNotifications boolean
  pushNotifications boolean
  privateProfile boolean
  showWatchHistory boolean
  showRatings boolean
  showReviews boolean
  allowFollowRequests boolean
  showOnlineStatus boolean
  language varchar
  theme varchar
}

Table UserStats {
  userId varchar [ref: > User.id]
  year int
  moviesWatched int
  tvShowsWatched int
  reviewsWritten int
  ratingsGiven int
  topGenre Genre
  averageRating float
  totalWatchTime int
}

Table Person {
  id varchar [pk]
  name varchar
  profileImage varchar
  biography text
  birthDate varchar
  deathDate varchar
  birthPlace varchar
  nationality varchar
  gender Gender
  height varchar
  imdbId varchar
  tmdbId varchar
  isPopular boolean
  popularityScore float
  totalMovies int
  totalAwards int
  netWorth varchar
  agent varchar
  spouseName varchar
  children int
  careerStartYear int
  isRetired boolean
  isDeceased boolean
}

Table SocialMediaLinks {
  personId varchar [pk, ref: - Person.id]
  instagram varchar
  twitter varchar
  facebook varchar
  youtube varchar
  tiktok varchar
  linkedin varchar
  personalWebsite varchar
  imdb varchar
  wikipedia varchar
}

Table Movie {
  id varchar [pk]
  name varchar
  releaseYear varchar
  moviePoster varchar
  backdropImage varchar
  description text
  rating float
  ratingCount int
  totalAwards int
  budget varchar
  boxOffice varchar
  runtime int
  language varchar
  country varchar
  imdbId varchar
  tmdbId varchar
  userRating float
  isWatched boolean
  isLiked boolean
  isFavourite boolean
  isMarkedToWatch boolean
  certification varchar
  tagline varchar
  homepage varchar
  trailer varchar
  isAdultContent boolean
  originalTitle varchar
  popularity float
  voteAverage float
  voteCount int
}

Table TVShow {
  id varchar [pk]
  name varchar
  startingYear varchar
  endingYear varchar
  tvShowPoster varchar
  backdropImage varchar
  description text
  rating float
  ratingCount int
  totalAwards int
  seasons int
  episodes varchar
  averageRuntime varchar
  language varchar
  country varchar
  network varchar
  status varchar
  imdbId varchar
  tmdbId varchar
  userRating float
  isWatched boolean
  isLiked boolean
  isFavourite boolean
  isMarkedToWatch boolean
}

Table PersonInMovie {
  id varchar [pk]
  personId varchar [ref: > Person.id]
  movieId varchar [ref: > Movie.id]
  personName varchar
  role PersonRole
  characterName varchar
  order int
  department varchar
  job varchar
  profileImage varchar
  creditId varchar
  isMainCast boolean
  screenTime int
  salary varchar
  episodeCount int
}

Table PersonInTVShow {
  id varchar [pk]
  personId varchar [ref: > Person.id]
  tvShowId varchar [ref: > TVShow.id]
  personName varchar
  role PersonRole
  characterName varchar
  order int
  department varchar
  job varchar
  profileImage varchar
  creditId varchar
  isMainCast boolean
  screenTime int
  salary varchar
  episodeCount int
}

Table ProductionCompany {
  id varchar [pk]
  name varchar
  logo varchar
  description text
  foundedYear int
  headquarters varchar
  website varchar
  totalMovies int
  totalRevenue varchar
  isActive boolean
  parentCompany varchar [ref: > ProductionCompany.id]
  companyType CompanyType
}

Table Award {
  id varchar [pk]
  name varchar
  category varchar
  awardShow varchar
  year int
  winner varchar [ref: > Person.id]
  winnerName varchar
  movieId varchar [ref: > Movie.id]
  movieName varchar
  isNomination boolean
  description text
  prestige AwardPrestige
  image varchar
  ceremony varchar
  presenter varchar
}

Table Review {
  id varchar [pk]
  movieId varchar [ref: > Movie.id]
  userId varchar [ref: > User.id]
  userName varchar
  userAvatar varchar
  rating float
  title varchar
  content text
  dateCreated varchar
  dateModified varchar
  isVerifiedPurchase boolean
  helpfulVotes int
  totalVotes int
  isSpoiler boolean
  isVerified boolean
  reviewType ReviewType
  wouldRecommend boolean
  watchedDate varchar
  watchedWith varchar
  watchedWhere varchar
}

Table Reply {
  id varchar [pk]
  userId varchar [ref: > User.id]
  parentId varchar
  parentType ParentType
  userProfilePhoto varchar
  userName varchar
  replyText text
  timestamp bigint
  likeCount int
  dislikeCount int
  isLiked boolean
  isDisliked boolean
  isEdited boolean
  editedTimestamp bigint
}

Table Rating {
  id varchar [pk]
  mediaRatingId varchar
  mediaType MediaType
  userId varchar [ref: > User.id]
  rating float
  timestamp bigint
  isPublic boolean
}

Table UserFollowing {
  followerId varchar [ref: > User.id]
  followingId varchar [ref: > User.id]
}

Table UserFavourites {
  userId varchar [ref: > User.id]
  mediaId varchar
  mediaType MediaType
}

Table UserWatchList {
  userId varchar [ref: > User.id]
  mediaId varchar
  mediaType MediaType
}

Table UserWatchHistory {
  userId varchar [ref: > User.id]
  mediaId varchar
  mediaType MediaType
  watchedAt bigint
}

Table UserReviews {
  userId varchar [ref: > User.id]
  reviewId varchar [ref: > Review.id]
}

Table UserRatings {
  userId varchar [ref: > User.id]
  ratingId varchar [ref: > Rating.id]
}

Table UserLikedReviews {
  userId varchar [ref: > User.id]
  reviewId varchar [ref: > Review.id]
}

Table UserLikedReplies {
  userId varchar [ref: > User.id]
  replyId varchar [ref: > Reply.id]
}

Table UserReplies {
  userId varchar [ref: > User.id]
  replyId varchar [ref: > Reply.id]
}

Table UserSocialLinks {
  userId varchar [ref: > User.id]
  platform varchar
  url varchar
}

Table UserTopGenres {
  userId varchar [ref: > User.id]
  genre Genre
  priority int
}

Table UserCustomListMovies {
  userId varchar [ref: > User.id]
  movieId varchar [ref: > Movie.id]
}

Table UserCustomListTVShows {
  userId varchar [ref: > User.id]
  tvShowId varchar [ref: > TVShow.id]
}

Table UserBlockedUsers {
  userId varchar [ref: > User.id]
  blockedUserId varchar [ref: > User.id]
}

Table MovieGenres {
  movieId varchar [ref: > Movie.id]
  genre Genre
}

Table TVShowGenres {
  tvShowId varchar [ref: > TVShow.id]
  genre Genre
}

Table MovieAwards {
  movieId varchar [ref: > Movie.id]
  awardId varchar [ref: > Award.id]
}

Table TVShowAwards {
  tvShowId varchar [ref: > TVShow.id]
  awardId varchar [ref: > Award.id]
}

Table MovieReviews {
  movieId varchar [ref: > Movie.id]
  reviewId varchar [ref: > Review.id]
}

Table TVShowReviews {
  tvShowId varchar [ref: > TVShow.id]
  reviewId varchar [ref: > Review.id]
}

Table MovieMoreLikeThis {
  movieId varchar [ref: > Movie.id]
  relatedMovieId varchar [ref: > Movie.id]
}

Table TVShowMoreLikeThis {
  tvShowId varchar [ref: > TVShow.id]
  relatedTVShowId varchar [ref: > TVShow.id]
}

Table MovieKeywords {
  movieId varchar [ref: > Movie.id]
  keyword varchar
}

Table MovieTrivia {
  movieId varchar [ref: > Movie.id]
  trivia text
}

Table MovieGoofs {
  movieId varchar [ref: > Movie.id]
  goof text
}

Table MovieQuotes {
  movieId varchar [ref: > Movie.id]
  quote text
}

Table MovieSoundtracks {
  movieId varchar [ref: > Movie.id]
  soundtrackId varchar
}

Table MovieAlternativeTitles {
  movieId varchar [ref: > Movie.id]
  country varchar
  title varchar
}

Table MovieSequels {
  movieId varchar [ref: > Movie.id]
  sequelId varchar [ref: > Movie.id]
}

Table MoviePrequels {
  movieId varchar [ref: > Movie.id]
  prequelId varchar [ref: > Movie.id]
}

Table MovieProductionCompanies {
  movieId varchar [ref: > Movie.id]
  companyId varchar [ref: > ProductionCompany.id]
}

Table MovieDistributors {
  movieId varchar [ref: > Movie.id]
  distributorId varchar [ref: > ProductionCompany.id]
}

Table TVShowCast {
  tvShowId varchar [ref: > TVShow.id]
  castId varchar [ref: > Person.id]
  role varchar
}

Table TVShowShowRunners {
  tvShowId varchar [ref: > TVShow.id]
  showRunnerId varchar [ref: > Person.id]
}

Table TVShowDirectors {
  tvShowId varchar [ref: > TVShow.id]
  directorId varchar [ref: > Person.id]
}

Table TVShowWriters {
  tvShowId varchar [ref: > TVShow.id]
  writerId varchar [ref: > Person.id]
}

Table PersonKnownFor {
  personId varchar [ref: > Person.id]
  mediaId varchar
  mediaType MediaType
}

Table PersonRoles {
  personId varchar [ref: > Person.id]
  role PersonRole
}

Table PersonAwards {
  personId varchar [ref: > Person.id]
  awardId varchar [ref: > Award.id]
}

Table PersonTrivia {
  personId varchar [ref: > Person.id]
  trivia text
}

Table PersonAlternateNames {
  personId varchar [ref: > Person.id]
  alternateName varchar
}

Table PersonEducation {
  personId varchar [ref: > Person.id]
  education varchar
}

Table CompanySubsidiaries {
  parentCompanyId varchar [ref: > ProductionCompany.id]
  subsidiaryId varchar [ref: > ProductionCompany.id]
}

Table CompanyKeyPeople {
  companyId varchar [ref: > ProductionCompany.id]
  personId varchar [ref: > Person.id]
}

Table CompanyMovies {
  companyId varchar [ref: > ProductionCompany.id]
  movieId varchar [ref: > Movie.id]
}

Table CompanyAwards {
  companyId varchar [ref: > ProductionCompany.id]
  awardId varchar [ref: > Award.id]
}

Table ReviewPros {
  reviewId varchar [ref: > Review.id]
  pro text
}

Table ReviewCons {
  reviewId varchar [ref: > Review.id]
  con text
}

Table ReviewTags {
  reviewId varchar [ref: > Review.id]
  tag varchar
}

Table ReviewReplies {
  reviewId varchar [ref: > Review.id]
  replyId varchar [ref: > Reply.id]
}

Table ReplyReplies {
  parentReplyId varchar [ref: > Reply.id]
  childReplyId varchar [ref: > Reply.id]
}