package accountservice.controller;

import accountservice.model.User;
import accountservice.payload.AuthRequest;
import accountservice.payload.AuthResponse;
import accountservice.payload.RegisterRequest;
import accountservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    try {
      User user = authService.registerUser(request.getUsername(), request.getPassword());
      return ResponseEntity.ok("User registered with id: " + user.getId());
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
      String token = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());
      return ResponseEntity.ok(new AuthResponse(token));
    } catch (RuntimeException e) {
      return ResponseEntity.status(401).body(e.getMessage());
    }
  }
}
