from bs4 import BeautifulSoup
from pathlib import Path

html = Path("linkedin-profile.html").read_text(
    encoding="utf-8",
    errors="ignore"
)

soup = BeautifulSoup(html, "html.parser")

targets = [
    "SRINATH NAGARAJ",
    "Backend Engineer",
    "Accenture",
    "About",
    "Experience",
    "Education",
    "Skills"
]

for target in targets:
    print("\n" + "=" * 80)
    print("TARGET:", target)
    print("=" * 80)

    element = soup.find(
        lambda tag: tag.name in ["h1", "h2", "h3", "div", "span", "p"]
        and target.lower() in tag.get_text(" ", strip=True).lower()
    )

    if element:
        # Print only a reasonable amount around the matching element
        print(element.prettify()[:5000])
    else:
        print("NOT FOUND")