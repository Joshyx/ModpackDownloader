# 🔩 Modpack Downloader 

[![Build](https://github.com/Joshyx/ModpackDownloader/actions/workflows/build.yml/badge.svg)](https://github.com/Joshyx/ModpackDownloader/actions/workflows/build.yml)

A command-line interface for downloading all mods from a CurseForge modpack.

- Simple to use.
- Mod downloading is done asynchronously and checks whether the file doesn't already exist in the output folder.
- Resource packs and overrides are handled properly.

## Usage
- Run the program with the first argument being the path of your modpack and the second one your destination path:
   ```cmd
   java -jar ModpackDownloader-1.1.jar {{folder_or_zip_with_manifest}} {{destination_path}}
   ```

- Optionally you can create and define you own API key:
   1. Go to https://console.curseforge.com/?#/signup and create an account to get access to their API ([Tutorial](https://docs.curseforge.com/#your-next-steps))
   2. [Generate an API Key](https://console.curseforge.com/#/api-keys)
   3. Put that API Key in an environment variable called _CURSEFORGE_API_KEY_

      `export CURSEFORGE_API_KEY='{{your_api_key}}'` on unix \
      `set CURSEFORGE_API_KEY={{your_api_key}}` on windows
   4. Run the program
