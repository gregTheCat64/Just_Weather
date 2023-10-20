package ru.javacat.justweather.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.javacat.justweather.domain.models.Astro
import ru.javacat.justweather.domain.models.Condition
import ru.javacat.justweather.domain.models.Current
import ru.javacat.justweather.domain.models.Day
import ru.javacat.justweather.domain.models.Location
import java.time.LocalDate

@Entity(
    tableName = "weathers"
)
data class DbWeather(
    @PrimaryKey val id: String,
    @Embedded val current: Current,
    @Embedded val location: Location
)


@Entity(
    tableName = "alerts",
    foreignKeys = [
        ForeignKey(
            entity = DbWeather::class,
            parentColumns = ["id"],
            childColumns = ["weatherId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
            )
    ]
)
data class DbAlert(
    @PrimaryKey val weatherId: String,
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



@Entity(
    tableName = "forecast_days",
    foreignKeys = [
        ForeignKey(
            entity = DbWeather::class,
            parentColumns = ["id"],
            childColumns = ["weatherId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
)
data class DbForecastday(
    val weatherId: String,
    @PrimaryKey val id: String,
    @Embedded val astro: Astro,
    val date: String,
    val date_epoch: Int,
    @Embedded val day: Day,
    //val hours: List<Hour>
)

@Entity(
    tableName = "hours",
    foreignKeys = [
        ForeignKey(
            entity = DbForecastday::class,
            parentColumns = ["id"],
            childColumns = ["forecastId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DbHour(
    @PrimaryKey val forecastId: String,
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




