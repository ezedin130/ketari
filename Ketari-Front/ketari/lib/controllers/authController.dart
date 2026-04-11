import 'package:ketari/models/auth_response.dart';
import 'package:ketari/services/auth_service.dart';
import 'package:shared_preferences/shared_preferences.dart';

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

  Future<AuthResponse> loginUser(String email) async {
    final response = await _authService.login(email: email);
    if (response.success && response.token != null) {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString("token", response.token!);
    }
    return response;
  }
}
