(function(){const o=document.createElement("link").relList;if(o&&o.supports&&o.supports("modulepreload"))return;for(const e of document.querySelectorAll('link[rel="modulepreload"]'))s(e);new MutationObserver(e=>{for(const t of e)if(t.type==="childList")for(const i of t.addedNodes)i.tagName==="LINK"&&i.rel==="modulepreload"&&s(i)}).observe(document,{childList:!0,subtree:!0});function n(e){const t={};return e.integrity&&(t.integrity=e.integrity),e.referrerPolicy&&(t.referrerPolicy=e.referrerPolicy),e.crossOrigin==="use-credentials"?t.credentials="include":e.crossOrigin==="anonymous"?t.credentials="omit":t.credentials="same-origin",t}function s(e){if(e.ep)return;e.ep=!0;const t=n(e);fetch(e.href,t)}})();const c=document.querySelector("#app");c.innerHTML=`
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
`;document.querySelector("#btn").addEventListener("click",async()=>{const r=document.querySelector("#out");r.textContent="Loading...";try{const o=await fetch("/api/hello",{headers:{Accept:"application/json"}}),n=await o.json().catch(()=>null);r.textContent=JSON.stringify({status:o.status,body:n},null,2)}catch(o){r.textContent=String(o)}});const a=document.createElement("style");a.textContent=`
  :root { color-scheme: light dark; }
  body { margin: 0; font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Arial; }
  .container { max-width: 880px; margin: 48px auto; padding: 0 16px; }
  .muted { opacity: 0.75; }
  .card { border: 1px solid rgba(127,127,127,.35); border-radius: 12px; padding: 16px; margin-top: 16px; }
  button { padding: 10px 14px; border-radius: 10px; border: 1px solid rgba(127,127,127,.35); background: transparent; cursor: pointer; }
  button:hover { border-color: rgba(127,127,127,.7); }
  pre { margin-top: 12px; padding: 12px; border-radius: 10px; background: rgba(127,127,127,.12); overflow: auto; }
  code { font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace; }
`;document.head.appendChild(a);
