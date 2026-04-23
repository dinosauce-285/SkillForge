import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { createClient, SupabaseClient } from '@supabase/supabase-js';

@Injectable()
export class SupabaseService {
    private readonly supabaseInstance: SupabaseClient;

    constructor() {
        const supabaseUrl = process.env.SUPABASE_URL || '';
        const supabaseKey = process.env.SUPABASE_SERVICE_ROLE_KEY || '';

        if (!supabaseUrl || !supabaseKey) {
            throw new InternalServerErrorException(
                'Supabase URL and Service Role Key must be provided in environment variables.',
            );
        }

        this.supabaseInstance = createClient(supabaseUrl, supabaseKey);
    }

    public getClient(): SupabaseClient {
        return this.supabaseInstance;
    }
}
