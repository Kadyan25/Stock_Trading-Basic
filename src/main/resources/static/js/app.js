const API_BASE = 'http://localhost:8080/api';

let currentUserId = null;
let authToken = null;

function setCurrentUser(id, token) {
    currentUserId = id;
    authToken = token;
    document.getElementById('currentUserId').innerText = currentUserId || '-';
    // Store in localStorage so reload keeps you logged in
    if (id && token) {
        localStorage.setItem('userId', id);
        localStorage.setItem('token', token);
    } else {
        localStorage.removeItem('userId');
        localStorage.removeItem('token');
    }
}

// On page load, restore from localStorage
window.addEventListener('load', () => {
    const storedId = localStorage.getItem('userId');
    const storedToken = localStorage.getItem('token');
    if (storedId && storedToken) {
        setCurrentUser(storedId, storedToken);
    }
});

async function register() {
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;

    if (!name || !email || !password) {
        alert('Fill all register fields');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Register failed');
        }
        const data = await res.json();
        setCurrentUser(data.userId, data.token);
        alert('Registered & logged in');
    } catch (e) {
        alert(e.message);
    }
}

async function login() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    if (!email || !password) {
        alert('Fill all login fields');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Login failed');
        }
        const data = await res.json();
        setCurrentUser(data.userId, data.token);
        alert('Logged in');
    } catch (e) {
        alert(e.message);
    }
}


async function createUser() {
    const name = document.getElementById('name').value || 'Test User';
    const email = document.getElementById('email').value || `test${Date.now()}@example.com`;

    const url = `${API_BASE}/test/create-user?name=${encodeURIComponent(name)}&email=${encodeURIComponent(email)}`;

    try {
        const res = await fetch(url, { method: 'POST' });
        if (!res.ok) {
            throw new Error('Failed to create user');
        }
        const user = await res.json();
        currentUserId = user.id;
        document.getElementById('currentUserId').innerText = currentUserId;
    } catch (e) {
        alert(e.message);
    }
}

async function loadStocks() {
    try {
        const res = await fetch(`${API_BASE}/stocks`);
        const stocks = await res.json();
        const tbody = document.getElementById('stocksTableBody');
        tbody.innerHTML = '';
        stocks.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${s.symbol}</td>
                <td>${s.name}</td>
                <td>${s.currentPrice != null ? s.currentPrice.toFixed(2) : '-'}</td>
                <td>${s.lastRealPrice != null ? s.lastRealPrice.toFixed(2) : '-'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        alert('Failed to load stocks');
    }
}


async function buy() {
    if (!currentUserId) {
        alert('Create a user first');
        throw new Error('No user');
    }
    const symbol = document.getElementById('tradeSymbol').value;
    const quantity = parseInt(document.getElementById('tradeQuantity').value, 10);

    const res = await fetch(`${API_BASE}/trades/buy`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: currentUserId, symbol, quantity })
    });
    const text = await res.text();
    document.getElementById('tradeStatus').innerText = text;
    // After trade, refresh portfolio + holdings + trades
    await loadPortfolio();
    await loadHoldings();
    await loadTrades();
}

async function sell() {
    if (!currentUserId) {
        alert('Create a user first');
        throw new Error('No user');
    }
    const symbol = document.getElementById('tradeSymbol').value;
    const quantity = parseInt(document.getElementById('tradeQuantity').value, 10);

    const res = await fetch(`${API_BASE}/trades/sell`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: currentUserId, symbol, quantity })
    });
    const text = await res.text();
    document.getElementById('tradeStatus').innerText = text;
    await loadPortfolio();
    await loadHoldings();
    await loadTrades();
}

async function loadPortfolio() {
    if (!currentUserId) {
        alert('Login or register first');
        return;
    }
    try {
        const res = await fetch(`${API_BASE}/portfolio?userId=${currentUserId}`);
        if (!res.ok) throw new Error('Failed to load portfolio');
        const data = await res.json();

        document.getElementById('pfCash').innerText = data.cashBalance.toFixed(2);
        document.getElementById('pfHoldings').innerText = data.holdingsValue.toFixed(2);
        document.getElementById('pfTotal').innerText = data.totalValue.toFixed(2);
        document.getElementById('pfPnl').innerText = data.unrealizedProfitLoss.toFixed(2);
    } catch (e) {
        alert(e.message);
    }
}


let performanceChart = null;

async function loadPerformance() {
    if (!currentUserId) {
        alert('Create a user first');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/performance?userId=${currentUserId}`);
        if (!res.ok) throw new Error('Failed to load performance');

        const data = await res.json(); // [{timestamp, totalValue}, ...]

        const labels = data.map(p => new Date(p.timestamp).toLocaleTimeString());
        const values = data.map(p => p.totalValue);

        const ctx = document.getElementById('performanceChart').getContext('2d');

        if (performanceChart) {
            performanceChart.destroy();
        }

        performanceChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Portfolio Value',
                    data: values,
                    borderColor: 'blue',
                    fill: false,
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: false
                    }
                }
            }
        });
    } catch (e) {
        alert(e.message);
    }
}
async function loadHoldings() {
    if (!currentUserId) {
        alert('Login or register first');
        return;
    }
    try {
        const res = await fetch(`${API_BASE}/holdings?userId=${currentUserId}`);
        if (!res.ok) throw new Error('Failed to load holdings');
        const data = await res.json(); // array of HoldingDto

        const tbody = document.getElementById('holdingsTableBody');
        tbody.innerHTML = '';

        data.forEach(h => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${h.symbol}</td>
                <td>${h.name}</td>
                <td>${h.quantity}</td>
                <td>${h.avgBuyPrice.toFixed(2)}</td>
                <td>${h.currentPrice.toFixed(2)}</td>
                <td>${h.value.toFixed(2)}</td>
                <td>${h.pnl.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        alert(e.message);
    }
}

async function loadTrades() {
    if (!currentUserId) {
        alert('Login or register first');
        return;
    }
    try {
        const res = await fetch(`${API_BASE}/trades?userId=${currentUserId}`);
        if (!res.ok) throw new Error('Failed to load trades');
        const data = await res.json(); // array of TradeDto

        const tbody = document.getElementById('tradesTableBody');
        tbody.innerHTML = '';

        // Show newest first
        data.slice().reverse().forEach(t => {
            const tr = document.createElement('tr');
            const timeStr = t.timestamp ? new Date(t.timestamp).toLocaleString() : '';
            tr.innerHTML = `
                <td>${t.id}</td>
                <td>${timeStr}</td>
                <td>${t.type}</td>
                <td>${t.symbol}</td>
                <td>${t.name}</td>
                <td>${t.quantity}</td>
                <td>${t.price.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        alert(e.message);
    }
}


async function refreshLivePrices() {
    try {
        const res = await fetch(`${API_BASE}/market/alpha-update`, {
            method: 'POST'
        });
        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Failed to refresh live prices');
        }
        // After updating prices on the backend, reload the stocks table
        await loadStocks();
        // Also refresh portfolio and holdings because values depend on prices
        if (currentUserId) {
            await loadPortfolio();
            await loadHoldings();
        }
        alert('Prices updated from global API');
    } catch (e) {
        alert(e.message);
    }
}
function placeTrade() {
    const side = document.getElementById('tradeType').value;
    const msgSpan = document.getElementById('tradeStatus');

    if (side === 'BUY') {
        buy().then(() => {
            msgSpan.textContent = 'BUY order executed';
        }).catch(e => {
            msgSpan.textContent = 'Error: ' + e.message;
        });
    } else {
        sell().then(() => {
            msgSpan.textContent = 'SELL order executed';
        }).catch(e => {
            msgSpan.textContent = 'Error: ' + e.message;
        });
    }
}

setInterval(() => {
    if (currentUserId) {
        loadPortfolio();
        loadHoldings();
        loadStocks();
    }
    // Stocks table can also be refreshed periodically if you want:
    // loadStocks();
}, 30000);

