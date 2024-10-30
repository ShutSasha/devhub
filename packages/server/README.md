
# How to Run All Services

This guide explains how to easily run all services using the provided taskfile script located in the `server` folder of the project.

## Prerequisites

Before running the services, ensure that you have:

- All necessary configuration files in place for each service.
- Installed [Task](https://github.com/go-task/task/releases) to manage the service tasks.

## Steps to Run All Services

1. **Paste configuration files into services**

   For AuthService paste this appsettings.Development.json file into **root** directory of a service **(Ask Developer for SendGridKey)**
   
```json
{  
   "Logging": {  
      "LogLevel": {  
         "Default": "Information",  
         "Microsoft.AspNetCore": "Warning"  
      }  
   },  
   "MongoDbSettings":{  
      "ConnectionUri":"mongodb+srv://root:<db_password>@devhubdb.jsttz.mongodb.net/?retryWrites=true&w=majority&appName=DevHubDB",  
      "DatabaseName": "DevHubDB",  
      "CollectionName": "users"  
   },  
   "SenderData": {    
      "SenderEmail" : "devhubmailsystem@gmail.com"  
   },   
   "JwtOptions": {  
      "AccessSecretKey": "MyExtraMegaSuperKeyThatYouCanUnderstand",  
      "RefreshSecretKey": "MyExtraMegaSuperRefreshKeyThatYouCanUnderstand",  
   }
}
```
   
   For PostService add a **config** folder into **root** directory of a service and paste this config.yaml file into this config folder.
   
  ```yaml
	env: "local"
	storage_path: "mongodb+srv://root:<db_password>@devhubdb.jsttz.mongodb.net/?retryWrites=true&w=majority&appName=DevHubDB"
	http:
	  port: 8080
	  timeout: 10s
  ```


2. **Navigate to the project folder:**

   Open a terminal or command prompt, and change the directory to the folder where the services are stored. For example:

   ```bash
   cd C:/Users/SomeFolder/devhub/packages/server
   ```

3. **Run all services using Task:**

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
