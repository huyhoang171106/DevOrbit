# DevOrbit Web

This is the frontend single page application for the DevOrbit portal, built using React and Vite. It provides interactive dashboards for students and administrators, renders a 3D knowledge graph of course syllabi, implements real-time chat communities, and manages photo frame compositing.

## System Details

- Framework: React 19
- Build Tool: Vite 6
- Language: TypeScript
- Style System: TailwindCSS 3.4.0
- Asset Optimizer: lightningcss, vite-plugin-compression (Brotli/Gzip)
- State Management: Redux Toolkit (API calls) and Zustand (3D Galaxy page state)

## Folder Structure

The application source resides in the src/ directory:
- components/ : Reusable UI components, including admin tables and student selectors.
- hooks/ : Custom React hooks (e.g., useDebounce, useSubjectQa).
- lib/ : Shared helper libraries (API client, authentication logic, Canvas photo compositors).
- pages/ : Page views for student paths (Home, Course List, Course Detail, Photobooth, 3D Galaxy) and admin paths (Dashboard, Notes, Roadmaps).
- router.tsx : Declarative application routing and authentication guard rails.
- types/ : TypeScript interfaces for API responses and payload payloads.
- utils/ : Pure helper functions for formatting dates, calculating GPAs, and pagination.

## Communication with Backend

- Proxying: During development, Vite is configured to proxy requests to avoid CORS issues:
  - All calls matching /api are redirected to the backend API target (VITE_PROXY_TARGET).
  - All calls matching /ws are mapped to the backend WebSocket endpoint with ws enabled.
- API Client: Fetches are handled using helper wrappers that automatically inject JWT credentials into the Authorization header from localStorage.

## Authentication State

- Tokens: Upon a successful login, the application receives a JWT from the backend.
- Storage: The token is saved in localStorage.
- Validation: When the application loads, routing guards check the token's presence, token type (ADMIN vs STUDENT), and expiration. If the token is invalid, the user is redirected to the login page.

## Real-Time WebSocket Chat

- Protocol: Uses STOMP over SockJS.
- Endpoint: Connects to /ws/community.
- Flow: The chat components subscribe to /topic/channel/{channelId} and publish messages to /app/chat.send/{channelId}.
- Presence: The application tracks and renders the active presence list of students currently connected to the WebSocket chat channels.

## Environment Variables

Configure these variables in a local .env file inside devorbit-web/:

- WEB_PORT: Port for the Vite dev server (default: 5173).
- VITE_PROXY_TARGET: Address of the running backend API (default: http://localhost:8080).
- VITE_API_BASE_URL: Root path for REST API calls (default: /api).
- VITE_ALLOWED_HOSTS: Hostnames allowed to connect to the dev server (default: localhost).

## Development Commands

Run these scripts from the devorbit-web/ directory:

- Start development server: npm run dev
- Type-check and compile bundle: npm run build
- Preview production build locally: npm run preview
- Run Vitest tests: npm test (or npm test -- --run for a single run)
- Run React diagnostics: npm run doctor

## Common Troubleshooting Frontend Errors

- Connection Refused on /api: Verify that the backend server is running on the address specified in VITE_PROXY_TARGET.
- WebSocket Disconnected: If the community chat fails to connect, check if the backend port matches and ensure that you are logged in as a STUDENT with a valid token.
- Missing packages: If build fails after pulling changes, run npm install to sync your node_modules directory.
