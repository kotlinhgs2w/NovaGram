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


def add_modification_notice(path: Path, notice: str, xml: bool = False) -> None:
    data = path.read_text(encoding="utf-8")
    if notice in data:
        return
    if xml:
        updated, count = re.subn(r"^(<\?xml[^>]+\?>\s*)", r"\1<!-- " + notice + " -->\n", data, count=1)
        if count != 1:
            raise SystemExit(f"Could not add modification notice to {path}")
        path.write_text(updated, encoding="utf-8")
    else:
        path.write_text(notice + "\n" + data, encoding="utf-8")


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
    replace_once(
        strings,
        r'(<string name="AppNameBeta">).*?(</string>)',
        r"\1NovaGram\2",
        "debug app display name",
    )
    replace_once(build_vars, r"public static int APP_ID = [0-9]+;",
                 f"public static int APP_ID = {api_id};", "Telegram API ID")
    replace_once(build_vars, r'public static String APP_HASH = "[a-fA-F0-9]+";',
                 f'public static String APP_HASH = "{api_hash}";', "Telegram API hash")
    replace_once(build_vars, r"public static boolean SUPPORTS_PASSKEYS = true;",
                 "public static boolean SUPPORTS_PASSKEYS = false;", "official-package passkey flag")

    add_modification_notice(gradle_props, "# Modified by NovaGram on 2026-11-22: application identity.")
    add_modification_notice(build_vars, "// Modified by NovaGram on 2026-11-22: app API credentials and capability flags.")
    add_modification_notice(strings, "Modified by NovaGram on 2026-11-22: application display name.", xml=True)
    add_modification_notice(app_gradle, "// Modified by NovaGram on 2026-11-22: Firebase setup is optional for CI builds.")

    overlay_root = Path(__file__).resolve().parents[1] / "overlay" / "java"
    java_root = source / "TMessagesProj/src/main/java"
    if not overlay_root.is_dir():
        raise SystemExit(f"Overlay sources not found: {overlay_root}")
    for java_file in overlay_root.rglob("*.java"):
        destination = java_root / java_file.relative_to(overlay_root)
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(java_file, destination)

    resource_overlay = Path(__file__).resolve().parents[1] / "overlay" / "res"
    resource_root = source / "TMessagesProj/src/main/res"
    for resource_file in resource_overlay.rglob("*"):
        if resource_file.is_file():
            destination = resource_root / resource_file.relative_to(resource_overlay)
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(resource_file, destination)

    for manifest in (
        source / "TMessagesProj/config/debug/AndroidManifest.xml",
        source / "TMessagesProj/config/release/AndroidManifest.xml",
    ):
        if not manifest.is_file():
            raise SystemExit(f"Expected app manifest is missing: {manifest}")
        replace_once(manifest, r'android:icon="@mipmap/ic_launcher"',
                     'android:icon="@mipmap/ic_novagram"', "launcher icon")
        replace_once(manifest, r'android:roundIcon="@mipmap/ic_launcher_round"',
                     'android:roundIcon="@mipmap/ic_novagram_round"', "round launcher icon")
        add_modification_notice(
            manifest,
            "Modified by NovaGram on 2026-11-22: application label and launcher icons.",
            xml=True,
        )

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
