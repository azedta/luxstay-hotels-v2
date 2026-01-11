const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function request(path, { method = "GET", body, headers } = {}) {
    const res = await fetch(`${BASE_URL}${path}`, {
        method,
        headers: { "Content-Type": "application/json", ...(headers || {}) },
        body: body ? JSON.stringify(body) : undefined,
    });

    const text = await res.text();
    const data = text ? JSON.parse(text) : null;

    if (!res.ok) {
        const msg = data?.message || data?.error || `Request failed (${res.status})`;
        throw new Error(msg);
    }
    return data;
}

export const http = {
    get: (p) => request(p),
    post: (p, b) => request(p, { method: "POST", body: b }),
    put: (p, b) => request(p, { method: "PUT", body: b }),
    del: (p) => request(p, { method: "DELETE" }),
};
