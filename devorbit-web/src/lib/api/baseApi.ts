import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { getStudentToken } from '../auth'
import { apiBaseUrl } from '../api'

/**
 * RTK Query base API instance.
 * Automatically injects auth headers from stored student token.
 * Uses the same auth system as the existing manual apiGet/apiPost.
 */
export const api = createApi({
    reducerPath: 'api',
    baseQuery: fetchBaseQuery({
        baseUrl: apiBaseUrl,
        prepareHeaders: (headers) => {
            const token = getStudentToken()
            if (token) {
                headers.set('Authorization', `Bearer ${token}`)
            }
            return headers
        },
    }),
    endpoints: () => ({}),
    tagTypes: ['Courses', 'Repos', 'Admin'],
})
