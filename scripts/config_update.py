import os
import re
import requests
from dotenv import load_dotenv, set_key

configs = [
    ".\\packages\\server\\CommentService\\config\\config.yaml.temp",
    ".\\packages\\server\\PostService\\config\\config.yaml.temp",
    ".\\packages\\server\\AuthService\\appsettings.Development.json.temp",
    ]

load_dotenv()

def update_env_variable(key, value):
    set_key('.env', key, value)
    os.environ[key] = value

HCP_CLIENT_ID = os.getenv("HCP_CLIENT_ID")
HCP_CLIENT_SECRET = os.getenv("HCP_CLIENT_SECRET")

if not HCP_CLIENT_ID or not HCP_CLIENT_SECRET:
    print("HCP_CLIENT_ID or HCP_CLIENT_SECRET not found in .env file.")
    exit(1)

token_url = "https://auth.idp.hashicorp.com/oauth2/token"

try:
    response = requests.post(
        token_url,
        headers={"Content-Type": "application/x-www-form-urlencoded"},
        data={
            "client_id": HCP_CLIENT_ID,
            "client_secret": HCP_CLIENT_SECRET,
            "grant_type": "client_credentials",
            "audience": "https://api.hashicorp.cloud"
        }
    )
    response.raise_for_status()
    HCP_API_TOKEN = response.json().get("access_token")

    if not HCP_API_TOKEN:
        print("Can not geta access token.")
        exit(1)

except requests.exceptions.RequestException as e:
    print(f"Failure while getting access token: {e}")
    exit(1)

secrets_url = "https://api.cloud.hashicorp.com/secrets/2023-11-28/organizations/2195f523-34e7-4ca7-88a4-b99d296410a5/projects/364cdeac-54de-4da0-96c2-b56a89ed8b01/apps/devhub/secrets:open"

try:
    secrets_response = requests.get(
        secrets_url,
        headers={"Authorization": f"Bearer {HCP_API_TOKEN}"}
    )
    secrets_response.raise_for_status()
    secrets_data = secrets_response.json()
    
    secrets_dict = {
        secret['name']: secret['static_version']['value']
        for secret in secrets_data.get('secrets', [])
    }

except requests.exceptions.RequestException as e:
    print(f"Failure while getting secrets: {e}")
    exit(1)


def update_config_files(directory="."):
    temp_file_path = directory
    
    config_file_path = temp_file_path[:-5]
    
    with open(temp_file_path, 'r') as temp_file:
        content = temp_file.read()
    
    for secret_name, secret_value in secrets_dict.items():
        placeholder_pattern = re.escape(secret_name)
        content = re.sub(placeholder_pattern, secret_value, content)
    
    with open(config_file_path, 'w') as config_file:
        config_file.write(content)
    
    print(f"Config file updated: {config_file_path}")

for file in configs:
    update_config_files(file)
