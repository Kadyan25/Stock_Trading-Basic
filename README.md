# Stock Trading Simulator

A demo **stock trading simulator** built with Java Spring Boot, MySQL, and a vanilla HTML/CSS/JavaScript frontend.  
It simulates moving stock prices, integrates with the Alpha Vantage API for real prices, and lets a user place BUY/SELL orders with portfolio and PnL tracking.[web:63][web:90]

---

## Architecture Overview

- **Backend**
  - Java 17, Spring Boot (REST APIs)
  - Spring Data JPA + Hibernate
  - MySQL database (`stock_trading`)
  - Alpha Vantage free API for live prices (IBM, MSFT, AAPL)
  - Scheduled jobs:
    - Simulated price ticks that adjust `current_price`
    - Periodic Alpha Vantage refresh that updates `lastRealPrice` and re‑anchors simulated prices[web:63][web:72]

- **Frontend**
  - Static files in `src/main/resources/static`
  - `index.html` + `css/style.css` + `js/app.js`
  - Layout:
    - Header with current user id
    - Sidebar for Register/Login
    - Main area with cards: Stocks, Trade, Portfolio, Portfolio Holdings, Trade History, Performance

---

## Features Implemented

### Market data

- `stocks` table:

id (PK), symbol, name, current_price, last_real_price

text

- Endpoints:
- `GET /api/stocks` – list all stocks for the table.
- `POST /api/market/alpha-update` – refresh prices from Alpha Vantage and update DB.
- Price behaviour:
- A scheduler simulates small random moves on `current_price`.
- Another scheduler periodically calls Alpha Vantage to update `lastRealPrice` and nudge simulated prices.[web:63][web:72]

### Trading (BUY / SELL)

- Single demo user at a time (identified by `userId` in requests).
- Endpoints:
- `POST /api/trades/buy`
- `POST /api/trades/sell`
- Logic:
- Look up `Stock` by `symbol`.
- Check and update `PortfolioAccount` cash balance.
- Update or create `Holding` (quantity and average price) for the user and stock.
- Save a `Trade` row with type BUY/SELL, quantity, price, timestamp.[web:67][web:68]

### Portfolio with holdings + unrealised PnL

- Backend:
- `PortfolioAccount` tracks cash and total value.
- `Holding` stores per‑symbol quantity and avg buy price.
- `PortfolioSummaryDto` returns:
  - `cashBalance`, `holdingsValue`, `totalValue`, `unrealizedProfitLoss`.
- `HoldingDto` returns:
  - `symbol`, `name`, `quantity`, `avgBuyPrice`, `currentPrice`, `value`, `pnl`.
- Endpoints:
- `GET /api/portfolio?userId=...`
- `GET /api/holdings?userId=...`
- Frontend:
- **Portfolio** card shows:
  - Cash Balance, Holdings Value, Total Value, Unrealized P&L.
- **Portfolio Holdings** card shows per‑symbol rows with unrealised PnL, based on  
  \((\text{current price} - \text{avg buy price}) \times \text{quantity}\).[web:3][web:8]

### Trade history & performance tracking

- Backend:
- `Trade` entity with `user`, `stock`, `quantity`, `price`, `type`, `timestamp`.
- `TradeDto` used by controller.
- `TradeRepository.findByUserOrderByTimestampAsc(user)`.
- Endpoint:
- `GET /api/trades?userId=...` – all trades for the user.
- Frontend:
- **Trade History** card:
  - Table columns: ID, Time, Type, Symbol, Name, Qty, Price.
  - Latest trade displayed at the top (array reversed in JS).
- Simple stats (BUY/SELL counts) can be added easily if needed.

### Performance chart

- Endpoint:
- `GET /api/performance?userId=...` – list of snapshots `{timestamp, totalValue}`.
- Frontend:
- Uses Chart.js line chart to plot portfolio value over time.

### Authentication

- Spring Security–based auth module:
- `POST /api/auth/register` – register user, returns `userId` and token.
- `POST /api/auth/login` – login, returns `userId` and token.
- Frontend:
- Register and Login blocks in sidebar.
- `userId` and token stored in `localStorage` and shown in header.

---

## Database & Data Initialization

### JPA indexes

- `Stock`:

@Entity
@Table(
name = "stocks",
indexes = @Index(name = "idx_stocks_symbol", columnList = "symbol")
)

text

- `Trade`:

@Entity
@Table(
name = "trades",
indexes = @Index(name = "idx_trades_user_time", columnList = "user_id, timestamp")
)

text

Hibernate (`ddl-auto: update`) creates these indexes automatically on startup, improving lookup speed for common queries (by symbol and by user + time).[web:19][web:13]

### Seed data with `data.sql`

Initial demo stocks are inserted automatically via `data.sql`:

`src/main/resources/data.sql`:

INSERT INTO stocks (symbol, name, current_price, last_real_price) VALUES
('IBM', 'IBM Corp', 200.00, 200.00),
('MSFT', 'Microsoft Corp', 400.00, 400.00),
('AAPL', 'Apple Inc', 180.00, 180.00);

text

Spring Boot runs `data.sql` on startup, so anyone who clones the repo gets these three stocks without manual SQL.[web:82][web:83]

---

## Configuration & Running

### application.yaml (snippet)

alpha:
vantage:
api-key: YOUR_ALPHA_VANTAGE_KEY

server:
port: 8080

spring:
application:
name: trading
datasource:
url: jdbc:mysql://localhost:3306/stock_trading?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
username: root
password: password
driver-class-name: com.mysql.cj.jdbc.Driver
jpa:
hibernate:
ddl-auto: update
show-sql: true
properties:
hibernate:
dialect: org.hibernate.dialect.MySQL8Dialect

text

### Steps to run

1. Install JDK 17+, Maven, and MySQL 8+.
2. Ensure MySQL has a user/password matching `application.yaml`.
3. In project root:

mvn spring-boot:run

text

4. Open `http://localhost:8080` in the browser.
5. Register or login, then:
- **Stocks** → Load Stocks / Fetch Global Live Prices.
- **Trade** → Place BUY/SELL orders.
- **Portfolio / Portfolio Holdings** → View holdings and unrealised PnL.
- **Trade History** → See all past trades.
- **Performance** → Plot portfolio value over time.

---

## Limitations & Future Work

- Single demo user at a time; no multi‑tenant dashboard yet.
- Only a few global symbols (IBM, MSFT, AAPL) due to Alpha Vantage free‑tier rate limits.
- No advanced order types (only simple market‑style BUY/SELL).
- No algorithmic or back‑testing engine; focus is on UI + basic trading experience.
- Logs show all SQL when `show-sql: true`; use a separate prod profile to quiet logs if needed.[web:80][web:87]