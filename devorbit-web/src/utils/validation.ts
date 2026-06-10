export interface ValidationResult {
  valid: boolean;
  errors: Record<string, string>;
}

export function validateEmail(email: string): boolean {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
}

export function validatePhone(phone: string): boolean {
  const re = /^(0[3|5|7|8|9])+([0-9]{8})$/;
  return re.test(phone.replace(/\s/g, ""));
}

export function validateStudentId(id: string): boolean {
  return /^\d{8,10}$/.test(id);
}

export function validateRequired(value: string, fieldName: string): string | null {
  if (!value || value.trim().length === 0) {
    return `${fieldName} không được để trống`;
  }
  return null;
}

export function validateMinLength(value: string, min: number, fieldName: string): string | null {
  if (value.length < min) {
    return `${fieldName} phải có ít nhất ${min} ký tự`;
  }
  return null;
}

export function validateMaxLength(value: string, max: number, fieldName: string): string | null {
  if (value.length > max) {
    return `${fieldName} không được vượt quá ${max} ký tự`;
  }
  return null;
}

export function validateForm<T extends Record<string, unknown>>(
  data: T,
  rules: Record<keyof T, (value: T[keyof T]) => string | null>
): ValidationResult {
  const errors: Record<string, string> = {};
  for (const [field, validator] of Object.entries(rules)) {
    const error = validator(data[field as keyof T]);
    if (error) errors[field] = error;
  }
  return { valid: Object.keys(errors).length === 0, errors };
}
