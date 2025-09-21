const API = "http://localhost:8080";

function saveToken(token) {
    localStorage.setItem("jwt", token);
}
function getToken() {
    return localStorage.getItem("jwt");
}
function clearToken() {
    localStorage.removeItem("jwt");
}

function isAuthed() {
    return !!getToken();
}
function logout() {
    clearToken();
    window.location.href = "/login.html";
}

async function signup(email, password) {
    const res = await fetch(`${API}/auth/signup`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!res.ok) {
        const msg = await safeMessage(res);
        throw new Error(msg || "Signup failed");
    }

    const data = await res.json();
    if (data?.token) saveToken(data.token);
    return data;
}

async function login(email, password) {
    const res = await fetch(`${API}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (res.status === 401) {
        throw new Error("UNAUTHORIZED");
    }
    if (!res.ok) {
        const msg = await safeMessage(res);
        throw new Error(msg || "Login failed");
    }

    const data = await res.json();
    if (data?.token) saveToken(data.token);
    return data;
}

async function me() {
    const res = await fetch(`${API}/auth/me`, {
        headers: { Authorization: `Bearer ${getToken()}` },
    });
    if (res.status === 401) throw new Error("UNAUTHORIZED");
    if (!res.ok) {
        const msg = await safeMessage(res);
        throw new Error(msg || "Failed to fetch /me");
    }
    return res.json();
}

async function authFetch(path, options = {}) {
    const token = getToken();
    const headers = {
        ...(options.headers || {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
    const res = await fetch(`${API}${path}`, { ...options, headers });

    if (res.status === 401) {
        alert("يبدو أن الجلسة انتهت. سجّلي الدخول من جديد.");
        logout();
        throw new Error("Unauthorized");
    }
    return res;
}

async function safeMessage(res) {
    try {
        const ct = res.headers.get("content-type") || "";
        if (ct.includes("application/json")) {
            const j = await res.json();
            return j?.message || j?.error || j?.detail || null;
        }
        const t = await res.text();
        return t?.slice(0, 200) || null;
    } catch {
        return null;
    }
}

window.signup = signup;
window.login = login;
window.me = me;
window.isAuthed = isAuthed;
window.logout = logout;
window.authFetch = authFetch;
