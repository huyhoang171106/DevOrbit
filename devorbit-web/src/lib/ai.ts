import { createOpenAICompatible } from '@ai-sdk/openai-compatible';

export const aiProvider = createOpenAICompatible({
  name: 'big-pickle',
  baseURL: 'https://opencode.ai/zen/v1',
  apiKey: import.meta.env.VITE_LLM_API_KEY,
});

export const aiModel = aiProvider('big-pickle');
