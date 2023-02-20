## Modpack Downloader
A simple commandline interface for downloading all mods from a Curseforge Modpack because 
I couldn't get the fucking launcher to work on Linux.

### Usage
1. Go to https://console.curseforge.com/?#/signup and create an account to get access to their API ([Tutorial](https://docs.curseforge.com/#your-next-steps))
2. [Generate an API Key](https://console.curseforge.com/#/api-keys)
3. Put that API Key in an environment variable called _CURSEFORGE_API_KEY_

   `export CURSEFORGE_API_KEY='{{your_key_here}}'` on unix
4. Run the program with the first argument being the path of your modpack (unzipped) and the second one your destination pathz