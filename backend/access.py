import os
from supabase import create_client, Client

# Recupera le variabili di ambiente
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")

# Initialize Supabase client
supabase: Client = create_client(
    supabase_url,
    supabase_key
)

class LoginError(Exception):
    pass

def handle_login(username: str, password: str):
    try:
        # Call the login RPC function
        response = supabase.rpc('login', {
            '_username': username,
            '_password': password
        }).execute()

        if response.data and len(response.data) > 0:
            user_data = response.data[0]
            return {
                "success": True,
                "user_id": user_data['u_id'],
                "username": user_data['user_name']
            }
        else:
            raise LoginError("Invalid credentials")

    except Exception as e:
        print(f"Login error: {str(e)}")
        raise LoginError(str(e))