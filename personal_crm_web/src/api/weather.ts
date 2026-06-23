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

/**
 * 获取天气信息
 * @param address 可选，联系人的模糊地址。如果不传，后端将根据请求的 IP 地址自动识别其常驻城市。
 */
export function getWeather(address?: string): Promise<WeatherData> {
  return request.get('/weather', { params: { address } })
}
