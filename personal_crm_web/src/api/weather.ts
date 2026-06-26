import request from './request'

export interface DailyForecast {
  date: string
  textDay: string
  textNight: string
  tempMin: string
  tempMax: string
  iconDay: string
}

export interface WeatherData {
  cityName: string
  currentTemp: string
  currentText: string
  currentIcon: string
  dailyForecast: DailyForecast[]
}

export interface WeatherQueryParams {
  address?: string
  latitude?: number
  longitude?: number
}

const resolveWeatherParams = (query?: string | WeatherQueryParams): WeatherQueryParams => {
  if (typeof query === 'string') {
    return { address: query }
  }
  return query ?? {}
}

/**
 * 获取天气信息
 * @param query 可选。支持联系人模糊地址，或浏览器 GEO 提供的经纬度。
 */
export function getWeather(query?: string | WeatherQueryParams): Promise<WeatherData> {
  return request.get('/weather', { params: resolveWeatherParams(query) })
}
