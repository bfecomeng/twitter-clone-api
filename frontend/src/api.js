// Backend'in adresi. React 3200'de, API 3000'de calisiyor:
// port farkli = origin farkli -> tarayici CORS kurallarini uygular.
const API_URL = 'http://localhost:3000'

// Tum istekler buradan gecer: JSON govde, opsiyonel token, tek tip hata.
async function request(path, { method = 'GET', body, token } = {}) {
  const headers = {}
  if (body) headers['Content-Type'] = 'application/json'
  if (token) headers['Authorization'] = `Bearer ${token}`

  let response
  try {
    response = await fetch(`${API_URL}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    })
  } catch {
    // CORS engeli de, sunucunun kapali olmasi da burada "Failed to fetch" olarak gorunur.
    // Tarayici guvenlik geregi ikisini JavaScript'e ayirt ettirmez; detay Console'dadir.
    throw new Error('Sunucuya ulasilamadi. API calisiyor mu? CORS hatasi olabilir (F12 > Console).')
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    // Backend'in GlobalExceptionHandler'i her hatada {status, message, ...} dondurur.
    throw new Error(data?.message ?? `HTTP ${response.status}`)
  }
  return data
}

export const login = (username, password) =>
  request('/login', { method: 'POST', body: { username, password } })

export const getTweetsByUserId = (userId, token) =>
  request(`/tweet/findByUserId?userId=${userId}`, { token })

export const createTweet = (content, token) =>
  request('/tweet', { method: 'POST', body: { content }, token })
