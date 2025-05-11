package com.example.mainservice.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String home() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Currency Conversion API</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 50px auto; background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { text-align: center; color: #333; }
                    .btn { display: inline-block; background-color: #4CAF50; color: white; padding: 10px 15px; text-decoration: none; border-radius: 4px; margin: 10px 5px 10px 0; }
                    .btn.signup { background-color: #2196F3; }
                    .btn:hover { opacity: 0.9; }
                    .endpoints { margin-top: 30px; }
                    .endpoint { margin-bottom: 20px; padding: 15px; background-color: #f9f9f9; border-radius: 4px; }
                    .method { display: inline-block; padding: 5px 10px; border-radius: 3px; font-weight: bold; margin-right: 10px; }
                    .get { background-color: #61affe; color: white; }
                    .post { background-color: #49cc90; color: white; }
                    .put { background-color: #fca130; color: white; }
                    .delete { background-color: #f93e3e; color: white; }
                    .path { font-family: monospace; font-size: 1.1em; }
                    .description { margin-top: 5px; color: #555; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Currency Conversion API</h1>
                    <p>Welcome to the Currency Conversion API. This API allows you to convert currencies using real-time exchange rates.</p>
                    <a href="/api/auth/login" class="btn">Login</a>
                    <a href="/api/auth/signup" class="btn signup">Sign Up</a>
                    
                    <div class="endpoints">
                        <h2>API Endpoints</h2>
                        
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="path">/api/auth/signup</span>
                            <div class="description">Register a new user account and receive a JWT token</div>
                        </div>
                        
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="path">/api/auth/login</span>
                            <div class="description">Authenticate and receive a JWT token</div>
                        </div>
                        
                        <div class="endpoint">
                            <span class="method get">GET</span>
                            <span class="path">/status</span>
                            <div class="description">Check API status</div>
                        </div>
                        
                        <div class="endpoint">
                            <span class="method post">POST</span>
                            <span class="path">/convert</span>
                            <div class="description">Convert currency (requires authentication)</div>
                        </div>
                        
                        <div class="endpoint">
                            <span class="method get">GET</span>
                            <span class="path">/rate?from=USD&to=EUR</span>
                            <div class="description">Get exchange rate (via rate-service, requires authentication)</div>
                        </div>
                    </div>
                    
                    <h2>Authentication Required</h2>
                    <p>Most endpoints require authentication using a JWT token. You can obtain a token by logging in or signing up.</p>
                    <p>Add the token to your requests using the Authorization header:</p>
                    <pre>Authorization: Bearer eyJhbG...</pre>
                    
                    <h3>Default Users</h3>
                    <p><strong>Regular user:</strong> username=user, password=password</p>
                    <p><strong>Admin:</strong> username=admin, password=admin123</p>
                    <p>Or sign up for a new account with your own credentials!</p>
                </div>
            </body>
            </html>
            """;
    }
}