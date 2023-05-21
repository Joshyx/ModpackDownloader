# Modpack Downloader
A commandline interface for downloading all mods from a CurseForge modpack.

## Usage
- Run the program with the first argument being the path of your modpack and the second one your destination path:
   ```cmd
   java -jar ModpackDownloader-1.0-jar-with-dependencies.jar {{folder_or_zip_with_manifest}} {{destination_path}}
   ```

- (Optional) To create and define you own API key:
   1. Go to https://console.curseforge.com/?#/signup and create an account to get access to their API ([Tutorial](https://docs.curseforge.com/#your-next-steps))
   2. [Generate an API Key](https://console.curseforge.com/#/api-keys)
   3. Put that API Key in an environment variable called _CURSEFORGE_API_KEY_

      `export CURSEFORGE_API_KEY='{{your_api_key}}'` on unix \
      `set CURSEFORGE_API_KEY={{your_api_key}}` on windows
   4. Run the program
