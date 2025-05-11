package com.example.mainservice.controller;

import com.example.mainservice.model.AuthRequest;
import com.example.mainservice.model.AuthResponse;
import com.example.mainservice.model.SignupRequest;
import com.example.mainservice.model.User;
import com.example.mainservice.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication operations for user login and signup")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register new user", description = "Creates a new user account and returns an authentication token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or username already exists"),
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            AuthResponse response = authService.signup(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "error_code", "SIGNUP_ERROR"
            ));
        }
    }

    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account disabled")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Validate token", description = "Validates a JWT token and returns user information if valid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        User user = authService.validateToken(request.get("token"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * HTML login form renderer for browser access
     * Shows a simple login form when accessing the API via browser
     */
    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Login</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 400px; margin: 50px auto; background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { text-align: center; color: #333; }
                    label { display: block; margin-bottom: 5px; }
                    input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
                    button { width: 100%; background-color: #4CAF50; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer; }
                    button:hover { background-color: #45a049; }
                    .result { margin-top: 20px; padding: 10px; background-color: #f0f0f0; border-radius: 4px; display: none; }
                    .error { color: #f44336; margin-top: 10px; }
                    .links { text-align: center; margin-top: 15px; }
                    .links a { color: #2196F3; text-decoration: none; }
                    .links a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Currency Conversion API</h1>
                    <h2>Login</h2>
                    <div>
                        <label for="username">Username:</label>
                        <input type="text" id="username" name="username" placeholder="Username" required>
                    </div>
                    <div>
                        <label for="password">Password:</label>
                        <input type="password" id="password" name="password" placeholder="Password" required>
                    </div>
                    <button id="loginBtn">Login</button>
                    <div class="error" id="error"></div>
                    <div class="result" id="result">
                        <p>Token: <span id="token"></span></p>
                        <p>Use this token in the Authorization header like this: <br>
                        <code>Authorization: Bearer {token}</code></p>
                    </div>
                    <div class="links">
                        <p>Don't have an account? <a href="/api/auth/signup">Sign up</a></p>
                    </div>
                </div>
                <script>
                    document.getElementById('loginBtn').addEventListener('click', function() {
                        const username = document.getElementById('username').value;
                        const password = document.getElementById('password').value;
                        const errorElem = document.getElementById('error');
                        
                        if (!username || !password) {
                            errorElem.textContent = 'Username and password are required';
                            return;
                        }
                        
                        fetch('/api/auth/login', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ username, password })
                        })
                        .then(response => {
                            if (!response.ok) {
                                return response.json().then(data => {
                                    throw new Error(data.error || 'Login failed');
                                });
                            }
                            return response.json();
                        })
                        .then(data => {
                            document.getElementById('token').textContent = data.token;
                            document.getElementById('result').style.display = 'block';
                            errorElem.textContent = '';
                        })
                        .catch(error => {
                            errorElem.textContent = error.message;
                        });
                    });
                </script>
            </body>
            </html>
            """;
    }
    
    /**
     * HTML signup form renderer for browser access
     */
    @GetMapping(value = "/signup", produces = MediaType.TEXT_HTML_VALUE)
    public String signupPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Signup</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 400px; margin: 50px auto; background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { text-align: center; color: #333; }
                    label { display: block; margin-bottom: 5px; }
                    input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
                    button { width: 100%; background-color: #2196F3; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer; }
                    button:hover { background-color: #0b7dda; }
                    .result { margin-top: 20px; padding: 10px; background-color: #f0f0f0; border-radius: 4px; display: none; }
                    .error { color: #f44336; margin-top: 10px; }
                    .links { text-align: center; margin-top: 15px; }
                    .links a { color: #4CAF50; text-decoration: none; }
                    .links a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Currency Conversion API</h1>
                    <h2>Sign Up</h2>
                    <div>
                        <label for="username">Username:</label>
                        <input type="text" id="username" name="username" placeholder="Choose a username (3+ characters)" required>
                    </div>
                    <div>
                        <label for="password">Password:</label>
                        <input type="password" id="password" name="password" placeholder="Choose a password (6+ characters)" required>
                    </div>
                    <button id="signupBtn">Sign Up</button>
                    <div class="error" id="error"></div>
                    <div class="result" id="result">
                        <p>Token: <span id="token"></span></p>
                        <p>Use this token in the Authorization header like this: <br>
                        <code>Authorization: Bearer {token}</code></p>
                    </div>
                    <div class="links">
                        <p>Already have an account? <a href="/api/auth/login">Log in</a></p>
                    </div>
                </div>
                <script>
                    document.getElementById('signupBtn').addEventListener('click', function() {
                        const username = document.getElementById('username').value;
                        const password = document.getElementById('password').value;
                        const errorElem = document.getElementById('error');
                        
                        if (!username || !password) {
                            errorElem.textContent = 'Username and password are required';
                            return;
                        }
                        
                        if (username.length < 3) {
                            errorElem.textContent = 'Username must be at least 3 characters';
                            return;
                        }
                        
                        if (password.length < 6) {
                            errorElem.textContent = 'Password must be at least 6 characters';
                            return;
                        }
                        
                        fetch('/api/auth/signup', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ username, password })
                        })
                        .then(response => {
                            if (!response.ok) {
                                return response.json().then(data => {
                                    throw new Error(data.error || 'Signup failed');
                                });
                            }
                            return response.json();
                        })
                        .then(data => {
                            document.getElementById('token').textContent = data.token;
                            document.getElementById('result').style.display = 'block';
                            errorElem.textContent = '';
                        })
                        .catch(error => {
                            errorElem.textContent = error.message;
                        });
                    });
                </script>
            </body>
            </html>
            """;
    }
}
