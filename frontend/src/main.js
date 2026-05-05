const app = document.querySelector("#app");

app.innerHTML = `
  <main class="container">
    <h1>Demo Deploy</h1>
    <p class="muted">Frontend: Vite (static). Backend: Spring Boot (JAR).</p>

    <section class="card">
      <h2>API test</h2>
      <p>Calls <code>/api/hello</code> on the same origin.</p>
      <button id="btn">Call API</button>
      <pre id="out" aria-live="polite"></pre>
    </section>

    <section class="card">
      <h2>Deploy check</h2>
      <ol>
        <li>If this page loads, static files are served.</li>
        <li>If the API button works, backend routing is OK.</li>
      </ol>
    </section>
  </main>
`;

document.querySelector("#btn").addEventListener("click", async () => {
  const out = document.querySelector("#out");
  out.textContent = "Loading...";
  try {
    const res = await fetch("/api/hello", { headers: { Accept: "application/json" } });
    const body = await res.json().catch(() => null);
    out.textContent = JSON.stringify({ status: res.status, body }, null, 2);
  } catch (e) {
    out.textContent = String(e);
  }
});

const style = document.createElement("style");
style.textContent = `
  :root { color-scheme: light dark; }
  body { margin: 0; font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Arial; }
  .container { max-width: 880px; margin: 48px auto; padding: 0 16px; }
  .muted { opacity: 0.75; }
  .card { border: 1px solid rgba(127,127,127,.35); border-radius: 12px; padding: 16px; margin-top: 16px; }
  button { padding: 10px 14px; border-radius: 10px; border: 1px solid rgba(127,127,127,.35); background: transparent; cursor: pointer; }
  button:hover { border-color: rgba(127,127,127,.7); }
  pre { margin-top: 12px; padding: 12px; border-radius: 10px; background: rgba(127,127,127,.12); overflow: auto; }
  code { font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace; }
`;
document.head.appendChild(style);

