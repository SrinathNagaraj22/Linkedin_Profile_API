package com.tross.linkedinapi.client;

import com.tross.linkedinapi.client.dto.RawLinkedInProfile;
import com.tross.linkedinapi.config.LinkedInConfig;
import com.tross.linkedinapi.exception.LinkedInAccessDeniedException;
import com.tross.linkedinapi.exception.LinkedInAuthException;
import com.tross.linkedinapi.exception.LinkedInClientException;
import com.tross.linkedinapi.exception.LinkedInRateLimitException;
import com.tross.linkedinapi.exception.ProfileNotFoundException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class LinkedInClientImpl implements LinkedInClient {

    private static final Logger log =
            LoggerFactory.getLogger(LinkedInClientImpl.class);

    private final LinkedInConfig config;
    private final RestClient restClient;

    public LinkedInClientImpl(
            LinkedInConfig config,
            RestClient linkedInRestClient) {

        this.config = config;
        this.restClient = linkedInRestClient;
    }

    @Override
    public RawLinkedInProfile fetchProfile(String publicIdentifier) {

        log.info("Fetching LinkedIn profile identifier={}", publicIdentifier);

        log.info("Cookie configured: {}",
                config.getCookie() != null &&
                        !config.getCookie().isBlank());

        log.info("CSRF token configured: {}",
                config.getCsrfToken() != null &&
                        !config.getCsrfToken().isBlank());

        try {

            log.debug(
                    "Requesting LinkedIn URL: {}",
                    config.getBaseUrl() + "/in/" + publicIdentifier + "/"
            );

            String html = restClient.get()
                    .uri("/in/" + publicIdentifier + "/")
                    .header(
                            "Cookie",
                            config.getCookie() != null
                                    ? config.getCookie()
                                    : ""
                    )
                    .header(
                            "csrf-token",
                            config.getCsrfToken() != null
                                    ? config.getCsrfToken()
                                    : ""
                    )
                    .header(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/151.0.0.0 Safari/537.36"
                    )
                    .header(
                            "Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                    )
                    .header(
                            "Accept-Language",
                            "en-US,en;q=0.9"
                    )
                    .header(
                            "x-restli-protocol-version",
                            "2.0.0"
                    )
                    .retrieve()
                    .body(String.class);

            if (html == null || html.isBlank()) {

                log.error("LinkedIn returned an empty response.");

                throw new LinkedInClientException(
                        "LinkedIn returned an empty response."
                );
            }

            log.info(
                    "Received LinkedIn HTML response. Length={}",
                    html.length()
            );

            /*
             * ------------------------------------------------------------
             * AUTH WALL DETECTION
             * ------------------------------------------------------------
             *
             * LinkedIn may return an authentication wall instead of
             * the actual profile HTML.
             *
             * The response observed during testing contains:
             *
             *     /authwall
             *     sessionRedirect
             *
             * Therefore we detect this before attempting HTML parsing.
             */

            if (isAuthenticationWall(html)) {

                log.warn(
                        "LinkedIn authentication wall detected for identifier={}",
                        publicIdentifier
                );

                throw new LinkedInAuthException(
                        "LinkedIn returned an authentication wall. " +
                                "The profile could not be accessed through the current request."
                );
            }

            /*
             * ------------------------------------------------------------
             * PROFILE HTML PARSING
             * ------------------------------------------------------------
             */

            RawLinkedInProfile profile =
                    parseProfileHtml(
                            html,
                            publicIdentifier
                    );

            log.info(
                    "Parsed LinkedIn profile: name={}, headline={}, " +
                            "location={}, experience={}, education={}, skills={}",
                    profile.getFirstName(),
                    profile.getHeadline(),
                    profile.getCity(),
                    profile.getExperience() != null
                            ? profile.getExperience().size()
                            : 0,
                    profile.getEducation() != null
                            ? profile.getEducation().size()
                            : 0,
                    profile.getSkills() != null
                            ? profile.getSkills().size()
                            : 0
            );

            return profile;

        } catch (RestClientResponseException ex) {

            log.error(
                    "LinkedIn returned HTTP error: {}",
                    ex.getStatusCode().value()
            );

            throw translateHttpException(ex);

        } catch (LinkedInClientException |
                 LinkedInAccessDeniedException |
                 LinkedInAuthException |
                 LinkedInRateLimitException |
                 ProfileNotFoundException ex) {

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "Unexpected error while processing LinkedIn profile",
                    ex
            );

            throw new LinkedInClientException(
                    "Unable to process LinkedIn profile."
            );
        }
    }

    /**
     * Detects whether LinkedIn returned an authentication wall.
     */
    private boolean isAuthenticationWall(String html) {

        if (html == null || html.isBlank()) {
            return false;
        }

        String lowerCaseHtml =
                html.toLowerCase();

        return lowerCaseHtml.contains("/authwall")
                || lowerCaseHtml.contains("sessionredirect")
                || lowerCaseHtml.contains("authentication")
                || lowerCaseHtml.contains("sign in to linkedin");
    }

    /**
     * Parses LinkedIn's server-rendered HTML.
     */
    private RawLinkedInProfile parseProfileHtml(
            String html,
            String publicIdentifier) {

        Document document =
                Jsoup.parse(html);

        RawLinkedInProfile profile =
                new RawLinkedInProfile();

        profile.setPublicIdentifier(
                publicIdentifier
        );

        /*
         * ---------------------------------------------------------------
         * NAME
         * ---------------------------------------------------------------
         */

        String name =
                extractName(document);

        if (name != null && !name.isBlank()) {

            String[] nameParts =
                    name.trim().split("\\s+", 2);

            profile.setFirstName(
                    nameParts[0]
            );

            if (nameParts.length > 1) {

                profile.setLastName(
                        nameParts[1]
                );
            }
        }

        /*
         * ---------------------------------------------------------------
         * HEADLINE
         * ---------------------------------------------------------------
         */

        profile.setHeadline(
                extractHeadline(document)
        );

        /*
         * ---------------------------------------------------------------
         * LOCATION
         * ---------------------------------------------------------------
         */

        String location =
                extractLocation(document);

        if (location != null &&
                !location.isBlank()) {

            String[] parts =
                    location.split(",");

            if (parts.length >= 2) {

                profile.setCity(
                        parts[0].trim()
                );

                profile.setCountry(
                        parts[parts.length - 1].trim()
                );

            } else {

                profile.setCity(
                        location.trim()
                );
            }
        }

        /*
         * ---------------------------------------------------------------
         * ABOUT
         * ---------------------------------------------------------------
         */

        profile.setAbout(
                extractSectionText(
                        document,
                        "About"
                )
        );

        /*
         * ---------------------------------------------------------------
         * PROFILE IMAGE
         * ---------------------------------------------------------------
         */

        profile.setProfileImageUrl(
                extractProfileImage(document)
        );

        /*
         * ---------------------------------------------------------------
         * EXPERIENCE
         * ---------------------------------------------------------------
         */

        profile.setExperience(
                extractExperience(document)
        );

        /*
         * ---------------------------------------------------------------
         * EDUCATION
         * ---------------------------------------------------------------
         */

        profile.setEducation(
                extractEducation(document)
        );

        /*
         * ---------------------------------------------------------------
         * SKILLS
         * ---------------------------------------------------------------
         */

        profile.setSkills(
                extractSkills(document)
        );

        /*
         * These sections were not available in the HTML
         * observed during the current investigation.
         *
         * Do not invent data.
         */

        profile.setCertifications(
                new ArrayList<>()
        );

        profile.setLanguages(
                new ArrayList<>()
        );

        return profile;
    }

    /**
     * Extract profile name.
     */
    private String extractName(Document document) {

        Element title =
                document.selectFirst("title");

        if (title != null) {

            String text =
                    cleanText(title.text());

            if (text.contains("| LinkedIn")) {

                String name =
                        text.replace(
                                "| LinkedIn",
                                ""
                        ).trim();

                if (!name.isBlank()) {

                    return name;
                }
            }
        }

        Elements headings =
                document.select("h1");

        for (Element heading : headings) {

            String text =
                    cleanText(
                            heading.text()
                    );

            if (!text.isBlank()
                    && text.length() < 100
                    && !text.equalsIgnoreCase("LinkedIn")) {

                return text;
            }
        }

        Element ogTitle =
                document.selectFirst(
                        "meta[property=og:title]"
                );

        if (ogTitle != null) {

            String content =
                    cleanText(
                            ogTitle.attr("content")
                    );

            if (!content.isBlank()) {

                return content;
            }
        }

        return null;
    }

    /**
     * Extract headline.
     */
    private String extractHeadline(Document document) {

        Element description =
                document.selectFirst(
                        "meta[name=description]"
                );

        if (description != null) {

            String content =
                    cleanText(
                            description.attr("content")
                    );

            if (!content.isBlank()
                    && content.length() <= 500) {

                return content;
            }
        }

        Element h1 =
                document.selectFirst("h1");

        if (h1 != null) {

            Element parent =
                    h1.parent();

            if (parent != null) {

                String text =
                        cleanText(
                                parent.text()
                        );

                if (text.length() >
                        h1.text().length()
                        && text.length() < 500) {

                    return removeNameFromText(
                            text,
                            h1.text()
                    );
                }
            }
        }

        for (Element element :
                document.select("p, span, div")) {

            String text =
                    cleanText(
                            element.ownText()
                    );

            if (text.length() >= 20
                    && text.length() <= 300
                    && (
                    text.contains("Engineer")
                            || text.contains("Developer")
                            || text.contains("Consultant")
                            || text.contains("Manager")
                            || text.contains("Analyst")
            )) {

                return text;
            }
        }

        return null;
    }

    /**
     * Extract location.
     */
    private String extractLocation(Document document) {

        for (Element element :
                document.select("span, div, p")) {

            String text =
                    cleanText(
                            element.ownText()
                    );

            if (text.length() >= 3
                    && text.length() <= 150
                    && (
                    text.contains("Area")
                            || text.contains("India")
                            || text.contains("Bangalore")
                            || text.contains("Bengaluru")
                            || text.contains("Chennai")
                            || text.contains("Coimbatore")
            )) {

                if (!text.contains("LinkedIn")
                        && !text.contains("connections")
                        && !text.contains("notifications")) {

                    return text;
                }
            }
        }

        return null;
    }

    /**
     * Extract section text using the section heading.
     */
    private String extractSectionText(
            Document document,
            String sectionName) {

        Elements headings =
                document.select("h2, h3");

        for (Element heading : headings) {

            String headingText =
                    cleanText(
                            heading.text()
                    );

            if (headingText.equalsIgnoreCase(
                    sectionName)) {

                Element parent =
                        heading.parent();

                if (parent != null) {

                    String text =
                            cleanText(
                                    parent.text()
                            );

                    text =
                            text.replaceFirst(
                                    "(?i)^" +
                                            Pattern.quote(sectionName),
                                    ""
                            ).trim();

                    if (!text.isBlank()) {

                        return text;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Extract profile image.
     */
    private String extractProfileImage(
            Document document) {

        Element image =
                document.selectFirst(
                        "meta[property=og:image]"
                );

        if (image != null) {

            String url =
                    image.attr("content");

            if (!url.isBlank()) {

                return url;
            }
        }

        image =
                document.selectFirst("img");

        if (image != null) {

            String src =
                    image.attr("src");

            if (!src.isBlank()) {

                return src;
            }
        }

        return null;
    }

    /**
     * Extract experience entries.
     */
    private List<RawLinkedInProfile.RawExperience>
    extractExperience(Document document) {

        List<RawLinkedInProfile.RawExperience>
                result =
                new ArrayList<>();

        Elements headings =
                document.select("h2, h3");

        for (Element heading : headings) {

            if (!cleanText(heading.text())
                    .equalsIgnoreCase("Experience")) {

                continue;
            }

            Element section =
                    heading.parent();

            if (section == null) {

                continue;
            }

            Elements articles =
                    section.select("li");

            for (Element article :
                    articles) {

                String text =
                        cleanText(
                                article.text()
                        );

                if (text.isBlank()) {

                    continue;
                }

                String title =
                        firstMeaningfulLine(text);

                if (title == null) {

                    continue;
                }

                result.add(
                        new RawLinkedInProfile.RawExperience(
                                title,
                                null,
                                null,
                                null,
                                null,
                                text
                        )
                );
            }

            break;
        }

        return result;
    }

    /**
     * Extract education entries.
     */
    private List<RawLinkedInProfile.RawEducation>
    extractEducation(Document document) {

        List<RawLinkedInProfile.RawEducation>
                result =
                new ArrayList<>();

        Elements headings =
                document.select("h2, h3");

        for (Element heading : headings) {

            if (!cleanText(heading.text())
                    .equalsIgnoreCase("Education")) {

                continue;
            }

            Element section =
                    heading.parent();

            if (section == null) {

                continue;
            }

            Elements articles =
                    section.select("li");

            for (Element article :
                    articles) {

                String text =
                        cleanText(
                                article.text()
                        );

                if (text.isBlank()) {

                    continue;
                }

                String school =
                        firstMeaningfulLine(text);

                if (school == null) {

                    continue;
                }

                result.add(
                        new RawLinkedInProfile.RawEducation(
                                school,
                                null,
                                null,
                                null,
                                null
                        )
                );
            }

            break;
        }

        return result;
    }

    /**
     * Extract skills.
     */
    private List<String> extractSkills(
            Document document) {

        List<String> skills =
                new ArrayList<>();

        Elements headings =
                document.select("h2, h3");

        for (Element heading : headings) {

            if (!cleanText(heading.text())
                    .equalsIgnoreCase("Skills")) {

                continue;
            }

            Element section =
                    heading.parent();

            if (section == null) {

                continue;
            }

            Elements items =
                    section.select("li");

            for (Element item :
                    items) {

                String skill =
                        cleanText(
                                item.text()
                        );

                if (!skill.isBlank()
                        && skill.length() <= 100) {

                    skills.add(skill);
                }
            }

            break;
        }

        return skills;
    }

    /**
     * Gets the first useful line.
     */
    private String firstMeaningfulLine(
            String text) {

        if (text == null ||
                text.isBlank()) {

            return null;
        }

        String[] lines =
                text.split("\\r?\\n");

        for (String line : lines) {

            String cleaned =
                    cleanText(line);

            if (!cleaned.isBlank()
                    && cleaned.length() > 1) {

                return cleaned;
            }
        }

        return cleanText(text);
    }

    private String removeNameFromText(
            String text,
            String name) {

        if (text == null) {

            return null;
        }

        if (name == null ||
                name.isBlank()) {

            return text;
        }

        return text
                .replace(name, "")
                .trim();
    }

    private String cleanText(
            String text) {

        if (text == null) {

            return "";
        }

        return text
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Translate upstream HTTP errors.
     */
    private RuntimeException translateHttpException(
            RestClientResponseException ex) {

        int status =
                ex.getStatusCode().value();

        return switch (status) {

            case 401 ->
                    new LinkedInAuthException(
                            "LinkedIn session is missing or expired."
                    );

            case 403 ->
                    new LinkedInAccessDeniedException(
                            "LinkedIn denied access to this profile."
                    );

            case 404 ->
                    new ProfileNotFoundException(
                            "LinkedIn profile could not be found."
                    );

            case 429 ->
                    new LinkedInRateLimitException(
                            "LinkedIn rate limit reached. Try again later."
                    );

            default ->
                    new LinkedInClientException(
                            "LinkedIn upstream error: HTTP "
                                    + status
                    );
        };
    }
}