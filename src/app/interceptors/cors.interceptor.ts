import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CorsInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Clone the request with proper headers for API communication
    const corsReq = req.clone({
      withCredentials: false,
      headers: req.headers
        .set('Content-Type', 'application/json')
        .set('Accept', 'application/json')
        // Add a Cache-Control header to prevent caching
        .set('Cache-Control', 'no-cache, no-store, must-revalidate, post-check=0, pre-check=0')
        .set('Pragma', 'no-cache')
        .set('Expires', '0')
    });

    return next.handle(corsReq);
  }
}
