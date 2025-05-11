# Finance Service - Project Service Integration

## Authentication Setup

The Finance Service needs to authenticate with the Project Service. Follow these steps:

1. Get an authentication token from your authentication provider (e.g., Keycloak)
2. Add the token to your `application.properties` file:

```properties
project-service.auth.token=your_access_token_here
```

3. For development/testing purposes, you can:
   - Use Postman to obtain a token from your auth provider
   - Copy the token value (without "Bearer" prefix) into your properties file
   - Remember to refresh the token before it expires

## Testing Project Integration

Once you have configured authentication:

1. Start the Eureka Server
2. Start the Project Service
3. Start the Finance Service
4. Test the integration by accessing: `http://localhost:8081/api/factures/projects`

## Troubleshooting

If you encounter 401 Unauthorized errors:
- Your token might be expired - get a new one
- The token format might be incorrect - ensure it's copied correctly
- The Project Service might be using a different authentication mechanism
