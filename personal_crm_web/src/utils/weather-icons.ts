const weatherIconModules = import.meta.glob('../../node_modules/qweather-icons/icons/*.svg', {
  eager: true,
  import: 'default',
}) as Record<string, string>

const weatherIconMap = Object.fromEntries(
  Object.entries(weatherIconModules).map(([path, url]) => {
    const filename = path.split('/').pop() ?? ''
    return [filename, url]
  }),
)

const FALLBACK_WEATHER_ICON = weatherIconMap['999.svg'] ?? ''

export function getWeatherIconUrl(iconCode: string): string {
  const normalizedIconCode = String(iconCode ?? '').trim()
  return weatherIconMap[`${normalizedIconCode}.svg`] ?? FALLBACK_WEATHER_ICON
}
