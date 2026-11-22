#!/usr/bin/env python3
"""Apply the NovaGram branding and Java overlay to a clean Telegram Android checkout."""

from __future__ import annotations

import argparse
import os
import re
import shutil
from pathlib import Path


def replace_once(path: Path, pattern: str, replacement: str, description: str) -> None:
    data = path.read_text(encoding="utf-8")
    updated, count = re.subn(pattern, replacement, data, count=1, flags=re.MULTILINE)
    if count != 1:
        raise SystemExit(f"Could not patch {description} in {path}; expected one match, found {count}.")
    path.write_text(updated, encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", required=True, type=Path, help="Path to Telegram-Android checkout")
    args = parser.parse_args()

    source = args.source.resolve()
    api_id = os.environ.get("NOVAGRAM_API_ID", "")
    api_hash = os.environ.get("NOVAGRAM_API_HASH", "")

    if not re.fullmatch(r"[0-9]+", api_id):
        raise SystemExit("NOVAGRAM_API_ID must be a numeric Telegram API ID.")
    if not re.fullmatch(r"[a-fA-F0-9]{32}", api_hash):
        raise SystemExit("NOVAGRAM_API_HASH must be 32 hexadecimal characters.")

    gradle_props = source / "gradle.properties"
    build_vars = source / "TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"
    strings = source / "TMessagesProj/src/main/res/values/strings.xml"
    app_gradle = source / "TMessagesProj_App/build.gradle"

    for required in (gradle_props, build_vars, strings, app_gradle):
        if not required.is_file():
            raise SystemExit(f"Expected upstream file is missing: {required}")

    replace_once(gradle_props, r"^APP_PACKAGE=.*$", "APP_PACKAGE=app.novagram.client", "application ID")
    replace_once(
        strings,
        r'(<string name="AppName">).*?(</string>)',
        r"\1NovaGram\2",
        "app display name",
    )
    replace_once(build_vars, r"public static int APP_ID = [0-9]+;",
                 f"public static int APP_ID = {api_id};", "Telegram API ID")
    replace_once(build_vars, r'public static String APP_HASH = "[a-fA-F0-9]+";',
                 f'public static String APP_HASH = "{api_hash}";', "Telegram API hash")
    replace_once(build_vars, r"public static boolean SUPPORTS_PASSKEYS = true;",
                 "public static boolean SUPPORTS_PASSKEYS = false;", "official-package passkey flag")

    overlay_root = Path(__file__).resolve().parents[1] / "overlay" / "java"
    java_root = source / "TMessagesProj/src/main/java"
    if not overlay_root.is_dir():
        raise SystemExit(f"Overlay sources not found: {overlay_root}")
    for java_file in overlay_root.rglob("*.java"):
        destination = java_root / java_file.relative_to(overlay_root)
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(java_file, destination)

    google_services = source / "TMessagesProj/google-services.json"
    if not google_services.exists():
        app_gradle.write_text(
            app_gradle.read_text(encoding="utf-8").replace(
                "apply plugin: 'com.google.gms.google-services'\n", ""
            ),
            encoding="utf-8",
        )
        print("Firebase config not supplied: Google Services plugin disabled for this test build.")

    print("NovaGram overlay applied.")
    print("Application ID: app.novagram.client")
    print("Display name: NovaGram")


if __name__ == "__main__":
    main()
