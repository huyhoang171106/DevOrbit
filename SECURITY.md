# Security Policy

## Vulnerability Disclosure

The DevOrbit team takes security seriously. If you discover a security vulnerability,
please report it responsibly using the process described below.

## What Qualifies as a Security Issue

A security issue includes, but is not limited to:

- Authentication or authorization bypass
- Remote code execution
- SQL injection or other injection attacks
- Cross-site scripting (XSS) that could compromise user data
- Sensitive data exposure (API keys, passwords, tokens)
- Insecure deserialization
- Security misconfiguration that could be exploited
- Denial of service vulnerabilities that affect availability

## How to Report

### Option 1: GitHub Security Advisory (Preferred)

Use GitHub's private vulnerability reporting feature:

1. Go to the **Security** tab of this repository
2. Click **"Report a vulnerability"**
3. Fill in the details of the vulnerability

### Option 2: Email

Send an email to the maintainers with **[SECURITY]** in the subject line.

### Option 3: GitHub Issue (for low-severity issues only)

If the issue is **low severity** (e.g., minor information disclosure, best-practice violations),
you may open a GitHub issue with the **[SECURITY]** tag in the title.

## Response Timeline

| Action | Expected Timeframe |
|--------|--------------------|
| Acknowledgment of report | Within 48 hours |
| Initial assessment | Within 1 week |
| Fix development | Depends on severity; critical issues prioritized |
| Public disclosure | After fix is released |

## Scope

This security policy applies to the DevOrbit application codebase, including:

- `devorbit-api/` — Backend services
- `devorbit-web/` — Frontend web application
- `devorbit-mobile/` — Mobile application

It does **not** cover third-party dependencies. For dependency vulnerabilities,
please report them to the respective upstream projects.

## Recognition

We appreciate responsible disclosure and will credit reporters (with permission)
in release notes and the SECURITY acknowledgments section.

## Supported Versions

| Version | Supported |
|---------|-----------|
| Latest `master` | ✅ Yes |
| Older releases | ❌ No |
