## Demo deploy fullstack (Vite + Spring Boot)

This is a tiny app you can use to test deployment on the WALAB school server.

- **Frontend**: Vite static build (produces `frontend/dist/`)
- **Backend**: Spring Boot JAR
- **Runtime**: the backend serves the built frontend and exposes `GET /api/hello`

### What you should see

- Visiting the server shows a page titled “Demo Deploy”
- Clicking **Call API** prints JSON from `GET /api/hello`

### Local run (recommended first)

#### Backend only (no frontend build needed)

```bash
cd demo-deploy-fullstack/backend
mvn spring-boot:run
```

Then open `http://localhost:8080/api/hello`.

#### Frontend dev server (proxy to backend)

```bash
cd demo-deploy-fullstack/frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal. `/api/*` is proxied to `http://localhost:8080`.

### Build a deployable WAR (Windows)

From the workspace root:

```powershell
.\demo-deploy-fullstack\build.ps1
```

This will:

1) `npm run build` in `frontend/`  
2) copy `frontend/dist/*` → `backend/src/main/resources/static/`  
3) `mvn package` in `backend/`

Result (Tomcat deploy):

- `demo-deploy-fullstack/backend/target/demo-deploy-backend-0.0.1.war`

### Deploy to WALAB (Tomcat `.war` + FileZilla)

Your milestone-style server usually auto-deploys anything placed in Tomcat’s `webapps/` folder.

1) **Build the WAR**:

```powershell
.\demo-deploy-fullstack\build.ps1
```

2) **Rename the WAR to match the required name**:

Rename:

- `demo-deploy-fullstack/backend/target/demo-deploy-backend-0.0.1.war`

to:

- `naimkim_1.war`

3) **Upload `naimkim_1.war` using FileZilla** to the same place you uploaded milestone WARs:

- Tomcat `webapps/` directory (commonly looks like `.../tomcat/webapps/` or `.../apache-tomcat-*/webapps/`)

4) **If a folder `naimkim_1/` already exists next to it**, delete that folder too (Tomcat expands WARs). This forces a clean redeploy.

5) **Verify**

- `/<context>/api/hello` returns JSON
- `/<context>/` shows the “Demo Deploy” page

> Note: the context path is typically the WAR filename (so `naimkim_1.war` → `/naimkim_1/`).

### Notes

- If your server setup uses an existing web server (Apache/Nginx) as a reverse proxy, you may need a proxy rule to forward a domain/path to `localhost:<PORT>`.
- If the page doesn’t update after redeploy, hard refresh the browser (Ctrl+F5) to bypass cache.

