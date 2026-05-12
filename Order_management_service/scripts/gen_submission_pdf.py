"""
Build coding-test submission PDF (HTML + Edge headless).
Output: Saifaofficial21_Java_Microservices_Coding_Test.pdf next to project root (Order_management_service/).
"""
from __future__ import annotations

import html
import os
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/Reflection/Order_management_service"
OUT_HTML = ROOT / "_submission_print.html"
OUT_PDF = ROOT / "Saifaofficial21_Java_Microservices_Coding_Test.pdf"

FILES: list[tuple[str, Path]] = [
    ("Controller — OrderController.java", JAVA / "controller" / "OrderController.java"),
    ("Service — OrderService.java", JAVA / "service" / "OrderService.java"),
    ("Service — OrderServiceImpl.java", JAVA / "service" / "OrderServiceImpl.java"),
    ("Model — Order.java", JAVA / "model" / "Order.java"),
    ("Model — OrderStatus.java", JAVA / "model" / "OrderStatus.java"),
    ("Exception — OrderNotFoundException.java", JAVA / "exception" / "OrderNotFoundException.java"),
    ("Exception — InvalidStatusTransitionException.java", JAVA / "exception" / "InvalidStatusTransitionException.java"),
    ("Exception — GlobalExceptionHandler.java", JAVA / "exception" / "GlobalExceptionHandler.java"),
]

APPROACH = (
    "This service uses a thin REST controller on top of an OrderService implementation that stores orders "
    "in a thread-safe in-memory map. Domain rules for status progression live on the OrderStatus enum and "
    "are enforced in the service layer, while a single RestControllerAdvice maps domain and validation "
    "failures to consistent JSON error bodies."
)


def find_edge() -> Path | None:
    candidates = [
        Path(r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"),
        Path(os.environ.get("PROGRAMFILES(X86)", "")) / "Microsoft" / "Edge" / "Application" / "msedge.exe",
        Path(os.environ.get("LOCALAPPDATA", "")) / "Microsoft" / "Edge" / "Application" / "msedge.exe",
    ]
    for p in candidates:
        if p and p.is_file():
            return p
    return None


def build_html() -> str:
    chunks: list[str] = []
    chunks.append(
        """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8"/>
<title>Saifaofficial21 — Java Microservices Coding Test</title>
<style>
  body { font-family: "Segoe UI", Arial, sans-serif; margin: 28px; color: #111; font-size: 11pt; line-height: 1.35; }
  h1 { font-size: 18pt; border-bottom: 2px solid #333; padding-bottom: 6px; margin-top: 28px; page-break-before: always; }
  h1.first { page-break-before: avoid; margin-top: 0; }
  h2 { font-size: 13pt; margin-top: 18px; color: #222; }
  .meta { background: #f4f4f4; padding: 14px 16px; border-radius: 6px; margin: 16px 0; }
  .meta a { word-break: break-all; }
  pre { background: #f8f8f8; border: 1px solid #ddd; padding: 12px 14px; border-radius: 4px;
        font-family: Consolas, "Courier New", monospace; font-size: 8.5pt; white-space: pre-wrap; word-wrap: break-word; }
  .approach { margin: 16px 0 24px; }
</style>
</head>
<body>
"""
    )
    chunks.append('<h1 class="first">Java Microservices Coding Test — Submission</h1>')
    chunks.append("<p><strong>Candidate (GitHub):</strong> saifaofficial21</p>")
    chunks.append('<div class="meta"><strong>Repository (HTTPS)</strong><br/>')
    chunks.append(
        '<a href="https://github.com/saifaofficial21/Order_Management_service.git">https://github.com/saifaofficial21/Order_Management_service.git</a></div>'
    )
    chunks.append('<div class="meta"><strong>Repository (SSH)</strong><br/>')
    chunks.append("<code>git@github.com:saifaofficial21/Order_Management_service.git</code></div>")
    chunks.append("<h2>Brief explanation of approach (2–3 lines)</h2>")
    chunks.append(f'<p class="approach">{html.escape(APPROACH)}</p>')

    for title, path in FILES:
        if not path.is_file():
            raise SystemExit(f"Missing source file: {path}")
        code = path.read_text(encoding="utf-8")
        chunks.append(f"<h1>{html.escape(title)}</h1>")
        chunks.append(f"<pre>{html.escape(code)}</pre>")

    chunks.append("</body></html>")
    return "".join(chunks)


def main() -> int:
    html_doc = build_html()
    OUT_HTML.write_text(html_doc, encoding="utf-8")
    edge = find_edge()
    if not edge:
        print("Microsoft Edge not found; cannot print PDF.", file=sys.stderr)
        return 1
    uri = OUT_HTML.as_uri()
    cmd = [
        str(edge),
        "--headless=new",
        "--disable-gpu",
        "--no-pdf-header-footer",
        f"--print-to-pdf={OUT_PDF.resolve()}",
        uri,
    ]
    subprocess.run(cmd, check=True, timeout=120)
    if not OUT_PDF.is_file():
        print("PDF was not created.", file=sys.stderr)
        return 1
    print(f"Wrote {OUT_PDF}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
