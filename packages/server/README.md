
# How to Run All Services

This guide explains how to easily run all services using the provided taskfile script located in the `server` folder of the project.

## Prerequisites

Before running the services, ensure that you have:

- All necessary configuration files in place for each service.
- Installed [Task](https://github.com/go-task/task/releases) to manage the service tasks.

## Steps to get autoupdate of config files

1. **Navigate to scripts folder:**
   Run this command

   powershell:
   ```ps1
   cd  ./scripts; New-Item .env;
   ```

   cmd
   ```cmd
   cd scripts && type NUL > .env
   ```

   In .env file paste the content that can be obtained from back-end developers.

2. **Ensure that you have installed Python on your PC**
   In terminal run the following command:

   ```
   python --version
   ```

   run
   ```py
   pip install requests python-dotenv
   ```

   run
   ```
   npm install
   ```
   
## Steps to Run All Services

1. **Navigate to the project folder:**

   Open a terminal or command prompt, and change the directory to the folder where the services are stored. For example:

   ```bash
   cd C:/Users/SomeFolder/devhub/packages/server
   ```

2. **Run all services using Task:**

   To start all services simultaneously, use the following command:

   ```bash
   task run_all
   ```

   This command will automatically launch all the services defined in the taskfile.

## Running a Single Service

If you want to run only a specific service, you can use the following command, replacing `[ServiceName]` with the actual name of the service (e.g., `auth`, `post`, etc.):

```bash
task run_[ServiceName]_service
```

## Additional Notes

- Ensure that all configuration files for each service are properly set up before running the services.
- If you encounter issues with the configuration, double-check that the necessary environment variables and config files are correctly placed in the respective service directories.

## Using of Api Getaway

- You can use api getaway after running `all` services.
- You can see swaggers of all services by pasting this adress: `http://localhost:5295/index.html`
