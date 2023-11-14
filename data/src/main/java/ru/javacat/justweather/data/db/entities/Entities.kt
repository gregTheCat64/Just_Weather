package ru.javacat.justweather.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "weathers_table"
)
data class DbWeather(
    var isCurrent: Boolean,
    var isLocated: Boolean,
    @PrimaryKey val id: String,
    var positionId: Int,
    @Embedded val current: DbCurrent,
//    @Embedded val location: DbLocation
)

@Entity(
    tableName = "locations_table",
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
data class DbLocation(
    @PrimaryKey val weatherId: String,
    val country: String,
    val lat: Double,
    val localtime: String,
    val localtime_epoch: Int,
    val lon: Double,
    val name: String,
    val region: String,
    val tz_id: String,
    var localTitle: String,
    var localSubtitle: String
)

@Entity(
    tableName = "alerts_table",
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
data class DbAlert(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val weatherId: String,
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
    tableName = "forecast_days_table",
    foreignKeys = [
        ForeignKey(
            entity = DbWeather::class,
            parentColumns = ["id"],
            childColumns = ["weatherId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["weatherId", "date"]
)
data class DbForecastday(
    val weatherId: String,
    val date: String,
    @Embedded val astro: DbAstro,
    val date_epoch: Int,
    @Embedded val day: DbDay,
    //@Embedded val hours: List<DbHour>
)

@Entity(
    tableName = "hours_table",
//    foreignKeys = [
//        ForeignKey(
//            entity = DbForecastday::class,
//            parentColumns = ["weatherId", "date"],
//            childColumns = ["weatherId", "forecastDate"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE
//        )
//    ],
    primaryKeys = [
        "weatherId",
        "forecastDate",
        "time"
        ]
)


data class DbHour(
    //@PrimaryKey val forecastId: String,
    val weatherId: String,
    val forecastDate: String,
    val chance_of_rain: Int,
    val chance_of_snow: Int,
    val cloud: Int,
    @Embedded val condition: DbCondition,
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

data class DbDay(
    val avghumidity: Double,
    val avgtemp_c: Double,
    val avgtemp_f: Double,
    val avgvis_km: Double,
    val avgvis_miles: Double,
    @Embedded val condition: DbCondition,
    val daily_chance_of_rain: Int,
    val daily_chance_of_snow: Int,
    val daily_will_it_rain: Int,
    val daily_will_it_snow: Int,
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val maxwind_kph: Double,
    val maxwind_mph: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val totalprecip_in: Double,
    val totalprecip_mm: Double,
    val totalsnow_cm: Double,
    val uv: Double
)

data class DbAstro(
    val is_moon_up: Int,
    val is_sun_up: Int,
    val moon_illumination: String,
    val moon_phase: String,
    val moonrise: String,
    val moonset: String,
    val sunrise: String,
    val sunset: String
)