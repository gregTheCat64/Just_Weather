package ru.javacat.justweather.data.mapper

import ru.javacat.justweather.common.util.toLocalDate
import ru.javacat.justweather.common.util.toLocalDateTime
import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbAstro
import ru.javacat.justweather.data.db.entities.DbCondition
import ru.javacat.justweather.data.db.entities.DbCurrent
import ru.javacat.justweather.data.db.entities.DbDay
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbLocation
import ru.javacat.justweather.data.db.entities.DbWeatherWithForecastsAndAlerts

fun DbWeatherWithForecastsAndAlerts.toModel(): ru.javacat.justweather.domain.models.Weather {
    return ru.javacat.justweather.domain.models.Weather(
        weather.id,
        alerts.map { it.toModel() },
        weather.current.toModel(),
        forecasts.map { it.toModel() },
        weather.location.toModel(),
        weather.isCurrent,
        weather.isLocated
    )
}

fun DbLocation.toModel(): ru.javacat.justweather.domain.models.Location =
    ru.javacat.justweather.domain.models.Location(
        country,
        lat,
        localtime,
        localtime_epoch,
        lon,
        name,
        region,
        tz_id
    )

fun DbCurrent.toModel(): ru.javacat.justweather.domain.models.Current =
    ru.javacat.justweather.domain.models.Current(
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

fun DbAlert.toModel(): ru.javacat.justweather.domain.models.Alert {
    return ru.javacat.justweather.domain.models.Alert(
        areas,
        category,
        certainty,
        desc,
        effective,
        event,
        expires,
        headline,
        instruction,
        msgtype,
        note,
        severity,
        urgency
    )
}
fun DbForecastday.toModel(): ru.javacat.justweather.domain.models.Forecastday {
    return ru.javacat.justweather.domain.models.Forecastday(
        weatherId, astro.toModel(), date.toLocalDate(), date_epoch, day.toModel(),
    )
}

fun DbAstro.toModel(): ru.javacat.justweather.domain.models.Astro =
    ru.javacat.justweather.domain.models.Astro(
        is_moon_up, is_sun_up, moon_illumination, moon_phase, moonrise, moonset, sunrise, sunset
    )


fun DbDay.toModel(): ru.javacat.justweather.domain.models.Day =
    ru.javacat.justweather.domain.models.Day(
        avghumidity,
        avgtemp_c,
        avgtemp_f,
        avgvis_km,
        avgvis_miles,
        condition.toModel(),
        daily_chance_of_rain,
        daily_chance_of_snow,
        daily_will_it_rain,
        daily_will_it_snow,
        maxtemp_c,
        maxtemp_f,
        maxwind_kph,
        maxwind_mph,
        mintemp_c,
        mintemp_f,
        totalprecip_in,
        totalprecip_mm,
        totalsnow_cm,
        uv
    )
//fun DbForecastWithHours.toModel() :ForecastdayWithHours {
//    return ForecastdayWithHours(
//        forecastday.astro, forecastday.date.toLocalDate(), forecastday.date_epoch,
//        forecastday.day, hours.map { it.toModel() }
//    )
//}

fun DbHour.toModel(): ru.javacat.justweather.domain.models.Hour =
    ru.javacat.justweather.domain.models.Hour(
        forecastDate.toLocalDate(),
        chance_of_rain,
        chance_of_snow,
        cloud,
        condition.toModel(),
        dewpoint_c,
        dewpoint_f,
        feelslike_c,
        feelslike_f,
        gust_kph,
        gust_mph,
        heatindex_c,
        heatindex_f,
        humidity,
        is_day,
        precip_in,
        precip_mm,
        precip_in,
        pressure_mb,
        temp_c,
        temp_f,
        time.toLocalDateTime(),
        time_epoch,
        uv,
        vis_km,
        vis_miles,
        will_it_rain,
        will_it_snow,
        wind_degree,
        wind_dir,
        wind_kph,
        wind_mph,
        windchill_c,
        windchill_f
    )

fun DbCondition.toModel(): ru.javacat.justweather.domain.models.Condition =
    ru.javacat.justweather.domain.models.Condition(code, icon, text)



