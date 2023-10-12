package ru.javacat.justweather.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.javacat.justweather.response_models.Astro
import ru.javacat.justweather.response_models.Condition
import ru.javacat.justweather.response_models.Day
import ru.javacat.justweather.response_models.Hour

@Entity
data class DbWeather(
    @Embedded val current: DbCurrent,
    @Embedded val location: DbLocation
)

@Entity
data class DbCurrent(
    val locationId: String,
    val cloud: Int,
    @Embedded val condition: Condition,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val gust_kph: Double,
    val gust_mph: Double,
    val humidity: Int,
    val is_day: Int,
    val last_updated: String,
    val last_updated_epoch: Int,
    val precip_in: Double,
    val precip_mm: Double,
    val pressure_in: Double,
    val pressure_mb: Double,
    val temp_c: Double,
    val temp_f: Double,
    val uv: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val wind_kph: Double,
    val wind_mph: Double
)

@Entity
data class DbLocation(
    @PrimaryKey
    val dbId: String,
    val country: String,
    val lat: Double,
    val localtime: String,
    val localtime_epoch: Int,
    val lon: Double,
    val name: String,
    val region: String,
)

@Entity
data class DbAlert(
    val locationId: String,
    val areas: String,
    val category: String,
    val certainty: String,
    val desc: String,
    val effective: String,
    val event: String,
    val expires: String,
    val headline: String,
    val instruction: String,
    val msgtype: String,
    val note: String,
    val severity: String,
    val urgency: String
)
@Entity
data class DbForecastday(
    val locationId: String,
    @Embedded val astro: Astro,
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hours: List<Hour>
)

@Entity
data class DbHour(
    val forecastDate: String,
    val chance_of_rain: Int,
    val chance_of_snow: Int,
    val cloud: Int,
    @Embedded val condition: Condition,
    val dewpoint_c: Double,
    val dewpoint_f: Double,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val gust_kph: Double,
    val gust_mph: Double,
    val heatindex_c: Double,
    val heatindex_f: Double,
    val humidity: Int,
    val is_day: Int,
    val precip_in: Double,
    val precip_mm: Double,
    val pressure_in: Double,
    val pressure_mb: Double,
    val temp_c: Double,
    val temp_f: Double,
    val time: String,
    val time_epoch: Int,
    val uv: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val will_it_rain: Int,
    val will_it_snow: Int,
    val wind_degree: Int,
    val wind_dir: String,
    val wind_kph: Double,
    val wind_mph: Double,
    val windchill_c: Double,
    val windchill_f: Double
)



data class DbCurrentWithLocation(
    val location: DbLocation,

    @Relation(
        parentColumn = "dbId",
        entityColumn = "locationId"
    )
    @Embedded val current: DbCurrent,
)

data class DbAlertsWithLocation(
    val location: DbLocation,

    @Relation(
        parentColumn = "dbId",
        entityColumn = "locationId"
    )
    val alerts: List<DbAlert>
)

data class DbForecastsWithLocation(
    val location: DbLocation,

    @Relation(
        parentColumn = "dbId",
        entityColumn = "locationId"
    )
    val forecasts: List<DbForecastday>
)

data class DbHoursWithForecast(
    @Embedded val forecastday: DbForecastday,

    @Relation(
        parentColumn = "date",
        entityColumn = "forecastDate"
    )
    val hours: List<Hour>
)

