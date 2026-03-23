import 'package:ketari/models/auth_response.dart';
import 'package:ketari/services/auth_service.dart';

class Authcontroller {
  final AuthService _authService = AuthService();

  Future<AuthResponse> registerUser(
    String firstName,
    String lastName,
    String email,
  ) {
    return _authService.register(
      firstName: firstName,
      lastName: lastName,
      email: email,
    );
  }
  
}
