package ru.javacat.justweather.data.mapper

import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbWeather
import ru.javacat.justweather.data.network.response_models.ForecastdayResponse
import ru.javacat.justweather.data.network.response_models.HourResponse
import ru.javacat.justweather.data.network.response_models.WeatherResponse
import ru.javacat.justweather.data.toBase64
import ru.javacat.justweather.domain.models.Alert
import ru.javacat.justweather.domain.models.Condition
import ru.javacat.justweather.domain.models.Current
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.Location
import ru.javacat.justweather.util.asLocalDate
import ru.javacat.justweather.util.asTime

fun WeatherResponse.toDbWeather(): DbWeather {
    val place = this.location.name
    val region = this.location.region
    val placeId = (place + region).toBase64()

    return DbWeather(
        placeId,
        current.toModel(),
        location.toModel()
    )
}

fun Current.toModel(): Current =
    Current(
        cloud,
        condition.toModel(),
        feelslike_c,
        feelslike_f,
        gust_kph,
        gust_mph,
        humidity,
        is_day,
        last_updated,
        last_updated_epoch,
        precip_in,
        precip_mm,
        pressure_in,
        pressure_mb,
        temp_c,
        temp_f,
        uv,
        vis_km,
        vis_miles,
        wind_degree,
        wind_dir,
        wind_kph,
        wind_mph
    )

fun Condition.toModel(): Condition =
    Condition(code, icon, text)

fun Location.toModel(): Location =
    Location(
        country, lat, localtime, localtime_epoch, lon, name, region, tz_id
    )


fun Alert.toDbAlert(weatherId: String): DbAlert {
    return DbAlert(
        weatherId, areas, category, certainty, desc, effective, event, expires, headline, instruction, msgtype, note, severity, urgency
    )
}

fun ForecastdayResponse.toDbForecastday(weatherId: String, forecastId: String): DbForecastday {
    return DbForecastday(
        weatherId, forecastId,  astro, date, date_epoch, day
    )
}

fun HourResponse.toDbHour(forecastId: String): DbHour = DbHour(
    forecastId,forecastDate, chance_of_rain, chance_of_snow, cloud, condition, dewpoint_c, dewpoint_f,
    feelslike_c, feelslike_f, gust_kph, gust_mph, heatindex_c, heatindex_f, humidity,is_day,precip_in,
    precip_mm, pressure_in, pressure_mb, temp_c, temp_f, time, time_epoch, uv, vis_km, vis_miles, will_it_rain, will_it_snow, wind_degree, wind_dir, wind_kph, wind_mph, windchill_c, windchill_f
)

