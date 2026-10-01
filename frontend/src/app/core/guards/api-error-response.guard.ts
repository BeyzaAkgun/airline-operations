import type { ApiErrorResponse } from "../models/api-error-response.model";

export function isApiErrorResponse(
  value: unknown
): value is ApiErrorResponse {
  if (
    typeof value !== 'object' ||
    value === null ||
    Array.isArray(value)
  ) {
    return false;
  }

  if (!('timestamp' in value) || typeof value.timestamp !== 'string') {
    return false;
  }

  if (!('status' in value) || typeof value.status !== 'number') {
    return false;
  }

  if (!('message' in value) || typeof value.message !== 'string') {
    return false;
  }

  if (!('path' in value) || typeof value.path !== 'string') {
    return false;
  }

  if (
    !('fieldErrors' in value) ||
    typeof value.fieldErrors !== 'object' ||
    value.fieldErrors === null ||
    Array.isArray(value.fieldErrors)
  ) {
    return false;
  }

  return Object.values(value.fieldErrors).every(
    message => typeof message === 'string'
  );
}