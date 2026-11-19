# 🐦 Twitter Clone API

Spring Boot + PostgreSQL ile geliştirilmiş Twitter benzeri bir REST API ve ona bağlanan basit bir React arayüzü.
Workintech Fullstack Sprint 20 bitirme projesi.

## Teknolojiler

| Katman | Teknoloji |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security |
| Kimlik doğrulama | JWT (HS256, Spring Security OAuth2 Resource Server) |
| Veritabanı | PostgreSQL |
| Test | JUnit 5, Mockito |
| Frontend | React 19, Vite |

## Kurulum ve Çalıştırma

### 1. Veritabanı
PostgreSQL'de boş bir veritabanı oluşturun (tablolar uygulama açılırken otomatik oluşur):
```sql
CREATE DATABASE twitter;
```

### 2. Ortam değişkenleri
Proje kök dizinindeki `.env.example` dosyasını `.env` adıyla kopyalayıp değerleri doldurun:
```
DB_PASSWORD=postgres_sifreniz
JWT_SECRET=en-az-32-karakterlik-rastgele-bir-deger
```
> `JWT_SECRET` tanımlı değilse uygulama bilerek başlamaz (zayıf varsayılan anahtar kullanılmaz).

### 3. Backend (port 3000)
```bash
mvn spring-boot:run
```
veya IDE'den `TwitterApplication` sınıfını çalıştırın.

### 4. Frontend (port 3200)
```bash
cd frontend
npm install
npm run dev
```
Tarayıcıda http://localhost:3200 adresini açın.

### 5. Testler
```bash
mvn test
```

## Endpoint'ler

`/register` ve `/login` dışındaki tüm endpoint'ler `Authorization: Bearer <token>` header'ı ister.
Kullanıcı kimliği her zaman token'dan alınır; istek gövdesinde `userId` gönderilmez.

| Metod | Adres | Açıklama | Yetki |
|---|---|---|---|
| POST | `/register` | Kayıt ol | Herkes |
| POST | `/login` | Giriş yap, JWT al | Herkes |
| GET | `/me` | Giriş yapmış kullanıcının bilgileri | Giriş yapmış |
| POST | `/tweet` | Tweet oluştur | Giriş yapmış |
| GET | `/tweet/findByUserId?userId=1` | Kullanıcının tweetleri | Giriş yapmış |
| GET | `/tweet/findById?id=1` | Tek tweet | Giriş yapmış |
| PUT | `/tweet/{id}` | Tweet güncelle | Sadece tweet sahibi |
| DELETE | `/tweet/{id}` | Tweet sil | Sadece tweet sahibi |
| POST | `/comment` | Yorum yaz `{tweetId, content}` | Giriş yapmış |
| PUT | `/comment/{id}` | Yorum güncelle | Sadece yorum sahibi |
| DELETE | `/comment/{id}` | Yorum sil | Yorum sahibi **veya** tweet sahibi |
| POST | `/like` | Beğen `{tweetId}` | Giriş yapmış |
| POST | `/dislike` | Beğeniyi geri al `{tweetId}` | Giriş yapmış |
| POST | `/retweet` | Retweet `{tweetId}` | Giriş yapmış |
| DELETE | `/retweet/{id}` | Retweet'i geri al | Sadece retweet sahibi |

### Örnek akış
```bash
# Kayıt
curl -X POST http://localhost:3000/register -H "Content-Type: application/json" \
  -d '{"username":"brusk","email":"brusk@test.com","password":"123456"}'

# Giriş -> cevaptaki "token" değerini kopyalayın
curl -X POST http://localhost:3000/login -H "Content-Type: application/json" \
  -d '{"username":"brusk","password":"123456"}'

# Tweet at
curl -X POST http://localhost:3000/tweet -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" -d '{"content":"Merhaba!"}'
```

### Hata formatı
Tüm hatalar aynı yapıda döner:
```json
{
  "status": 400,
  "message": "Gecersiz istek",
  "path": "/tweet",
  "timestamp": "2026-11-18T20:32:17.12",
  "fieldErrors": { "content": "Tweet bos olamaz" }
}
```

## Veritabanı Tasarımı

```
users ──< tweets ──< comments
  │         │
  │         ├──< likes     UNIQUE(user_id, tweet_id)
  │         └──< retweets  UNIQUE(user_id, tweet_id)
  └──────────────(her tablonun user_id'si)
```

- **Like ve Retweet ayrı tablolar:** Aynı kullanıcı aynı tweet'i iki kez beğenemez / retweet edemez; veritabanı `UNIQUE` kısıtıyla garanti eder.
- **Tweet silinince** yorumları, beğenileri ve retweet'leri veritabanı tarafından silinir (`ON DELETE CASCADE`).
- İlişkiler tek yönlüdür (çocuk → ebeveyn); bu sayede JSON'da sonsuz döngü riski yoktur.

## Proje Yapısı

```
src/main/java/com/workintech/twitter
├── controller   HTTP katmanı (istek/cevap, @Valid)
├── service      İş kuralları ve yetki kontrolleri
├── repository   Spring Data JPA arayüzleri
├── entity       JPA entity'leri ve validation kuralları
├── dto          İstek/cevap modelleri (entity'ler dışarı açılmaz)
├── security     Spring Security, JWT, CORS yapılandırması
├── exception    Global exception handling
└── config       Diğer bean'ler (PasswordEncoder)
frontend/        React + Vite arayüzü
```

## CORS

React uygulaması `localhost:3200`, API `localhost:3000` üzerinde çalıştığı için tarayıcı istekleri farklı origin sayar ve CORS politikası gereği engeller.
Çözüm `SecurityConfig` içinde yapılmıştır: yalnızca `http://localhost:3200` origin'ine izin verilir.
CORS ayarı Spring MVC yerine Security filtre zincirinde tanımlanmıştır; aksi halde token taşımayan preflight (`OPTIONS`) istekleri Security filtresinde 401 alırdı.
