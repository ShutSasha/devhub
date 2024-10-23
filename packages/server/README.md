
# How to Run All Services

This guide explains how to easily run all services using the provided taskfile script located in the `server` folder of the project.

## Prerequisites

Before running the services, ensure that you have:

- All necessary configuration files in place for each service.

## Steps to Run All Services

1. **Navigate to the project folder:**

   Open a terminal or command prompt, and change the directory to the folder where the services are stored. For example:

   ```pwsh
   cd C:/Users/SomeFolder/devhub/packages/server
   ```

2. **Run all services using Task:**

   To start all services simultaneously, use the following command:

   ```pwsh
   task run_all
   ```

   This command will automatically launch all the services defined in the taskfile.

## Running a Single Service

If you want to run only a specific service, you can use the following command, replacing `[ServiceName]` with the actual name of the service (e.g., `auth`, `post`, etc.):

```pwsh
task run_[ServiceName]_service
```

## Additional Notes

- Ensure that all configuration files for each service are properly set up before running the services.
- If you encounter issues with the configuration, double-check that the necessary environment variables and config files are correctly placed in the respective service directories.

