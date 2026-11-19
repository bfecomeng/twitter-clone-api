import { useState } from 'react'
import { login, getTweetsByUserId, createTweet } from './api'

function LoginForm({ onLogin }) {
  const [username, setUsername] = useState('brusk')
  const [password, setPassword] = useState('123456')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const data = await login(username, password)
      onLogin(data) // {token, user, ...}
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h2>Giris yap</h2>
      <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Kullanici adi" />
      <input value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Sifre" type="password" />
      <button disabled={loading}>{loading ? 'Giris yapiliyor...' : 'Giris yap'}</button>
      {error && <p className="error">{error}</p>}
    </form>
  )
}

function TweetList({ tweets }) {
  if (tweets.length === 0) return <p className="muted">Henuz tweet yok.</p>
  return (
    <ul className="tweets">
      {tweets.map((t) => (
        <li key={t.id} className="tweet">
          <strong>@{t.username}</strong>
          <p>{t.content}</p>
          <small className="muted">{new Date(t.createdAt).toLocaleString('tr-TR')}</small>
        </li>
      ))}
    </ul>
  )
}

export default function App() {
  // Token sadece bellekte (state) tutuluyor: sayfa yenilenince cikis yapilir.
  // localStorage'a koymak kolay ama XSS ile calinabilir; bu proje icin bellek yeterli.
  const [session, setSession] = useState(null) // {token, user}
  const [userId, setUserId] = useState('')
  const [tweets, setTweets] = useState([])
  const [newTweet, setNewTweet] = useState('')
  const [error, setError] = useState(null)

  async function loadTweets(id, token = session.token) {
    setError(null)
    try {
      setTweets(await getTweetsByUserId(id, token))
    } catch (err) {
      setTweets([])
      setError(err.message)
    }
  }

  async function handleLogin(data) {
    setSession(data)
    setUserId(String(data.user.id))
    await loadTweets(data.user.id, data.token)
  }

  async function handleCreate(e) {
    e.preventDefault()
    if (!newTweet.trim()) return
    setError(null)
    try {
      await createTweet(newTweet, session.token)
      setNewTweet('')
      await loadTweets(session.user.id)
      setUserId(String(session.user.id))
    } catch (err) {
      setError(err.message)
    }
  }

  if (!session) {
    return (
      <main>
        <h1>🐦 Twitter Clone</h1>
        <LoginForm onLogin={handleLogin} />
      </main>
    )
  }

  return (
    <main>
      <header className="header">
        <h1>🐦 Twitter Clone</h1>
        <span>
          @{session.user.username}{' '}
          <button className="link" onClick={() => { setSession(null); setTweets([]) }}>Cikis</button>
        </span>
      </header>

      <form className="card" onSubmit={handleCreate}>
        <textarea value={newTweet} onChange={(e) => setNewTweet(e.target.value)}
                  placeholder="Neler oluyor?" maxLength={280} rows={3} />
        <div className="row">
          <small className="muted">{newTweet.length}/280</small>
          <button>Tweetle</button>
        </div>
      </form>

      <form className="card row" onSubmit={(e) => { e.preventDefault(); loadTweets(userId) }}>
        <label>Kullanici ID:</label>
        <input value={userId} onChange={(e) => setUserId(e.target.value)} style={{ width: 80 }} />
        <button>Tweetleri getir</button>
      </form>

      {error && <p className="error">{error}</p>}
      <TweetList tweets={tweets} />
    </main>
  )
}
