package ru.javacat.justweather.data.mapper

import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbForecastWithHours
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbWeatherWithForecastsAndAlerts
import ru.javacat.justweather.domain.models.Alert
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.ForecastdayWithHours
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.util.toLocalDate
import ru.javacat.justweather.util.toLocalDateTime

fun DbWeatherWithForecastsAndAlerts.toModel(): Weather {
    return Weather(
        alerts.map { it.toModel() },
        this.weather.current,
        forecasts.map { it.toModel() },
        this.weather.location
    )
}

fun DbAlert.toModel(): Alert {
    return Alert(
        areas, category, certainty, desc, effective, event, expires, headline, instruction, msgtype, note, severity, urgency
    )
}
fun DbForecastday.toModel(): Forecastday {
    return Forecastday(
        astro, date.toLocalDate(), date_epoch, day
    )
}

fun DbForecastWithHours.toModel() :ForecastdayWithHours {
    return ForecastdayWithHours(
        this.forecastday.astro, this.forecastday.date.toLocalDate(), this.forecastday.date_epoch,
        this.forecastday.day, this.hours.map { it.toModel() }
    )
}

fun DbHour.toModel(): Hour = Hour(
    forecastDate.toLocalDate(), chance_of_rain, chance_of_snow, cloud, condition, dewpoint_c, dewpoint_f, feelslike_c,
    feelslike_f, gust_kph, gust_mph, heatindex_c, heatindex_f, humidity, is_day, precip_in, precip_mm, precip_in,
    pressure_mb, temp_c, temp_f, time.toLocalDateTime(), time_epoch, uv, vis_km, vis_miles, will_it_rain, will_it_snow, wind_degree, wind_dir, wind_kph, wind_mph, windchill_c, windchill_f
)



