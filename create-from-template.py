#!/usr/bin/env python3

import datetime
import os
import re
import shutil
import stat
import subprocess
from pathlib import Path


TEMPLATE_REPO_URL = "https://github.com/kylecorry31/Trail-Sense-Sample-Plugin.git"
TEMPLATE_APP_NAME = "Trail Sense Sample Plugin"
TEMPLATE_ARTIFACT_NAME = "Trail-Sense-Sample-Plugin"
TEMPLATE_PACKAGE_NAME = "com.kylecorry.trail_sense.plugin.sample"


def clone(repo_url, dir_name):
    subprocess.run(["git", "clone", repo_url, dir_name], check=True)


def on_rm_error(func, path, exc_info):
    # From: https://stackoverflow.com/questions/4829043/how-to-remove-read-only-attrib-directory-with-python-in-windows
    os.chmod(path, stat.S_IWRITE)
    os.unlink(path)


def delete_dir(dir_name):
    if not os.path.exists(dir_name):
        return

    shutil.rmtree(dir_name, onerror=on_rm_error)


def replace_in_file(path, replacements):
    contents = path.read_text()
    for old, new in replacements.items():
        contents = contents.replace(old, new)
    path.write_text(contents)


def replace_in_files(dir_name, replacements):
    valid_extensions = {
        ".gradle",
        ".java",
        ".json",
        ".kt",
        ".kts",
        ".md",
        ".iml",
        ".properties",
        ".xml",
        ".yaml",
        ".yml",
    }

    for path in Path(dir_name).rglob("*"):
        if path.is_file() and path.suffix in valid_extensions:
            replace_in_file(path, replacements)


def rename_package_dir(dir_name, source_set, package_name):
    old_path = Path(dir_name) / "app" / "src" / source_set / "java" / Path(
        TEMPLATE_PACKAGE_NAME.replace(".", "/")
    )
    if not old_path.exists():
        return

    new_path = Path(dir_name) / "app" / "src" / source_set / "java" / Path(
        package_name.replace(".", "/")
    )
    new_path.parent.mkdir(parents=True, exist_ok=True)
    old_path.rename(new_path)


def update_license(dir_name):
    path = Path(dir_name) / "LICENSE"
    if not path.exists():
        return

    contents = path.read_text()
    contents = re.sub(r"202\d", str(datetime.datetime.now().year), contents)
    path.write_text(contents)


def get_artifact_name(app_name):
    return app_name.replace(" ", "-").replace(".", "-")


def main():
    app_name = input("Plugin Name: ")
    package_name = input("Package Name: ")
    default_dir_name = app_name.replace(" ", "-").replace(".", "-")
    dir_name = input("Directory Name [" + default_dir_name + "]: ")

    if dir_name == "":
        dir_name = default_dir_name

    if os.path.exists(dir_name):
        should_overwrite = input("Directory already exists. Overwrite? [Y/N]: ")
        if should_overwrite.lower() != "y":
            return

        delete_dir(dir_name)

    clone(TEMPLATE_REPO_URL, dir_name)

    delete_dir(f"{dir_name}/.git")

    replace_in_files(
        dir_name,
        {
            TEMPLATE_PACKAGE_NAME: package_name,
            TEMPLATE_APP_NAME: app_name,
            TEMPLATE_ARTIFACT_NAME: get_artifact_name(app_name),
            "Sample plugin is ready to use in Trail Sense.": (
                app_name + " is ready to use in Trail Sense."
            ),
        },
    )

    for source_set in ["main", "test", "androidTest"]:
        rename_package_dir(dir_name, source_set, package_name)

    update_license(dir_name)

    readme = Path(dir_name) / "README.md"
    if readme.exists():
        readme.write_text(f"# {app_name}\n")

    idea_name = Path(dir_name) / ".idea" / ".name"
    if idea_name.exists():
        idea_name.write_text(app_name)

    script = Path(dir_name) / "create-from-template.py"
    if script.exists():
        script.unlink()


if __name__ == "__main__":
    main()
