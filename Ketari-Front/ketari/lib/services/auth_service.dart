import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:ketari/models/auth_response.dart';

class AuthService {
  final String baseUrl = "http://192.168.137.158:3000";

  Future<AuthResponse> register({
    required String firstName,
    required String lastName,
    required String email,
  }) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/api/auth/register"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({
          "firstName": firstName,
          "lastName": lastName,
          "email": email,
        }),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200 || response.statusCode == 201) {
        return AuthResponse.success(data);
      } else {
        return AuthResponse.error(data);
      }
    } catch (e) {
      print("Auth Error : $e");
      return AuthResponse(success: false, message: e.toString());
    }
  }

  Future<AuthResponse> login({required email}) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/api/auth/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"email": email}),
      );
      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return AuthResponse.success(data);
      } else {
        return AuthResponse.error(data);
      }
    } catch (e) {
      print("Auth Error: $e");
      return AuthResponse(success: false, message: e.toString());
    }
  }
}
