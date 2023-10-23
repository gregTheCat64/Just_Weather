package ru.javacat.justweather.data.mapper

import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbAstro
import ru.javacat.justweather.data.db.entities.DbDay
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbWeatherWithForecastsAndAlerts
import ru.javacat.justweather.domain.models.Alert
import ru.javacat.justweather.domain.models.Astro
import ru.javacat.justweather.domain.models.Day
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.util.toLocalDate
import ru.javacat.justweather.util.toLocalDateTime

fun DbWeatherWithForecastsAndAlerts.toModel(): Weather {
    return Weather(
        weather.id,
        alerts.map { it.toModel() },
        weather.current,
        forecasts.map { it.toModel() },
        weather.location,
        //hours.map { it.toModel() }
    )
}

fun DbAlert.toModel(): Alert {
    return Alert(
        areas, category, certainty, desc, effective, event, expires, headline, instruction, msgtype, note, severity, urgency
    )
}
fun DbForecastday.toModel(): Forecastday {
    return Forecastday(
        astro.toModel(), date.toLocalDate(), date_epoch, day.toModel(),
    )
}

fun DbAstro.toModel(): Astro = Astro(
    is_moon_up, is_sun_up, moon_illumination, moon_phase, moonrise, moonset, sunrise, sunset
)


fun DbDay.toModel(): Day = Day(
    avghumidity, avgtemp_c, avgtemp_f, avgvis_km, avgvis_miles, condition, daily_chance_of_rain, daily_chance_of_snow, daily_will_it_rain, daily_will_it_snow, maxtemp_c, maxtemp_f, maxwind_kph, maxwind_mph, mintemp_c, mintemp_f, totalprecip_in, totalprecip_mm, totalsnow_cm, uv
)
//fun DbForecastWithHours.toModel() :ForecastdayWithHours {
//    return ForecastdayWithHours(
//        forecastday.astro, forecastday.date.toLocalDate(), forecastday.date_epoch,
//        forecastday.day, hours.map { it.toModel() }
//    )
//}

fun DbHour.toModel(): Hour = Hour(
    forecastDate.toLocalDate(), chance_of_rain, chance_of_snow, cloud, condition, dewpoint_c, dewpoint_f, feelslike_c,
    feelslike_f, gust_kph, gust_mph, heatindex_c, heatindex_f, humidity, is_day, precip_in, precip_mm, precip_in,
    pressure_mb, temp_c, temp_f, time.toLocalDateTime(), time_epoch, uv, vis_km, vis_miles, will_it_rain, will_it_snow, wind_degree, wind_dir, wind_kph, wind_mph, windchill_c, windchill_f
)



