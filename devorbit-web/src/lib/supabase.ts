import { createClient, SupabaseClient } from "@supabase/supabase-js";

const supabaseUrl =
  import.meta.env.VITE_SUPABASE_URL ??
  import.meta.env.NEXT_PUBLIC_SUPABASE_URL ??
  "";

const supabaseAnonKey =
  import.meta.env.VITE_SUPABASE_ANON_KEY ??
  import.meta.env.NEXT_PUBLIC_SUPABASE_PUBLISHABLE_KEY ??
  "";

let _supabaseInstance: SupabaseClient | null = null;

function getSupabase(): SupabaseClient | null {
  if (_supabaseInstance) return _supabaseInstance;
  if (!supabaseUrl || !supabaseAnonKey) {
    console.warn(
      "Supabase credentials missing. Set VITE_SUPABASE_URL and VITE_SUPABASE_ANON_KEY in .env",
    );
    return null;
  }
  _supabaseInstance = createClient(supabaseUrl, supabaseAnonKey);
  return _supabaseInstance;
}

export { getSupabase };
