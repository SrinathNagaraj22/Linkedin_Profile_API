## LinkedIn Integration Findings

During reverse engineering of the LinkedIn profile request, the public
profile URL was found to return HTML rather than a directly consumable
profile JSON response.

The browser-rendered profile page contains profile information such as:

- Name
- Headline
- Location
- Experience
- Other profile sections when available

A direct backend request to the LinkedIn profile URL was also tested using
the available session Cookie and CSRF token.

However, LinkedIn returned an authentication-wall HTML response instead
of the actual profile page. The response contained an `/authwall` flow and
a `sessionRedirect` parameter.

Because of this behavior, a stable public profile JSON endpoint could not
be reliably identified and used.

### Implementation

The LinkedIn client therefore implements:

1. Direct HTTP request to the LinkedIn profile URL.
2. Session Cookie and CSRF token support through configuration.
3. Server-rendered HTML parsing using Jsoup.
4. Profile field extraction when profile HTML is available.
5. Authentication-wall detection.
6. Mapping of upstream HTTP errors:
    - 401 → LinkedIn authentication error
    - 403 → LinkedIn access denied
    - 404 → Profile not found
    - 429 → Rate limit exceeded
7. Graceful error handling instead of returning fabricated profile data.

### Current Limitation

The tested LinkedIn request currently returns an authentication-wall HTML
response to the backend client. Therefore, complete profile extraction
cannot be guaranteed from the current non-browser request.

The application is intentionally designed so that the LinkedIn-specific
integration is isolated inside `LinkedInClientImpl`. If a supported and
stable LinkedIn profile endpoint becomes available, only this integration
layer needs to be updated.