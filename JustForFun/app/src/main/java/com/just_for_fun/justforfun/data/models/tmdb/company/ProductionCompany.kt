package com.just_for_fun.justforfun.data.models.tmdb.company

import com.just_for_fun.justforfun.data.models.tmdb.award.company.CompanyType

// Represents a production company
data class ProductionCompany(
    val id: String = "",                     // Unique identifier
    val name: String = "",                   // Company name
    val logo: String = "",                   // Company logo URL
    val description: String = "",            // Company description
    val foundedYear: Int = 0,                // Year founded (0 if unknown)
    val headquarters: String = "",           // Headquarters location
    val website: String = "",                // Official website
    val totalMovies: Int = 0,                // Total movies produced
    val totalRevenue: String = "",           // Total revenue generated
    val isActive: Boolean = true,            // Is company still active
    val parentCompany: String = "",          // Parent company ID if applicable
    val subsidiaries: List<String> = emptyList(), // Subsidiary company IDs
    val keyPeople: List<String> = emptyList(),    // Key people person IDs
    val movieIds: List<String> = emptyList(),     // Movies produced by this company
    val awards: List<String> = emptyList(),       // Award IDs won by company
    val companyType: CompanyType = CompanyType.PRODUCTION
)
