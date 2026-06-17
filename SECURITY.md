# Security Policy

## Supported Versions

The following software versions are currently active and supported for security patches:

- devorbit-api : Spring Boot 4.0.6 / Java 21
- devorbit-web : React 19 / Vite 6
- devorbit-mobile : Kotlin / Jetpack Compose Android Client

## Reporting a Vulnerability

If you discover a security vulnerability, please do not disclose it publicly by opening an issue on GitHub. Instead, report it privately by emailing the project maintainers at [PROJECT CONTACT EMAIL].

Please include the following information in your security report:
- The component or module affected (backend, web, mobile, database).
- A detailed description of the vulnerability and its potential impact.
- Step-by-step instructions (with proof-of-concept code if available) to reproduce the issue.
- Any suggestions for mitigation or a permanent fix.

We will review your report and coordinate a fix before disclosing the issue publicly.

## Credentials and Secrets Protection

To prevent exposing credentials, follow these mandatory practices:

- Do not commit environment configuration files containing secrets (e.g. .env, .env.properties, local.properties) to the Git repository.
- Avoid hardcoding API keys (such as OpenCode AI, Fireworks, Exa, or Supabase credentials), passwords, or JWT secrets in source code files.
- In production or test staging environments, inject secrets via environment variables rather than static property files.
- Use the provided .env.example templates to configure local environments.
- Regularly audit dependency trees using automated scans (e.g. npm audit for the frontend and OWASP dependency-check for the backend).
