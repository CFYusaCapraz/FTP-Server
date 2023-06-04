# Description

This is an application for concurrent FTP like server implemented by using JAVA programming language.

## Folder Structure

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

### Running the application

While running the application there must be 3 parameter given in the command line. These parameters are (in order) :

- Password file which contains the list of usernames and the passwords as a comma seperated values
- Port number. Must be higer than 1024 if you are not running the application as root or admin user.
- Directory to run on. The directory you want to serve in the FTP server. This should be a valid directory.
