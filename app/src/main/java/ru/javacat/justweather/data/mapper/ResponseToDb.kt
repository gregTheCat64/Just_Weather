package ru.javacat.justweather.data.mapper

import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbAstro
import ru.javacat.justweather.data.db.entities.DbDay
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbWeather
import ru.javacat.justweather.data.network.response_models.AlertResponse
import ru.javacat.justweather.data.network.response_models.AstroResponse
import ru.javacat.justweather.data.network.response_models.ConditionResponse
import ru.javacat.justweather.data.network.response_models.CurrentResponse
import ru.javacat.justweather.data.network.response_models.DayResponse
import ru.javacat.justweather.data.network.response_models.ForecastdayResponse
import ru.javacat.justweather.data.network.response_models.HourResponse
import ru.javacat.justweather.data.network.response_models.LocationResponse
import ru.javacat.justweather.data.network.response_models.WeatherResponse
import ru.javacat.justweather.domain.models.Alert
import ru.javacat.justweather.domain.models.Astro
import ru.javacat.justweather.domain.models.Condition
import ru.javacat.justweather.domain.models.Current
import ru.javacat.justweather.domain.models.Day
import ru.javacat.justweather.domain.models.Location

fun WeatherResponse.toDbWeather(weatherId: String): DbWeather {

    return DbWeather(
        weatherId,
        false,
        current.toDb(),
        location.toDb()
    )
}

fun CurrentResponse.toDb(): Current =
    Current(
        cloud,
        condition.toDb(),
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

fun ConditionResponse.toDb(): Condition =
    Condition(code, icon, text)

fun LocationResponse.toDb(): Location =
    Location(
        country, lat, localtime, localtime_epoch, lon, name, region, tz_id
    )


fun AlertResponse.toDbAlert(weatherId: String): DbAlert {
    return DbAlert(
        weatherId, areas, category, certainty, desc, effective, event, expires, headline, instruction, msgtype, note, severity, urgency
    )
}

fun ForecastdayResponse.toDbForecastday(weatherId: String): DbForecastday {
    return DbForecastday(
        weatherId, date,  astro.toDb(),  date_epoch, day.toDb()
    )
}

fun AstroResponse.toDb(): DbAstro = DbAstro(
    is_moon_up, is_sun_up, moon_illumination, moon_phase, moonrise, moonset, sunrise, sunset
)

fun DayResponse.toDb(): DbDay = DbDay(
    avghumidity, avgtemp_c, avgtemp_f, avgvis_km, avgvis_miles, condition.toDb(), daily_chance_of_rain, daily_chance_of_snow, daily_will_it_rain, daily_will_it_snow, maxtemp_c, maxtemp_f, maxwind_kph, maxwind_mph, mintemp_c, mintemp_f, totalprecip_in, totalprecip_mm, totalsnow_cm, uv
)

fun HourResponse.toDbHour(weatherId: String, forecastDate: String): DbHour = DbHour(
    weatherId, forecastDate, chance_of_rain, chance_of_snow, cloud, condition.toDb(), dewpoint_c, dewpoint_f,
    feelslike_c, feelslike_f, gust_kph, gust_mph, heatindex_c, heatindex_f, humidity,is_day,precip_in,
    precip_mm, pressure_in, pressure_mb, temp_c, temp_f, time, time_epoch, uv, vis_km, vis_miles, will_it_rain, will_it_snow, wind_degree, wind_dir, wind_kph, wind_mph, windchill_c, windchill_f
)

