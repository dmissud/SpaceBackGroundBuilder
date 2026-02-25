import {HttpErrorHandlerService} from './http-error-handler.service';

describe('HttpErrorHandlerService', () => {
  let service: HttpErrorHandlerService;

  beforeEach(() => {
    service = new HttpErrorHandlerService();
  });

  it('should extract message from error.error.message (HTTP backend error)', () => {
    const error = {error: {message: 'Backend validation failed'}, message: 'Http failure'};
    expect(service.extractMessage(error)).toBe('Backend validation failed');
  });

  it('should fall back to error.message when error.error.message is absent', () => {
    const error = {message: 'Network error'};
    expect(service.extractMessage(error)).toBe('Network error');
  });

  it('should fall back to error.statusText when message is absent', () => {
    const error = {statusText: 'Internal Server Error'};
    expect(service.extractMessage(error)).toBe('Internal Server Error');
  });

  it('should return default message when all fields are absent', () => {
    expect(service.extractMessage({})).toBe('An unknown error occurred');
  });

  it('should handle null/undefined error gracefully', () => {
    expect(service.extractMessage(null)).toBe('An unknown error occurred');
    expect(service.extractMessage(undefined)).toBe('An unknown error occurred');
  });
});
